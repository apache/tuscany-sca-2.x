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
import org.apache.tuscany.binding.ws.WebServiceBinding;
import org.apache.tuscany.binding.ws.xml.WebServiceConstants;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.extension.ReferenceBindingExtension;
import org.apache.tuscany.spi.wire.TargetInvoker;

public class Axis2WSReference extends ReferenceBindingExtension {

    private WorkContext workContext;
    private WebServicePortMetaData wsPortMetaData;
    private WebServiceBinding wsBinding;
    private ServiceClient serviceClient;

    public Axis2WSReference(URI name, URI targetUri, WebServiceBinding wsBinding) {
        super(name, targetUri);
        this.wsPortMetaData = new WebServicePortMetaData(wsBinding.getWSDLDefinition().getDefinition(), wsBinding.getPort(), wsBinding.getURI(), false);
        this.serviceClient = createServiceClient(wsBinding.getWSDLDefinition().getDefinition(), wsPortMetaData);
    }

    public QName getBindingType() {
        return WebServiceConstants.BINDING_WS_QNAME;
    }

    public TargetInvoker createTargetInvoker(String targetName, Operation operation, boolean isCallback) throws TargetInvokerCreationException {
            boolean isOneWay = operation.isNonBlocking();
            Axis2TargetInvoker invoker = createOperationInvoker(serviceClient, operation, wsPortMetaData, false, isOneWay);
            return invoker;
    }

    /**
     * Create an Axis2 ServiceClient
     */
    protected ServiceClient createServiceClient(Definition wsdlDefinition, WebServicePortMetaData wsPortMetaData) {
        try {

            TuscanyAxisConfigurator tuscanyAxisConfigurator = new TuscanyAxisConfigurator();
            ConfigurationContext configurationContext = tuscanyAxisConfigurator.getConfigurationContext();
            QName serviceQName = wsPortMetaData.getServiceName();
            String portName = wsPortMetaData.getPortName().getLocalPart();
            AxisService axisService = AxisService.createClientSideAxisService(wsdlDefinition, serviceQName, portName, new Options());

            return new ServiceClient(configurationContext, axisService);

        } catch (AxisFault e) {
            throw new RuntimeException(e); // TODO: better exception
        }
    }

    /**
     * Create and configure an Axis2TargetInvoker for each operations
     */
    protected Axis2TargetInvoker createOperationInvoker(ServiceClient serviceClient,
                                                      Operation operation,
                                                      WebServicePortMetaData wsPortMetaData,
                                                      boolean hasCallback,
                                                      boolean isOneWay) {

        SOAPFactory soapFactory = OMAbstractFactory.getSOAP11Factory();
        String portTypeNS = wsPortMetaData.getPortTypeName().getNamespaceURI();

        String operationName = operation.getName();

        WebServiceOperationMetaData operationMetaData = wsPortMetaData.getOperationMetaData(operationName);

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
            invoker = new Axis2AsyncTargetInvoker(serviceClient, wsdlOperationQName, options, soapFactory, workContext);
        } else if (isOneWay) {
            invoker = new Axis2OneWayTargetInvoker(serviceClient, wsdlOperationQName, options, soapFactory, workContext);
        } else {
            invoker = new Axis2TargetInvoker(serviceClient, wsdlOperationQName, options, soapFactory, workContext);
        }

        return invoker;
    }
}
