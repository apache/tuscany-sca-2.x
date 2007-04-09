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

import org.osoa.sca.annotations.AllowsPassByReference;

import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;

import junit.framework.TestCase;

/**
 * @version $Rev: 452761 $ $Date: 2006-10-04 12:03:20 +0530 (Wed, 04 Oct 2006) $
 */
public class AllowsPassByReferenceProcessorTestCase extends TestCase {

    PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type;
    AllowsPassByReferenceProcessor processor;

    public void testClassAnnotation() throws Exception {
        processor.visitClass(Foo.class, type, null);
        assertEquals(true, type.isAllowsPassByReference());

        processor.visitClass(Bar.class, type, null);
        assertEquals(false, type.isAllowsPassByReference());

        Method m1 = Bar.class.getMethod("m1", new Class[] {});
        processor.visitMethod(m1, type, null);
        assertTrue(type.isAllowsPassByReference(m1));
    }

    protected void setUp() throws Exception {
        super.setUp();
        type = new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor = new AllowsPassByReferenceProcessor();
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
