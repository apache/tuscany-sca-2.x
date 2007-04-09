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
package org.apache.tuscany.core.loader;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.PropertyValue;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class StringParserPropertyFactoryTestCase extends TestCase {

    private <T> PropertyValue<T> mock(String value) {
        Document document = EasyMock.createMock(Document.class);
        Element element = EasyMock.createMock(Element.class);
        EasyMock.expect(document.getDocumentElement()).andReturn(element);
        EasyMock.expect(element.getTextContent()).andReturn(value);
        EasyMock.replay(document, element);
        return new PropertyValue<T>(null, document);
    }

    public void testInteger() throws Exception {

        StringParserPropertyFactory factory = new StringParserPropertyFactory();
        Property<Integer> property = new Property<Integer>();
        property.setJavaType(Integer.class);
        PropertyValue<Integer> propertyValue = mock("1");
        ObjectFactory<Integer> oFactory = factory.createObjectFactory(property, propertyValue);
        assertEquals(1, oFactory.getInstance().intValue());
    }

    public void testPrimitiveInt() throws Exception {
        StringParserPropertyFactory factory = new StringParserPropertyFactory();
        Property<Integer> property = new Property<Integer>();
        property.setJavaType(Integer.TYPE);
        PropertyValue<Integer> propertyValue = mock("1");
        ObjectFactory<Integer> oFactory = factory.createObjectFactory(property, propertyValue);
        assertEquals(1, oFactory.getInstance().intValue());
    }

    public void testString() throws Exception {
        StringParserPropertyFactory factory = new StringParserPropertyFactory();
        Property<String> property = new Property<String>();
        property.setJavaType(String.class);
        PropertyValue<String> propertyValue = mock("1");
        ObjectFactory<String> oFactory = factory.createObjectFactory(property, propertyValue);
        assertEquals("1", oFactory.getInstance());
    }

    public void testByteArray() throws Exception {
        StringParserPropertyFactory factory = new StringParserPropertyFactory();
        Property<byte[]> property = new Property<byte[]>();
        property.setJavaType(byte[].class);
        PropertyValue<byte[]> propertyValue = mock("1");
        ObjectFactory<byte[]> oFactory = factory.createObjectFactory(property, propertyValue);
        byte[] result = oFactory.getInstance();
        byte[] expected = "1".getBytes();
        for (int i = 0; i < result.length; i++) {
            byte b = result[i];
            if (b != expected[i]) {
                fail();
            }
        }
    }

    public void testBoolean() throws Exception {
        StringParserPropertyFactory factory = new StringParserPropertyFactory();
        Property<Boolean> property = new Property<Boolean>();
        property.setJavaType(Boolean.class);
        PropertyValue<Boolean> propertyValue = mock("true");
        ObjectFactory<Boolean> oFactory = factory.createObjectFactory(property, propertyValue);
        assertTrue(oFactory.getInstance());
    }

    public void testPrimitiveBoolean() throws Exception {
        StringParserPropertyFactory factory = new StringParserPropertyFactory();
        Property<Boolean> property = new Property<Boolean>();
        property.setJavaType(Boolean.TYPE);
        PropertyValue<Boolean> propertyValue = mock("true");
        ObjectFactory<Boolean> oFactory = factory.createObjectFactory(property, propertyValue);
        assertTrue(oFactory.getInstance());
    }

    public void testStringConstructor() throws Exception {
        StringParserPropertyFactory factory = new StringParserPropertyFactory();
        Property<Foo> property = new Property<Foo>();
        property.setJavaType(Foo.class);
        PropertyValue<Foo> propertyValue = mock("test");
        ObjectFactory<Foo> oFactory = factory.createObjectFactory(property, propertyValue);
        assertEquals("test", oFactory.getInstance().getFoo());
    }

    private static class Foo {
        private String foo;

        public Foo(String foo) {
            this.foo = foo;
        }

        public String getFoo() {
            return foo;
        }
    }

}
