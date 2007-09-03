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
package org.apache.tuscany.sca.binding.ws.axis2;

import java.lang.reflect.InvocationTargetException;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.AddressingConstants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.osoa.sca.Constants;

/**
 * Axis2BindingInvoker uses an Axis2 OperationClient to invoke a remote web service
 */
public class Axis2BindingInvoker implements Invoker {

    private ServiceClient serviceClient;
    private QName wsdlOperationName;
    private Options options;
    private SOAPFactory soapFactory;

    public static final QName CALLBACK_ID_REFPARM_QN = new QName(Constants.SCA_NS, "CallbackID");
    public static final QName CONVERSATION_ID_REFPARM_QN = new QName(Constants.SCA_NS, "ConversationID");

    public Axis2BindingInvoker(ServiceClient serviceClient,
                               QName wsdlOperationName,
                               Options options,
                               SOAPFactory soapFactory) {
        this.serviceClient = serviceClient;
        this.wsdlOperationName = wsdlOperationName;
        this.options = options;
        this.soapFactory = soapFactory;
    }

    public Message invoke(Message msg) {
        try {
            Object resp = invokeTarget(msg);
            msg.setBody(resp);
        } catch (InvocationTargetException e) {
            msg.setFaultBody(e.getCause());
        } catch (Throwable e) {
            msg.setFaultBody(e);
        }

        return msg;
    }

    protected Object invokeTarget(Message msg) throws InvocationTargetException {
        try {
            OperationClient operationClient = createOperationClient(msg);

            // ensure connections are tracked so that they can be closed by the reference binding
            MessageContext requestMC = operationClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_OUT_VALUE);
            requestMC.getOptions().setProperty(HTTPConstants.REUSE_HTTP_CLIENT, Boolean.TRUE);
            requestMC.getOptions().setTimeOutInMilliSeconds(120000L);

            operationClient.execute(true);

            MessageContext responseMC = operationClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);

            OMElement response = responseMC.getEnvelope().getBody().getFirstElement();
            
            // FIXME: [rfeng] We have to pay performance penality to build the complete OM as the operationClient.complete() will
            // release the underlying HTTP connection. 
            // Force the response to be populated, see https://issues.apache.org/jira/browse/TUSCANY-1541
            response.build();
            
            operationClient.complete(requestMC);

            return response;

        } catch (AxisFault e) {
            throw new InvocationTargetException(e);
        }
    }

    @SuppressWarnings("deprecation")
    protected OperationClient createOperationClient(Message msg) throws AxisFault {
        SOAPEnvelope env = soapFactory.getDefaultEnvelope();
        Object[] args = (Object[])msg.getBody();
        if (args != null && args.length > 0) {
            SOAPBody body = env.getBody();
            for (Object bc : args) {
                if (bc instanceof OMElement) {
                    body.addChild((OMElement)bc);
                } else {
                    throw new IllegalArgumentException(
                                                       "Can't handle mixed payloads betweem OMElements and other types.");
                }
            }
        }
        MessageContext requestMC = new MessageContext();
        requestMC.setEnvelope(env);

        // Axis2 operationClients can not be shared so create a new one for each request
        OperationClient operationClient = serviceClient.createClient(wsdlOperationName);
        operationClient.setOptions(options);

        // set callback endpoint and callback ID for WS-Addressing header
        EndpointReference fromEPR = null;
        if (msg.getFrom() != null) {
            fromEPR = new EndpointReference(msg.getFrom().getBinding().getURI());
            //FIXME: serialize callback ID to XML in case it is not a string
            fromEPR.addReferenceParameter(CALLBACK_ID_REFPARM_QN,
                                          (String)msg.getCallableReference().getCallbackID());
        }

        // set conversation ID for WS-Addressing header
        //FIXME: get conversation ID from the message's callable reference
        Object conversationId = msg.getConversationID();
        if (conversationId != null) {
            if (fromEPR == null) {
                fromEPR = new EndpointReference(AddressingConstants.Final.WSA_ANONYMOUS_URL);
            }
            //FIXME: serialize conversation ID to XML in case it is not a string
            fromEPR.addReferenceParameter(CONVERSATION_ID_REFPARM_QN, conversationId.toString());
        }

        // add WS-Addressing header
        //FIXME: is there any way to use the Axis2 addressing support for this?
        if (fromEPR != null) {
            SOAPEnvelope sev = requestMC.getEnvelope();
            SOAPHeader sh = sev.getHeader();
            OMElement el =
                fromEPR.toOM(AddressingConstants.Final.WSA_NAMESPACE,
                             AddressingConstants.WSA_FROM,
                             AddressingConstants.WSA_DEFAULT_PREFIX);
            sh.addChild(el);
            requestMC.setFrom(fromEPR);
        }

        // if target endpoint was not specified when this invoker was created, 
        // use dynamically specified target endpoint passed in on this call
        if (options.getTo() == null) {
            org.apache.tuscany.sca.runtime.EndpointReference ep = msg.getTo();
            if (ep != null) {
                requestMC.setTo(new EndpointReference(ep.getURI()));
            } else {
                throw new RuntimeException("Unable to determine destination endpoint");
            }
        }

        operationClient.addMessageContext(requestMC);

        return operationClient;
    }

}
