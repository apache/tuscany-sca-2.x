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
package org.apache.tuscany.core.marshaller.extensions.java;

import static org.apache.tuscany.core.marshaller.extensions.java.JavaPhysicalComponentDefinitionMarshaller.JAVA_NS;
import static org.apache.tuscany.core.marshaller.extensions.java.JavaPhysicalComponentDefinitionMarshaller.JAVA_PREFIX;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.core.marshaller.PhysicalWireDefinitionMarshaller;
import org.apache.tuscany.core.marshaller.extensions.AbstractPhysicalWireTargetDefinitionMarshaller;
import org.apache.tuscany.core.model.physical.java.JavaPhysicalWireTargetDefinition;

/**
 * Marshaller for java physical service definition.
 * 
 * @version $Revision$ $Date: 2007-03-03 16:41:22 +0000 (Sat, 03 Mar
 *          2007) $
 */
public class JavaPhysicalWireTargetDefinitionMarshaller extends
    AbstractPhysicalWireTargetDefinitionMarshaller<JavaPhysicalWireTargetDefinition> {

    // QName for the root element
    private static final QName QNAME = new QName(JAVA_NS, PhysicalWireDefinitionMarshaller.TARGET, JAVA_PREFIX);
    
    /**
     * Gets the qualified name of the XML fragment for the marshalled model
     * object.
     * 
     * @return {"http://tuscany.apache.org/xmlns/marshaller/reference/java/1.0-SNAPSHOT",
     *         "service"}.
     */
    @Override
    protected QName getModelObjectQName() {
        return QNAME;
    }

    /**
     * Returns the type of the model object.
     * 
     * @return <code>JavaPhysicalWireTargetDefinition.class</code>.
     */
    @Override
    protected Class<JavaPhysicalWireTargetDefinition> getModelObjectType() {
        return JavaPhysicalWireTargetDefinition.class;
    }

    /**
     * Create the concrete model object.
     * 
     * @return An instance of <code>JavaPhysicalWireTargetDefinition</code>.
     */
    @Override
    protected JavaPhysicalWireTargetDefinition getConcreteModelObject() {
        return new JavaPhysicalWireTargetDefinition();
    }

    /**
     * Handles extensions for unmarshalling Java wire target definitions.
     * 
     * @param modelObject Concrete model object.
     * @param reader Reader from which marshalled data is read.
     */
    @Override
    protected void handleExtension(JavaPhysicalWireTargetDefinition modelObject, XMLStreamReader reader) {
    }

    /**
     * Handles extensions for marshalling Java wire target definitions.
     * 
     * @param modelObject Concrete model object.
     * @param reader Writer to which marshalled data is written.
     */
    @Override
    protected void handleExtension(JavaPhysicalWireTargetDefinition modelObject, XMLStreamWriter writer) {
    }

}
