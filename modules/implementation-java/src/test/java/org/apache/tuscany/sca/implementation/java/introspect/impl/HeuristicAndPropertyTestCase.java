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
package org.apache.tuscany.sca.implementation.java.introspect.impl;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Constructor;

import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.implementation.java.DefaultJavaImplementationFactory;
import org.apache.tuscany.sca.implementation.java.JavaConstructorImpl;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.JavaImplementationFactory;
import org.junit.Before;
import org.junit.Test;
import org.oasisopen.sca.annotation.Property;

/**
 * @version $Rev$ $Date$
 */
public class HeuristicAndPropertyTestCase {

    private PropertyProcessor propertyProcessor;
    private HeuristicPojoProcessor heuristicProcessor;
    private JavaImplementationFactory javaImplementationFactory = new DefaultJavaImplementationFactory();

    /**
     * Verifies the property and heuristic processors don't collide
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testPropertyProcessorWithHeuristicProcessor() throws Exception {
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        Constructor ctor = Foo.class.getConstructor(String.class);
        type.setConstructor(new JavaConstructorImpl(ctor));
        propertyProcessor.visitConstructorParameter(type.getConstructor().getParameters()[0], type);
        heuristicProcessor.visitEnd(Foo.class, type);
        assertEquals(1, type.getProperties().size());
        assertEquals("foo", type.getProperties().get(0).getName());
    }

    @Before
    public void setUp() throws Exception {
        ExtensionPointRegistry registry = new DefaultExtensionPointRegistry();
        propertyProcessor = new PropertyProcessor(registry);
        heuristicProcessor = new HeuristicPojoProcessor(registry);
    }

    public static class Foo {
        public Foo(@Property(name = "foo")
        String prop) {
        }
    }

}
