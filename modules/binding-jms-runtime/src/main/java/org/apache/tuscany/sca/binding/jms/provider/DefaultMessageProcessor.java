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
package org.apache.tuscany.sca.binding.jms.provider;

import java.io.IOException;
import java.util.logging.Logger;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.namespace.QName;

import org.apache.tuscany.sca.binding.jms.JMSBinding;
import org.apache.tuscany.sca.binding.jms.JMSBindingConstants;
import org.apache.tuscany.sca.binding.jms.JMSBindingException;
import org.apache.tuscany.sca.common.xml.dom.DOMHelper;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.interfacedef.util.FaultException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * MessageProcessor for sending/receiving XML over javax.jms.TextMessage or javax.jms.BytesMessage 
 * with the JMSBinding.
 * This is very specific to the default wire format and is not tied into the usual hierarchy
 * of message processors
 * 
 * @version $Rev$ $Date$
 */
public class DefaultMessageProcessor extends AbstractMessageProcessor {
    private static final Logger logger = Logger.getLogger(DefaultMessageProcessor.class.getName());

    private DOMHelper domHelper;

    public DefaultMessageProcessor(JMSBinding jmsBinding, ExtensionPointRegistry registry) {
        super(jmsBinding);
        this.domHelper = DOMHelper.getInstance(registry);
    }
    
    // inherited methods that don't do anything useful
    @Override
    protected Message createJMSMessage(Session session, Object o) {
        // should not be used
        return null;
    }
    
    @Override
    protected Object extractPayload(Message msg) {
        // if it's not a text/bytes message or a fault then we don;t know what to do with it
        return null;
    }
    
    // TODO - This makes the assumption that whatever the text/bytes configuration of the
    //        jms binding, unchecked faults will be sent as bytes. 
    @Override
    public Message createFaultMessage(Session session, Throwable o) {
        return createFaultJMSBytesMessage(session, o);
    }
    
    // handle text messages
    
    public Object extractPayloadFromJMSTextMessage(Message msg, Node wrapper) {
        if (msg instanceof TextMessage) {
            try {
                String xml = ((TextMessage) msg).getText();

                Object os;
                if (xml != null && xml.length() > 0) {
                    os = domHelper.load(xml);
                } else {
                    os = null;
                }
                
                if (wrapper != null){
                    //don't modify the original wrapper since it will be reused
                    //clone the wrapper
                    Node node = ((Node)os);
                    if (node == null) {
                        node = domHelper.newDocument();
                    }
                    Element newWrapper = DOMHelper.createElement((Document)node, new QName(wrapper.getNamespaceURI(), wrapper.getLocalName()));
                    if (os != null){
                        Node child = node.getFirstChild();
                        newWrapper.appendChild(child);
                    } 
                    return newWrapper;
                }
                
                return os;
    
            } catch (JMSException e) {
                throw new JMSBindingException(e);
            } catch (IOException e) {
                throw new JMSBindingException(e);
            } catch (SAXException e) {
                throw new JMSBindingException(e);
            }
        } else {
            // handle the non-text fault case
            return super.extractPayloadFromJMSMessage(msg);
        }
    }
    
    public Message insertPayloadIntoJMSTextMessage(Session session, Object o, boolean unwrap) {

        try {

            TextMessage message = session.createTextMessage();

            if (o instanceof Node) {
                
                if (unwrap){
                    Node firstElement = ((Node)o).getFirstChild();
                    if (firstElement == null ) {
                        message.setText("");
                    } else {
                        message.setText(domHelper.saveAsString(firstElement));
                    }
                }else {
                    message.setText(domHelper.saveAsString((Node)o));
                }
            } else if ((o instanceof Object[]) && ((Object[]) o)[0] instanceof Node) {
                if (unwrap){
                    Node firstElement = ((Node)((Object[]) o)[0]).getFirstChild();
                    if (firstElement == null ) {
                        message.setText(null);
                    } else {
                        message.setText(domHelper.saveAsString(firstElement));
                    }
                }else {
                    message.setText(domHelper.saveAsString((Node)((Object[])o)[0]));
                }
            } else if (o != null) {
                throw new IllegalStateException("expecting Node payload: " + o);
            }

            return message;

        } catch (JMSException e) {
            throw new JMSBindingException(e);
        }
    }

    public Message createFaultJMSTextMessage(Session session, Throwable o) {

        if (session == null) {
            logger.fine("no response session to create fault message: " + String.valueOf(o));
            return null;
        }
        if (o instanceof FaultException) {
            try {

                TextMessage message = session.createTextMessage();
                message.setText(domHelper.saveAsString((Node)((FaultException)o).getFaultInfo()));
                message.setBooleanProperty(JMSBindingConstants.FAULT_PROPERTY, true);
                return message;

            } catch (JMSException e) {
                throw new JMSBindingException(e);
            }
        } else {
            // handle the non XML fault case
            return super.createFaultMessage(session, o);
        }
    }

    // handle bytes messages
    
    public Object extractPayloadFromJMSBytesMessage(Message msg, Node wrapper) {
        
        if (msg instanceof BytesMessage) {        
            try {
                Object os;
    
                long noOfBytes = ((BytesMessage) msg).getBodyLength();
                byte[] bytes = new byte[(int) noOfBytes];
                ((BytesMessage) msg).readBytes(bytes);
                ((BytesMessage)msg).reset();

                if ((bytes != null) && (bytes.length > 0)) {
                    os = domHelper.load(new String(bytes));
                } else {
                    os = null;
                }
                
                if (wrapper != null){
                    //don't modify the original wrapper since it will be reused
                    //clone the wrapper
                    Node node = ((Node)os);
                    if (node == null) {
                        node = domHelper.newDocument();
                    }
                    Element newWrapper = DOMHelper.createElement((Document)node, new QName(wrapper.getNamespaceURI(), wrapper.getLocalName()));
                    if (os != null){
                        Node child = node.getFirstChild();
                        newWrapper.appendChild(child);
                    } 
                    return newWrapper;
                }
                
                return os;
    
            } catch (JMSException e) {
                throw new JMSBindingException(e);
            } catch (IOException e) {
                throw new JMSBindingException(e);
            } catch (SAXException e) {
                throw new JMSBindingException(e);
            }
        } else {
            // trap the non-bytes fault case
            return super.extractPayloadFromJMSMessage(msg);
        }
    }
    
    public Message insertPayloadIntoJMSBytesMessage(Session session, Object o, boolean unwrap) {

        try {

            BytesMessage message = session.createBytesMessage();
            

            if (o instanceof Node) {
                if (unwrap) {
                    Node firstElement = ((Node)o).getFirstChild();
                    if (firstElement == null ) {
                        //do nothing, the message will just be set with a byte[0]
                    } else {
                        message.writeBytes(domHelper.saveAsString(firstElement).getBytes());
                    }

                } else {
                    message.writeBytes(domHelper.saveAsString((Node)o).getBytes());                    
                }

            } else if ((o instanceof Object[]) && ((Object[]) o)[0] instanceof Node) {
                if (unwrap){
                    Node firstElement = ((Node)((Object[]) o)[0]).getFirstChild();
                    if (firstElement == null ) {
                        //do nothing, the message will just be set with a byte[0]
                    } else {
                        message.writeBytes(domHelper.saveAsString(firstElement).getBytes());
                    }

                }else {
                    message.writeBytes(domHelper.saveAsString((Node)((Object[]) o)[0]).getBytes());
                }
            } else if (o != null) {
                throw new IllegalStateException("expecting Node payload: " + o);
            }

            return message;

        } catch (JMSException e) {
            throw new JMSBindingException(e);
        }
    }
    
    public Message createFaultJMSBytesMessage(Session session, Throwable o) {

        if (session == null) {
            logger.fine("no response session to create fault message: " + String.valueOf(o));
            return null;
        }
        
        if (o instanceof FaultException) {
            try {

                BytesMessage message = session.createBytesMessage();
                String s = domHelper.saveAsString((Node)((FaultException)o).getFaultInfo());
                message.writeBytes(s.getBytes());
                message.setBooleanProperty(JMSBindingConstants.FAULT_PROPERTY, true);
                return message;

            } catch (JMSException e) {
                throw new JMSBindingException(e);
            }
        } else {
            return super.createFaultMessage(session, o);
        }
    }    
}
