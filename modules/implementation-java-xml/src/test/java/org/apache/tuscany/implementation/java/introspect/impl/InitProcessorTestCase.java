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
package org.apache.tuscany.implementation.java.introspect.impl;

import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.apache.tuscany.assembly.impl.DefaultAssemblyFactory;
import org.apache.tuscany.implementation.java.JavaImplementation;
import org.apache.tuscany.implementation.java.JavaImplementationFactory;
import org.apache.tuscany.implementation.java.impl.DefaultJavaImplementationFactory;
import org.osoa.sca.annotations.Init;

/**
 * @version $Rev$ $Date$
 */
public class InitProcessorTestCase extends TestCase {
    
    private JavaImplementationFactory javaImplementationFactory;
    
    public InitProcessorTestCase() {
        javaImplementationFactory = new DefaultJavaImplementationFactory(new DefaultAssemblyFactory());
    }

    public void testInit() throws Exception {
        InitProcessor processor = new InitProcessor(new DefaultAssemblyFactory());
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        Method method = InitProcessorTestCase.Foo.class.getMethod("init");
        processor.visitMethod(method, type);
        assertNotNull(type.getInitMethod());
    }

    public void testBadInit() throws Exception {
        InitProcessor processor = new InitProcessor(new DefaultAssemblyFactory());
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        Method method = InitProcessorTestCase.Bar.class.getMethod("badInit", String.class);
        try {
            processor.visitMethod(method, type);
            fail();
        } catch (IllegalInitException e) {
            // expected
        }
    }

    public void testTwoInit() throws Exception {
        InitProcessor processor = new InitProcessor(new DefaultAssemblyFactory());
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        Method method = InitProcessorTestCase.Bar.class.getMethod("init");
        Method method2 = InitProcessorTestCase.Bar.class.getMethod("init2");
        processor.visitMethod(method, type);
        try {
            processor.visitMethod(method2, type);
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
