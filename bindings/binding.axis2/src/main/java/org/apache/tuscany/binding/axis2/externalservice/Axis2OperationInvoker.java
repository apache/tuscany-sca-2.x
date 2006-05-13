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
package org.apache.tuscany.binding.axis2.externalservice;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.client.Options;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.tuscany.binding.axis2.util.SDODataBinding;

/**
 * Axis2OperationInvoker uses an Axis2 OperationClient to invoke a remote web service
 */
public class Axis2OperationInvoker {

    public QName wsdlOperationName;

    public Options options;

    public SDODataBinding dataBinding;

    private SOAPFactory soapFactory;

    public Axis2OperationInvoker(QName wsdlOperationName, Options options, SDODataBinding dataBinding, SOAPFactory soapFactory) {
        this.wsdlOperationName = wsdlOperationName;
        this.options = options;
        this.dataBinding = dataBinding;
        this.soapFactory = soapFactory;
    }

    /**
     * Invoke a WS operation
     * 
     * @param operationClient
     * @param args
     * @return
     * @throws AxisFault
     */
    protected Object invokeOperation(OperationClient operationClient, Object[] args) throws AxisFault {

        operationClient.setOptions(options);

        SOAPEnvelope env = soapFactory.getDefaultEnvelope();

        if (args != null && args.length > 0) {
            OMElement requestOM = dataBinding.toOMElement(args);
            env.getBody().addChild(requestOM);
        }

        MessageContext requestMC = new MessageContext();
        requestMC.setEnvelope(env);

        operationClient.addMessageContext(requestMC);

        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        ClassLoader scl = this.getClass().getClassLoader();
        try {
            if (tccl != scl) {
                Thread.currentThread().setContextClassLoader(scl);
            }

            operationClient.execute(true);

        } finally {
            if (tccl != scl) {
                Thread.currentThread().setContextClassLoader(tccl);
            }
        }

        MessageContext responseMC = operationClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
        OMElement responseOM = responseMC.getEnvelope().getBody().getFirstElement();

        Object[] os = null;
        if (responseOM != null) {
            os = dataBinding.fromOMElement(responseOM);
        }

        Object response;
        if (os == null || os.length < 1) {
            response = null;
        } else {
            response = os[0];
        }

        return response;
    }

    /**
     * Get the WSDL operation name that this can invoke
     */
    public QName getWSDLOperationName() {
        return wsdlOperationName;
    }

}
