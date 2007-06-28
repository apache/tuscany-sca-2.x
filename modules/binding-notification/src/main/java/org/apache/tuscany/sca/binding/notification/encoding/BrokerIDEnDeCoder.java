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
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

/**
 * @version $Rev$ $Date$
 */
public class BrokerIDEnDeCoder extends AbstractEnDeCoder<BrokerID> {

    // QName for the root element
    public static final QName QNAME = new QName(Constants.NOTIFICATION_NS, Constants.BrokerID);

    public BrokerIDEnDeCoder(EncodingRegistry registry) {
        super(registry);
    }

    public void encode(BrokerID encodingObject, XMLStreamWriter writer) throws EncodingException {
        
        try {
            writer.writeStartElement(Constants.NOTIFICATION_PREFIX, QNAME.getLocalPart(), QNAME.getNamespaceURI());
            writer.writeNamespace(Constants.NOTIFICATION_PREFIX, QNAME.getNamespaceURI());
            writer.writeCharacters(encodingObject.getID());
            writer.writeEndElement();
        } catch(XMLStreamException e) {
            throw new EncodingException(e);
        }
    }

    public BrokerID decode(XMLStreamReader reader) throws EncodingException {
        
        try {
            BrokerID brokerIDElement = new BrokerID();
            while (true) {
                switch (reader.next()) {
                    case START_ELEMENT:
                        break;
                    case XMLStreamConstants.CHARACTERS:
                        if (reader.hasText()) {
                            String id = reader.getText();
                            brokerIDElement.setID(id);
                        }
                        else {
                            throw new EncodingException("Broker ID missing value");
                        }
                        break;
                    case END_ELEMENT:
                        return brokerIDElement;
                }
            }
        } catch (XMLStreamException ex) {
            throw new EncodingException(ex);
        }
    }

    protected QName getEncodingObjectQName() {
        
        return QNAME;
    }

    protected Class<BrokerID> getEncodingObjectType() {
        
        return BrokerID.class;
    }
}
