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

import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.spi.marshaller.MarshallException;
import org.apache.tuscany.spi.model.ModelObject;
import org.apache.tuscany.spi.model.physical.PhysicalOperationDefinition;
import org.apache.tuscany.spi.model.physical.PhysicalWireDefinition;

/**
 * Marshaller for physical wire definition.
 * 
 * @version $Revision$ $Date$
 */
public class PhysicalWireDefinitionMarshaller extends AbstractMarshallerExtension<PhysicalWireDefinition> {

    // Source URI attribute
    private static final String SOURCE_URI = "sourceUri";

    // Source URI attribute
    private static final String TARGET_URI = "targetUri";

    // QName for the root element
    private static final QName QNAME = new QName("http://tuscany.apache.org/xmlns/marshaller/1.0-SNAPSHOT", "wire");

    /**
     * Marshalls a physical wire to the xml writer.
     */
    public void marshall(PhysicalWireDefinition modelObject, XMLStreamWriter writer) throws MarshallException {
        throw new UnsupportedOperationException();
    }

    /**
     * Unmarshalls a physical wire from the xml reader.
     */
    public PhysicalWireDefinition unmarshall(XMLStreamReader reader) throws MarshallException {

        try {
            PhysicalWireDefinition wireDefinition = new PhysicalWireDefinition();
            wireDefinition.setSourceUri(new URI(reader.getAttributeValue(null, SOURCE_URI)));
            wireDefinition.setTargetUri(new URI(reader.getAttributeValue(null, TARGET_URI)));
            while (true) {
                switch (reader.next()) {
                    case START_ELEMENT:
                        ModelObject modelObject = registry.unmarshall(reader);
                        wireDefinition.addOperation((PhysicalOperationDefinition)modelObject);
                        break;
                    case END_ELEMENT:
                        return wireDefinition;

                }
            }
        } catch (XMLStreamException ex) {
            throw new MarshallException(ex);
        } catch (URISyntaxException ex) {
            throw new MarshallException(ex);
        }

    }

    @Override
    protected QName getModelObjectQName() {
        return QNAME;
    }

    @Override
    protected Class<PhysicalWireDefinition> getModelObjectType() {
        return PhysicalWireDefinition.class;
    }

}
