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
import static javax.xml.stream.XMLStreamConstants.END_DOCUMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.spi.marshaller.MarshalException;
import org.apache.tuscany.spi.model.ModelObject;
import org.apache.tuscany.spi.model.physical.PhysicalChangeSet;
import org.apache.tuscany.spi.model.physical.PhysicalComponentDefinition;
import org.apache.tuscany.spi.model.physical.PhysicalWireDefinition;

/**
 * Marshaller for physical changeset.
 * 
 * @version $Revision$ $Date: 2007-03-10 13:21:21 +0000 (Sat, 10 Mar
 *          2007) $
 */
public class PhysicalChangeSetMarshaller extends AbstractMarshallerExtension<PhysicalChangeSet> {

    // Core marshaller namespace
    public static final String CORE_NS = "http://tuscany.apache.org/xmlns/marshaller/1.0-SNAPSHOT";

    // Core marshaller prefix
    public static final String CORE_PREFIX = "core";

    // QName for the root element
    public static final QName QNAME = new QName(CORE_NS, "changeSet", CORE_PREFIX);

    // Local part for wire
    public static final String WIRE = "wire";

    // Local part for component
    public static final String COMPONENT = "component";

    /**
     * Marshalls a physical change set to the xml writer.
     */
    public void marshal(PhysicalChangeSet modelObject, XMLStreamWriter writer) throws MarshalException {

        try {
            writer.writeStartDocument();
            writer.writeStartElement(QNAME.getPrefix(), QNAME.getLocalPart(), QNAME.getNamespaceURI());
            writer.writeNamespace(CORE_PREFIX, CORE_NS);
            for (PhysicalComponentDefinition pcd : modelObject.getComponentDefinitions()) {
                registry.marshall(pcd, writer);
            }
            for (PhysicalWireDefinition pcd : modelObject.getWireDefinitions()) {
                registry.marshall(pcd, writer);
            }
            writer.writeEndElement();
            writer.writeEndDocument();
        } catch (XMLStreamException ex) {
            throw new MarshalException(ex);
        }

    }

    /**
     * Unmarshalls a physical change set from the xml reader.
     */
    public PhysicalChangeSet unmarshal(XMLStreamReader reader) throws MarshalException {

        try {
            PhysicalChangeSet changeSet = new PhysicalChangeSet();
            while (true) {
                switch (reader.next()) {
                    case START_ELEMENT:
                        String name = reader.getName().getLocalPart();
                        ModelObject modelObject = registry.unmarshall(reader);
                        if (COMPONENT.equals(name)) {
                            changeSet.addComponentDefinition((PhysicalComponentDefinition)modelObject);
                        } else if (WIRE.equals(name)) {
                            changeSet.addWireDefinition((PhysicalWireDefinition)modelObject);
                        }
                        break;
                    case END_DOCUMENT:
                        return changeSet;
                }
            }
        } catch (XMLStreamException ex) {
            throw new MarshalException(ex);
        }

    }

    @Override
    protected QName getModelObjectQName() {
        return QNAME;
    }

    @Override
    protected Class<PhysicalChangeSet> getModelObjectType() {
        return PhysicalChangeSet.class;
    }

}
