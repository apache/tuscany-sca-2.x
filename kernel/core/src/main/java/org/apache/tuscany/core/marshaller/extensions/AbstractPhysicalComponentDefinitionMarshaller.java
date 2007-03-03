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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.spi.marshaller.MarshallException;
import org.apache.tuscany.spi.model.ModelObject;
import org.apache.tuscany.spi.model.physical.PhysicalComponentDefinition;
import org.apache.tuscany.spi.model.physical.PhysicalReferenceDefinition;
import org.apache.tuscany.spi.model.physical.PhysicalServiceDefinition;

/**
 * Abstract super class for all PCD marshallers.
 * 
 * @version $Revision$ $Date$
 */
public abstract class AbstractPhysicalComponentDefinitionMarshaller<PCD extends PhysicalComponentDefinition> extends AbstractExtensibleMarshallerExtension<PCD> {

    // Component id attribute
    private static final String COMPONENT_ID = "componentId";

    // Reference
    private static final String REFERENCE = "reference";

    // Service
    private static final String SERVICE = "service";

    /**
     * Marshalls a physical change set to the xml writer.
     */
    public final void marshall(PCD modelObject, XMLStreamWriter writer) throws MarshallException {
        throw new UnsupportedOperationException();
    }

    /**
     * Unmarshalls a physical change set from the xml reader.
     */
    public final PCD unmarshall(XMLStreamReader reader) throws MarshallException {

        try {
            PCD componentDefinition = getConcreteModelObject();
            componentDefinition.setComponentId(new URI(reader.getAttributeValue(null, COMPONENT_ID)));
            while (true) {
                switch (reader.next()) {
                    case START_ELEMENT:
                        String name = reader.getName().getLocalPart();
                        ModelObject modelObject = registry.unmarshall(reader);;
                        if(REFERENCE.equals(name)) {
                            componentDefinition.addReference((PhysicalReferenceDefinition) modelObject);
                        } else if(SERVICE.equals(name)) {
                            componentDefinition.addService((PhysicalServiceDefinition) modelObject);
                        } else {
                            handleExtensions(componentDefinition, reader);
                        }
                        break;
                    case END_ELEMENT:
                        return componentDefinition;

                }
            }
        } catch (XMLStreamException ex) {
            throw new MarshallException(ex);
        } catch (URISyntaxException ex) {
            throw new MarshallException(ex);
        }

    }

}
