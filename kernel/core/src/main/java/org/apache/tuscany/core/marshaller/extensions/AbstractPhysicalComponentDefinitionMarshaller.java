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

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.spi.marshaller.MarshalException;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.physical.PhysicalComponentDefinition;

/**
 * Abstract super class for all PCD marshallers.
 * 
 * @version $Revision$ $Date: 2007-03-03 16:41:22 +0000 (Sat, 03 Mar
 *          2007) $
 */
public abstract class AbstractPhysicalComponentDefinitionMarshaller<PCD extends PhysicalComponentDefinition>
    extends AbstractExtensibleMarshallerExtension<PCD> {

    // Component id attribute
    public static final String COMPONENT_ID = "componentId";

    // Group id attribute
    public static final String GROUP_ID = "groupId";

    // Init level attribute
    public static final String INIT_LEVEL = "initLevel";

    // Scope attribute
    private static final String SCOPE = "scope";

    /**
     * Marshalls a physical change set to the xml writer.
     */
    public final void marshal(PCD modelObject, XMLStreamWriter writer) throws MarshalException {

        try {
            
            QName qname = getModelObjectQName();
            writer.writeStartElement(qname.getPrefix(), qname.getLocalPart(), qname.getNamespaceURI());
            writer.writeAttribute(COMPONENT_ID, modelObject.getComponentId().toASCIIString());
            
            URI groupId = modelObject.getGroupId();
            if(groupId != null) {
                writer.writeAttribute(GROUP_ID, groupId.toASCIIString());
            }
            
            writer.writeAttribute(SCOPE, modelObject.getScope().toString());
            writer.writeAttribute(INIT_LEVEL, String.valueOf(modelObject.getInitLevel()));
            
            writer.writeNamespace(qname.getPrefix(), qname.getNamespaceURI());
            
            handleExtension(modelObject, writer);
            
            writer.writeEndElement();
            
        } catch (XMLStreamException ex) {
            throw new MarshalException(ex);
        }
        
    }

    /**
     * Unmarshalls a physical change set from the xml reader.
     */
    public final PCD unmarshal(XMLStreamReader reader) throws MarshalException {

        try {
            PCD componentDefinition = getConcreteModelObject();
            componentDefinition.setComponentId(new URI(reader.getAttributeValue(null, COMPONENT_ID)));
            
            String groupId = reader.getAttributeValue(null, GROUP_ID);
            if(groupId != null) {
                componentDefinition.setGroupId(new URI(groupId));
            }
            componentDefinition.setScope(new Scope(reader.getAttributeValue(null, SCOPE)));
            componentDefinition.setInitLevel(Integer.parseInt(reader.getAttributeValue(null, INIT_LEVEL)));
            while (true) {
                switch (reader.next()) {
                    case START_ELEMENT:
                        handleExtension(componentDefinition, reader);
                        break;
                    case END_ELEMENT:
                        if (getModelObjectQName().equals(reader.getName())) {
                            return componentDefinition;
                        }

                }
            }
        } catch (XMLStreamException ex) {
            throw new MarshalException(ex);
        } catch (URISyntaxException ex) {
            throw new MarshalException(ex);
        }

    }

}
