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

import java.util.List;
import java.util.ArrayList;
import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.Location;

import junit.framework.TestCase;

import org.apache.tuscany.core.loader.impl.StAXLoaderRegistryImpl;
import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.model.assembly.AssemblyObject;
import org.apache.tuscany.model.assembly.AssemblyContext;
import org.apache.tuscany.model.assembly.AssemblyInitializationException;
import org.apache.tuscany.model.assembly.AssemblyVisitor;
import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.common.resource.impl.ResourceLoaderImpl;

/**
 * @version $Rev$ $Date$
 */
public class StAXLoaderRegistryTestCase extends TestCase {
    private StAXLoaderRegistryImpl registry;
    private MockElementLoader loader;
    private MockObject mockObject;
    private MockReader reader;
    private MockMonitor monitor;
    private QName qname;
    private ResourceLoaderImpl rl;

    public void testRegistrationEvents() throws XMLStreamException, ConfigurationLoadException {
        reader.name = qname;
        registry.registerLoader(qname, loader);
        assertTrue(monitor.registered.contains(qname));
        assertEquals(1, monitor.registered.size());
        assertTrue(monitor.unregistered.isEmpty());
        assertTrue(monitor.loading.isEmpty());

        registry.unregisterLoader(qname, loader);
        assertTrue(monitor.registered.contains(qname));
        assertEquals(1, monitor.registered.size());
        assertTrue(monitor.unregistered.contains(qname));
        assertEquals(1, monitor.unregistered.size());
        assertTrue(monitor.loading.isEmpty());
    }

    public void testSuccessfulLoad() throws XMLStreamException, ConfigurationLoadException {
        reader.name = qname;
        registry.registerLoader(qname, loader);
        assertSame(mockObject, registry.load(reader, rl));
        assertEquals(1, monitor.loading.size());
        assertTrue(monitor.loading.contains(qname));
    }

    public void testFailedLoad() throws XMLStreamException, ConfigurationLoadException {
        registry.registerLoader(qname, loader);
        reader.name = new QName("foo");
        try {
            registry.load(reader, rl);
            fail();
        } catch (ConfigurationLoadException e) {
            assertEquals(1, monitor.loading.size());
            assertTrue(monitor.loading.contains(reader.name));
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        qname = new QName("test");
        monitor = new MockMonitor();
        registry = new StAXLoaderRegistryImpl();
        registry.setMonitor(monitor);
        mockObject = new MockObject();
        loader = new MockElementLoader();
        reader = new MockReader();
        rl = new ResourceLoaderImpl(getClass().getClassLoader());
    }

    public static class MockMonitor implements StAXLoaderRegistryImpl.Monitor {
        private List<QName> registered = new ArrayList<QName>();
        private List<QName> unregistered = new ArrayList<QName>();
        private List<QName> loading = new ArrayList<QName>();

        public void registeringLoader(QName xmlType) {
            registered.add(xmlType);
        }

        public void unregisteringLoader(QName xmlType) {
            unregistered.add(xmlType);
        }

        public void elementLoad(QName xmlType) {
            loading.add(xmlType);
        }
    }

    @SuppressWarnings({"NonStaticInnerClassInSecureContext"})
    public class MockElementLoader implements StAXElementLoader {
        public AssemblyObject load(XMLStreamReader reader, ResourceLoader resourceLoader) throws XMLStreamException, ConfigurationLoadException {
            assertEquals(qname, reader.getName());
            assertSame(rl, resourceLoader);
            return mockObject;
        }
    }

    public static class MockObject implements AssemblyObject {
        public void initialize(AssemblyContext modelContext) throws AssemblyInitializationException {
            throw new UnsupportedOperationException();
        }

        public void freeze() {
            throw new UnsupportedOperationException();
        }

        public boolean accept(AssemblyVisitor visitor) {
            throw new UnsupportedOperationException();
        }
    }

    public static class MockReader implements XMLStreamReader {
        private QName name;

        public QName getName() {
            return name;
        }

        public Object getProperty(String name) throws IllegalArgumentException {
            throw new UnsupportedOperationException();
        }

        public int next() throws XMLStreamException {
            throw new UnsupportedOperationException();
        }

        public void require(int i, String name, String name1) throws XMLStreamException {
            throw new UnsupportedOperationException();
        }

        public String getElementText() throws XMLStreamException {
            throw new UnsupportedOperationException();
        }

        public int nextTag() throws XMLStreamException {
            throw new UnsupportedOperationException();
        }

        public boolean hasNext() throws XMLStreamException {
            throw new UnsupportedOperationException();
        }

        public void close() throws XMLStreamException {
            throw new UnsupportedOperationException();
        }

        public String getNamespaceURI(String name) {
            throw new UnsupportedOperationException();
        }

        public boolean isStartElement() {
            throw new UnsupportedOperationException();
        }

        public boolean isEndElement() {
            throw new UnsupportedOperationException();
        }

        public boolean isCharacters() {
            throw new UnsupportedOperationException();
        }

        public boolean isWhiteSpace() {
            throw new UnsupportedOperationException();
        }

        public String getAttributeValue(String name, String name1) {
            throw new UnsupportedOperationException();
        }

        public int getAttributeCount() {
            throw new UnsupportedOperationException();
        }

        public QName getAttributeName(int i) {
            throw new UnsupportedOperationException();
        }

        public String getAttributeNamespace(int i) {
            throw new UnsupportedOperationException();
        }

        public String getAttributeLocalName(int i) {
            throw new UnsupportedOperationException();
        }

        public String getAttributePrefix(int i) {
            throw new UnsupportedOperationException();
        }

        public String getAttributeType(int i) {
            throw new UnsupportedOperationException();
        }

        public String getAttributeValue(int i) {
            throw new UnsupportedOperationException();
        }

        public boolean isAttributeSpecified(int i) {
            throw new UnsupportedOperationException();
        }

        public int getNamespaceCount() {
            throw new UnsupportedOperationException();
        }

        public String getNamespacePrefix(int i) {
            throw new UnsupportedOperationException();
        }

        public String getNamespaceURI(int i) {
            throw new UnsupportedOperationException();
        }

        public NamespaceContext getNamespaceContext() {
            throw new UnsupportedOperationException();
        }

        public int getEventType() {
            throw new UnsupportedOperationException();
        }

        public String getText() {
            throw new UnsupportedOperationException();
        }

        public char[] getTextCharacters() {
            throw new UnsupportedOperationException();
        }

        public int getTextCharacters(int i, char[] chars, int i1, int i2) throws XMLStreamException {
            throw new UnsupportedOperationException();
        }

        public int getTextStart() {
            throw new UnsupportedOperationException();
        }

        public int getTextLength() {
            throw new UnsupportedOperationException();
        }

        public String getEncoding() {
            throw new UnsupportedOperationException();
        }

        public boolean hasText() {
            throw new UnsupportedOperationException();
        }

        public Location getLocation() {
            throw new UnsupportedOperationException();
        }

        public String getLocalName() {
            throw new UnsupportedOperationException();
        }

        public boolean hasName() {
            throw new UnsupportedOperationException();
        }

        public String getNamespaceURI() {
            throw new UnsupportedOperationException();
        }

        public String getPrefix() {
            throw new UnsupportedOperationException();
        }

        public String getVersion() {
            throw new UnsupportedOperationException();
        }

        public boolean isStandalone() {
            throw new UnsupportedOperationException();
        }

        public boolean standaloneSet() {
            throw new UnsupportedOperationException();
        }

        public String getCharacterEncodingScheme() {
            throw new UnsupportedOperationException();
        }

        public String getPITarget() {
            throw new UnsupportedOperationException();
        }

        public String getPIData() {
            throw new UnsupportedOperationException();
        }
    }
}
