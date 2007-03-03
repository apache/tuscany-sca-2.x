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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.spi.marshaller.MarshallException;
import org.apache.tuscany.spi.model.physical.PhysicalOperationDefinition;

/**
 * Marshaller for physical operation definition.
 * 
 * @version $Revision$ $Date$
 */
public class PhysicalOperationDefinitionMarshaller extends AbstractMarshallerExtension<PhysicalOperationDefinition> {

    // Source name attribute
    private static final String NAME = "name";

    // QName for the root element
    private static final QName QNAME = new QName("http://tuscany.apache.org/xmlns/marshaller/1.0-SNAPSHOT", "operation");

    /**
     * Marshalls a physical operation to the xml writer.
     */
    public void marshall(PhysicalOperationDefinition modelObject, XMLStreamWriter writer) throws MarshallException {
        throw new UnsupportedOperationException();
    }

    /**
     * Unmarshalls a physical operation from the xml reader.
     */
    public PhysicalOperationDefinition unmarshall(XMLStreamReader reader) throws MarshallException {

        PhysicalOperationDefinition operation = new PhysicalOperationDefinition();
        operation.setName(reader.getAttributeValue(null, NAME));
        operation.setCallback(Boolean.valueOf(reader.getAttributeValue(null, NAME)));
        return operation;

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
