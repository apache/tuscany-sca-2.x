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

import org.apache.tuscany.core.marshaller.AbstractPhysicalReferenceDefinitionMarshaller;
import org.apache.tuscany.core.model.physical.java.JavaPhysicalReferenceDefinition;

/**
 * Marshaller for java physical reference definition.
 * 
 * @version $Revision$ $Date$
 */
public class JavaPhysicalReferenceDefinitionMarshaller extends AbstractPhysicalReferenceDefinitionMarshaller<JavaPhysicalReferenceDefinition> {
    
    // QName for the root element
    private static final QName QNAME = new QName("http://tuscany.apache.org/xmlns/marshaller/reference/java/1.0-SNAPSHOT", "service");

    /**
     * Gets the qualified name of the XML fragment for the marshalled model object.
     * @return Qualified name of the XML fragment.
     */
    @Override
    protected QName getModelObjectQName() {
        return QNAME;
    }

    /**
     * Retursn the type of the model object.
     * @return Model object type.
     */
    @Override
    protected Class<JavaPhysicalReferenceDefinition> getModelObjectType() {
        return JavaPhysicalReferenceDefinition.class;
    }

    /**
     * Create the concrete model object.
     * @return Concrete model object.
     */
    @Override
    protected JavaPhysicalReferenceDefinition getConcreteModelObject() {
        return new JavaPhysicalReferenceDefinition();
    }

    /**
     * Handles extensions for unmarshalling.
     * @param modelObject Concrete model object.
     * @param reader Reader from which marshalled data is read.
     */
    @Override
    protected void handleExtensions(JavaPhysicalReferenceDefinition modelObject, XMLStreamReader reader) {
        // TODO Auto-generated method stub
        
    }

    /**
     * Handles extensions for marshalling.
     * @param modelObject Concrete model object.
     * @param reader Writer to which marshalled data is written.
     */
    @Override
    protected void handleExtensions(JavaPhysicalReferenceDefinition modelObject, XMLStreamWriter writer) {
        // TODO Auto-generated method stub
        
    }

}
