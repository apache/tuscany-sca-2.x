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
import org.apache.tuscany.spi.model.physical.PhysicalWireSourceDefinition;

/**
 * Abstract marshaller for physical wire source definition.
 * 
 * @version $Revision$ $Date$
 */
public abstract class AbstractPhysicalWireSourceDefinitionMarshaller<PWSD extends PhysicalWireSourceDefinition> extends
    AbstractExtensibleMarshallerExtension<PWSD> {

    // URI attribute
    public static final String URI_ATTRIBUTE = "uri";

    // URI attribute
    public static final String CALLBACK_URI = "callbackUri";
    
    // Optimizable attribute
    public static final String OPTIMIZABLE = "optimizable";
    
    // Conversational
    public static final String CONVERSATIONAL = "conversational";
    

    /**
     * Marshalls a physical java reference definition to the xml writer.
     */
    public void marshal(PWSD modelObject, XMLStreamWriter writer) throws MarshalException {

        try {
            
            QName qname = getModelObjectQName();
            writer.writeStartElement(qname.getPrefix(), qname.getLocalPart(), qname.getNamespaceURI());
            writer.writeAttribute(URI_ATTRIBUTE, modelObject.getUri().toASCIIString());
            
            URI callbackUri = modelObject.getCallbackUri();
            if(callbackUri != null) {
                writer.writeAttribute(CALLBACK_URI, callbackUri.toASCIIString());
            }
            
            writer.writeAttribute(OPTIMIZABLE, String.valueOf(modelObject.isOptimizable()));
            writer.writeAttribute(CONVERSATIONAL, String.valueOf(modelObject.isConversational()));
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
    public PWSD unmarshal(XMLStreamReader reader) throws MarshalException {

        try {
            PWSD sourceDefinition = getConcreteModelObject();
            sourceDefinition.setUri(new URI(reader.getAttributeValue(null, URI_ATTRIBUTE)));
            
            String callbackUri = reader.getAttributeValue(null, CALLBACK_URI);
            if(callbackUri != null) {
                sourceDefinition.setCallbackUri(new URI(callbackUri));
            }
            sourceDefinition.setOptimizable(Boolean.valueOf(reader.getAttributeValue(null, OPTIMIZABLE)));
            sourceDefinition.setConversational(Boolean.valueOf(reader.getAttributeValue(null, CONVERSATIONAL)));
            handleExtension(sourceDefinition, reader);
            return sourceDefinition;
        } catch (URISyntaxException ex) {
            throw new MarshalException(ex);
        }

    }

}
