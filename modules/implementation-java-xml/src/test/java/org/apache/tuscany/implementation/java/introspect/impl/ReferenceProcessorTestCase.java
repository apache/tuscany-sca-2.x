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

import static org.apache.tuscany.implementation.java.introspect.impl.ModelHelper.getReference;

import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

import org.apache.tuscany.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.assembly.Multiplicity;
import org.apache.tuscany.implementation.java.DefaultJavaImplementationFactory;
import org.apache.tuscany.implementation.java.JavaImplementation;
import org.apache.tuscany.implementation.java.JavaImplementationFactory;
import org.apache.tuscany.interfacedef.java.DefaultJavaInterfaceFactory;
import org.apache.tuscany.interfacedef.java.JavaInterface;
import org.apache.tuscany.interfacedef.java.introspect.ExtensibleJavaInterfaceIntrospector;
import org.apache.tuscany.interfacedef.java.introspect.DefaultJavaInterfaceIntrospectorExtensionPoint;
import org.apache.tuscany.interfacedef.java.introspect.JavaInterfaceIntrospectorExtensionPoint;
import org.osoa.sca.annotations.Reference;

/**
 * @version $Rev$ $Date$
 */
public class ReferenceProcessorTestCase extends TestCase {

    private JavaImplementation type;
    private ReferenceProcessor processor;

    public void testMethodAnnotation() throws Exception {
        processor.visitMethod(ReferenceProcessorTestCase.Foo.class.getMethod("setFoo", Ref.class), type);
        org.apache.tuscany.assembly.Reference reference = getReference(type, "foo");
        assertNotNull(reference);
        assertEquals(Ref.class, ((JavaInterface)reference.getInterfaceContract().getInterface()).getJavaClass());
    }

    public void testMethodRequired() throws Exception {
        processor.visitMethod(ReferenceProcessorTestCase.Foo.class.getMethod("setFooRequired", Ref.class), type);
        org.apache.tuscany.assembly.Reference ref = getReference(type, "fooRequired");
        assertNotNull(ref);
        assertEquals(Multiplicity.ONE_ONE, ref.getMultiplicity());
    }

    public void testMethodName() throws Exception {
        processor.visitMethod(ReferenceProcessorTestCase.Foo.class.getMethod("setBarMethod", Ref.class), type);
        assertNotNull(getReference(type, "bar"));
    }

    public void testFieldAnnotation() throws Exception {
        processor.visitField(ReferenceProcessorTestCase.Foo.class.getDeclaredField("baz"), type);
        org.apache.tuscany.assembly.Reference reference = getReference(type, "baz");
        assertNotNull(reference);
        assertEquals(Ref.class, ((JavaInterface)reference.getInterfaceContract().getInterface()).getJavaClass());
    }

    public void testFieldRequired() throws Exception {
        processor.visitField(ReferenceProcessorTestCase.Foo.class.getDeclaredField("bazRequired"), type);
        org.apache.tuscany.assembly.Reference ref = getReference(type, "bazRequired");
        assertNotNull(ref);
        assertEquals(Multiplicity.ONE_ONE, ref.getMultiplicity());
    }

    public void testFieldName() throws Exception {
        processor.visitField(ReferenceProcessorTestCase.Foo.class.getDeclaredField("bazField"), type);
        assertNotNull(getReference(type, "theBaz"));
    }

    public void testDuplicateFields() throws Exception {
        processor.visitField(ReferenceProcessorTestCase.Bar.class.getDeclaredField("dup"), type);
        try {
            processor.visitField(ReferenceProcessorTestCase.Bar.class.getDeclaredField("baz"), type);
            fail();
        } catch (DuplicateReferenceException e) {
            // expected
        }
    }

    public void testDuplicateMethods() throws Exception {
        processor.visitMethod(ReferenceProcessorTestCase.Bar.class.getMethod("dupMethod", Ref.class), type);
        try {
            processor.visitMethod(ReferenceProcessorTestCase.Bar.class.getMethod("dupSomeMethod", Ref.class), type);
            fail();
        } catch (DuplicateReferenceException e) {
            // expected
        }
    }

    public void testInvalidProperty() throws Exception {
        try {
            processor.visitMethod(ReferenceProcessorTestCase.Bar.class.getMethod("badMethod"), type);
            fail();
        } catch (IllegalReferenceException e) {
            // expected
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        JavaImplementationFactory javaImplementationFactory = new DefaultJavaImplementationFactory(new DefaultAssemblyFactory());
        type = javaImplementationFactory.createJavaImplementation();
        JavaInterfaceIntrospectorExtensionPoint visitors = new DefaultJavaInterfaceIntrospectorExtensionPoint();
        processor = new ReferenceProcessor(new DefaultAssemblyFactory(), new DefaultJavaInterfaceFactory(), new ExtensibleJavaInterfaceIntrospector(new DefaultJavaInterfaceFactory(), visitors));
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
        processor.visitField(Multiple.class.getDeclaredField("refs1"), type);
        org.apache.tuscany.assembly.Reference ref = getReference(type, "refs1");
        assertNotNull(ref);
        assertSame(Ref.class, ((JavaInterface)ref.getInterfaceContract().getInterface()).getJavaClass());
        assertEquals(Multiplicity.ONE_N, ref.getMultiplicity());
        // assertEquals(Multiplicity.ONE_ONE, ref.getMultiplicity());
    }

    public void testMultiplicityTo0ToN() throws Exception {
        processor.visitField(Multiple.class.getDeclaredField("refs2"), type);
        org.apache.tuscany.assembly.Reference ref = getReference(type, "refs2");
        assertNotNull(ref);
        assertSame(Ref.class, ((JavaInterface)ref.getInterfaceContract().getInterface()).getJavaClass());
        assertEquals(Multiplicity.ZERO_N, ref.getMultiplicity());
        // assertFalse(ref.isMustSupply());
    }

    public void testMultiplicity1ToNMethod() throws Exception {
        processor.visitMethod(Multiple.class.getMethod("setRefs3", Ref[].class), type);
        org.apache.tuscany.assembly.Reference ref = getReference(type, "refs3");
        assertNotNull(ref);
        assertSame(Ref.class, ((JavaInterface)ref.getInterfaceContract().getInterface()).getJavaClass());
        assertEquals(Multiplicity.ONE_N, ref.getMultiplicity());
        // assertEquals(Multiplicity.ONE_ONE, ref.getMultiplicity());
    }

    public void testMultiplicity0ToNMethod() throws Exception {
        processor.visitMethod(Multiple.class.getMethod("setRefs4", Collection.class), type);
        org.apache.tuscany.assembly.Reference ref = getReference(type, "refs4");
        assertNotNull(ref);
        assertSame(Ref.class, ((JavaInterface)ref.getInterfaceContract().getInterface()).getJavaClass());
        assertEquals(Multiplicity.ZERO_N, ref.getMultiplicity());
        // assertFalse(ref.isMustSupply());
    }

}
