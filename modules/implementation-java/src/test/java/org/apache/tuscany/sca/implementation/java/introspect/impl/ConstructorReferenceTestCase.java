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

import static org.apache.tuscany.sca.implementation.java.introspect.impl.ModelHelper.getReference;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.tuscany.sca.assembly.Multiplicity;
import org.apache.tuscany.sca.implementation.java.DefaultJavaImplementationFactory;
import org.apache.tuscany.sca.implementation.java.JavaConstructorImpl;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.JavaImplementationFactory;
import org.apache.tuscany.sca.implementation.java.JavaParameterImpl;
import org.junit.Ignore;
import org.junit.Test;
import org.oasisopen.sca.annotation.Property;
import org.oasisopen.sca.annotation.Reference;

//import com.sun.xml.internal.ws.model.JavaMethodImpl;

/**
 * @version $Rev$ $Date$
 */
public class ConstructorReferenceTestCase extends AbstractProcessorTest {
    
    private JavaImplementationFactory javaImplementationFactory = new DefaultJavaImplementationFactory();

    @Test
    public void testReference() throws Exception {
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        Constructor<Foo> ctor = Foo.class.getConstructor(String.class);
        visitConstructor(ctor, type);
        org.apache.tuscany.sca.assembly.Reference reference = getReference(type, "myRef");
        assertEquals(Multiplicity.ONE_ONE, reference.getMultiplicity());
        assertEquals("myRef", reference.getName());
    }

    @Test
    public void testTwoReferencesSameType() throws Exception {
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        Constructor<Foo> ctor = Foo.class.getConstructor(String.class, String.class);
        visitConstructor(ctor, type);
        assertNotNull(getReference(type, "myRef1"));
        assertNotNull(getReference(type, "myRef2"));
    }

    @Test
    public void testDuplicateProperty() throws Exception {
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        Constructor<BadFoo> ctor = BadFoo.class.getConstructor(String.class, String.class);
        try {
            visitConstructor(ctor, type);
            fail();
        } catch (DuplicateReferenceException e) {
            // expected
        }
    }

    @Test
    public void testNoName() throws Exception {
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        Constructor<NoNameFoo> ctor = NoNameFoo.class.getConstructor(String.class);
        try {
            visitConstructor(ctor, type);
            fail();
        } catch (InvalidReferenceException e) {
           //expected   
        }
    }

    @Test
    public void testNamesOnConstructor() throws Exception {
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        Constructor<Foo> ctor = Foo.class.getConstructor(Integer.class);
        visitConstructor(ctor, type);
        assertNotNull(getReference(type, "myRef2"));
    }

    @Test
    public void testInvalidNumberOfNames() throws Exception {
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        Constructor<BadFoo> ctor = BadFoo.class.getConstructor(Integer.class, Integer.class);
        try {
            visitConstructor(ctor, type);
            fail();
        } catch (InvalidConstructorException e) {
            // expected
        }
    }

    @Test
    public void testNoMatchingNames() throws Exception {
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        Constructor<BadFoo> ctor = BadFoo.class.getConstructor(List.class, List.class);
        try {
            visitConstructor(ctor, type);
            fail();
        } catch (InvalidReferenceException e) {
            // expected
        }
    }
    

//    public void testMultiplicityRequired() throws Exception {
    // TODO multiplicity
//    }

    private static class Foo {

        @org.oasisopen.sca.annotation.Constructor()
        public Foo(@Reference(name = "myRef", required = true)String prop) {

        }

        @org.oasisopen.sca.annotation.Constructor()
        public Foo(@Reference(name = "myRef1")String prop1, @Reference(name = "myRef2")String prop2) {

        }

        @org.oasisopen.sca.annotation.Constructor("myRef2")
        public Foo(@Reference(name = "myRef2") Integer prop) {

        }

        @org.oasisopen.sca.annotation.Constructor()
        public Foo(@Reference(name = "myRef3") List prop) {

        }
    }

    private static class NoNameFoo {

        @org.oasisopen.sca.annotation.Constructor
        public NoNameFoo(@Reference String prop) {

        }
    }

    private static class BadFoo {

        @org.oasisopen.sca.annotation.Constructor
        public BadFoo(@Reference(name = "myRef")String prop1, @Reference(name = "myRef")String prop2) {

        }

        @org.oasisopen.sca.annotation.Constructor
        public BadFoo(@Reference String prop) {

        }

        @org.oasisopen.sca.annotation.Constructor("myRef")
        public BadFoo(@Reference Integer ref, @Reference Integer ref2) {

        }

        @org.oasisopen.sca.annotation.Constructor({"myRef", "myRef2"})
        public BadFoo(@Reference List ref, @Reference(name = "myOtherRef")List ref2) {

        }

    }
    



}
