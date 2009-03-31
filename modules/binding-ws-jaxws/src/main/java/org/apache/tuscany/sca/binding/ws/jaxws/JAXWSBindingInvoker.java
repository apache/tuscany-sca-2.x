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
package org.apache.tuscany.sca.binding.ws.jaxws;

import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.Dispatch;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.axis2.addressing.AddressingConstants;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.DataExchangeSemantics;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.w3c.dom.Node;

/**
 * Axis2BindingInvoker uses an Axis2 OperationClient to invoke a remote web service
 *
 * @version $Rev$ $Date$
 */
public class JAXWSBindingInvoker implements Invoker, DataExchangeSemantics {
    private final static String SCA11_TUSCANY_NS = "http://tuscany.apache.org/xmlns/sca/1.1";

    public static final QName QNAME_WSA_FROM =
        new QName(AddressingConstants.Final.WSA_NAMESPACE, AddressingConstants.WSA_FROM,
                  AddressingConstants.WSA_DEFAULT_PREFIX);
    public static final String TUSCANY_PREFIX = "tuscany";
    public static final QName CALLBACK_ID_REFPARM_QN = new QName(SCA11_TUSCANY_NS, "CallbackID", TUSCANY_PREFIX);
    public static final QName CONVERSATION_ID_REFPARM_QN =
        new QName(SCA11_TUSCANY_NS, "ConversationID", TUSCANY_PREFIX);

    private Dispatch<SOAPMessage> dispatch;
    private MessageFactory messageFactory;
    private Operation operation;
    private WebServiceBinding wsBinding;

    public JAXWSBindingInvoker(Dispatch<SOAPMessage> dispatch,
                               Operation operation,
                               WebServiceFeature[] features,
                               MessageFactory messageFactory,
                               WebServiceBinding wsBinding) {
        this.dispatch = dispatch;
        this.messageFactory = messageFactory;
        this.operation = operation;
        this.wsBinding = wsBinding;

    }

    public Message invoke(Message msg) {
        try {
            SOAPMessage resp = invokeTarget(msg);
            SOAPBody body = resp.getSOAPBody();
            if (body != null) {
                SOAPFault fault = body.getFault();
                if (fault != null) {
                    setFault(msg, fault);
                } else {
                    // The 1st child element
                    msg.setBody(body.getChildElements().next());
                }

            }
            msg.setBody(resp);
        } catch (SOAPFaultException e) {
            setFault(msg, e.getFault());
        } catch (WebServiceException e) {
            msg.setFaultBody(e);
        } catch (SOAPException e) {
            msg.setFaultBody(e);
        } catch (Throwable e) {
            msg.setFaultBody(e);
        }

        return msg;
    }

    private void setFault(Message msg, SOAPFault fault) {
        for (Iterator i = fault.getDetail().getDetailEntries(); i.hasNext();) {
            DetailEntry entry = (DetailEntry)i.next();
            msg.setFaultBody(entry);
        }
    }

    protected SOAPMessage invokeTarget(Message msg) throws SOAPException {
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        javax.xml.soap.SOAPEnvelope envelope = soapPart.getEnvelope();
        javax.xml.soap.SOAPBody body = envelope.addBody();
        body.addDocument(((Node)msg.getBody()).getOwnerDocument());
        soapMessage.saveChanges();
        if (operation.isNonBlocking()) {
            dispatch.invokeOneWay(soapMessage);
            return null;
        }
        SOAPMessage response = dispatch.invoke(soapMessage);
        return response;
    }

    public boolean allowsPassByReference() {
        return true;
    }
}
