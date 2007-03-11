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
package org.apache.tuscany.core.marshaller.extensions;

import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.spi.marshaller.MarshalException;
import org.apache.tuscany.spi.model.physical.PhysicalWireTargetDefinition;

/**
 * Abstract marshaller for physical wire target definition.
 * 
 * @version $Revision$ $Date$
 */
public abstract class AbstractPhysicalWireTargetDefinitionMarshaller<PWTD extends PhysicalWireTargetDefinition> extends
    AbstractExtensibleMarshallerExtension<PWTD> {

    // Source name attribute
    public static final String URI_ATTRIBUTE = "uri";

    /**
     * Marshalls a physical java reference definition to the xml writer.
     */
    public void marshal(PWTD modelObject, XMLStreamWriter writer) throws MarshalException {

        try {
            
            QName qname = getModelObjectQName();
            writer.writeStartElement(qname.getPrefix(), qname.getLocalPart(), qname.getNamespaceURI());
            writer.writeAttribute(URI_ATTRIBUTE, modelObject.getUri().toASCIIString());
            writer.writeNamespace(qname.getPrefix(), qname.getNamespaceURI());
            
            handleExtension(modelObject, writer);
            
            writer.writeEndElement();
            
        } catch (XMLStreamException ex) {
            throw new MarshalException(ex);
        }
        
    }

    /**
     * Unmarshalls a java physical reference definition from the xml reader.
     */
    public PWTD unmarshal(XMLStreamReader reader) throws MarshalException {

        try {
            PWTD targetDefinition = getConcreteModelObject();
            targetDefinition.setUri(new URI(reader.getAttributeValue(null, URI_ATTRIBUTE)));
            handleExtension(targetDefinition, reader);
            return targetDefinition;
        } catch (URISyntaxException ex) {
            throw new MarshalException(ex);
        }

    }

}
