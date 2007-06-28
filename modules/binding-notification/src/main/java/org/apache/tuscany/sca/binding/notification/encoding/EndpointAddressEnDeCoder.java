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
public class EndpointAddressEnDeCoder extends AbstractEnDeCoder<EndpointAddress> {

    // QName for the root element
    public static final QName QNAME = new QName(Constants.ADDRESSING_NS, Constants.Address);

    public EndpointAddressEnDeCoder(EncodingRegistry registry) {
        super(registry);
    }

    public void encode(EndpointAddress encodingObject, XMLStreamWriter writer) throws EncodingException {
        
        try {
            writer.writeStartElement(Constants.ADDRESSING_PREFIX, QNAME.getLocalPart(), QNAME.getNamespaceURI());
            writer.writeNamespace(Constants.ADDRESSING_PREFIX, QNAME.getNamespaceURI());
            writer.writeCharacters(encodingObject.getAddress().toString());
            writer.writeEndElement();
        } catch(XMLStreamException e) {
            throw new EncodingException(e);
        }
    }

    public EndpointAddress decode(XMLStreamReader reader) throws EncodingException {
        
        try {
            EndpointAddress endpointAddressElement = new EndpointAddress();
            while (true) {
                switch (reader.next()) {
                    case START_ELEMENT:
                        break;
                    case XMLStreamConstants.CHARACTERS:
                        if (reader.hasText()) {
                            String address = reader.getText();
                            endpointAddressElement.setAddress(address);
                        }
                        else {
                            throw new EncodingException("Endpoint address is missing address");
                        }
                        break;
                    case END_ELEMENT:
                        return endpointAddressElement;
                }
            }
        } catch (XMLStreamException ex) {
            throw new EncodingException(ex);
        }
    }

    protected QName getEncodingObjectQName() {
        
        return QNAME;
    }

    protected Class<EndpointAddress> getEncodingObjectType() {
        
        return EndpointAddress.class;
    }
}
