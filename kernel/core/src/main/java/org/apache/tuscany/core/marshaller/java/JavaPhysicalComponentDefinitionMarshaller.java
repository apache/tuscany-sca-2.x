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

import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.codec.binary.Base64;
import org.apache.tuscany.core.marshaller.AbstractMarshallerExtension;
import org.apache.tuscany.core.model.physical.java.JavaPhysicalComponentDefinition;
import org.apache.tuscany.spi.marshaller.MarshallException;
import org.apache.tuscany.spi.model.physical.PhysicalReferenceDefinition;
import org.apache.tuscany.spi.model.physical.PhysicalServiceDefinition;

/**
 * Marshaller for physical wire definition.
 * 
 * @version $Revision$ $Date$
 */
public class JavaPhysicalComponentDefinitionMarshaller extends AbstractMarshallerExtension<JavaPhysicalComponentDefinition> {

    // Component id attribute
    private static final String COMPONENT_ID = "componentId";

    // Reference
    private static final String REFERENCE = "reference";

    // Service
    private static final String SERVICE = "service";

    // Instance factory
    private static final String INSTANCE_FACTORY = "instanceFactory";

    // QName for the root element
    private static final QName QNAME = new QName("http://tuscany.apache.org/xmlns/marshaller/component/java/1.0-SNAPSHOT", "component");

    /**
     * Marshalls a physical change set to the xml writer.
     */
    public void marshall(JavaPhysicalComponentDefinition modelObject, XMLStreamWriter writer) throws MarshallException {
        throw new UnsupportedOperationException();
    }

    /**
     * Unmarshalls a physical change set from the xml reader.
     */
    public JavaPhysicalComponentDefinition unmarshall(XMLStreamReader reader) throws MarshallException {

        try {
            JavaPhysicalComponentDefinition javaPhysicalComponentDefinition = new JavaPhysicalComponentDefinition();
            javaPhysicalComponentDefinition.setComponentId(new URI(reader.getAttributeValue(null, COMPONENT_ID)));
            while (true) {
                switch (reader.next()) {
                    case START_ELEMENT:
                        String name = reader.getName().getLocalPart();
                        if(REFERENCE.equals(name)) {
                            PhysicalReferenceDefinition reference = (PhysicalReferenceDefinition) registry.unmarshall(reader);
                            javaPhysicalComponentDefinition.addReference(reference);
                        } else if(SERVICE.equals(name)) {
                            PhysicalServiceDefinition service = (PhysicalServiceDefinition) registry.unmarshall(reader);
                            javaPhysicalComponentDefinition.addService(service);
                        } else if(INSTANCE_FACTORY.equals(name)) {
                            byte[] base64ByteCode = reader.getText().getBytes();
                            byte[] byteCode = Base64.decodeBase64(base64ByteCode);
                            javaPhysicalComponentDefinition.setInstanceFactoryByteCode(byteCode);
                        }
                        break;
                    case END_ELEMENT:
                        return javaPhysicalComponentDefinition;

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
    protected Class<JavaPhysicalComponentDefinition> getModelObjectType() {
        return JavaPhysicalComponentDefinition.class;
    }

}
