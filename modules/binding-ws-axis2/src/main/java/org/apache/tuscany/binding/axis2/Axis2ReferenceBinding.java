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

import static org.osoa.sca.Constants.SCA_NS;

import java.net.URI;
import java.util.Collection;

import javax.wsdl.Definition;
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
import org.apache.tuscany.binding.axis2.util.TuscanyAxisConfigurator;
import org.apache.tuscany.binding.axis2.util.WebServiceOperationMetaData;
import org.apache.tuscany.binding.axis2.util.WebServicePortMetaData;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.extension.ReferenceBindingExtension;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.model.physical.PhysicalOperationDefinition;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * Axis2Reference uses Axis2 to invoke a remote web service
 */
public class Axis2ReferenceBinding<T> extends ReferenceBindingExtension {
    private static final QName BINDING_WS = new QName(SCA_NS, "binding.ws");

    private WebServicePortMetaData wsPortMetaData;
    private ServiceClient serviceClient;
    private WorkContext workContext;

    @SuppressWarnings("unchecked")
    public Axis2ReferenceBinding(URI uri,
                                 WebServiceBindingDefinition wsBinding,
                                 ServiceContract contract,
                                 ServiceContract<?> bindingServiceContract,
                                 WorkContext workContext) {
        super(uri, uri); // TODO: what should these be
        this.bindingServiceContract = bindingServiceContract;
        this.workContext = workContext;
        try {
            Definition wsdlDefinition = wsBinding.getWSDLDefinition();
            wsPortMetaData =
                new WebServicePortMetaData(wsdlDefinition, wsBinding.getWSDLPort(), wsBinding.getURI(), false);
            serviceClient = createServiceClient(wsdlDefinition, wsPortMetaData);
        } catch (AxisFault e) {
            throw new Axis2BindingRunTimeException(e);
        }
    }

    public QName getBindingType() {
        return BINDING_WS;
    }

    public TargetInvoker createTargetInvoker(ServiceContract contract, Operation operation) {
        Axis2TargetInvoker invoker;
        try {
            boolean operationHasCallback = contract.getCallbackName() != null;
            if (operationHasCallback) {
                // FIXME: SDODataBinding needs to pass in TypeHelper and classLoader
                // as parameters.
                Axis2AsyncTargetInvoker asyncInvoker =
                    (Axis2AsyncTargetInvoker) createOperationInvoker(serviceClient,
                        operation,
                        wsPortMetaData,
                        true,
                        false);
                // FIXME: This makes the (BIG) assumption that there is only one
                // callback method
                // Relaxing this assumption, however, does not seem to be trivial,
                // it may depend on knowledge
                // of what actual callback method was invoked by the service at the
                // other end
                Operation callbackOperation = findCallbackOperation();
                Axis2CallbackInvocationHandler invocationHandler =
                    new Axis2CallbackInvocationHandler(wire);
                Axis2ReferenceCallbackTargetInvoker callbackInvoker =
                    new Axis2ReferenceCallbackTargetInvoker(callbackOperation, wire, invocationHandler);
                asyncInvoker.setCallbackTargetInvoker(callbackInvoker);

                invoker = asyncInvoker;
            } else {
                boolean isOneWay = operation.isNonBlocking();
                invoker = createOperationInvoker(serviceClient, operation, wsPortMetaData, false, isOneWay);
            }
        } catch (AxisFault e) {
            throw new Axis2BindingRunTimeException(e);
        }
        return invoker;
    }

    private Operation findCallbackOperation() {
        ServiceContract contract = wire.getTargetContract(); // TODO: which end?
        Operation callbackOperation = null;
        Collection callbackOperations = contract.getCallbackOperations().values();
        if (callbackOperations.size() != 1) {
            throw new Axis2BindingRunTimeException("Can only handle one callback operation");
        } else {
            callbackOperation = (Operation) callbackOperations.iterator().next();
        }
        return callbackOperation;
    }

    /**
     * Create an Axis2 ServiceClient
     */
    private ServiceClient createServiceClient(Definition wsdlDefinition, WebServicePortMetaData wsPortMetaData)
        throws AxisFault {

        TuscanyAxisConfigurator tuscanyAxisConfigurator = new TuscanyAxisConfigurator();
        ConfigurationContext configurationContext = tuscanyAxisConfigurator.getConfigurationContext();
        QName serviceQName = wsPortMetaData.getServiceName();
        String portName = wsPortMetaData.getPortName().getLocalPart();
        AxisService axisService =
            AxisService.createClientSideAxisService(wsdlDefinition, serviceQName, portName, new Options());
        return new ServiceClient(configurationContext, axisService);
    }

    /**
     * Create and configure an Axis2TargetInvoker for each operations
     */
    private Axis2TargetInvoker createOperationInvoker(ServiceClient serviceClient,
                                                      Operation m,
                                                      WebServicePortMetaData wsPortMetaData,
                                                      boolean hasCallback,
                                                      boolean isOneWay) throws AxisFault {
        SOAPFactory soapFactory = OMAbstractFactory.getSOAP11Factory();
        String portTypeNS = wsPortMetaData.getPortTypeName().getNamespaceURI();

        String methodName = m.getName();

        WebServiceOperationMetaData operationMetaData = wsPortMetaData.getOperationMetaData(methodName);

        Options options = new Options();
        options.setTo(new EndpointReference(wsPortMetaData.getEndpoint()));
        options.setProperty(HTTPConstants.CHUNKED, Boolean.FALSE);

        String wsdlOperationName = operationMetaData.getBindingOperation().getOperation().getName();

        String soapAction = wsPortMetaData.getOperationMetaData(wsdlOperationName).getSOAPAction();
        if (soapAction != null && soapAction.length() > 1) {
            options.setAction(soapAction);
        }

        options.setTimeOutInMilliSeconds(5 * 60 * 1000);

        QName wsdlOperationQName = new QName(portTypeNS, wsdlOperationName);

        Axis2TargetInvoker invoker;
        if (hasCallback) {
            invoker =
                new Axis2AsyncTargetInvoker(serviceClient, wsdlOperationQName, options, soapFactory, workContext);
        } else if (isOneWay) {
            invoker =
                new Axis2OneWayTargetInvoker(serviceClient, wsdlOperationQName, options, soapFactory, workContext);
        } else {
            invoker = new Axis2TargetInvoker(serviceClient, wsdlOperationQName, options, soapFactory, workContext);
        }

        return invoker;
    }

    public TargetInvoker createTargetInvoker(String targetName, Operation operation) throws TargetInvokerCreationException {
        // TODO Auto-generated method stub
        return null;
    }

    public TargetInvoker createTargetInvoker(String targetName, PhysicalOperationDefinition operation) throws TargetInvokerCreationException {
        // TODO Auto-generated method stub
        return null;
    }

}
