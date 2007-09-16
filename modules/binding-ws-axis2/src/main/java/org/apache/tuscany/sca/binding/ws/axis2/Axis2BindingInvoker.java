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
import org.apache.axis2.addressing.EndpointReferenceHelper;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.interfacedef.util.FaultException;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.ReferenceParameters;

/**
 * Axis2BindingInvoker uses an Axis2 OperationClient to invoke a remote web service
 */
public class Axis2BindingInvoker implements Invoker {

    private ServiceClient serviceClient;
    private QName wsdlOperationName;
    private Options options;
    private SOAPFactory soapFactory;

    public static final QName QNAME_WSA_FROM =
        new QName(AddressingConstants.Final.WSA_NAMESPACE, AddressingConstants.WSA_FROM);
    public static final QName QNAME_WSA_TO =
        new QName(AddressingConstants.Final.WSA_NAMESPACE, AddressingConstants.WSA_TO);

    public static final QName CALLBACK_REFERENCE_REFPARM_QN = new QName(Constants.SCA10_TUSCANY_NS, "CallbackReference");
    public static final QName CALLBACK_ID_REFPARM_QN = new QName(Constants.SCA10_TUSCANY_NS, "CallbackID");
    public static final QName CONVERSATION_ID_REFPARM_QN = new QName(Constants.SCA10_TUSCANY_NS, "ConversationID");

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
        } catch (AxisFault e) {
            if (e.getDetail() != null) {
                FaultException f = new FaultException(e.getMessage(), e.getDetail());
                f.setLogical(e.getDetail().getQName());
                msg.setFaultBody(f);
            } else {
                msg.setFaultBody(e);
            }
        } catch (Throwable e) {
            msg.setFaultBody(e);
        }

        return msg;
    }

    protected Object invokeTarget(Message msg) throws AxisFault {
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

        ReferenceParameters parameters = msg.getTo().getReferenceParameters();

        // if target endpoint was not specified when this invoker was created, 
        // use dynamically specified target endpoint passed in on this call
        EndpointReference toEPR = options.getTo();
        if (toEPR == null) {
            org.apache.tuscany.sca.runtime.EndpointReference ep = msg.getTo();
            toEPR = new EndpointReference(ep.getURI());
        }

        // set callback endpoint and callback ID for WS-Addressing header
        if (parameters.getCallbackReference() != null) {
            toEPR.addReferenceParameter(CALLBACK_REFERENCE_REFPARM_QN,
                                        parameters.getCallbackReference().getBinding().getURI());
        }
        if (parameters.getCallbackID() != null) {
            //FIXME: serialize callback ID to XML in case it is not a string
            toEPR.addReferenceParameter(CALLBACK_ID_REFPARM_QN, parameters.getCallbackID().toString());
        }

        // set conversation ID for WS-Addressing header
        //FIXME: get conversation ID from the message's callable reference
        Object conversationId = parameters.getConversationID();
        if (conversationId != null) {
            //FIXME: serialize conversation ID to XML in case it is not a string
            toEPR.addReferenceParameter(CONVERSATION_ID_REFPARM_QN, conversationId.toString());
        }

        // add WS-Addressing header
        //FIXME: is there any way to use the Axis2 addressing support for this?
        if (toEPR != null) {
            SOAPEnvelope sev = requestMC.getEnvelope();
            SOAPHeader sh = sev.getHeader();
            OMElement epr =
                EndpointReferenceHelper.toOM(sev.getOMFactory(),
                                             toEPR,
                                             QNAME_WSA_TO,
                                             AddressingConstants.Final.WSA_NAMESPACE);
            sh.addChild(epr);
            requestMC.setTo(toEPR);
        }

        operationClient.addMessageContext(requestMC);

        return operationClient;
    }
}
