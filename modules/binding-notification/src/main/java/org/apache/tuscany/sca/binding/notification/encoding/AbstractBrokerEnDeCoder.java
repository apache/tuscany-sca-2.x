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
public abstract class AbstractBrokerEnDeCoder<B extends AbstractBroker> extends AbstractEnDeCoder<B> {

    public AbstractBrokerEnDeCoder(EncodingRegistry registry) {
        super(registry);
    }

    public void encode(B encodingObject, XMLStreamWriter writer) throws EncodingException {
        
        try {
            QName qName = getEncodingObjectQName();
            writer.writeStartElement(Constants.NOTIFICATION_PREFIX, qName.getLocalPart(), qName.getNamespaceURI());
            writer.writeNamespace(Constants.NOTIFICATION_PREFIX, qName.getNamespaceURI());
            registry.encode(encodingObject.getBrokerConsumerReference(), writer);
            registry.encode(encodingObject.getBrokerProducerReference(), writer);
            writer.writeEndElement();
        } catch(XMLStreamException e) {
            throw new EncodingException(e);
        }
    }

    public B decode(XMLStreamReader reader) throws EncodingException {
        
        try {
            B brokerElement = getEncodingObjectType().newInstance();
            boolean haveBCR = false;
            boolean haveBPR = false;
            while (true) {
                switch (reader.next()) {
                    case START_ELEMENT:
                        EncodingObject encodingObject = registry.decode(reader);
                        if (encodingObject instanceof BrokerConsumerReference && !haveBCR) {
                            brokerElement.setBrokerConsumerReference((BrokerConsumerReference)encodingObject);
                            haveBCR = true;
                        }
                        else if(encodingObject instanceof BrokerProducerReference && !haveBPR) {
                            brokerElement.setBrokerProducerReference((BrokerProducerReference)encodingObject);
                            haveBPR = true;
                        }
                        else {
                            throw new EncodingException("Invalid encoding object");
                        }
                        break;
                    case END_ELEMENT:
                        if (!haveBCR) {
                            throw new EncodingException("Missing broker consumer reference");
                        }
                        if (!haveBPR) {
                            throw new EncodingException("Missing broker producer reference");
                        }
                        return brokerElement;
                }
            }
        } catch (Exception ex) {
            throw new EncodingException(ex);
        }
    }
}
