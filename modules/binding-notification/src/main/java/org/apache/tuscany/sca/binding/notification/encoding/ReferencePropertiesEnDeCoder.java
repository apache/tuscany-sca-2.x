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
public class ReferencePropertiesEnDeCoder extends AbstractEnDeCoder<ReferenceProperties> {

    // QName for the root element
    public static final QName QNAME = new QName(Constants.ADDRESSING_NS, Constants.ReferenceProperties);

    public ReferencePropertiesEnDeCoder(EncodingRegistry registry) {
        super(registry);
    }

    public void encode(ReferenceProperties encodingObject, XMLStreamWriter writer) throws EncodingException {
        
        try {
            writer.writeStartElement(Constants.ADDRESSING_PREFIX, QNAME.getLocalPart(), QNAME.getNamespaceURI());
            writer.writeNamespace(Constants.ADDRESSING_PREFIX, QNAME.getNamespaceURI());
            if (encodingObject.getProperties() != null) {
                for (EncodingObject property : encodingObject.getProperties()) {
                    registry.encode(property, writer);
                }
            }
            writer.writeEndElement();
        } catch(XMLStreamException e) {
            throw new EncodingException(e);
        }
    }

    public ReferenceProperties decode(XMLStreamReader reader) throws EncodingException {
        
        try {
            ReferenceProperties referencePropertiesElement = new ReferenceProperties();
            while (true) {
                switch (reader.next()) {
                    case START_ELEMENT:
                        EncodingObject property = registry.decode(reader);
                        referencePropertiesElement.addProperty(property);
                        break;
                    case END_ELEMENT:
                        return referencePropertiesElement;
                }
            }
        } catch (XMLStreamException ex) {
            throw new EncodingException(ex);
        }
    }

    @Override
    protected QName getEncodingObjectQName() {
        
        return QNAME;
    }

    @Override
    protected Class<ReferenceProperties> getEncodingObjectType() {
        
        return ReferenceProperties.class;
    }
}
