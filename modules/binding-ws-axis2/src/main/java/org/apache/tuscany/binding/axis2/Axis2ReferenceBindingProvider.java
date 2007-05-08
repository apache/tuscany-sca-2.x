/**
 *
 * Copyright 2006 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.tuscany.binding.axis2;

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
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.tuscany.binding.ws.WebServiceBinding;
import org.apache.tuscany.core.RuntimeComponent;
import org.apache.tuscany.core.RuntimeComponentReference;
import org.apache.tuscany.core.RuntimeWire;
import org.apache.tuscany.interfacedef.InterfaceContract;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.invocation.Invoker;
import org.apache.tuscany.provider.ReferenceBindingProvider;

public class Axis2ReferenceBindingProvider implements ReferenceBindingProvider {

    private RuntimeComponent component;
    private RuntimeComponentReference reference;
    private WebServiceBinding wsBinding;
    private ConfigurationContext configContext;
    private ServiceClient serviceClient;

    public Axis2ReferenceBindingProvider(RuntimeComponent component,
                                         RuntimeComponentReference reference,
                                         WebServiceBinding wsBinding) {

        // TODO: before the SPI changes, a composite reference was passed to the builder.
        // Is the change to a component reference OK?

        this.component = component;
        this.reference = reference;
        this.wsBinding = wsBinding;

        try {
            TuscanyAxisConfigurator tuscanyAxisConfigurator = new TuscanyAxisConfigurator();
            configContext = tuscanyAxisConfigurator.getConfigurationContext();
        } catch (AxisFault e) {
            throw new RuntimeException(e); // TODO: better exception
        }
    }

    // methods for ReferenceBindingActivator

    public void start() {

        InterfaceContract contract = wsBinding.getBindingInterfaceContract();
        if (contract == null) {
            contract = reference.getInterfaceContract();
            wsBinding.setBindingInterfaceContract(contract);
        }

        // Set to use the Axiom data binding 
        contract.getInterface().setDefaultDataBinding(OMElement.class.getName());        

        // ??? following line was in Axis2BindingBuilder before the SPI changes and code reorg
        //
        // URI targetURI = wsBinding.getURI() != null ? URI.create(wsBinding.getURI()) : URI.create("foo");
        //
        // targetURI was passed to the ReferenceBindingExtension constructor and apparently was unused
        // Do we still need a targetURI?

        wsBinding.setURI(component.getURI() + "#" + reference.getName());

        // create an Axis2 ServiceClient
        serviceClient = createServiceClient();
    }

    public void stop() {

        // close all connections that we have initiated, so that the jetty server
        // can be restarted without seeing ConnectExceptions
        HttpClient httpClient = (HttpClient)serviceClient.getServiceContext().getConfigurationContext()
                .getProperty(HTTPConstants.CACHED_HTTP_CLIENT);
        if (httpClient != null)
            ((MultiThreadedHttpConnectionManager)httpClient.getHttpConnectionManager()).shutdown();
    }

    /**
     * Create an Axis2 ServiceClient
     */
    protected ServiceClient createServiceClient() {
        try {
            QName serviceQName = wsBinding.getServiceName();
            String portName = wsBinding.getPortName();
            Definition wsdlDefinition = wsBinding.getWSDLDefinition().getDefinition();
            AxisService axisService = AxisService.createClientSideAxisService(wsdlDefinition, serviceQName, portName, new Options());

            return new ServiceClient(configContext, axisService);
        } catch (AxisFault e) {
            throw new RuntimeException(e); // TODO: better exception
        }
    }

    // methods for ReferenceBindingProvider

    public InterfaceContract getBindingInterfaceContract() {
        return wsBinding.getBindingInterfaceContract();
    }

    public Invoker createInvoker(Operation operation, boolean isCallback) {
        Axis2BindingInvoker invoker;

        InterfaceContract contract = wsBinding.getBindingInterfaceContract();
        if (contract == null) {
            contract = reference.getInterfaceContract();
            wsBinding.setBindingInterfaceContract(contract);
        }
 
        if (wsBinding.getBindingInterfaceContract().getCallbackInterface() == null) {
            invoker = createOperationInvoker(serviceClient, operation, false, operation.isNonBlocking());
        } else {
            // FIXME: SDODataBinding needs to pass in TypeHelper and classLoader as parameters.

            // FIXME: This makes the (BIG) assumption that there is only one callback method
            // Relaxing this assumption, however, does not seem to be trivial, it may depend on knowledge
            // of what actual callback method was invoked by the service at the other end

            RuntimeWire wire = reference.getRuntimeWire(wsBinding);
            Operation callbackOperation = findCallbackOperation(wire);
            Axis2CallbackInvocationHandler invocationHandler = new Axis2CallbackInvocationHandler(wire);
            Axis2ReferenceCallbackTargetInvoker callbackInvoker =
                new Axis2ReferenceCallbackTargetInvoker(callbackOperation, wire, invocationHandler);

            Axis2AsyncBindingInvoker asyncInvoker = 
                (Axis2AsyncBindingInvoker)createOperationInvoker(serviceClient, operation, true, false);
            asyncInvoker.setCallbackTargetInvoker(callbackInvoker);
            invoker = asyncInvoker;
        }

        return invoker;
    }

    private Operation findCallbackOperation(RuntimeWire wire) {
        InterfaceContract contract = wire.getTarget().getInterfaceContract(); // TODO: which end?
        List callbackOperations = contract.getCallbackInterface().getOperations();
        if (callbackOperations.size() != 1) {
            throw new RuntimeException("Can only handle one callback operation");
        }
        Operation callbackOperation = (Operation) callbackOperations.get(0);
        return callbackOperation;
    }

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
    
    protected EndpointReference getPortLocationEPR() {
        String ep = wsBinding.getURI();
        if (ep == null && wsBinding.getPort() != null) {
            List wsdlPortExtensions = wsBinding.getPort().getExtensibilityElements();
            for (final Object extension : wsdlPortExtensions) {
                if (extension instanceof SOAPAddress) {
                    ep = ((SOAPAddress) extension).getLocationURI();
                    break;
                }
            }
        }
        return new EndpointReference(ep);
    }

    protected String getSOAPAction(String operationName) {
        Binding binding = wsBinding.getBinding();
        if (binding != null) {
            for (Object o : binding.getBindingOperations()) {
                BindingOperation bop = (BindingOperation) o;
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
