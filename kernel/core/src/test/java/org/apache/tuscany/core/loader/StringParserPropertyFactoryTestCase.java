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

import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.model.Property;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class StringParserPropertyFactoryTestCase extends TestCase {


    public void testInteger() throws Exception {
        XMLStreamReader reader = EasyMock.createMock(XMLStreamReader.class);
        EasyMock.expect(reader.getElementText()).andReturn("1");
        EasyMock.replay(reader);
        StringParserPropertyFactory factory = new StringParserPropertyFactory();
        Property<Integer> property = new Property<Integer>();
        property.setJavaType(Integer.class);
        ObjectFactory<Integer> oFactory = factory.createObjectFactory(reader, property);
        assertEquals(1, oFactory.getInstance().intValue());
        EasyMock.verify(reader);
    }

    public void testPrimitiveInt() throws Exception {
        XMLStreamReader reader = EasyMock.createMock(XMLStreamReader.class);
        EasyMock.expect(reader.getElementText()).andReturn("1");
        EasyMock.replay(reader);
        StringParserPropertyFactory factory = new StringParserPropertyFactory();
        Property<Integer> property = new Property<Integer>();
        property.setJavaType(Integer.TYPE);
        ObjectFactory<Integer> oFactory = factory.createObjectFactory(reader, property);
        assertEquals(1, oFactory.getInstance().intValue());
        EasyMock.verify(reader);
    }

    public void testString() throws Exception {
        XMLStreamReader reader = EasyMock.createMock(XMLStreamReader.class);
        EasyMock.expect(reader.getElementText()).andReturn("1");
        EasyMock.replay(reader);
        StringParserPropertyFactory factory = new StringParserPropertyFactory();
        Property<String> property = new Property<String>();
        property.setJavaType(String.class);
        ObjectFactory<String> oFactory = factory.createObjectFactory(reader, property);
        assertEquals("1", oFactory.getInstance());
        EasyMock.verify(reader);
    }

    public void testByteArray() throws Exception {
        XMLStreamReader reader = EasyMock.createMock(XMLStreamReader.class);
        EasyMock.expect(reader.getElementText()).andReturn("1");
        EasyMock.replay(reader);
        StringParserPropertyFactory factory = new StringParserPropertyFactory();
        Property<byte[]> property = new Property<byte[]>();
        property.setJavaType(byte[].class);
        ObjectFactory<byte[]> oFactory = factory.createObjectFactory(reader, property);
        byte[] result = oFactory.getInstance();
        byte[] expected = "1".getBytes();
        for (int i = 0; i < result.length; i++) {
            byte b = result[i];
            if (b != expected[i]) {
                fail();
            }
        }
        EasyMock.verify(reader);
    }

    public void testBoolean() throws Exception {
        XMLStreamReader reader = EasyMock.createMock(XMLStreamReader.class);
        EasyMock.expect(reader.getElementText()).andReturn("true");
        EasyMock.replay(reader);
        StringParserPropertyFactory factory = new StringParserPropertyFactory();
        Property<Boolean> property = new Property<Boolean>();
        property.setJavaType(Boolean.class);
        ObjectFactory<Boolean> oFactory = factory.createObjectFactory(reader, property);
        assertTrue(oFactory.getInstance());
        EasyMock.verify(reader);
    }

    public void testPrimitiveBoolean() throws Exception {
        XMLStreamReader reader = EasyMock.createMock(XMLStreamReader.class);
        EasyMock.expect(reader.getElementText()).andReturn("true");
        EasyMock.replay(reader);
        StringParserPropertyFactory factory = new StringParserPropertyFactory();
        Property<Boolean> property = new Property<Boolean>();
        property.setJavaType(Boolean.TYPE);
        ObjectFactory<Boolean> oFactory = factory.createObjectFactory(reader, property);
        assertTrue(oFactory.getInstance());
        EasyMock.verify(reader);
    }

    public void testStringConstructor() throws Exception {
        XMLStreamReader reader = EasyMock.createMock(XMLStreamReader.class);
        EasyMock.expect(reader.getElementText()).andReturn("test");
        EasyMock.replay(reader);
        StringParserPropertyFactory factory = new StringParserPropertyFactory();
        Property<Foo> property = new Property<Foo>();
        property.setJavaType(Foo.class);
        ObjectFactory<Foo> oFactory = factory.createObjectFactory(reader, property);
        assertEquals("test", oFactory.getInstance().getFoo());
        EasyMock.verify(reader);
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
