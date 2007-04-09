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

import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.UnrecognizedElementException;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.apache.tuscany.core.implementation.composite.Dependency;

/**
 * @version $Rev$ $Date$
 */
public class DependencyLoaderTestCase extends TestCase {
    private static final String NS = "http://tuscany.apache.org/xmlns/sca/2.0-alpha";
    private static final QName DEPENDENCY = new QName(NS, "dependency");
    private static final QName GROUP = new QName(NS, "group");
    private static final QName NAME = new QName(NS, "name");
    private static final QName VERSION = new QName(NS, "version");
    private static final QName CLASSIFIER = new QName(NS, "classifier");
    private static final QName TYPE = new QName(NS, "type");

    public void testLoad() throws Exception {
        LoaderRegistry registry = EasyMock.createNiceMock(LoaderRegistry.class);
        EasyMock.replay(registry);
        XMLStreamReader reader = EasyMock.createMock(XMLStreamReader.class);
        EasyMock.expect(reader.nextTag()).andReturn(XMLStreamConstants.START_ELEMENT);
        EasyMock.expect(reader.getName()).andReturn(GROUP);
        EasyMock.expect(reader.getElementText()).andReturn("group");
        EasyMock.expect(reader.nextTag()).andReturn(XMLStreamConstants.START_ELEMENT);
        EasyMock.expect(reader.getName()).andReturn(NAME);
        EasyMock.expect(reader.getElementText()).andReturn("name");
        EasyMock.expect(reader.nextTag()).andReturn(XMLStreamConstants.START_ELEMENT);
        EasyMock.expect(reader.getName()).andReturn(VERSION);
        EasyMock.expect(reader.getElementText()).andReturn("1");
        EasyMock.expect(reader.nextTag()).andReturn(XMLStreamConstants.START_ELEMENT);
        EasyMock.expect(reader.getName()).andReturn(CLASSIFIER);
        EasyMock.expect(reader.getElementText()).andReturn("classifier");
        EasyMock.expect(reader.nextTag()).andReturn(XMLStreamConstants.START_ELEMENT);
        EasyMock.expect(reader.getName()).andReturn(TYPE);
        EasyMock.expect(reader.getElementText()).andReturn("type");
        EasyMock.expect(reader.nextTag()).andReturn(XMLStreamConstants.END_ELEMENT);
        EasyMock.replay(reader);
        DependencyLoader loader = new DependencyLoader(registry);
        Dependency dependency = loader.load(null, reader, null);
        assertEquals("group", dependency.getArtifact().getGroup());
        assertEquals("name", dependency.getArtifact().getName());
        assertEquals("1", dependency.getArtifact().getVersion());
        assertEquals("classifier", dependency.getArtifact().getClassifier());
        assertEquals("type", dependency.getArtifact().getType());
    }

    public void testLoaderRegister() {
        LoaderRegistry registry = EasyMock.createMock(LoaderRegistry.class);
        registry.registerLoader(EasyMock.eq(DEPENDENCY), EasyMock.isA(DependencyLoader.class));
        EasyMock.replay(registry);
        DependencyLoader loader = new DependencyLoader(registry);
        loader.start();
        EasyMock.verify(registry);
    }

    public void testUnrecognizedElement() throws Exception {
        LoaderRegistry registry = EasyMock.createNiceMock(LoaderRegistry.class);
        EasyMock.replay(registry);
        XMLStreamReader reader = EasyMock.createMock(XMLStreamReader.class);
        EasyMock.expect(reader.nextTag()).andReturn(XMLStreamConstants.START_ELEMENT);
        EasyMock.expect(reader.getName()).andReturn(new QName("foo", "bar"));
        EasyMock.expect(reader.getElementText()).andReturn("foo");
        EasyMock.expect(reader.getLocation()).andReturn(new MockLocation());
        EasyMock.replay(reader);
        DependencyLoader loader = new DependencyLoader(registry);
        try {
            loader.load(null, reader, null);
            fail();
        } catch (UnrecognizedElementException e) {
            // expected
        }
    }

    private class MockLocation implements Location {

        public int getLineNumber() {
            return 0;
        }

        public int getColumnNumber() {
            return 0;
        }

        public int getCharacterOffset() {
            return 0;
        }

        public String getPublicId() {
            return null;
        }

        public String getSystemId() {
            return null;
        }
    }
}
