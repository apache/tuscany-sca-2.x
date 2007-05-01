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
import org.osoa.sca.annotations.AllowsPassByReference;

/**
 * @version $Rev$ $Date$
 */
public class AllowsPassByReferenceProcessorTestCase extends TestCase {

    JavaImplementation type;
    AllowsPassByReferenceProcessor processor;
    private JavaImplementationFactory javaImplementationFactory;

    public void testClassAnnotation() throws Exception {
        processor.visitClass(Foo.class, type);
        assertEquals(true, type.isAllowsPassByReference());

        processor.visitClass(Bar.class, type);
        assertEquals(false, type.isAllowsPassByReference());

        Method m1 = Bar.class.getMethod("m1", new Class[] {});
        processor.visitMethod(m1, type);
        assertTrue(type.isAllowsPassByReference(m1));
    }

    protected void setUp() throws Exception {
        super.setUp();
        javaImplementationFactory = new DefaultJavaImplementationFactory(new DefaultAssemblyFactory());
        type = javaImplementationFactory.createJavaImplementation();
        processor = new AllowsPassByReferenceProcessor(new DefaultAssemblyFactory());
    }

    @AllowsPassByReference
    private class Foo {
    }

    // no annotation
    private class Bar {
        @AllowsPassByReference
        public void m1() {

        }
    }
}
