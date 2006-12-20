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
package org.apache.tuscany.binding.jms.databinding;

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
import org.apache.tuscany.binding.jms.JMSDataBinding;
import org.apache.tuscany.binding.jms.JMSBindingRuntimeException;

public class XMLTextMsgDataBinding implements JMSDataBinding {

    public Object fromJMSMessage(Message msg) throws JMSException {
        try {

            String xml = ((TextMessage)msg).getText();

            XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(xml));
            StAXOMBuilder builder = new StAXOMBuilder(reader);
            OMElement omElement = builder.getDocumentElement();

            return new Object[] {omElement };

        } catch (XMLStreamException e) {
            throw new JMSBindingRuntimeException(e);
        }
    }

    public Message toJMSMessage(Session session, Object o) {
        try {

            TextMessage message = session.createTextMessage();
            
            if (o instanceof OMElement) {
                message.setText(o.toString());
            } else {
                message.setText(((Object[])o)[0].toString());
            }

            return message;

        } catch (JMSException e) {
            throw new JMSBindingRuntimeException(e);
        }
    }

}
