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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.tuscany.sca.implementation.java.DefaultJavaImplementationFactory;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.JavaImplementationFactory;
import org.junit.Test;
import org.oasisopen.sca.annotation.Property;

/**
 * @version $Rev$ $Date$
 */
public class ConstructorPropertyTestCase extends AbstractProcessorTest {
    
    private JavaImplementationFactory javaImplementationFactory = new DefaultJavaImplementationFactory();

    @Test
    public void testProperty() throws Exception {
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        Constructor<Foo> ctor = Foo.class.getConstructor(String.class);
        visitConstructor(ctor, type);
        org.apache.tuscany.sca.assembly.Property property = getProperty(type, "myProp");
        assertTrue(property.isMustSupply());
        assertEquals("myProp", property.getName());
    }

    @Test
    public void testTwoPropertiesSameType() throws Exception {
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        Constructor<Foo> ctor = Foo.class.getConstructor(String.class, String.class);
        visitConstructor(ctor, type);
        assertNotNull(getProperty(type, "myProp1"));
        assertNotNull(getProperty(type, "myProp2"));
    }

    @Test
    public void testDuplicateProperty() throws Exception {
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        Constructor<BadFoo> ctor = BadFoo.class.getConstructor(String.class, String.class);
        try {
            visitConstructor(ctor, type);
            fail();
        } catch (DuplicatePropertyException e) {
            // expected
        }
    }

    @Test
    public void testNoName() throws Exception {
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        Constructor<BadFoo> ctor = BadFoo.class.getConstructor(String.class);
        try {
            visitConstructor(ctor, type);
            fail();
        } catch (InvalidPropertyException e) {
            // expected
        }
    }

    @Test
    public void testNamesOnConstructor() throws Exception {
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        Constructor<Foo> ctor = Foo.class.getConstructor(Integer.class);
        visitConstructor(ctor, type);
        assertNotNull(getProperty(type, "myProp"));
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
        } catch (InvalidConstructorException e) {
            // expected
        }
    }

//    public void testMultiplicityRequired() throws Exception {
    // TODO multiplicity
//    }

    @Test
    public void testClassWithBadMethodArgProperty() throws Exception {
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        Method meth = BadFoo2.class.getMethod("BadFoo2Method", String.class);

        try {
        	propertyProcessor.visitMethod(meth, type);
        	
            fail();
        } catch (IllegalPropertyException e) {
        	e.printStackTrace();
        	System.out.println("Exception successfully received");
        }

    }
    
    private static class Foo {

        @org.oasisopen.sca.annotation.Constructor()
        public Foo(@Property(name = "myProp", required = true)String prop) {

        }

        @org.oasisopen.sca.annotation.Constructor("myProp")
        public Foo(@Property Integer prop) {

        }

        @org.oasisopen.sca.annotation.Constructor()
        public Foo(@Property(name = "myProp1")String prop1, @Property(name = "myProp2")String prop2) {

        }

        @org.oasisopen.sca.annotation.Constructor()
        public Foo(@Property List prop) {

        }
    }

    private static class BadFoo {

        @org.oasisopen.sca.annotation.Constructor()
        public BadFoo(@Property(name = "myProp")String prop1, @Property(name = "myProp")String prop2) {

        }

        @org.oasisopen.sca.annotation.Constructor()
        public BadFoo(@Property String prop) {

        }

        @org.oasisopen.sca.annotation.Constructor("myProp")
        public BadFoo(@Property Integer prop, @Property Integer prop2) {

        }

        @org.oasisopen.sca.annotation.Constructor({"myRef", "myRef2"})
        public BadFoo(@Property List ref, @Property(name = "myOtherRef")List ref2) {

        }

    }
    
    private static class BadFoo2 {

        @org.oasisopen.sca.annotation.Constructor()
        public BadFoo2(@Property(name = "myProp", required = true)String prop) {

        }
        
        /** Java can't tell that the @reference argument is disallowed by SCA, but the run time must reject it*/
        public void BadFoo2Method(@Property(name = "badMethodArgProp")String methArg) 
        {}

 
    }

}
