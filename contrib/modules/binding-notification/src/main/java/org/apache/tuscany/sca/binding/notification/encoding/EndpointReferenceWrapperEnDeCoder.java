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
public abstract class EndpointReferenceWrapperEnDeCoder<ERW extends EndpointReferenceWrapper> extends AbstractEnDeCoder<ERW> {

    public EndpointReferenceWrapperEnDeCoder(EncodingRegistry registry) {
        super(registry);
    }

    public void encode(ERW encodingObject, XMLStreamWriter writer) throws EncodingException {
        
        try {
            QName qName = getEncodingObjectQName();
            writer.writeStartElement(Constants.NOTIFICATION_PREFIX, qName.getLocalPart(), qName.getNamespaceURI());
            writer.writeNamespace(Constants.NOTIFICATION_PREFIX, qName.getNamespaceURI());
            registry.encode(encodingObject.getReference(), writer);
            writer.writeEndElement();
        } catch(XMLStreamException e) {
            throw new EncodingException(e);
        }
    }

    public ERW decode(XMLStreamReader reader) throws EncodingException {
        
        try {
            ERW endpointReferenceWrapperElement = null;
            try {
                endpointReferenceWrapperElement = getEncodingObjectType().newInstance();
            } catch(Exception e) {
                throw new EncodingException(e);
            }
            while (true) {
                switch (reader.next()) {
                    case START_ELEMENT:
                        EncodingObject encodingObject = registry.decode(reader);
                        endpointReferenceWrapperElement.setReference((EndpointReference)encodingObject);
                        break;
                    case END_ELEMENT:
                        return endpointReferenceWrapperElement;
                }
            }
        } catch (XMLStreamException ex) {
            throw new EncodingException(ex);
        }
    }
}
