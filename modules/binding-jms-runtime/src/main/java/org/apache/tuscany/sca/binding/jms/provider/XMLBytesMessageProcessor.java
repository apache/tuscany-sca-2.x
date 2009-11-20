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

import java.io.ByteArrayInputStream;
import java.util.logging.Logger;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.tuscany.sca.binding.jms.JMSBinding;
import org.apache.tuscany.sca.binding.jms.JMSBindingConstants;
import org.apache.tuscany.sca.binding.jms.JMSBindingException;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.interfacedef.util.FaultException;

/**
 * MessageProcessor for sending/receiving XML javax.jms.BytesMessage with the JMSBinding.
 */
public class XMLBytesMessageProcessor extends AbstractMessageProcessor {
    private static final Logger logger = Logger.getLogger(XMLBytesMessageProcessor.class.getName());

    public XMLBytesMessageProcessor(JMSBinding jmsBinding, ExtensionPointRegistry registry) {
        super(jmsBinding);
    }

    @Override
    protected Object extractPayload(Message msg) {
        try {

            if (!(msg instanceof BytesMessage)) {
                throw new IllegalStateException("expecting JMS BytesMessage: " + msg);
            }
            
            long noOfBytes = ((BytesMessage)msg).getBodyLength();
            byte [] bytes = new byte[(int)noOfBytes];
            ((BytesMessage)msg).readBytes(bytes);
            ((BytesMessage)msg).reset();
            
            Object os;
            if (noOfBytes > 0) {
                XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(new ByteArrayInputStream(bytes));
                StAXOMBuilder builder = new StAXOMBuilder(reader);
                os = builder.getDocumentElement();
            } else {
                os = null;
            }
            return os;
        } catch (XMLStreamException e) {
            throw new JMSBindingException(e);
        } catch (JMSException e) {
            throw new JMSBindingException(e);
        }
    }

    @Override
    public Object extractPayloadFromJMSMessage(Message msg) {
        if (msg instanceof BytesMessage) {
            return extractPayload(msg);
        } else {
            return super.extractPayloadFromJMSMessage(msg);
        }
    }

    @Override
    protected Message createJMSMessage(Session session, Object o) {
        if (session == null) {
            logger.fine("no response session to create message: " + String.valueOf(o));
            return null;
        }
        try {
            BytesMessage message = session.createBytesMessage();
            
            if (o instanceof OMElement) {
                message.writeBytes(o.toString().getBytes());
            } else if ((o instanceof Object[]) && ((Object[])o)[0] instanceof OMElement) {
                message.writeBytes(((Object[])o)[0].toString().getBytes());
            } else if (o != null) {
                throw new IllegalStateException("expecting OMElement payload: " + o);
            }            
            
            return message;

        } catch (JMSException e) {
            throw new JMSBindingException(e);
        }
    }
    
    @Override
    public Message createFaultMessage(Session session, Throwable o) {

        if (session == null) {
            logger.fine("no response session to create fault message: " + String.valueOf(o));
            return null;
        }
        if (o instanceof FaultException) {
            try {

                BytesMessage message = session.createBytesMessage();
                message.writeBytes(String.valueOf(((FaultException) o).getFaultInfo()).getBytes());
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
