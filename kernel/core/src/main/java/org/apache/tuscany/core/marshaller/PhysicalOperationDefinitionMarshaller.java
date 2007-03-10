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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.spi.marshaller.MarshalException;
import org.apache.tuscany.spi.model.physical.PhysicalOperationDefinition;

/**
 * Marshaller for physical operation definition.
 * 
 * @version $Revision$ $Date: 2007-03-03 11:36:03 +0000 (Sat, 03 Mar
 *          2007) $
 */
public class PhysicalOperationDefinitionMarshaller extends AbstractMarshallerExtension<PhysicalOperationDefinition> {

    // Operation name attribute
    private static final String NAME = "name";

    // Return
    private static final String RETURN_TYPE = "returnType";

    // argument
    private static final String PARAMETER = "parameter";


    // QName for the root element
    private static final QName QNAME =
        new QName("http://tuscany.apache.org/xmlns/marshaller/1.0-SNAPSHOT", "operation");

    /**
     * Marshalls a physical operation to the xml writer.
     */
    public void marshal(PhysicalOperationDefinition modelObject, XMLStreamWriter writer) throws MarshalException {
        throw new UnsupportedOperationException();
    }

    /**
     * Unmarshalls a physical operation from the xml reader.
     */
    public PhysicalOperationDefinition unmarshal(XMLStreamReader reader) throws MarshalException {

        try {
            PhysicalOperationDefinition operation = new PhysicalOperationDefinition();
            operation.setName(reader.getAttributeValue(null, NAME));
            operation.setCallback(Boolean.valueOf(reader.getAttributeValue(null, NAME)));
            while (true) {
                switch (reader.next()) {
                    case START_ELEMENT:
                        String name = reader.getName().getLocalPart();
                        String textContent = reader.getElementText();
                        if (PARAMETER.equals(name)) {
                            operation.addParameter(textContent);
                        } else if (RETURN_TYPE.equals(name)) {
                            operation.setReturnType(textContent);
                        }
                        break;
                    case END_ELEMENT:
                        if (getModelObjectQName().equals(reader.getName())) {
                            return operation;
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
    protected Class<PhysicalOperationDefinition> getModelObjectType() {
        return PhysicalOperationDefinition.class;
    }

}
