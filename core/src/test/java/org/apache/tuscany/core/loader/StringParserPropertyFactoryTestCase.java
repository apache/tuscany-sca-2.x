/**
 *
 * Copyright 2006 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.loader;

import java.io.StringReader;
import java.net.URI;
import java.util.Arrays;
import java.beans.PropertyEditorManager;
import java.beans.PropertyEditorSupport;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.tuscany.core.builder.ObjectFactory;
import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.loader.impl.StringParserPropertyFactory;
import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.Property;
import org.apache.tuscany.model.assembly.impl.AssemblyFactoryImpl;

/**
 * @version $Rev$ $Date$
 */
public class StringParserPropertyFactoryTestCase extends TestCase {
    private StringParserPropertyFactory factory;
    private XMLInputFactory xmlFactory;
    private Property property;

    public void testSimpleString() throws XMLStreamException, ConfigurationLoadException {
        String instance = getInstance(String.class, "<foo>Hello World</foo>");
        assertEquals("Hello World", instance);
    }

    public void testByteArray() throws XMLStreamException, ConfigurationLoadException {
        byte[] instance = getInstance(byte[].class, "<foo>01020304</foo>");
        assertTrue(Arrays.equals(new byte[]{1, 2, 3, 4}, instance));
    }

    public void testInteger() throws XMLStreamException, ConfigurationLoadException {
        Integer instance = getInstance(Integer.class, "<foo>1234</foo>");
        assertEquals(Integer.valueOf(1234), instance);
    }

    public void testInt() throws XMLStreamException, ConfigurationLoadException {
        int instance = getInstance(Integer.TYPE, "<foo>1234</foo>");
        assertEquals(1234, instance);
    }

    public void testBoolean() throws XMLStreamException, ConfigurationLoadException {
        Boolean instance = getInstance(Boolean.class, "<foo>true</foo>");
        assertSame(Boolean.TRUE, instance);
    }

    public void testConstructor() throws XMLStreamException, ConfigurationLoadException {
        // java.net.URI has a ctr that takes a String
        URI instance = getInstance(URI.class, "<foo>http://www.apache.org</foo>");
        assertEquals(URI.create("http://www.apache.org"), instance);
    }

    public void testPropertyEditor() throws XMLStreamException, ConfigurationLoadException {
        // register a property editor for java.lang.Class
        PropertyEditorManager.registerEditor(Class.class, ClassEditor.class);
        try {
            Class<?> instance = getInstance(Class.class, "<foo>java.lang.Integer</foo>");
            assertEquals(Integer.class, instance);
        } finally{
            PropertyEditorManager.registerEditor(Class.class, null);
        }
    }

    private <T> T getInstance(Class<T> type, String xml) throws XMLStreamException, ConfigurationLoadException {
        property.setType(type);
        XMLStreamReader reader = xmlFactory.createXMLStreamReader(new StringReader(xml));
        reader.next();
        ObjectFactory<T> objectFactory = (ObjectFactory<T>) factory.createObjectFactory(reader, property);
        return objectFactory.getInstance();
    }

    protected void setUp() throws Exception {
        super.setUp();
        factory = new StringParserPropertyFactory();
        xmlFactory = XMLInputFactory.newInstance();
        AssemblyFactory assemblyFactory = new AssemblyFactoryImpl();
        property = assemblyFactory.createProperty();
    }

    public static class ClassEditor extends PropertyEditorSupport {
        public void setAsText(String text) throws IllegalArgumentException {
            try {
                setValue(Class.forName(text));
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(text);
            }
        }
    }
}
