/**
 *
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
package org.apache.tuscany.binding.axis2.handler;

import javax.xml.namespace.QName;

import commonj.sdo.helper.TypeHelper;

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContextConstants;
import org.apache.axis2.description.AxisService;
import org.apache.tuscany.binding.axis2.util.AxiomHelper;
import org.apache.ws.commons.om.OMElement;
import org.osoa.sca.ServiceRuntimeException;


/**
 * An ExternalWebServiceClient using Axis2
 */
public class ExternalWebServiceClient {

    private ConfigurationContext configurationContext;

    private AxisService axisService;

    private TypeHelper typeHelper;

    private WebServicePortMetaData wsPortMetaData;

    public ExternalWebServiceClient(ConfigurationContext configurationContext,
                                    AxisService axisService,
                                    WebServicePortMetaData wsPortMetaData,
                                    TypeHelper typeHelper) {
        this.configurationContext = configurationContext;
        this.axisService = axisService;
        this.wsPortMetaData = wsPortMetaData;
        this.typeHelper = typeHelper;
    }

    /**
     * Invoke an operation on the external Web service.
     * 
     * @param operationName
     *            the name of the WS operation to invoke
     * @param args
     *            the Java object arguments to the WS operation
     * @return the response from the WS as a Java object
     */
    public Object invoke(String operationName, Object[] args) {

        String serviceNamespace = wsPortMetaData.getServiceName().getNamespaceURI();
        QName operationQName = new QName(serviceNamespace, operationName);

        OMElement requestOM = AxiomHelper.toOMElement(typeHelper, args, operationQName);

        OMElement responseOM;
        ClassLoader ccl = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());

            ServiceClient serviceClient = new ServiceClient(configurationContext, axisService);
            setServiceOptions(serviceClient, operationName);
            responseOM = serviceClient.sendReceive(operationQName, requestOM);

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

    /**
     * Set all the Axis2 options needed for the wire TODO: review whats needed 
     * here once createClientSideAxisService is added to the Axis2 build
     */
    private void setServiceOptions(ServiceClient serviceClient, String operationName) {
        Options options = new Options();

        WebServiceOperationMetaData operationMetaData = wsPortMetaData.getOperationMetaData(operationName);

        options.setProperty(Constants.Configuration.DISABLE_ADDRESSING_FOR_OUT_MESSAGES, Boolean.TRUE);

        EndpointReference targetEPR = new EndpointReference(wsPortMetaData.getEndpoint());
        options.setTo(targetEPR);

        String soapAction = operationMetaData.getSOAPAction();
        if (soapAction != null) {
            options.setAction(soapAction);
        }

        // If use is encoded assume its an old style service and wont understand chunking
        if ("encoded".equals(operationMetaData.getUse())) {
            options.setProperty(MessageContextConstants.CHUNKED, Boolean.FALSE);
        }

        serviceClient.setOptions(options);
    }

}
