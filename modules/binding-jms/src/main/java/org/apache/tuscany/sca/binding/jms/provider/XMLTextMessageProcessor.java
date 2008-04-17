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

import java.io.StringReader;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.tuscany.sca.binding.jms.impl.JMSBinding;
import org.apache.tuscany.sca.binding.jms.impl.JMSBindingException;

public class XMLTextMessageProcessor extends AbstractMessageProcessor {

    public XMLTextMessageProcessor(JMSBinding jmsBinding) {
        super(jmsBinding);
    }

    @Override
    protected Object[] extractPayload(Message msg) {
        try {

            String xml = ((TextMessage)msg).getText();
            Object[] os = null;
            if (xml != null) {
                XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(xml));
                StAXOMBuilder builder = new StAXOMBuilder(reader);
                os = new Object[] { builder.getDocumentElement() };
            } else {
                os = new Object[]{};
            }
            return os;

        } catch (XMLStreamException e) {
            throw new JMSBindingException(e);
        } catch (JMSException e) {
            throw new JMSBindingException(e);
        }
    }

    @Override
    protected Message createJMSMessage(Session session, Object o) {
        try {

            TextMessage message = session.createTextMessage();

            if (o instanceof OMElement) {
                message.setText(o.toString());
            } else if ((o instanceof Object[]) && ((Object[])o)[0] instanceof OMElement) {
                message.setText(((Object[])o)[0].toString());
            } else if (o != null) {
                throw new IllegalStateException("expecting OMElement payload: " + o);
            }

            return message;

        } catch (JMSException e) {
            throw new JMSBindingException(e);
        }
    }

}
