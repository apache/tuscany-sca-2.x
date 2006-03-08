/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.binding.axis2.handler;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.parsers.FactoryConfigurationError;

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.context.MessageContextConstants;
import org.apache.axis2.deployment.AxisConfigBuilder;
import org.apache.axis2.deployment.DeploymentEngine;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.engine.AxisConfigurator;
import org.apache.axis2.om.OMElement;
import org.apache.tuscany.binding.axis2.assembly.WebServiceBinding;
import org.apache.tuscany.binding.axis2.util.AxiomHelper;
import org.apache.tuscany.binding.axis2.util.TuscanyAxisConfigurator;
import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.common.resource.impl.ResourceLoaderImpl;
import org.apache.tuscany.model.assembly.ExternalService;
import org.osoa.sca.ServiceRuntimeException;

import commonj.sdo.helper.TypeHelper;

/**
 * An ExternalWebServiceClient using Axis2
 */
public class ExternalWebServiceClient {

    private ExternalService externalService;

    private TypeHelper typeHelper;

    private WebServicePortMetaData wsPortMetaData;

    /**
     * Constructs a new ExternalWebServiceClient.
     * 
     * @param externalService
     * @param wsBinding
     */
    public ExternalWebServiceClient(ExternalService externalService, WebServiceBinding wsBinding, TypeHelper typeHelper) {
        this.externalService = externalService;
        this.typeHelper = typeHelper;
        this.wsPortMetaData = new WebServicePortMetaData(wsBinding.getWSDLDefinition(), wsBinding.getWSDLPort(), wsBinding.getURI(), false);
    }

    /**
     * Invoke an operation on the external Web service.
     * 
     * @param method
     * @param args
     * @return
     */
    public Object invoke(Method method, Object[] args) {

        ServiceClient serviceClient;
        ClassLoader ccl = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(ExternalWebServiceClient.class.getClassLoader());

            serviceClient = createServiceClient(method);

        } finally {
            Thread.currentThread().setContextClassLoader(ccl);
        }

        String typeName = method.getName();
        String typeNS = wsPortMetaData.getPortType().getQName().getNamespaceURI();

        OMElement requestOM = AxiomHelper.toOMElement(typeHelper, args, new QName(typeNS, typeName));

        OMElement responseOM;
        ccl = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(ExternalWebServiceClient.class.getClassLoader());

            responseOM = serviceClient.sendReceive(requestOM);

        } catch (AxisFault e) {
            throw new ServiceRuntimeException(e);
        } finally {
            Thread.currentThread().setContextClassLoader(ccl);
        }

        Object[] os = AxiomHelper.toObjects(typeHelper, responseOM);
        Object response;
        if (os == null || os.length < 1) {
            response = null;
        } else {
            response = os[0];
        }

        return response;
    }

    private ServiceClient createServiceClient(Method method) throws ServiceRuntimeException {

        /*
         * TODO: Simlistic impl for now, needs to be redone. Should cache our ConfigurationContext and pass in on ServiceClient constructor, should
         * probably use WSDL configured Axis2 OperationClient
         */

        Options options = new Options();

        options.setProperty(Constants.Configuration.DISABLE_ADDRESSING_FOR_OUT_MESSAGES, Boolean.TRUE);

        WebServiceOperationMetaData operationMetaData = wsPortMetaData.getOperationMetaData(method.getName());

        EndpointReference targetEPR = new EndpointReference(wsPortMetaData.getEndpoint());
        options.setTo(targetEPR);

        String soapAction = operationMetaData.getSOAPAction();
        if (soapAction != null) {
            options.setSoapAction(soapAction);
        }

        // If use is encoded assume its an old style service and wont understand chunking
        if ("encoded".equals(operationMetaData.getUse())) {
            options.setProperty(MessageContextConstants.CHUNKED, Boolean.FALSE);
        }

        try {
            ServiceClient serviceClient;
            // serviceClient = new ServiceClient();
            TuscanyAxisConfigurator tuscanyAxisConfigurator = new TuscanyAxisConfigurator(null, null);

            serviceClient = new ServiceClient(tuscanyAxisConfigurator.getConfigurationContext(), null);
            serviceClient.setOptions(options);

            return serviceClient;

        } catch (org.apache.axis2.AxisFault e) {
            throw new ServiceRuntimeException(e);
        } catch (IOException e) {
            throw new ServiceRuntimeException(e);
        }

    }

}
