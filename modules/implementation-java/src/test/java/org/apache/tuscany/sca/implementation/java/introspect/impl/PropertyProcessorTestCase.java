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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import org.apache.tuscany.sca.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.sca.implementation.java.DefaultJavaImplementationFactory;
import org.apache.tuscany.sca.implementation.java.JavaElementImpl;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.JavaImplementationFactory;
import org.apache.tuscany.sca.implementation.java.introspect.JavaIntrospectionHelper;
import org.junit.Before;
import org.junit.Test;
import org.oasisopen.sca.annotation.Property;

/**
 * @version $Rev$ $Date$
 */
public class PropertyProcessorTestCase {

    JavaImplementation type;
    PropertyProcessor processor;

    @Test
    public void testMethodAnnotation() throws Exception {
        processor.visitMethod(Foo.class.getMethod("setFoo", String.class), type);
        assertNotNull(getProperty(type, "foo"));
    }

    @Test
    public void testMethodRequired() throws Exception {
        processor.visitMethod(Foo.class.getMethod("setFooRequired", String.class), type);
        org.apache.tuscany.sca.assembly.Property prop = getProperty(type, "fooRequired");
        assertNotNull(prop);
        assertTrue(prop.isMustSupply());
    }

    @Test
    public void testMethodName() throws Exception {
        processor.visitMethod(Foo.class.getMethod("setBarMethod", String.class), type);
        assertNotNull(getProperty(type, "bar"));
    }

    @Test
    public void testFieldAnnotation() throws Exception {
        processor.visitField(Foo.class.getDeclaredField("baz"), type);
        assertNotNull(getProperty(type, "baz"));
    }

    @Test
    public void testFieldRequired() throws Exception {
        processor.visitField(Foo.class.getDeclaredField("bazRequired"), type);
        org.apache.tuscany.sca.assembly.Property prop = getProperty(type, "bazRequired");
        assertNotNull(prop);
        assertTrue(prop.isMustSupply());
    }

    @Test
    public void testFieldName() throws Exception {
        processor.visitField(Foo.class.getDeclaredField("bazField"), type);
        assertNotNull(getProperty(type, "theBaz"));
    }

    @Test
    public void testDuplicateFields() throws Exception {
        processor.visitField(Bar.class.getDeclaredField("dup"), type);
        try {
            processor.visitField(Bar.class.getDeclaredField("baz"), type);
            fail();
        } catch (DuplicatePropertyException e) {
            // expected
        }
    }

    @Test
    public void testDuplicateMethods() throws Exception {
        processor.visitMethod(Bar.class.getMethod("setDupMethod", String.class), type);
        try {
            processor.visitMethod(Bar.class.getMethod("setDupSomeMethod", String.class), type);
            fail();
        } catch (DuplicatePropertyException e) {
            // expected
        }
    }

    @Test
    public void testInvalidProperty() throws Exception {
        try {
            processor.visitMethod(Bar.class.getMethod("badMethod"), type);
            fail();
        } catch (IllegalPropertyException e) {
            // expected
        }
    }

    @Before
    public void setUp() throws Exception {
        JavaImplementationFactory javaImplementationFactory = new DefaultJavaImplementationFactory();
        type = javaImplementationFactory.createJavaImplementation();
        processor = new PropertyProcessor(new DefaultAssemblyFactory());
    }

    private class Foo {

        @Property
        protected String baz;
        @Property(required = true)
        protected String bazRequired;
        @Property(name = "theBaz")
        protected String bazField;

        @Property
        public void setFoo(String string) {
        }

        @Property(required = true)
        public void setFooRequired(String string) {
        }

        @Property(name = "bar")
        public void setBarMethod(String string) {
        }

    }

    private class Bar {

        @Property
        protected String dup;

        @Property(name = "dup")
        protected String baz;

        @Property
        public void setDupMethod(String s) {
        }

        @Property(name = "dupMethod")
        public void setDupSomeMethod(String s) {
        }

        @Property
        public void badMethod() {
        }

    }

    private class Multiple {
        @Property
        protected List<String> refs1;

        @Property
        protected String[] refs2;

        @Property
        public void setRefs3(String[] refs) {
        }

        @Property
        public void setRefs4(Collection<String> refs) {
        }

    }
    
    private static class BadMethodProps {

        @org.oasisopen.sca.annotation.Constructor()
        public BadMethodProps(@Property(name = "myProp", required = true)String prop) {

        }
        
        /** Java can't tell that the @reference argument is disallowed by SCA, but the run time must reject it*/
        public void BadMethod(@Property(name = "badMethodArgProp")String methArg) 
        {}

 
    }
    
    private static class BadStaticProps {
    	
    	@Property(name="badstaticfield")static int stint;
    	
    	@Property(name="badstaticfield")static void setStint(int theStint) {
    		stint = theStint;
    	}
    }

    private Class<?> getBaseType(JavaElementImpl element) {
        return JavaIntrospectionHelper.getBaseType(element.getType(), element.getGenericType());
    }

    @Test
    public void testMultiplicityCollection() throws Exception {
        processor.visitField(Multiple.class.getDeclaredField("refs1"), type);
        org.apache.tuscany.sca.assembly.Property prop = getProperty(type, "refs1");
        assertNotNull(prop);
        assertSame(String.class, getBaseType(type.getPropertyMembers().get(prop.getName())));
        assertTrue(prop.isMany());
    }

    @Test
    public void testMultiplicityArray() throws Exception {
        processor.visitField(Multiple.class.getDeclaredField("refs2"), type);
        org.apache.tuscany.sca.assembly.Property prop = getProperty(type, "refs2");
        assertNotNull(prop);
        assertSame(String.class, getBaseType(type.getPropertyMembers().get(prop.getName())));
        assertTrue(prop.isMany());
    }

    @Test
    public void testMultiplicityArrayMethod() throws Exception {
        processor.visitMethod(Multiple.class.getMethod("setRefs3", String[].class), type);
        org.apache.tuscany.sca.assembly.Property prop = getProperty(type, "refs3");
        assertNotNull(prop);
        assertSame(String.class, getBaseType(type.getPropertyMembers().get(prop.getName())));
        assertTrue(prop.isMany());
    }

    @Test
    public void testMultiplicityCollectionMethod() throws Exception {
        processor.visitMethod(Multiple.class.getMethod("setRefs4", Collection.class), type);
        org.apache.tuscany.sca.assembly.Property prop = getProperty(type, "refs4");
        assertNotNull(prop);
        assertSame(String.class, getBaseType(type.getPropertyMembers().get(prop.getName())));
        assertTrue(prop.isMany());
    }
    
    @Test
    public void testRejectStaticFieldProperty() throws Exception {
    	try {
    		processor.visitField(BadStaticProps.class.getDeclaredField("stint"), type);
    		fail("Processor should not accept a static field with Property annotation");
    	}
    	catch (IllegalPropertyException e) {
			// System.out.println("Caught expected exception");
		}
    	catch (Exception e) {
			fail("Wrong exception detected");
		}
    }
    	
    @Test
    public void testRejectStaticMethodProperty() throws Exception {
    	try {
    		processor.visitMethod(BadStaticProps.class.getDeclaredMethod("setStint",int.class), type);
    		fail("Processor should not accept a static method with Property annotation");
    	}
    	catch (IllegalPropertyException e) {
			// System.out.println("Caught expected exception");
		}
    	catch (Exception e) {
			fail("Wrong exception detected");
			e.printStackTrace();
		}

    }
    
    @Test
    public void testClassWithBadMethodArgProperty() throws Exception {
        Method meth = BadMethodProps.class.getMethod("BadMethod", String.class);

        try {
        	processor.visitMethod(meth, type);
        	
            fail("Method with @Property annotated args should be rejected");
        } catch (IllegalPropertyException e) {
//        	e.printStackTrace();
//        	System.out.println("Exception successfully received");
        }
        catch (Exception e) {
			fail("Wrong exception received");
			e.printStackTrace();
		}

    }

}
