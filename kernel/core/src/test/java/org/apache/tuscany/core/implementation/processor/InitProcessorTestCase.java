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

import java.lang.reflect.Method;

import org.osoa.sca.annotations.Init;

import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class InitProcessorTestCase extends TestCase {

    public void testInit() throws Exception {
        InitProcessor processor = new InitProcessor();
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Method method = InitProcessorTestCase.Foo.class.getMethod("init");
        processor.visitMethod(method, type, null);
        assertNotNull(type.getInitMethod());
        assertEquals(0, type.getInitLevel());
    }

    public void testBadInit() throws Exception {
        InitProcessor processor = new InitProcessor();
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Method method = InitProcessorTestCase.Bar.class.getMethod("badInit", String.class);
        try {
            processor.visitMethod(method, type, null);
            fail();
        } catch (IllegalInitException e) {
            // expected
        }
    }

    public void testTwoInit() throws Exception {
        InitProcessor processor = new InitProcessor();
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Method method = InitProcessorTestCase.Bar.class.getMethod("init");
        Method method2 = InitProcessorTestCase.Bar.class.getMethod("init2");
        processor.visitMethod(method, type, null);
        try {
            processor.visitMethod(method2, type, null);
            fail();
        } catch (DuplicateInitException e) {
            // expected
        }
    }


    private class Foo {
        @Init
        public void init() {
        }
    }


    private class Bar {
        @Init
        public void init() {
        }

        @Init
        public void init2() {
        }

        @Init
        public void badInit(String foo) {
        }


    }
}
