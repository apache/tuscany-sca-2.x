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
package org.apache.tuscany.implementation.java.processor;

import java.lang.reflect.Constructor;

import junit.framework.TestCase;

import org.apache.tuscany.implementation.java.impl.ConstructorDefinition;
import org.apache.tuscany.implementation.java.impl.JavaImplementationDefinition;
import org.apache.tuscany.implementation.java.processor.HeuristicPojoProcessor;
import org.apache.tuscany.implementation.java.processor.PropertyProcessor;
import org.apache.tuscany.interfacedef.java.introspection.JavaInterfaceProcessorRegistry;
import org.apache.tuscany.interfacedef.java.introspection.impl.JavaInterfaceProcessorRegistryImpl;
import org.osoa.sca.annotations.Property;

/**
 * @version $Rev$ $Date$
 */
public class HeuristicAndPropertyTestCase extends TestCase {

    private PropertyProcessor propertyProcessor;
    private HeuristicPojoProcessor heuristicProcessor;

    /**
     * Verifies the property and heuristic processors don't collide
     */
    @SuppressWarnings("unchecked")
    public void testPropertyProcessorWithHeuristicProcessor() throws Exception {
        JavaImplementationDefinition type = new JavaImplementationDefinition();
        Constructor ctor = Foo.class.getConstructor(String.class);
        type.setConstructorDefinition(new ConstructorDefinition(ctor));
        propertyProcessor.visitConstructorParameter(type.getConstructorDefinition().getParameters()[0], type);
        heuristicProcessor.visitEnd(Foo.class, type);
        assertEquals(1, type.getProperties().size());
        assertEquals("foo", type.getProperties().get(0).getName());
    }

    protected void setUp() throws Exception {
        super.setUp();
        JavaInterfaceProcessorRegistry registry = new JavaInterfaceProcessorRegistryImpl();
        propertyProcessor = new PropertyProcessor();
        propertyProcessor.setInterfaceProcessorRegistry(registry);
        heuristicProcessor = new HeuristicPojoProcessor();
        heuristicProcessor.setInterfaceProcessorRegistry(registry);
    }

    public static class Foo {
        public Foo(@Property(name = "foo")
        String prop) {
        }
    }

}
