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
import org.apache.tuscany.binding.ws.WebServiceBinding;
import org.apache.tuscany.binding.ws.xml.WebServiceConstants;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.extension.ReferenceBindingExtension;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * Axis2Reference uses Axis2 to invoke a remote web service
 */
public class Axis2ReferenceBinding extends ReferenceBindingExtension {

    private WorkContext workContext;
    private ServiceClient serviceClient;
    private WebServiceBinding wsBinding;

    public Axis2ReferenceBinding(URI name, URI targetUri, WebServiceBinding wsBinding) {
        super(name, targetUri);
        this.wsBinding = wsBinding;
        this.serviceClient = createServiceClient();
        this.bindingServiceContract = wsBinding.getBindingInterfaceContract();
    }

    public QName getBindingType() {
        return WebServiceConstants.BINDING_WS_QNAME;
    }

    public TargetInvoker createTargetInvoker(String targetName, Operation operation, boolean isCallback) throws TargetInvokerCreationException {
        Axis2TargetInvoker invoker;
//        boolean operationHasCallback = operation.getInterface().contract.getCallbackInterface() != null;
// TODO: this isn't right, need to get the InterfaceContract         
        if (isCallback) {
            // FIXME: SDODataBinding needs to pass in TypeHelper and classLoader
            // as parameters.
            Axis2AsyncTargetInvoker asyncInvoker =
                (Axis2AsyncTargetInvoker) createOperationInvoker(serviceClient,
                    operation,
                    true,
                    false);
            // FIXME: This makes the (BIG) assumption that there is only one
            // callback method
            // Relaxing this assumption, however, does not seem to be trivial,
            // it may depend on knowledge
            // of what actual callback method was invoked by the service at the
            // other end
//            Operation callbackOperation = findCallbackOperation();
            Operation callbackOperation = null;
            Axis2CallbackInvocationHandler invocationHandler =
                new Axis2CallbackInvocationHandler(wire);
            Axis2ReferenceCallbackTargetInvoker callbackInvoker =
                new Axis2ReferenceCallbackTargetInvoker(callbackOperation, wire, invocationHandler);
            asyncInvoker.setCallbackTargetInvoker(callbackInvoker);

            invoker = asyncInvoker;
        } else {
            invoker = createOperationInvoker(serviceClient, operation, false, operation.isNonBlocking());
        }
        return invoker;
    }

//    private Operation findCallbackOperation() {
//        ServiceContract contract = wire.getTargetContract(); // TODO: which end?
//        Operation callbackOperation = null;
//        Collection callbackOperations = contract.getCallbackOperations().values();
//        if (callbackOperations.size() != 1) {
//            throw new Axis2BindingRunTimeException("Can only handle one callback operation");
//        } else {
//            callbackOperation = (Operation) callbackOperations.iterator().next();
//        }
//        return callbackOperation;
//    }

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

        options.setTimeOutInMilliSeconds(5 * 60 * 1000);

        SOAPFactory soapFactory = OMAbstractFactory.getSOAP11Factory();
        QName wsdlOperationQName = new QName(wsBinding.getNamespace(), operationName);

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
