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

import java.io.Serializable;
import java.io.StringReader;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.tuscany.sca.binding.jms.JMSBinding;
import org.apache.tuscany.sca.binding.jms.JMSBindingException;

public class JMSMessageProcessorImpl implements JMSMessageProcessor {

    protected String  operationPropertyName;
    protected boolean xmlFormat;

    public JMSMessageProcessorImpl(JMSBinding jmsBinding) {
        this.operationPropertyName = jmsBinding.getOperationSelectorPropertyName();
        this.xmlFormat             = jmsBinding.getXMLFormat();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.tuscany.binding.jms.OperationAndDataBinding#getOperationName(javax.jms.Message)
     */
    public String getOperationName(Message message) {
        try {

            return message.getStringProperty(operationPropertyName);

        } catch (JMSException e) {
            throw new JMSBindingException("Exception retreiving operation name from message", e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.tuscany.binding.jms.OperationAndDataBinding#setOperationName(javax.jms.Message,
     *      java.lang.String)
     */
    public void setOperationName(String operationName, Message message) {
        try {

            message.setStringProperty(operationPropertyName, operationName);

        } catch (JMSException e) {
            throw new JMSBindingException("Exception setting the operation name on message", e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.tuscany.binding.jms.OperationAndDataBinding#extractPayload(javax.jms.Session,
     *      java.lang.Object)
     */
    public Message insertPayloadIntoJMSMessage(Session session, Object o) {
        if (xmlFormat) {
            return createXMLJMSMessage(session, o);
        } else {
            return createObjectJMSMessage(session, o);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.tuscany.binding.jms.OperationAndDataBinding#extractPayload(javax.jms.Message)
     */
    public Object extractPayloadFromJMSMessage(Message msg) {
        if (xmlFormat) {
            return extractXMLPayload(msg);
        } else {
            return extractObjectPayload(msg);
        }
    }

    protected Object extractXMLPayload(Message msg) {
        try {

            String xml = ((TextMessage)msg).getText();

            XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(xml));
            StAXOMBuilder builder = new StAXOMBuilder(reader);
            OMElement omElement = builder.getDocumentElement();

            return new Object[] {omElement};

        } catch (XMLStreamException e) {
            throw new JMSBindingException(e);
        } catch (JMSException e) {
            throw new JMSBindingException(e);
        }
    }
    
    protected Object extractObjectPayload(Message msg) {
        try {

            return ((ObjectMessage)msg).getObject();

        } catch (JMSException e) {
            throw new JMSBindingException(e);
        }
    }    

    protected Message createXMLJMSMessage(Session session, Object o) {
        try {

            TextMessage message = session.createTextMessage();

            if (o instanceof OMElement) {
                message.setText(o.toString());
            } else {
                message.setText(((Object[])o)[0].toString());
            }

            return message;

        } catch (JMSException e) {
            throw new JMSBindingException(e);
        }
    }

    protected Message createObjectJMSMessage(Session session, Object o) {
        try {

            ObjectMessage message = session.createObjectMessage(); // default
            message.setObject((Serializable)o);
            return message;

        } catch (JMSException e) {
            throw new JMSBindingException(e);
        }
    }
}
