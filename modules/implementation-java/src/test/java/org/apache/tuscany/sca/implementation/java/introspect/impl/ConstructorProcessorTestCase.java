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

import static org.apache.tuscany.sca.implementation.java.introspect.impl.ModelHelper.getProperty;
import static org.apache.tuscany.sca.implementation.java.introspect.impl.ModelHelper.getReference;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.sca.assembly.Multiplicity;
import org.apache.tuscany.sca.implementation.java.DefaultJavaImplementationFactory;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.JavaImplementationFactory;
import org.apache.tuscany.sca.implementation.java.JavaParameterImpl;
import org.apache.tuscany.sca.interfacedef.java.DefaultJavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.junit.Test;
import org.oasisopen.sca.annotation.Property;
import org.oasisopen.sca.annotation.Reference;

/**
 * @version $Rev$ $Date$
 */
public class ConstructorProcessorTestCase {
    private ConstructorProcessor processor = new ConstructorProcessor(new DefaultAssemblyFactory());
    
    private JavaImplementationFactory javaImplementationFactory = new DefaultJavaImplementationFactory();

    @Test
    public void testDuplicateConstructor() throws Exception {
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        try {
            processor.visitClass(BadFoo.class, type);
            fail();
        } catch (DuplicateConstructorException e) {
            // expected
        }
    }

    @Test
    public void testConstructorAnnotation() throws Exception {
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        Constructor<Foo> ctor1 = Foo.class.getConstructor(String.class);
        processor.visitConstructor(ctor1, type);
        assertEquals("foo", type.getConstructor().getParameters()[0].getName());
    }

    @Test
    public void testNoAnnotation() throws Exception {
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        Constructor<NoAnnotation> ctor1 = NoAnnotation.class.getConstructor();
        processor.visitConstructor(ctor1, type);
        assertNull(type.getConstructor());
    }

    @Test
    public void testBadAnnotation() throws Exception {
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        Constructor<BadAnnotation> ctor1 = BadAnnotation.class.getConstructor(String.class, Foo.class);
        try {
            processor.visitConstructor(ctor1, type);
            fail();
        } catch (InvalidConstructorException e) {
            // expected
        }
    }

    @Test
    public void testMixedParameters() throws Exception {
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        Constructor<Mixed> ctor1 = Mixed.class.getConstructor(String.class, String.class, String.class);
        processor.visitConstructor(ctor1, type);

        AssemblyFactory assemblyFactory = new DefaultAssemblyFactory();
        JavaInterfaceFactory javaFactory = new DefaultJavaInterfaceFactory();
        ReferenceProcessor referenceProcessor = new ReferenceProcessor(assemblyFactory, javaFactory);
        PropertyProcessor propertyProcessor = new PropertyProcessor(assemblyFactory);
        JavaParameterImpl[] parameters = type.getConstructor().getParameters();
        for (int i = 0; i < parameters.length; i++) {
            referenceProcessor.visitConstructorParameter(parameters[i], type);
            propertyProcessor.visitConstructorParameter(parameters[i], type);
        }

        assertEquals("_ref0", parameters[0].getName());
        assertEquals("foo", parameters[1].getName());
        assertEquals("bar", parameters[2].getName());
    }

    private static class BadFoo {

        @org.oasisopen.sca.annotation.Constructor("foo")
        public BadFoo(String foo) {

        }

        @org.oasisopen.sca.annotation.Constructor( {"foo", "bar"})
        public BadFoo(String foo, String bar) {

        }
    }

    private static class Foo {
        @org.oasisopen.sca.annotation.Constructor("foo")
        public Foo(@Property String foo) {

        }
    }

    private static class NoAnnotation {
        public NoAnnotation() {
        }
    }

    private static class BadAnnotation {
        @org.oasisopen.sca.annotation.Constructor("foo")
        public BadAnnotation(String foo, Foo ref) {
        }
    }

    public static final class Mixed {
        @org.oasisopen.sca.annotation.Constructor
        public Mixed(@Reference
        String param1, @Property(name = "foo")
        String param2, @Reference(name = "bar")
        String param3) {
        }
    }

    public static final class Multiple {
        @org.oasisopen.sca.annotation.Constructor
        public Multiple(@Reference
        Collection<String> param1, @Property(name = "foo")
        String[] param2, @Reference(name = "bar", required = true)
        List<String> param3, @Property(name = "abc")
        Set<String> param4, @Reference(name = "xyz")
        String[] param5) {
        }
    }

    @Test
    public void testMultiplicity() throws Exception {
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        Constructor<Multiple> ctor1 = Multiple.class.getConstructor(Collection.class,
                                                                    String[].class,
                                                                    List.class,
                                                                    Set.class,
                                                                    String[].class);
        processor.visitConstructor(ctor1, type);
        AssemblyFactory assemblyFactory = new DefaultAssemblyFactory();
        JavaInterfaceFactory javaFactory = new DefaultJavaInterfaceFactory();
        ReferenceProcessor referenceProcessor = new ReferenceProcessor(assemblyFactory, javaFactory);
        PropertyProcessor propertyProcessor = new PropertyProcessor(assemblyFactory);
        JavaParameterImpl[] parameters = type.getConstructor().getParameters();
        for (int i = 0; i < parameters.length; i++) {
            referenceProcessor.visitConstructorParameter(parameters[i], type);
            propertyProcessor.visitConstructorParameter(parameters[i], type);
        }

        org.apache.tuscany.sca.assembly.Reference ref0 = getReference(type, "_ref0");
        assertNotNull(ref0);
        assertEquals(Multiplicity.ONE_N, ref0.getMultiplicity());
        org.apache.tuscany.sca.assembly.Reference ref1 = getReference(type, "bar");
        assertNotNull(ref1);
        assertEquals(Multiplicity.ONE_N, ref1.getMultiplicity());
        org.apache.tuscany.sca.assembly.Reference ref2 = getReference(type, "xyz");
        assertNotNull(ref2);
        assertEquals(Multiplicity.ONE_N, ref2.getMultiplicity());
        org.apache.tuscany.sca.assembly.Property prop1 = getProperty(type, "foo");
        assertNotNull(prop1);
        assertTrue(prop1.isMany());
        org.apache.tuscany.sca.assembly.Property prop2 = getProperty(type, "abc");
        assertNotNull(prop2);
        assertTrue(prop2.isMany());
    }

}
