/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.apache.tuscany.sca.binding.axis2;

import java.util.List;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider2;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeWire;

public class Axis2ReferenceBindingProvider implements ReferenceBindingProvider2 {

    private RuntimeComponent component;
    private RuntimeComponentReference reference;
    private WebServiceBinding wsBinding;
    private ServletHost servletHost;
    private MessageFactory messageFactory;
    private Axis2ServiceClient axisClient;
    private Axis2ServiceProvider axisProvider;
    private WebServiceBinding callbackBinding;

    //FIXME: following are only needed for the current tactical solutionn
    private boolean tactical = true;
    private ServiceClient serviceClient;

    public Axis2ReferenceBindingProvider(RuntimeComponent component,
                                         RuntimeComponentReference reference,
                                         WebServiceBinding wsBinding,
                                         ServletHost servletHost,
                                         MessageFactory messageFactory) {

        this.component = component;
        this.reference = reference;
        this.wsBinding = wsBinding;
        this.servletHost = servletHost;
        this.messageFactory = messageFactory;

        InterfaceContract contract = wsBinding.getBindingInterfaceContract();
        if (contract == null) {
            contract = reference.getInterfaceContract().makeUnidirectional(wsBinding.isCallback());
            wsBinding.setBindingInterfaceContract(contract);
        }

        // Set to use the Axiom data binding
        if (contract.getInterface() != null) {
            contract.getInterface().setDefaultDataBinding(OMElement.class.getName());
        }
        if (contract.getCallbackInterface() != null) {
            contract.getCallbackInterface().setDefaultDataBinding(OMElement.class.getName());
        }

        // ??? following line was in Axis2BindingBuilder before the SPI changes
        // and code reorg
        //
        // URI targetURI = wsBinding.getURI() != null ?
        // URI.create(wsBinding.getURI()) : URI.create("foo");
        //
        // targetURI was passed to the ReferenceBindingExtension constructor and
        // apparently was unused
        // Do we still need a targetURI?

        // wsBinding.setURI(component.getURI() + "#" + reference.getName());

        if (!wsBinding.isCallback()) {
            // this is a forward binding, so look for a matching callback binding
            if (reference.getCallback() != null) {
                for (org.apache.tuscany.sca.assembly.Binding binding :
                                          reference.getCallback().getBindings()) {
                    if (binding instanceof WebServiceBinding) {
                        // set the first compatible callback binding
                        setCallbackBinding((WebServiceBinding)binding);
                        continue;
                    }
                }
            }
        } else {
            // this is a callback binding, so look for all matching forward bindings
            for (org.apache.tuscany.sca.assembly.Binding binding : reference.getBindings()) {
                if (reference.getBindingProvider(binding) instanceof Axis2ReferenceBindingProvider) {
                    // set all compatible forward binding providers for this reference
                    ((Axis2ReferenceBindingProvider)reference.getBindingProvider(binding)).
                            setCallbackBinding(wsBinding);
                }
            }
        }

        if (tactical) {
            if (!wsBinding.isCallback()) {
                serviceClient = createServiceClient();
            }
        } else {
            if (!wsBinding.isCallback()) {
                axisClient = new Axis2ServiceClient(component, reference, wsBinding, servletHost,
                                                    messageFactory, callbackBinding);
            } else {
                //FIXME: need to support callbacks through self-references
                // For now, don't create a callback service provider for a self-reference
                // because this modifies the binding URI.  This messes up the service callback
                // wires because the self-reference has the same binding object as the service.
                if (!reference.getName().startsWith("$self$.")) {
                    axisProvider = new Axis2ServiceProvider(component, reference, wsBinding, servletHost,
                                                            messageFactory);
                }
            }
        }
    }

    protected void setCallbackBinding(WebServiceBinding callbackBinding) {
        if (this.callbackBinding == null) {
            this.callbackBinding = callbackBinding;
        }
    }

    //FIXME: only needed for the current tactical solution
    protected ServiceClient createServiceClient() {
        try {
            TuscanyAxisConfigurator tuscanyAxisConfigurator = new TuscanyAxisConfigurator();
            ConfigurationContext configContext = tuscanyAxisConfigurator.getConfigurationContext();
            QName serviceQName = wsBinding.getServiceName();
            String portName = wsBinding.getPortName();
            Definition wsdlDefinition = wsBinding.getWSDLDefinition().getDefinition();
            AxisService axisService = AxisService.createClientSideAxisService(wsdlDefinition,
                                                                              serviceQName,
                                                                              portName,
                                                                              new Options());

            return new ServiceClient(configContext, axisService);
        } catch (AxisFault e) {
            throw new RuntimeException(e); // TODO: better exception
        }
    }

    public void start() {
        if (tactical) {
            //FIXME: only needed for the current tactical solution
            for (InvocationChain chain : reference.getRuntimeWire(wsBinding).getInvocationChains()) {
                Invoker tailInvoker = chain.getTailInvoker();
                if (tailInvoker instanceof Axis2AsyncBindingInvoker) {
                    RuntimeWire callbackWire = reference.getRuntimeWire(callbackBinding);
                    Operation callbackOperation = findCallbackOperation(callbackBinding.getBindingInterfaceContract());
                    Axis2CallbackInvocationHandler invocationHandler
                        = new Axis2CallbackInvocationHandler(messageFactory, callbackWire);
                    Axis2ReferenceCallbackTargetInvoker callbackInvoker
                        = new Axis2ReferenceCallbackTargetInvoker(callbackOperation, callbackWire, invocationHandler);
                    ((Axis2AsyncBindingInvoker)tailInvoker).setCallbackTargetInvoker(callbackInvoker);
                }
            }
        } else {
            if (!wsBinding.isCallback()) {
                axisClient.start();
            } else {
                //FIXME: need to support callbacks through self-references
                if (!reference.getName().startsWith("$self$.")) {
                    axisProvider.start();
                }
            }
        }
    }

    public void stop() {
        if (tactical) {
            if (!wsBinding.isCallback()) {
                closeAxis2Connections();
            }
        } else {
            if (!wsBinding.isCallback()) {
                axisClient.stop();
            } else {
                //FIXME: need to support callbacks through self-references
                if (!reference.getName().startsWith("$self$.")) {
                    axisProvider.stop();
                }
            }
        }
    }

    private void closeAxis2Connections() {
        // close all connections that we have initiated, so that the jetty server
        // can be restarted without seeing ConnectExceptions
        HttpClient httpClient = (HttpClient)serviceClient.getServiceContext().getConfigurationContext()
            .getProperty(HTTPConstants.CACHED_HTTP_CLIENT);
        if (httpClient != null)
            ((MultiThreadedHttpConnectionManager)httpClient.getHttpConnectionManager()).shutdown();
    }

    public InterfaceContract getBindingInterfaceContract() {
        return wsBinding.getBindingInterfaceContract();
    }

    public boolean supportsAsyncOneWayInvocation() {
        return true;
    }

    @Deprecated
    public Invoker createInvoker(Operation operation, boolean isCallback) {
        if (isCallback) {
            throw new UnsupportedOperationException();
        } else {
            return createInvoker(operation);
        }
    }

    public Invoker createInvoker(Operation operation) {
        if (wsBinding.isCallback()) {
            throw new RuntimeException("Cannot create invoker for a callback binding");
        }
        if (!tactical) {
            return axisClient.createInvoker(operation);
        }

        //FIXME: remainder of this method's code only needed for the current tactical solution
        Axis2BindingInvoker invoker;

        if (reference.getInterfaceContract().getCallbackInterface() == null) {
            invoker = createOperationInvoker(serviceClient, operation, false, operation.isNonBlocking());
        } else {
            // FIXME: SDODataBinding needs to pass in TypeHelper and classLoader
            // as parameters.

            // FIXME: This makes the (BIG) assumption that there is only one
            // callback method
            // Relaxing this assumption, however, does not seem to be trivial,
            // it may depend on knowledge
            // of what actual callback method was invoked by the service at the
            // other end

            // the code to create the callback invoker has been moved to the start() method

            Axis2AsyncBindingInvoker asyncInvoker
                    = (Axis2AsyncBindingInvoker)createOperationInvoker(serviceClient, operation, true, false);
            invoker = asyncInvoker;
        }

        return invoker;
    }

    //FIXME: only needed for the current tactical solution
    private Operation findCallbackOperation(InterfaceContract contract) {
        List callbackOperations = contract.getCallbackInterface().getOperations();
        if (callbackOperations.size() != 1) {
            throw new RuntimeException("Can only handle one callback operation");
        }
        Operation callbackOperation = (Operation)callbackOperations.get(0);
        return callbackOperation;
    }

    //FIXME: only needed for the current tactical solution
    /**
     * Create and configure an Axis2BindingInvoker for each operation
     */
    private Axis2BindingInvoker createOperationInvoker(ServiceClient serviceClient,
                                                       Operation operation,
                                                       boolean hasCallback,
                                                       boolean isOneWay) {

        Options options = new Options();
        options.setTo(getPortLocationEPR());
        options.setProperty(HTTPConstants.CHUNKED, Boolean.FALSE);

        String operationName = operation.getName();

        String soapAction = getSOAPAction(operationName);
        if (soapAction != null && soapAction.length() > 1) {
            options.setAction(soapAction);
        }

        options.setTimeOutInMilliSeconds(30 * 1000); // 30 seconds

        SOAPFactory soapFactory = OMAbstractFactory.getSOAP11Factory();
        QName wsdlOperationQName = new QName(operationName);

        Axis2BindingInvoker invoker;
        if (hasCallback) {
            invoker = new Axis2AsyncBindingInvoker(serviceClient, wsdlOperationQName, options, soapFactory);
        } else if (isOneWay) {
            invoker = new Axis2OneWayBindingInvoker(serviceClient, wsdlOperationQName, options, soapFactory);
        } else {
            invoker = new Axis2BindingInvoker(serviceClient, wsdlOperationQName, options, soapFactory);
        }

        return invoker;
    }

    //FIXME: only needed for the current tactical solution
    protected EndpointReference getPortLocationEPR() {
        String ep = wsBinding.getURI();
        if (ep == null && wsBinding.getPort() != null) {
            List wsdlPortExtensions = wsBinding.getPort().getExtensibilityElements();
            for (final Object extension : wsdlPortExtensions) {
                if (extension instanceof SOAPAddress) {
                    ep = ((SOAPAddress)extension).getLocationURI();
                    break;
                }
            }
        }
        return new EndpointReference(ep);
    }

    //FIXME: only needed for the current tactical solution
    protected String getSOAPAction(String operationName) {
        Binding binding = wsBinding.getBinding();
        if (binding != null) {
            for (Object o : binding.getBindingOperations()) {
                BindingOperation bop = (BindingOperation)o;
                if (bop.getName().equalsIgnoreCase(operationName)) {
                    for (Object o2 : bop.getExtensibilityElements()) {
                        if (o2 instanceof SOAPOperation) {
                            return ((SOAPOperation)o2).getSoapActionURI();
                        }
                    }
                }
            }
        }
        return null;
    }

}
