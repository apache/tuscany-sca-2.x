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

import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.tuscany.core.marshaller.extensions.java.JavaPhysicalComponentDefinitionMarshaller;
import org.apache.tuscany.core.marshaller.extensions.java.JavaPhysicalReferenceDefinitionMarshaller;
import org.apache.tuscany.core.marshaller.extensions.java.JavaPhysicalServiceDefinitionMarshaller;
import org.apache.tuscany.spi.marshaller.ModelMarshallerRegistry;
import org.apache.tuscany.spi.model.physical.PhysicalChangeSet;

/**
 * Test case for Java physical change set marshaller.
 * 
 * @version $Revision$ $Date$
 *
 */
public class JavaPhysicalChangeSetMarshallerTest extends TestCase {

    private ModelMarshallerRegistry registry;
    
    public JavaPhysicalChangeSetMarshallerTest(String arg0) {
        super(arg0);
    }
    
    public void setUp() {
        
        registry = new DefaultModelMarshallerRegistry();
        
        AbstractMarshallerExtension<?>[] marshallers = new AbstractMarshallerExtension<?>[6];
        
        for(int i = 0;i < 6; i++) {
            marshallers[i] = new JavaPhysicalComponentDefinitionMarshaller();
            marshallers[i] = new JavaPhysicalServiceDefinitionMarshaller();
            marshallers[i] = new JavaPhysicalReferenceDefinitionMarshaller();
            marshallers[i] = new PhysicalOperationDefinitionMarshaller();
            marshallers[i] = new PhysicalWireDefinitionMarshaller();
            marshallers[i] = new PhysicalChangeSetMarshaller();
        }
        
        for(int i = 0;i < 6; i++) {
            marshallers[i].setRegistry(registry);
        }
        
    }

    public void testUnmarshall() throws Exception {
        
        ClassLoader cl = getClass().getClassLoader();
        InputStream inputStream = cl.getResourceAsStream("marshall/javaChangeSet.xml");
        XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(inputStream);
        
        //PhysicalChangeSet changeSet = (PhysicalChangeSet) registry.unmarshall(reader);
        //assertNotNull(changeSet);
        
        
    }

}
