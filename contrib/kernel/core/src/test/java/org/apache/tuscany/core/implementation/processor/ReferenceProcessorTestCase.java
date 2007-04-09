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

import java.util.Collection;
import java.util.List;

import org.osoa.sca.annotations.Reference;

import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.model.Multiplicity;
import org.apache.tuscany.spi.model.ServiceContract;

import junit.framework.TestCase;
import org.apache.tuscany.core.idl.java.JavaInterfaceProcessorRegistryImpl;

/**
 * @version $Rev$ $Date$
 */
public class ReferenceProcessorTestCase extends TestCase {

    private PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type;
    private ReferenceProcessor processor;

    public void testMethodAnnotation() throws Exception {
        processor.visitMethod(ReferenceProcessorTestCase.Foo.class.getMethod("setFoo", Ref.class), type, null);
        JavaMappedReference reference = type.getReferences().get("foo");
        assertNotNull(reference);
        ServiceContract contract = reference.getServiceContract();
        assertEquals(Ref.class, contract.getInterfaceClass());
        assertEquals("Ref", contract.getInterfaceName());
    }

    public void testMethodRequired() throws Exception {
        processor.visitMethod(
            ReferenceProcessorTestCase.Foo.class.getMethod("setFooRequired", Ref.class),
                              type,
                              null);
        JavaMappedReference ref = type.getReferences().get("fooRequired");
        assertNotNull(ref);
        assertTrue(ref.isRequired());
    }

    public void testMethodName() throws Exception {
        processor.visitMethod(
            ReferenceProcessorTestCase.Foo.class.getMethod("setBarMethod", Ref.class),
                              type,
                              null);
        assertNotNull(type.getReferences().get("bar"));
    }

    public void testFieldAnnotation() throws Exception {
        processor.visitField(ReferenceProcessorTestCase.Foo.class.getDeclaredField("baz"), type, null);
        JavaMappedReference reference = type.getReferences().get("baz");
        assertNotNull(reference);
        ServiceContract contract = reference.getServiceContract();
        assertEquals(Ref.class, contract.getInterfaceClass());
        assertEquals("Ref", contract.getInterfaceName());
    }

    public void testFieldRequired() throws Exception {
        processor.visitField(ReferenceProcessorTestCase.Foo.class.getDeclaredField("bazRequired"), type, null);
        JavaMappedReference prop = type.getReferences().get("bazRequired");
        assertNotNull(prop);
        assertTrue(prop.isRequired());
    }

    public void testFieldName() throws Exception {
        processor.visitField(ReferenceProcessorTestCase.Foo.class.getDeclaredField("bazField"), type, null);
        assertNotNull(type.getReferences().get("theBaz"));
    }

    public void testDuplicateFields() throws Exception {
        processor.visitField(ReferenceProcessorTestCase.Bar.class.getDeclaredField("dup"), type, null);
        try {
            processor.visitField(ReferenceProcessorTestCase.Bar.class.getDeclaredField("baz"), type, null);
            fail();
        } catch (DuplicateReferenceException e) {
            // expected
        }
    }

    public void testDuplicateMethods() throws Exception {
        processor.visitMethod(ReferenceProcessorTestCase.Bar.class.getMethod("dupMethod", Ref.class), type, null);
        try {
            processor.visitMethod(
                ReferenceProcessorTestCase.Bar.class.getMethod("dupSomeMethod", Ref.class),
                                  type,
                                  null);
            fail();
        } catch (DuplicateReferenceException e) {
            // expected
        }
    }

    public void testInvalidProperty() throws Exception {
        try {
            processor.visitMethod(ReferenceProcessorTestCase.Bar.class.getMethod("badMethod"), type, null);
            fail();
        } catch (IllegalReferenceException e) {
            // expected
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        type = new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor = new ReferenceProcessor();
        processor.setInterfaceProcessorRegistry(new JavaInterfaceProcessorRegistryImpl());
    }

    private interface Ref {
    }

    private class Foo {

        @Reference
        protected Ref baz;
        @Reference(required = true)
        protected Ref bazRequired;
        @Reference(name = "theBaz")
        protected Ref bazField;

        @Reference
        public void setFoo(Ref ref) {
        }

        @Reference(required = true)
        public void setFooRequired(Ref ref) {
        }

        @Reference(name = "bar")
        public void setBarMethod(Ref ref) {
        }

    }

    private class Bar {

        @Reference
        protected Ref dup;

        @Reference(name = "dup")
        protected Ref baz;

        @Reference
        public void dupMethod(Ref s) {
        }

        @Reference(name = "dupMethod")
        public void dupSomeMethod(Ref s) {
        }

        @Reference
        public void badMethod() {
        }

    }

    private class Multiple {
        @Reference(required = true)
        protected List<Ref> refs1;

        @Reference(required = false)
        protected Ref[] refs2;

        @Reference(required = true)
        public void setRefs3(Ref[] refs) {
        }

        @Reference(required = false)
        public void setRefs4(Collection<Ref> refs) {
        }

    }

    public void testMultiplicity1ToN() throws Exception {
        processor.visitField(Multiple.class.getDeclaredField("refs1"), type, null);
        JavaMappedReference prop = type.getReferences().get("refs1");
        assertNotNull(prop);
        assertSame(Ref.class, prop.getServiceContract().getInterfaceClass());
        assertEquals(Multiplicity.ONE_N, prop.getMultiplicity());
        assertTrue(prop.isRequired());
    }

    public void testMultiplicityTo0ToN() throws Exception {
        processor.visitField(Multiple.class.getDeclaredField("refs2"), type, null);
        JavaMappedReference prop = type.getReferences().get("refs2");
        assertNotNull(prop);
        assertSame(Ref.class, prop.getServiceContract().getInterfaceClass());
        assertEquals(Multiplicity.ZERO_N, prop.getMultiplicity());
        assertFalse(prop.isRequired());
    }

    public void testMultiplicity1ToNMethod() throws Exception {
        processor.visitMethod(Multiple.class.getMethod("setRefs3", Ref[].class), type, null);
        JavaMappedReference prop = type.getReferences().get("refs3");
        assertNotNull(prop);
        assertSame(Ref.class, prop.getServiceContract().getInterfaceClass());
        assertEquals(Multiplicity.ONE_N, prop.getMultiplicity());
        assertTrue(prop.isRequired());
    }

    public void testMultiplicity0ToNMethod() throws Exception {
        processor.visitMethod(Multiple.class.getMethod("setRefs4", Collection.class), type, null);
        JavaMappedReference prop = type.getReferences().get("refs4");
        assertNotNull(prop);
        assertSame(Ref.class, prop.getServiceContract().getInterfaceClass());
        assertEquals(Multiplicity.ZERO_N, prop.getMultiplicity());
        assertFalse(prop.isRequired());
    }

}
