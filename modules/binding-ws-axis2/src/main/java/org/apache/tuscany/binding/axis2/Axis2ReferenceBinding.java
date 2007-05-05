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
package org.apache.tuscany.binding.axis2;

import java.net.URI;
import java.util.List;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
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
import org.apache.tuscany.binding.ws.WebServiceBinding;
import org.apache.tuscany.binding.ws.xml.WebServiceConstants;
import org.apache.tuscany.interfacedef.InterfaceContract;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.invocation.TargetInvoker;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.spi.extension.ReferenceBindingExtension;

/**
 * Axis2Reference uses Axis2 to invoke a remote web service
 */
public class Axis2ReferenceBinding extends ReferenceBindingExtension {

    private ServiceClient serviceClient;
    private WebServiceBinding wsBinding;

    public Axis2ReferenceBinding(URI name, URI targetUri, WebServiceBinding wsBinding) {
        super(name, targetUri);
        this.wsBinding = wsBinding;
        this.serviceClient = createServiceClient();
        this.bindingServiceContract = wsBinding.getBindingInterfaceContract();
    }

    public void stop() {
        // close all connections that we have initiated, so that the jetty server
        // can be restarted without seeing ConnectExceptions
        HttpClient httpClient = (HttpClient)serviceClient.getServiceContext().getConfigurationContext()
                .getProperty(HTTPConstants.CACHED_HTTP_CLIENT);
        if (httpClient != null) {
            ((MultiThreadedHttpConnectionManager)httpClient.getHttpConnectionManager()).shutdown();
        }
        super.stop();
    }

    public QName getBindingType() {
        return WebServiceConstants.BINDING_WS_QNAME;
    }

    public TargetInvoker createTargetInvoker(String targetName, Operation operation, boolean isCallback) throws TargetInvokerCreationException {
        Axis2TargetInvoker invoker;

        if (wsBinding.getBindingInterfaceContract().getCallbackInterface() == null) {
            invoker = createOperationInvoker(serviceClient, operation, false, operation.isNonBlocking());
        } else {
            // FIXME: SDODataBinding needs to pass in TypeHelper and classLoader as parameters.

            // FIXME: This makes the (BIG) assumption that there is only one callback method
            // Relaxing this assumption, however, does not seem to be trivial, it may depend on knowledge
            // of what actual callback method was invoked by the service at the other end
            Operation callbackOperation = findCallbackOperation();
            Axis2CallbackInvocationHandler invocationHandler = new Axis2CallbackInvocationHandler(wire);
            Axis2ReferenceCallbackTargetInvoker callbackInvoker =
                new Axis2ReferenceCallbackTargetInvoker(callbackOperation, wire, invocationHandler);

            Axis2AsyncTargetInvoker asyncInvoker = 
                (Axis2AsyncTargetInvoker) createOperationInvoker(serviceClient, operation, true, false);
            asyncInvoker.setCallbackTargetInvoker(callbackInvoker);
            invoker = asyncInvoker;
        }
        return invoker;
    }

    private Operation findCallbackOperation() {
        InterfaceContract contract = wire.getTargetContract(); // TODO: which end?
        List callbackOperations = contract.getCallbackInterface().getOperations();
        if (callbackOperations.size() != 1) {
            throw new RuntimeException("Can only handle one callback operation");
        }
        Operation callbackOperation = (Operation) callbackOperations.get(0);
        return callbackOperation;
    }

    /**
     * Create an Axis2 ServiceClient
     */
    protected ServiceClient createServiceClient() {
        try {

            TuscanyAxisConfigurator tuscanyAxisConfigurator = new TuscanyAxisConfigurator();
            ConfigurationContext configurationContext = tuscanyAxisConfigurator.getConfigurationContext();
            QName serviceQName = wsBinding.getServiceName();
            String portName = wsBinding.getPortName();
            Definition wsdlDefinition = wsBinding.getWSDLDefinition().getDefinition();
            AxisService axisService = AxisService.createClientSideAxisService(wsdlDefinition, serviceQName, portName, new Options());

            return new ServiceClient(configurationContext, axisService);

        } catch (AxisFault e) {
            throw new RuntimeException(e); // TODO: better exception
        }
    }

    /**
     * Create and configure an Axis2TargetInvoker for each operations
     */
    private Axis2TargetInvoker createOperationInvoker(ServiceClient serviceClient,
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

        Axis2TargetInvoker invoker;
        if (hasCallback) {
            invoker = new Axis2AsyncTargetInvoker(serviceClient, wsdlOperationQName, options, soapFactory);
        } else if (isOneWay) {
            invoker = new Axis2OneWayTargetInvoker(serviceClient, wsdlOperationQName, options, soapFactory);
        } else {
            invoker = new Axis2TargetInvoker(serviceClient, wsdlOperationQName, options, soapFactory);
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
