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
package org.apache.tuscany.sca.implementation.java.injection;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.core.factory.ObjectCreationException;
import org.apache.tuscany.sca.core.factory.ObjectFactory;
import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.databinding.DefaultDataBindingExtensionPoint;
import org.apache.tuscany.sca.databinding.Mediator;
import org.apache.tuscany.sca.databinding.impl.SimpleTypeMapperImpl;
import org.apache.tuscany.sca.implementation.java.impl.JavaElementImpl;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This test case will test the JavaPropertyValueObjectFactory.
 *
 * @version $Rev$ $Date$
 */
public class JavaPropertyValueObjectFactoryTestCase {

    /**
     * The factory we should use for testing.
     */
    private static JavaPropertyValueObjectFactory factory;

    /**
     * The assembly factory used to create Properties.
     */
    private static AssemblyFactory assemblyFactory;

    /**
     * Test Setup.
     */
    @BeforeClass
    public static void setup() {
        // Create the factory
        Mediator mediator = EasyMock.createNiceMock(Mediator.class);
        DataBindingExtensionPoint dpep = new DefaultDataBindingExtensionPoint();
        EasyMock.expect(mediator.getDataBindings()).andReturn(dpep).anyTimes();
        EasyMock.replay(mediator);
        factory = new JavaPropertyValueObjectFactory(mediator);
        
        // Create the AssemblyFactory we should use
        assemblyFactory = new DefaultAssemblyFactory();
    }

    /**
     * A test that will attempt to inject positive, negative and zero into an
     * int property.
     */
    @Test
    public void testIntegerInjectionValid() {
        InjectionTestParams params = new InjectionTestParams();
        params.propertyName = "intField";
        params.xsdType = SimpleTypeMapperImpl.XSD_INT;
        params.expectedType = Integer.TYPE;

        for (int i = -5; i <= 5; i++) {
            params.propertyValue = Integer.toString(i);
            params.expectedValueFromFactory = i;
            doInjection(params);
        }
    }

    /**
     * A test that will attempt to inject positive, negative and zero into an
     * int property using a JavaElement.
     */
    @Test
    public void testIntegerInjectionValidWithJavaElement() {
        InjectionTestParams params = new InjectionTestParams();
        params.propertyName = "intField";
        params.xsdType = SimpleTypeMapperImpl.XSD_INT;
        params.expectedJavaElement = new JavaElementImpl(int.class);

        for (int i = -5; i <= 5; i++) {
            params.propertyValue = Integer.toString(i);
            params.expectedValueFromFactory = i;
            doInjection(params);
        }
    }

    /**
     * A test that will attempt to inject multiple int values into an
     * int property.
     */
    @Test
    public void testIntegerArrayInjectionValid() {
        InjectionTestParams params = new InjectionTestParams();
        params.propertyName = "intField";
        params.xsdType = SimpleTypeMapperImpl.XSD_INT;
        params.isMany = true;
        params.expectedType = int.class;
        params.propertyValue = "1 2 3 4 5";
        params.expectedValueFromFactory = Arrays.asList(1, 2, 3, 4, 5);
        doInjection(params);
    }

    /**
     * A test that will attempt to inject multiple int values into an
     * int property using a JavaElement.
     */
    @Test
    public void testIntegerArrayInjectionValidWithJavaElement() {
        InjectionTestParams params = new InjectionTestParams();
        params.propertyName = "intField";
        params.xsdType = SimpleTypeMapperImpl.XSD_INT;
        params.isMany = true;
        params.expectedJavaElement = new JavaElementImpl(int[].class);
        params.propertyValue = "1 2 3 4 5";
        int[] expected = { 1, 2, 3, 4, 5 };
        params.expectedValueFromFactory = expected; 
        doInjection(params);
    }

    /**
     * A test that will attempt to inject a non-number into an
     * int property.
     */
    @Test
    public void testIntegerInjectionBadNumberInvalid() {
        InjectionTestParams params = new InjectionTestParams();
        params.propertyName = "intField";
        params.xsdType = SimpleTypeMapperImpl.XSD_INT;
        params.expectedType = Integer.TYPE;
        params.propertyValue = "a";
        params.exceptionExpected = true;
        doInjection(params);
    }

    /**
     * A test that will attempt to inject a non-number into an
     * int property using a JavaElement.
     */
    @Test
    public void testIntegerInjectionBadNumberInvalidWithJavaElement() {
        InjectionTestParams params = new InjectionTestParams();
        params.propertyName = "intField";
        params.xsdType = SimpleTypeMapperImpl.XSD_INT;
        params.expectedJavaElement = new JavaElementImpl(Integer.TYPE);
        params.propertyValue = "a";
        params.exceptionExpected = true;
        doInjection(params);
    }

    /**
     * A test that will attempt to inject multiple int values into an
     * int property where one of the property values is not a number.
     * The injection should throw ObjectCreationException
     */
    @Test
    public void testIntegerArrayInjectionBadNumberInvalid() {
        InjectionTestParams params = new InjectionTestParams();
        params.propertyName = "intField";
        params.xsdType = SimpleTypeMapperImpl.XSD_INT;
        params.isMany = true;
        params.expectedType = int.class;
        params.propertyValue = "1 2 aa 4 5";
        params.exceptionExpected = true;
        doInjection(params);
    }

    /**
     * A test that will attempt to inject multiple int values into an
     * int property using a JavaElement where one of the property
     * values is not a number.
     * The injection should throw ObjectCreationException
     */
    @Test
    public void testIntegerArrayInjectionBadNumberInvalidWithJavaElement() {
        InjectionTestParams params = new InjectionTestParams();
        params.propertyName = "intField";
        params.xsdType = SimpleTypeMapperImpl.XSD_INT;
        params.isMany = true;
        params.expectedJavaElement = new JavaElementImpl(int[].class);
        params.propertyValue = "1 2 aa 4 5";
        params.exceptionExpected = true;
        doInjection(params);
    }

    /**
     * A test that will attempt to inject an empty string into an int property.
     * The injection should throw ObjectCreationException
     */
    @Test
    public void testIntegerInjectionEmptyStringInvalid() {
        InjectionTestParams params = new InjectionTestParams();
        params.propertyName = "intField";
        params.xsdType = SimpleTypeMapperImpl.XSD_INT;
        params.expectedType = Integer.TYPE;
        params.propertyValue = "";
        params.exceptionExpected = true;
        doInjection(params);
    }

    /**
     * A test that will attempt to inject an empty string into an int property
     * using a JavaElement.
     * The injection should throw ObjectCreationException
     */
    @Test
    public void testIntegerInjectionEmptyStringInvalidWithJavaElement() {
        InjectionTestParams params = new InjectionTestParams();
        params.propertyName = "intField";
        params.xsdType = SimpleTypeMapperImpl.XSD_INT;
        params.expectedJavaElement = new JavaElementImpl(Integer.TYPE);
        params.propertyValue = "";
        params.exceptionExpected = true;
        doInjection(params);
    }

    /**
     * A test that will attempt to inject a String into a String 
     * property.
     */
    @Test
    public void testStringInjectionValid() {
        InjectionTestParams params = new InjectionTestParams();
        params.propertyName = "StringField";
        params.xsdType = SimpleTypeMapperImpl.XSD_STRING;
        params.expectedType = String.class;

        params.propertyValue = "Some Test String";
        params.expectedValueFromFactory = "Some Test String";
        doInjection(params);
    }

    /**
     * A test that will attempt to inject a String into a String 
     * property using a JavaElement.
     */
    @Test
    public void testStringInjectionValidWithJavaElement() {
        InjectionTestParams params = new InjectionTestParams();
        params.propertyName = "StringField";
        params.xsdType = SimpleTypeMapperImpl.XSD_STRING;
        params.expectedJavaElement = new JavaElementImpl(String.class);

        params.propertyValue = "Some Test String";
        params.expectedValueFromFactory = "Some Test String";
        doInjection(params);
    }

    /**
     * This class defines all the parameters for the Property Injection test.
     */
    private class InjectionTestParams {
        // Input parameters for the test
        public boolean isMany = false;
        public String propertyName;
        public String propertyValue;
        public QName xsdType;
        
        // Expected result for test
        public Object expectedValueFromFactory;
        public Class<?> expectedType;
        public JavaElementImpl expectedJavaElement;
        public boolean exceptionExpected = false;
    }

    /**
     * A test that will attempt to inject multiple String values into an
     * String property.
     */
    @Test
    public void testStringArrayInjectionValid() {
        InjectionTestParams params = new InjectionTestParams();
        params.propertyName = "StringField";
        params.xsdType = SimpleTypeMapperImpl.XSD_STRING;
        params.isMany = true;
        params.expectedType = String.class;
        params.propertyValue = "\"String1\" \"String2\" \"String3\" \"String4\" \"String5\"";
        params.expectedValueFromFactory = Arrays.asList(
                "String1", "String2", "String3", "String4", "String5");
        doInjection(params);
    }

    /**
     * A test that will attempt to inject multiple String values into an
     * String property using a JavaElement.
     */
    @Test
    public void testStringArrayInjectionValidWithJavaElement() {
        InjectionTestParams params = new InjectionTestParams();
        params.propertyName = "StringField";
        params.xsdType = SimpleTypeMapperImpl.XSD_STRING;
        params.isMany = true;
        params.expectedJavaElement = new JavaElementImpl(String.class);
        params.propertyValue = "\"String1\" \"String2\" \"String3\" \"String4\" \"String5\"";
        params.expectedValueFromFactory = Arrays.asList(
                "String1", "String2", "String3", "String4", "String5");
        doInjection(params);
    }

    /**
     * Utility method for testing creating properties  with the 
     * JavaPropertyValueObjectFactory.
     * 
     * @param testParams The parameters for the test
     */
    private void doInjection(final InjectionTestParams testParams) {
        // Create the property
        Property prop = assemblyFactory.createProperty();
        prop.setMany(testParams.isMany);
        prop.setName(testParams.propertyName);
        prop.setXSDType(testParams.xsdType);

        // Mock up the XML that will contain the Property details
        Document doc = EasyMock.createNiceMock(Document.class);
        Element rootElement = EasyMock.createMock(Element.class);
        EasyMock.expect(doc.getDocumentElement()).andReturn(rootElement);
        NodeList nodeList = EasyMock.createMock(NodeList.class);
        EasyMock.expect(rootElement.getChildNodes()).andReturn(nodeList).anyTimes();
        EasyMock.expect(nodeList.getLength()).andReturn(1);
        Node node = EasyMock.createMock(Node.class);
        EasyMock.expect(nodeList.item(0)).andReturn(node);
        EasyMock.expect(node.getTextContent()).andReturn(testParams.propertyValue);
        EasyMock.replay(doc, rootElement, nodeList, node);

        // Create a factory either using the Class or JavaElementImpl constructor
        ObjectFactory<?> objectFactory;
        if (testParams.expectedJavaElement != null) {
            objectFactory = factory.createValueFactory(prop, doc, testParams.expectedJavaElement);
        } else {
            objectFactory = factory.createValueFactory(prop, doc, testParams.expectedType);
        }
        Assert.assertNotNull(objectFactory);

        // Lets test the factory
        try {
            // Create a new instance with the factory
            Object value = objectFactory.getInstance();

            // Did we expect an exception to be thrown?
            if (testParams.exceptionExpected) {
                Assert.fail("Test should have thrown ObjectCreationException");
            }

            // Make sure the result is of the correct type
            if (testParams.expectedValueFromFactory instanceof Collection<?>) {
                // Make sure the Collections contain the same type
                Assert.assertTrue(value instanceof Collection<?>);
                Iterator<?> iter1 = ((Collection<?>) testParams.expectedValueFromFactory).iterator();
                Iterator<?> iter2 = ((Collection<?>) value).iterator();
                Assert.assertEquals(iter1.next().getClass(), iter2.next().getClass());
            } else {
                Assert.assertEquals(testParams.expectedValueFromFactory.getClass(), value.getClass());
            }

            // Validate the result
            Assert.assertNotNull(value);
            if (testParams.expectedValueFromFactory.getClass().isArray()) {
                Assert.assertTrue(compareArrays(testParams.expectedValueFromFactory, value));
            } else {
                Assert.assertEquals(testParams.expectedValueFromFactory, value);
            }
        } catch (ObjectCreationException ex) {
            // Is this an expected exception?
            if (testParams.exceptionExpected) {
                // Make sure the exception error message contains the property name
                Assert.assertTrue(ex.toString().indexOf(testParams.propertyName) != -1);

                // Make sure the exception error message contains the property value
                if (testParams.propertyValue != null) {
                    if (testParams.isMany) {
                        // FIXME: No simple way to do this for multi-value properties
                    } else {
                        Assert.assertTrue(ex.toString().indexOf(testParams.propertyValue) != -1);
                    }
                }
            } else {
                // Test failure. We were not expecting an exception
                ex.printStackTrace();
                Assert.fail("Unexpected exception " + ex);
            }
        }
    }

    /**
     * Compares two Objects that are actually arrays to make sure that they are
     * equal.
     * 
     * @param array1 The first array
     * @param array2 The second array
     * @return True if they are equal. False if they are not
     */
    private boolean compareArrays(final Object array1, final Object array2) {
        // Check for primitive array types
        if (array1 instanceof boolean[]) {
            return Arrays.equals((boolean[]) array1, (boolean[]) array2);
        }
        if (array1 instanceof byte[]) {
            return Arrays.equals((byte[]) array1, (byte[]) array2);
        }
        if (array1 instanceof char[]) {
            return Arrays.equals((char[]) array1, (char[]) array2);
        }
        if (array1 instanceof double[]) {
            return Arrays.equals((double[]) array1, (double[]) array2);
        }
        if (array1 instanceof float[]) {
            return Arrays.equals((float[]) array1, (float[]) array2);
        }
        if (array1 instanceof int[]) {
            return Arrays.equals((int[]) array1, (int[]) array2);
        }
        if (array1 instanceof long[]) {
            return Arrays.equals((long[]) array1, (long[]) array2);
        }
        if (array1 instanceof short[]) {
            return Arrays.equals((short[]) array1, (short[]) array2);
        }
        
        // Not a primitive so must be an Object[]
        return Arrays.equals((Object[]) array1, (Object[]) array2);
    }
}
