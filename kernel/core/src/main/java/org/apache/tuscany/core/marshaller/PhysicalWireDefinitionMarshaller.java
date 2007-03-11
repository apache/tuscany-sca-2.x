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

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import static org.apache.tuscany.core.marshaller.PhysicalChangeSetMarshaller.CORE_NS;
import static org.apache.tuscany.core.marshaller.PhysicalChangeSetMarshaller.CORE_PREFIX;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.spi.marshaller.MarshalException;
import org.apache.tuscany.spi.model.ModelObject;
import org.apache.tuscany.spi.model.physical.PhysicalOperationDefinition;
import org.apache.tuscany.spi.model.physical.PhysicalWireDefinition;
import org.apache.tuscany.spi.model.physical.PhysicalWireSourceDefinition;
import org.apache.tuscany.spi.model.physical.PhysicalWireTargetDefinition;

/**
 * Marshaller for physical wire definition.
 * 
 * @version $Revision$ $Date: 2007-03-03 11:36:03 +0000 (Sat, 03 Mar
 *          2007) $
 */
public class PhysicalWireDefinitionMarshaller extends AbstractMarshallerExtension<PhysicalWireDefinition> {

    // Source
    public static final String SOURCE = "wireSource";

    // Target
    public static final String TARGET = "wireTarget";

    // Operation
    public static final String OPERATION = "operation";

    // QName for the root element
    public static final QName QNAME = new QName(CORE_NS, "wire", CORE_PREFIX);

    /**
     * Marshalls a physical wire to the xml writer.
     */
    public void marshal(PhysicalWireDefinition modelObject, XMLStreamWriter writer) throws MarshalException {

        try {
            writer.writeStartElement(QNAME.getPrefix(), QNAME.getLocalPart(), QNAME.getNamespaceURI());
            for (PhysicalOperationDefinition pod : modelObject.getOperations()) {
                registry.marshall(pod, writer);
            }
            registry.marshall(modelObject.getSource(), writer);
            registry.marshall(modelObject.getTarget(), writer);
            writer.writeEndElement();
        } catch (XMLStreamException ex) {
            throw new MarshalException(ex);
        }

    }

    /**
     * Unmarshalls a physical wire from the xml reader.
     */
    public PhysicalWireDefinition unmarshal(XMLStreamReader reader) throws MarshalException {

        try {
            PhysicalWireDefinition wireDefinition = new PhysicalWireDefinition();
            while (true) {
                switch (reader.next()) {
                    case START_ELEMENT:
                        String name = reader.getName().getLocalPart();
                        ModelObject modelObject = registry.unmarshall(reader);
                        if (OPERATION.equals(name)) {
                            wireDefinition.addOperation((PhysicalOperationDefinition)modelObject);
                        } else if (SOURCE.equals(name)) {
                            wireDefinition.setSource((PhysicalWireSourceDefinition)modelObject);
                        } else if (TARGET.equals(name)) {
                            wireDefinition.setTarget((PhysicalWireTargetDefinition)modelObject);
                        }
                        break;
                    case END_ELEMENT:
                        if (QNAME.equals(reader.getName())) {
                            return wireDefinition;
                        }
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
    protected Class<PhysicalWireDefinition> getModelObjectType() {
        return PhysicalWireDefinition.class;
    }

}
