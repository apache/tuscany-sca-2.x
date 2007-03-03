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
package org.apache.tuscany.core.marshaller.java;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.core.marshaller.AbstractMarshallerExtension;
import org.apache.tuscany.core.model.physical.java.JavaPhysicalReferenceDefinition;
import org.apache.tuscany.spi.marshaller.MarshallException;
import org.apache.tuscany.spi.model.ModelObject;
import org.apache.tuscany.spi.model.physical.PhysicalOperationDefinition;

/**
 * Marshaller for java physical reference definition.
 * 
 * @version $Revision$ $Date$
 */
public class JavaPhysicalReferenceDefinitionMarshaller extends AbstractMarshallerExtension<JavaPhysicalReferenceDefinition> {

    // Local part for operation
    private static final String OPERATION = "operation";
    
    // Source name attribute
    private static final String NAME = "name";
    
    // QName for the root element
    private static final QName QNAME = new QName("http://tuscany.apache.org/xmlns/marshaller/reference/java/1.0-SNAPSHOT", "service");

    /**
     * Marshalls a physical java reference definition to the xml writer.
     */
    public void marshall(JavaPhysicalReferenceDefinition modelObject, XMLStreamWriter writer) throws MarshallException {
        throw new UnsupportedOperationException();
    }

    /**
     * Unmarshalls a java physical reference definition from the xml reader.
     */
    public JavaPhysicalReferenceDefinition unmarshall(XMLStreamReader reader) throws MarshallException {
        
        try {
            JavaPhysicalReferenceDefinition referenceDefinition = new JavaPhysicalReferenceDefinition();
            referenceDefinition.setName(reader.getAttributeValue(null, NAME));
            while (true) {
                switch (reader.next()) {
                    case START_ELEMENT:
                        ModelObject modelObject = registry.unmarshall(reader);
                        String name = reader.getName().getLocalPart();
                        if(OPERATION.equals(name)) {
                            referenceDefinition.addOperation((PhysicalOperationDefinition)modelObject);
                        }
                        break;
                    case END_ELEMENT:
                        return referenceDefinition;

                }
            }
        } catch (XMLStreamException ex) {
            throw new MarshallException(ex);
        }
        
    }

    @Override
    protected QName getModelObjectQName() {
        return QNAME;
    }

    @Override
    protected Class<JavaPhysicalReferenceDefinition> getModelObjectType() {
        return JavaPhysicalReferenceDefinition.class;
    }

}
