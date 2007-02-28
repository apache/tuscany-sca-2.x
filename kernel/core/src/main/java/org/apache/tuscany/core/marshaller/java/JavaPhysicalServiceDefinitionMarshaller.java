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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.core.marshaller.AbstractMarshallerExtension;
import org.apache.tuscany.core.model.physical.java.JavaPhysicalServiceDefinition;
import org.apache.tuscany.spi.marshaller.MarshallException;

/**
 * Marshaller for java physical service definition.
 * 
 * @version $Revision$ $Date$
 */
public class JavaPhysicalServiceDefinitionMarshaller extends AbstractMarshallerExtension<JavaPhysicalServiceDefinition> {

    // QName for the root element
    private static final QName QNAME = new QName("http://tuscany.apache.org/xmlns/marshaller/service/java/1.0-SNAPSHOT", "service");

    /**
     * Marshalls a physical java service definition to the xml writer.
     */
    public void marshall(JavaPhysicalServiceDefinition modelObject, XMLStreamWriter writer) throws MarshallException {
        throw new UnsupportedOperationException();
    }

    /**
     * Unmarshalls a java physical service definition from the xml reader.
     */
    public JavaPhysicalServiceDefinition unmarshall(XMLStreamReader reader) throws MarshallException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected QName getModelObjectQName() {
        return QNAME;
    }

    @Override
    protected Class<JavaPhysicalServiceDefinition> getModelObjectType() {
        return JavaPhysicalServiceDefinition.class;
    }

}
