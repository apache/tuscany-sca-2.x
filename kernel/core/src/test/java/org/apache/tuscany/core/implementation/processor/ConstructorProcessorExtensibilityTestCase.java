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
package org.apache.tuscany.core.implementation.processor;

import java.lang.reflect.Constructor;

import org.osoa.sca.annotations.Property;

import org.apache.tuscany.spi.implementation.java.ConstructorDefinition;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;

import junit.framework.TestCase;
import org.apache.tuscany.core.idl.java.JavaInterfaceProcessorRegistryImpl;

/**
 * Verifies the constructor processor works when parameters are marked with custom extension annotations
 *
 * @version $Rev$ $Date$
 */
public class ConstructorProcessorExtensibilityTestCase extends TestCase {
    private ConstructorProcessor processor =
        new ConstructorProcessor(new ImplementationProcessorServiceImpl(new JavaInterfaceProcessorRegistryImpl()));

    public void testProcessFirst() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Constructor ctor1 = Foo.class.getConstructor(String.class, String.class);
        processor.visitConstructor(null, ctor1, type, null);
        assertEquals("foo", type.getConstructorDefinition().getInjectionNames().get(0));
    }

    /**
     * Verifies the constructor processor can be called after another processor has evaluated the constructor and found
     * an annotation
     *
     * @throws Exception
     */
    public void testProcessLast() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Constructor<Foo> ctor1 = Foo.class.getConstructor(String.class, String.class);
        ConstructorDefinition<Foo> definition = new ConstructorDefinition<Foo>(ctor1);
        definition.getInjectionNames().add("");
        definition.getInjectionNames().add("mybar");
        type.setConstructorDefinition(definition);
        processor.visitConstructor(null, ctor1, type, null);
        assertEquals("foo", type.getConstructorDefinition().getInjectionNames().get(0));
    }


    private @interface Bar {

    }

    private static class Foo {
        @org.osoa.sca.annotations.Constructor
        public Foo(@Property(name = "foo") String foo, @Bar String bar) {

        }
    }


}
