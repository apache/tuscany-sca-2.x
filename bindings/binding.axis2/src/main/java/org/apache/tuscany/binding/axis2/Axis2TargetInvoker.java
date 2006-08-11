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


import java.lang.reflect.InvocationTargetException;
import javax.xml.namespace.QName;

import org.apache.tuscany.spi.wire.InvocationRuntimeException;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.TargetInvoker;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.client.Options;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.tuscany.binding.axis2.util.SDODataBinding;

/**
 * Axis2TargetInvoker uses an Axis2 OperationClient to invoke a remote web service
 */
public class Axis2TargetInvoker implements TargetInvoker {

    //private QName wsdlOperationName;
    private Options options;
    private SDODataBinding dataBinding;
    private SOAPFactory soapFactory;
    private OperationClient operationClient;
    

    public Axis2TargetInvoker(QName wsdlOperationName, Options options, SDODataBinding dataBinding,
                              SOAPFactory soapFactory,
                              OperationClient operationClient) {
        //this.wsdlOperationName = wsdlOperationName;
        this.options = options;
        this.dataBinding = dataBinding;
        this.soapFactory = soapFactory;
        this.operationClient = operationClient;
    }

    /**
     * Invoke a WS operation
     *
     * @param payload
     * @return
     * @throws InvocationTargetException
     */
    public Object invokeTarget(final Object payload) throws InvocationTargetException {
        try {
            operationClient.setOptions(options);

            SOAPEnvelope env = soapFactory.getDefaultEnvelope();

            if (payload != null && payload.getClass().isArray() && ((Object[]) payload).length > 0) {
//                OMElement requestOM = dataBinding.toOMElement((Object[]) payload);
//                env.getBody().addChild(requestOM);
//TODO HACK 
                OMFactory fac = env.getOMFactory();
                OMElement opE = fac.createOMElement("getGreetings" ,"http://helloworld", "helloworld");
                //            <helloworld:name>World</helloworld:name>

                OMElement parmE = fac.createOMElement("name" ,"http://helloworld", "helloworld");
                opE.addChild(parmE);
                parmE.addChild(fac.createOMText(((Object[])payload)[0] + ""));
                env.getBody().addChild((opE));
//TODO HACK     
                
               
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

        } catch (AxisFault e) {
            throw new InvocationTargetException(e);
        }

    }

    public Message invoke(Message msg) throws InvocationRuntimeException {
        try {
            Object resp = invokeTarget(msg.getBody());
            msg.setBody(resp);
        } catch (Throwable e) {
            msg.setBody(e);
        }
        return msg;
    }

    public Axis2TargetInvoker clone() throws CloneNotSupportedException {
        try {
            return (Axis2TargetInvoker) super.clone();
        } catch (CloneNotSupportedException e) {
            // will not happen
            return null;
        }
    }

    public boolean isCacheable() {
        return true;
    }

    public void setCacheable(boolean cacheable) {

    }

    public boolean isOptimizable() {
        return false;
    }

}
