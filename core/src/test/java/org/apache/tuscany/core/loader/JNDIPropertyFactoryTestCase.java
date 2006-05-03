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

import static javax.naming.Context.INITIAL_CONTEXT_FACTORY;
import java.io.StringReader;
import java.util.Hashtable;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.Binding;
import javax.naming.NameParser;
import javax.naming.spi.InitialContextFactory;

import junit.framework.TestCase;

import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.loader.impl.JNDIPropertyFactory;
import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.Property;
import org.apache.tuscany.model.assembly.impl.AssemblyFactoryImpl;

/**
 * @version $Rev$ $Date$
 */
@SuppressWarnings({"AccessOfSystemProperties"})
public class JNDIPropertyFactoryTestCase extends TestCase {
    private JNDIPropertyFactory factory;
    private XMLInputFactory xmlFactory;
    private Property property;
    private String oldICF;

    public void testLookup() throws XMLStreamException, ConfigurationLoadException {
        String instance = getInstance(String.class, "<foo>foo:/hello</foo>");
        assertEquals("Hello World", instance);
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
        factory = new JNDIPropertyFactory();
        xmlFactory = XMLInputFactory.newInstance();
        AssemblyFactory assemblyFactory = new AssemblyFactoryImpl();
        property = assemblyFactory.createProperty();

        oldICF = System.getProperty(INITIAL_CONTEXT_FACTORY);
        System.setProperty(INITIAL_CONTEXT_FACTORY, MockContextFactory.class.getName());
    }

    protected void tearDown() throws Exception {
        if (oldICF != null) {
            System.getProperty(INITIAL_CONTEXT_FACTORY, oldICF);
        }
        super.tearDown();
    }

    public static class MockContextFactory implements InitialContextFactory {
        public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
            return new MockContext();
        }
    }

    public static class MockContext implements Context {
        public Object lookup(String name) throws NamingException {
            if ("foo:/hello".equals(name)) {
                return "Hello World";
            }
            throw new AssertionError();
        }

        public Object lookup(Name name) throws NamingException {
            throw new UnsupportedOperationException();
        }

        public void bind(Name name, Object obj) throws NamingException {
            throw new UnsupportedOperationException();
        }

        public void bind(String name, Object obj) throws NamingException {
            throw new UnsupportedOperationException();
        }

        public void rebind(Name name, Object obj) throws NamingException {
            throw new UnsupportedOperationException();
        }

        public void rebind(String name, Object obj) throws NamingException {
            throw new UnsupportedOperationException();
        }

        public void unbind(Name name) throws NamingException {
            throw new UnsupportedOperationException();
        }

        public void unbind(String name) throws NamingException {
            throw new UnsupportedOperationException();
        }

        public void rename(Name oldName, Name newName) throws NamingException {
            throw new UnsupportedOperationException();
        }

        public void rename(String oldName, String newName) throws NamingException {
            throw new UnsupportedOperationException();
        }

        public NamingEnumeration<NameClassPair> list(Name name) throws NamingException {
            throw new UnsupportedOperationException();
        }

        public NamingEnumeration<NameClassPair> list(String name) throws NamingException {
            throw new UnsupportedOperationException();
        }

        public NamingEnumeration<Binding> listBindings(Name name) throws NamingException {
            throw new UnsupportedOperationException();
        }

        public NamingEnumeration<Binding> listBindings(String name) throws NamingException {
            throw new UnsupportedOperationException();
        }

        public void destroySubcontext(Name name) throws NamingException {
            throw new UnsupportedOperationException();
        }

        public void destroySubcontext(String name) throws NamingException {
            throw new UnsupportedOperationException();
        }

        public Context createSubcontext(Name name) throws NamingException {
            throw new UnsupportedOperationException();
        }

        public Context createSubcontext(String name) throws NamingException {
            throw new UnsupportedOperationException();
        }

        public Object lookupLink(Name name) throws NamingException {
            throw new UnsupportedOperationException();
        }

        public Object lookupLink(String name) throws NamingException {
            throw new UnsupportedOperationException();
        }

        public NameParser getNameParser(Name name) throws NamingException {
            throw new UnsupportedOperationException();
        }

        public NameParser getNameParser(String name) throws NamingException {
            throw new UnsupportedOperationException();
        }

        public Name composeName(Name name, Name prefix) throws NamingException {
            throw new UnsupportedOperationException();
        }

        public String composeName(String name, String prefix) throws NamingException {
            throw new UnsupportedOperationException();
        }

        public Object addToEnvironment(String propName, Object propVal) throws NamingException {
            throw new UnsupportedOperationException();
        }

        public Object removeFromEnvironment(String propName) throws NamingException {
            throw new UnsupportedOperationException();
        }

        public Hashtable<?, ?> getEnvironment() throws NamingException {
            throw new UnsupportedOperationException();
        }

        public void close() throws NamingException {
            throw new UnsupportedOperationException();
        }

        public String getNameInNamespace() throws NamingException {
            throw new UnsupportedOperationException();
        }
    }
}
