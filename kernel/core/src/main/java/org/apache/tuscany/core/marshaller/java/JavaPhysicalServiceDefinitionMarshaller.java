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

import org.apache.tuscany.core.marshaller.AbstractPhysicalServiceDefinitionMarshaller;
import org.apache.tuscany.core.model.physical.java.JavaPhysicalServiceDefinition;

/**
 * Marshaller for java physical service definition.
 * 
 * @version $Revision$ $Date$
 */
public class JavaPhysicalServiceDefinitionMarshaller extends AbstractPhysicalServiceDefinitionMarshaller<JavaPhysicalServiceDefinition> {
    
    // QName for the root element
    private static final QName QNAME = new QName("http://tuscany.apache.org/xmlns/marshaller/java/1.0-SNAPSHOT", "service");

    /**
     * Gets the qualified name of the XML fragment for the marshalled model object.
     * @return {"http://tuscany.apache.org/xmlns/marshaller/reference/java/1.0-SNAPSHOT", "service"}.
     */
    @Override
    protected QName getModelObjectQName() {
        return QNAME;
    }

    /**
     * Returns the type of the model object.
     * @return <code>JavaPhysicalServiceDefinition.class</code>.
     */
    @Override
    protected Class<JavaPhysicalServiceDefinition> getModelObjectType() {
        return JavaPhysicalServiceDefinition.class;
    }

    /**
     * Create the concrete model object.
     * @return An instance of <code>JavaPhysicalServiceDefinition</code>.
     */
    @Override
    protected JavaPhysicalServiceDefinition getConcreteModelObject() {
        return new JavaPhysicalServiceDefinition();
    }

    /**
     * Handles extensions for unmarshalling Java service definitions.
     * @param modelObject Concrete model object.
     * @param reader Reader from which marshalled data is read.
     */
    @Override
    protected void handleExtensions(JavaPhysicalServiceDefinition modelObject, XMLStreamReader reader) {
    }

    /**
     * Handles extensions for marshalling Java service definitions.
     * @param modelObject Concrete model object.
     * @param reader Writer to which marshalled data is written.
     */
    @Override
    protected void handleExtensions(JavaPhysicalServiceDefinition modelObject, XMLStreamWriter writer) {
    }

}
