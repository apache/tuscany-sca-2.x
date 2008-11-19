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
public abstract class EndpointReferenceSequenceEnDeCoder<ERS extends EndpointReferenceSequence> extends AbstractEnDeCoder<ERS> {

    public EndpointReferenceSequenceEnDeCoder(EncodingRegistry registry) {
        super(registry);
    }

    public void encode(ERS encodingObject, XMLStreamWriter writer) throws EncodingException {
        
        try {
            QName qName = getEncodingObjectQName();
            writer.writeStartElement(Constants.NOTIFICATION_PREFIX, qName.getLocalPart(), qName.getNamespaceURI());
            writer.writeNamespace(Constants.NOTIFICATION_PREFIX, qName.getNamespaceURI());
            encodeSequenceTypeAttribute(encodingObject, writer);
            if (encodingObject.getReferenceSequence() != null) {
                for (EndpointReference endpointReference : encodingObject.getReferenceSequence()) {
                    registry.encode(endpointReference, writer);
                }
            }
            writer.writeEndElement();
        } catch(XMLStreamException e) {
            throw new EncodingException(e);
        }
    }
    
    protected abstract void encodeSequenceTypeAttribute(ERS encodingObject, XMLStreamWriter writer) throws EncodingException;

    public ERS decode(XMLStreamReader reader) throws EncodingException {
        
        try {
            ERS endpointReferenceSequenceElement = null;
            try {
                endpointReferenceSequenceElement = getEncodingObjectType().newInstance();
            } catch(Exception e) {
                throw new EncodingException(e);
            }
            String sequenceType = decodeSequenceTypeAttribute(reader);
            endpointReferenceSequenceElement.setSequenceType(sequenceType);
            while (true) {
                switch (reader.next()) {
                    case START_ELEMENT:
                        EncodingObject encodingObject = registry.decode(reader);
                        endpointReferenceSequenceElement.addReferenceToSequence((EndpointReference)encodingObject);
                        break;
                    case END_ELEMENT:
                        return endpointReferenceSequenceElement;
                }
            }
        } catch (XMLStreamException ex) {
            throw new EncodingException(ex);
        }
    }
    
    protected abstract String decodeSequenceTypeAttribute(XMLStreamReader reader);
}
