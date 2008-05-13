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

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.List;

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
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.interfacedef.util.FaultException;
import org.apache.tuscany.sca.invocation.DataExchangeSemantics;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.policy.util.PolicyHandler;
import org.apache.tuscany.sca.runtime.ReferenceParameters;

/**
 * Axis2BindingInvoker uses an Axis2 OperationClient to invoke a remote web service
 *
 * @version $Rev$ $Date$
 */
public class Axis2BindingInvoker implements Invoker, DataExchangeSemantics {

    private Axis2ServiceClient serviceClient;
    private QName wsdlOperationName;
    private Options options;
    private SOAPFactory soapFactory;

    public static final QName QNAME_WSA_FROM =
        new QName(AddressingConstants.Final.WSA_NAMESPACE, AddressingConstants.WSA_FROM,
                  AddressingConstants.WSA_DEFAULT_PREFIX);

    public static final String TUSCANY_PREFIX = "tuscany";
    public static final QName CALLBACK_ID_REFPARM_QN =
        new QName(Constants.SCA10_TUSCANY_NS, "CallbackID", TUSCANY_PREFIX);
    public static final QName CONVERSATION_ID_REFPARM_QN =
        new QName(Constants.SCA10_TUSCANY_NS, "ConversationID", TUSCANY_PREFIX);
    
    private List<PolicyHandler> policyHandlerList = null;

    public Axis2BindingInvoker(Axis2ServiceClient serviceClient,
                               QName wsdlOperationName,
                               Options options,
                               SOAPFactory soapFactory,
                               List<PolicyHandler> policyHandlerList) {
        this.serviceClient = serviceClient;
        this.wsdlOperationName = wsdlOperationName;
        this.options = options;
        this.soapFactory = soapFactory;
        this.policyHandlerList = policyHandlerList;
    }

    private final static QName EXCEPTION = new QName("", "Exception");
    
    public Message invoke(Message msg) {
        try {
            for ( PolicyHandler policyHandler : policyHandlerList ) {
                policyHandler.beforeInvoke(msg);
            }
            
            Object resp = invokeTarget(msg);
            
            for ( PolicyHandler policyHandler : policyHandlerList ) {
                policyHandler.afterInvoke(msg);
            }
            msg.setBody(resp);
        } catch (AxisFault e) {
            if (e.getDetail() != null ) {
                FaultException f = new FaultException(e.getMessage(), e.getDetail(), e);
                f.setFaultName(e.getDetail().getQName());
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
        final OperationClient operationClient = createOperationClient(msg);

        // ensure connections are tracked so that they can be closed by the reference binding
        MessageContext requestMC = operationClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_OUT_VALUE);
        requestMC.getOptions().setProperty(HTTPConstants.REUSE_HTTP_CLIENT, Boolean.TRUE);
        requestMC.getOptions().setTimeOutInMilliSeconds(240000L);

        // Allow privileged access to read properties. Requires PropertiesPermission read in
        // security policy.
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                public Object run() throws AxisFault {
                    operationClient.execute(true);
                    return null;
                }
            });
        } catch (PrivilegedActionException e) {
            operationClient.complete(requestMC);
            throw (AxisFault)e.getException();
        }

        MessageContext responseMC = operationClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);

        OMElement response = responseMC.getEnvelope().getBody().getFirstElement();

        // FIXME: [rfeng] We have to pay performance penalty to build the complete OM as the operationClient.complete() will
        // release the underlying HTTP connection. 
        // Force the response to be populated, see https://issues.apache.org/jira/browse/TUSCANY-1541
        if (response != null) {
            response.build();
        }

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
                                                       "Can't handle mixed payloads between OMElements and other types.");
                }
            }
        }
        final MessageContext requestMC = new MessageContext();
        requestMC.setEnvelope(env);

        // Axis2 operationClients can not be shared so create a new one for each request
        final OperationClient operationClient = serviceClient.getServiceClient().createClient(wsdlOperationName);
        operationClient.setOptions(options);

        ReferenceParameters parameters = msg.getFrom().getReferenceParameters();

        // set callback endpoint and callback ID for WS-Addressing header
        EndpointReference fromEPR = null;
        org.apache.tuscany.sca.runtime.EndpointReference callbackEPR = parameters.getCallbackReference();
        if (callbackEPR != null) {
            fromEPR = new EndpointReference(callbackEPR.getBinding().getURI());
        }
        Object callbackID = parameters.getCallbackID();
        if (callbackID != null) {
            if (fromEPR == null) {
                fromEPR = new EndpointReference(AddressingConstants.Final.WSA_ANONYMOUS_URL);
            }
            //FIXME: serialize callback ID to XML in case it is not a string
            fromEPR.addReferenceParameter(CALLBACK_ID_REFPARM_QN, callbackID.toString());
        }

        // set conversation ID for WS-Addressing header
        Object conversationId = parameters.getConversationID();
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
            OMElement epr =
                EndpointReferenceHelper.toOM(sev.getOMFactory(),
                                             fromEPR,
                                             QNAME_WSA_FROM,
                                             AddressingConstants.Final.WSA_NAMESPACE);
            sh.addChild(epr);
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
        } else {
            requestMC.setTo(new EndpointReference(options.getTo().getAddress())); 
        }
        
        // Allow privileged access to read properties. Requires PropertiesPermission read in
        // security policy.
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                public Object run() throws AxisFault {
                    operationClient.addMessageContext(requestMC);
                    return null;
                }
            });
        } catch (PrivilegedActionException e) {
            throw (AxisFault)e.getException();
        }
        return operationClient;
    }
    
    public boolean allowsPassByReference() {
        return true;
    }
}
