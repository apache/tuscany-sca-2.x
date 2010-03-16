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
package scatours.smsgateway;

import java.io.ByteArrayInputStream;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;

public class SMSGatewayImpl implements SMSGateway {

    private final Session session;
    private final MessageConsumer consumer;
    private final MessageProducer producer;

    public SMSGatewayImpl(Session session) throws JMSException {
        this.session = session;
        Destination requestDest = session.createQueue("SMSRequestQueue");
        consumer = session.createConsumer(requestDest);

        Destination responseDest = session.createQueue("SMSResponseQueue");
        producer = session.createProducer(responseDest);
    }

    public void start() throws JMSException, XMLStreamException {
        while (true) {
            Message message = consumer.receive();
            handleRequest(message);
        }
    }

    private void handleRequest(Message inMessage) throws XMLStreamException, JMSException {
        OMElement xml = parseXMLPayload(inMessage);

        String fromNumber = getStringArg(xml, 0);
        String toNumber = getStringArg(xml, 1);
        String text = getStringArg(xml, 2);

        boolean result = sendSMS(fromNumber, toNumber, text);

        String responseXML = createXMLResponse(result);

        TextMessage outMessage = createResponseMessage(inMessage.getJMSMessageID(), responseXML);
        producer.send(outMessage);
    }

    private OMElement parseXMLPayload(Message inMessage) throws JMSException, XMLStreamException {
        final byte[] msgData;
        if (inMessage instanceof TextMessage) {
            msgData = ((TextMessage)inMessage).getText().getBytes();
        } else if (inMessage instanceof BytesMessage) {
            BytesMessage bytesMessage = (BytesMessage)inMessage;
            msgData = new byte[(int)bytesMessage.getBodyLength()];
            bytesMessage.readBytes(msgData);
        } else {
            throw new JMSException("Unsupported JMS message type of " + inMessage.getClass().getName());
        }
        ByteArrayInputStream in = new ByteArrayInputStream(msgData);
        StAXOMBuilder builder = new StAXOMBuilder(in);
        OMElement doc = builder.getDocumentElement();
        return doc;
    }

    private String getStringArg(OMElement doc, int i) {
        QName argQName = new QName("arg" + i);
        OMElement arg = doc.getFirstChildWithName(argQName);
        if (arg == null) {
            return null;
        }
        return arg.getText();
    }

    public boolean sendSMS(String fromNumber, String toNumber, String text) {
        System.out.println("From: " + fromNumber);
        System.out.println("To: " + toNumber);
        System.out.println(text);
        return true;
    }

    private String createXMLResponse(boolean result) {
        OMFactory omFactory = OMAbstractFactory.getOMFactory();
        OMElement response = omFactory.createOMElement("sendSMS", "http://smsgateway.scatours/", "ns2");
        OMElement returnValue = omFactory.createOMElement("return", null);
        OMText returnV = omFactory.createOMText(Boolean.toString(result));
        returnValue.addChild(returnV);
        response.addChild(returnValue);
        return response.toString();
    }

    private TextMessage createResponseMessage(String correlationID, String responseXML) throws JMSException {
        TextMessage outMessage = session.createTextMessage("sendSMS");
        outMessage.setText(responseXML);
        outMessage.setJMSCorrelationID(correlationID);
        return outMessage;
    }
}
