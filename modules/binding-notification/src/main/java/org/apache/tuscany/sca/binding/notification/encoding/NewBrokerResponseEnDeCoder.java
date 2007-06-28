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
package org.apache.tuscany.sca.binding.notification.encoding;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

/**
 * @version $Rev$ $Date$
 */
public class NewBrokerResponseEnDeCoder extends AbstractEnDeCoder<NewBrokerResponse> {

    // QName for the root element
    public static final QName QNAME = new QName(Constants.NOTIFICATION_NS, Constants.NewBrokerResponse);
        
    public NewBrokerResponseEnDeCoder(EncodingRegistry registry) {
        super(registry);
    }

    public void encode(NewBrokerResponse encodingObject, XMLStreamWriter writer) throws EncodingException {
        
        try {
            QName qName = getEncodingObjectQName();
            writer.writeStartElement(Constants.NOTIFICATION_PREFIX, qName.getLocalPart(), qName.getNamespaceURI());
            writer.writeNamespace(Constants.NOTIFICATION_PREFIX, qName.getNamespaceURI());
            writer.writeAttribute(Constants.FirstBroker, String.valueOf(encodingObject.isFirstBroker()));
            if (encodingObject.isFirstBroker()) {
                registry.encode(encodingObject.getEndConsumers(), writer);
                registry.encode(encodingObject.getEndProducers(), writer);
            }
            else {
                registry.encode(encodingObject.getBrokers(), writer);
            }
            writer.writeEndElement();
        } catch(XMLStreamException e) {
            throw new EncodingException(e);
        }
    }

    public NewBrokerResponse decode(XMLStreamReader reader) throws EncodingException {
        
        try {
            NewBrokerResponse newBrokerResponseElement = new NewBrokerResponse();
            boolean firstBroker = Boolean.parseBoolean(reader.getAttributeValue(null, Constants.FirstBroker));
            newBrokerResponseElement.setFirstBroker(firstBroker);
            boolean haveEC = false;
            boolean haveEP = false;
            boolean haveB = false;
            while (true) {
                switch (reader.next()) {
                    case START_ELEMENT:
                        EncodingObject encodingObject = registry.decode(reader);
                        if (encodingObject instanceof EndProducers && !haveEP && firstBroker) {
                            newBrokerResponseElement.setEndProducers((EndProducers)encodingObject);
                            haveEP = true;
                        }
                        else if(encodingObject instanceof EndConsumers && !haveEC && firstBroker) {
                            newBrokerResponseElement.setEndConsumers((EndConsumers)encodingObject);
                            haveEC = true;
                        }
                        else if(encodingObject instanceof Brokers && !haveB && !firstBroker) {
                            newBrokerResponseElement.setBrokers((Brokers)encodingObject);
                            haveB = true;
                        }
                        else {
                            throw new EncodingException("Invalid encoding object");
                        }
                        break;
                    case END_ELEMENT:
                        if (!haveEP && firstBroker) {
                            throw new EncodingException("Missing end producers");
                        }
                        if (!haveEC && firstBroker) {
                            throw new EncodingException("Missing end consumers");
                        }
                        if (!haveB && !firstBroker) {
                            throw new EncodingException("Missing brokers");
                        }
                        return newBrokerResponseElement;
                }
            }
        } catch (Exception ex) {
            throw new EncodingException(ex);
        }
    }

    protected QName getEncodingObjectQName() {
        
        return QNAME;
    }

    protected Class<NewBrokerResponse> getEncodingObjectType() {
        
        return NewBrokerResponse.class;
    }
}
