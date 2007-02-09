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
package org.apache.tuscany.core.marshaller;

import static javax.xml.stream.XMLStreamConstants.END_DOCUMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.codec.binary.Base64;
import org.apache.tuscany.core.component.JavaPhysicalComponentDefinition;
import org.apache.tuscany.spi.marshaller.MarshalException;
import org.apache.tuscany.spi.marshaller.ModelMarshaller;

/**
 * Marshaller used for marshalling and unmarshalling component definition. Marshalled 
 * physical Java component definitions are of the form,
 * 
 * <code>
 *   <componentJava componentId="uri" xmlns="http://tuscany.apache.org/xmlns/1.0-SNAPSHOT">
 *     <instanceFactoryByteCode>Base 64 Encoded byte code</instanceFactoryByteCode>
 *   </component-java>
 * </code>
 * 
 * @version $Rev$ $Date$
 *
 */
public class JavaPhysicalComponentDefinitionMarshaller implements ModelMarshaller<JavaPhysicalComponentDefinition> {

    /** QName of the serialized Java physical component definition.. */
    public static final QName MESSAGE_TYPE =
        new QName("http://tuscany.apache.org/xmlns/1.0-SNAPSHOT", "componentJava");

    /** URI attribute. */
    public static final String COMPONENT_ID = "componentId";

    /** Instance factory byte code. */
    public static final QName INSTANCE_FACTORY_BYTE_CODE = 
        new QName("http://tuscany.apache.org/xmlns/1.0-SNAPSHOT", "instanceFactoryByteCode");

    /**
     * Marshalls the component definition object to the specified stream writer.
     * 
     * @param modelObject Component definition object to be serialized.
     * @param writer Stream writer to which the infoset is serialized.
     * @throws MarshalException In case of any marshalling error.
     */
    public void marshall(JavaPhysicalComponentDefinition modelObject, XMLStreamWriter writer) throws MarshalException {
        
        try {
            
            writer.setDefaultNamespace(MESSAGE_TYPE.getNamespaceURI());
            
            writer.writeStartDocument();
            writer.writeStartElement(MESSAGE_TYPE.getLocalPart());
            writer.writeNamespace(null, MESSAGE_TYPE.getNamespaceURI());
            writer.writeAttribute(COMPONENT_ID, modelObject.getComponentId().toASCIIString());
            writer.writeStartElement(INSTANCE_FACTORY_BYTE_CODE.getLocalPart());
            
            byte[] byteCode = modelObject.getInstanceFactoryByteCode();
            String encodedByteCode = new String(Base64.encodeBase64(byteCode));
            writer.writeCharacters(encodedByteCode);
            
            writer.writeEndElement();
            writer.writeEndElement();
            writer.writeEndDocument();
            
            writer.flush();
            
        } catch (XMLStreamException ex) {
            throw new MarshalException(ex);
        }
    }

    /**
     * Unmarshalls the component definition object from an XML stream.
     * 
     * @param reader XML stream from where the marshalled XML is read.
     * @return Hydrated component definition object.
     * @throws MarshalException In case of any unmarshalling error.
     */
    public JavaPhysicalComponentDefinition unmarshall(XMLStreamReader reader) throws MarshalException {
        
        try {
            
            JavaPhysicalComponentDefinition definition = null;
            
            for (int i = reader.next(); i != END_DOCUMENT; i = reader.next()) {
                switch (i) {
                    case START_ELEMENT:
                        if (reader.getName().equals(MESSAGE_TYPE)) {
                            final URI componentId = getComponentId(reader);
                            definition = new JavaPhysicalComponentDefinition(componentId);
                        } else if (reader.getName().equals(INSTANCE_FACTORY_BYTE_CODE)) {
                            setInstanceFactoryByteCode(reader, definition);
                        }
                        break;
                }
            }
            
            if (definition == null || definition.getInstanceFactoryByteCode() == null) {
                throw new MarshalException("Invalid component definition");
            }
            
            return definition;
            
        } catch (XMLStreamException ex) {
            throw new MarshalException(ex);
        } catch (URISyntaxException ex) {
            throw new MarshalException(ex);
        }
        
    }

    /*
     * Set the instance factory byte code.
     */
    private void setInstanceFactoryByteCode(XMLStreamReader reader, JavaPhysicalComponentDefinition definition)
        throws XMLStreamException {
        final String byteCode = reader.getElementText();
        final byte[] decodedByteCode = Base64.decodeBase64(byteCode.getBytes());
        definition.setInstanceFactoryByteCode(decodedByteCode);
    }

    /*
     * Gets the component id.
     */
    private URI getComponentId(XMLStreamReader reader)
        throws URISyntaxException {
        final String uri = reader.getAttributeValue(null, COMPONENT_ID);
        return new URI(uri);
    }

}
