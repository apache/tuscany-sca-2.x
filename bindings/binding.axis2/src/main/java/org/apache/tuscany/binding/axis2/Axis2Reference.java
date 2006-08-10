/**
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
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


import java.lang.reflect.Method;
import java.util.List;
import javax.wsdl.Definition;
import javax.xml.namespace.QName;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.extension.ReferenceExtension;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireService;

import commonj.sdo.helper.TypeHelper;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContextConstants;
import org.apache.axis2.description.AxisService;
import org.apache.tuscany.binding.axis2.util.SDODataBinding;
import org.apache.tuscany.binding.axis2.util.TuscanyAxisConfigurator;
import org.apache.tuscany.binding.axis2.util.WebServiceOperationMetaData;
import org.apache.tuscany.binding.axis2.util.WebServicePortMetaData;


/**
 * Axis2Reference uses Axis2 to invoke a remote web service
 */
public class Axis2Reference<T> extends ReferenceExtension<T> {

    private WebServicePortMetaData wsPortMetaData;
    private ServiceClient serviceClient;
    
    public Axis2Reference(String theName,
                          CompositeComponent<?> parent,
                          WireService wireService,
                          WebServiceBinding wsBinding,
                          ServiceContract contract) {
        super(theName, (Class<T>)contract.getInterfaceClass(), parent, wireService);
        try {
            Definition wsdlDefinition = wsBinding.getWSDLDefinition();
            wsPortMetaData =
                new WebServicePortMetaData(wsdlDefinition, wsBinding.getWSDLPort(), wsBinding.getURI(), false);
            serviceClient = createServiceClient(wsdlDefinition, wsPortMetaData);
        } catch (AxisFault e) {
            //TODO
        }
    }

    public TargetInvoker createTargetInvoker(Method operation) {
        Axis2TargetInvoker invoker = null;
        try {
            //FIXME: SDODataBinding needs to pass in TypeHelper and classLoader as parameters.
            //TypeHelper typeHelper = wsBinding.getTypeHelper();
            //ClassLoader cl = wsBinding.getResourceLoader().getClassLoader();
            TypeHelper typeHelper = null;
            ClassLoader cl = null;
            invoker = createOperationInvokers(serviceClient, operation, typeHelper, cl, wsPortMetaData);
        } catch (AxisFault e) {
            //TODO
        }
        return invoker;
    }

    /**
     * Create an Axis2 ServiceClient
     */
    private ServiceClient createServiceClient(Definition wsdlDefinition,
                                              WebServicePortMetaData wsPortMetaData) throws AxisFault {

        TuscanyAxisConfigurator tuscanyAxisConfigurator = new TuscanyAxisConfigurator(null);
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
    private Axis2TargetInvoker createOperationInvokers(ServiceClient serviceClient,
                                                       Method m,
                                                       TypeHelper typeHelper,
                                                       ClassLoader cl,
                                                       WebServicePortMetaData wsPortMetaData)
        throws AxisFault {
        SOAPFactory soapFactory = OMAbstractFactory.getSOAP11Factory();
        String portTypeNS = wsPortMetaData.getPortTypeName().getNamespaceURI();

        String methodName = m.getName();

        WebServiceOperationMetaData operationMetaData = wsPortMetaData.getOperationMetaData(methodName);
        boolean isWrapped = operationMetaData.isDocLitWrapped();
        List<?> sig = operationMetaData.getOperationSignature();

        SDODataBinding dataBinding =
            new SDODataBinding(cl, typeHelper, isWrapped, sig.size() > 0 ? (QName) sig.get(0) : null);

        Options options = new Options();
        options.setTo(new EndpointReference(wsPortMetaData.getEndpoint()));
        options.setProperty(MessageContextConstants.CHUNKED, Boolean.FALSE);

        String wsdlOperationName = operationMetaData.getBindingOperation().getOperation().getName();

        String soapAction = wsPortMetaData.getOperationMetaData(wsdlOperationName).getSOAPAction();
        if (soapAction != null && soapAction.length() > 1) {
            options.setAction(soapAction);
        }

        QName wsdlOperationQName = new QName(portTypeNS, wsdlOperationName);
        // Axis2 operationClients can not be shared so create a new one for each request
        OperationClient operationClient = serviceClient.createClient(wsdlOperationQName);

        return new Axis2TargetInvoker(wsdlOperationQName, options, dataBinding, soapFactory, operationClient);
    }

}
