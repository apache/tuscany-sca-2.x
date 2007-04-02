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
package org.apache.tuscany.container.script;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.osoa.sca.Version.XML_NAMESPACE_1_0;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.MissingResourceException;

/**
 * 
 */
public class ScriptImplementationLoaderTestCase extends TestCase {
    private CompositeComponent parent;

    private XMLStreamReader reader;

    private DeploymentContext deploymentContext;

    private ClassLoader classLoader;

    private LoaderRegistry registry;

    private ScriptImplementationLoader loader;

    public void testLoadNoScriptAttribute() throws LoaderException, XMLStreamException {
        expect(reader.getAttributeValue(null, "script")).andReturn(null);
        replay(reader);
        replay(deploymentContext);

        try {
            loader.load(parent, null, reader, deploymentContext);
            fail();
        } catch (MissingResourceException e) {
            // ok
        }
        verify(reader);
        verify(deploymentContext);
    }

    public void testLoad() throws LoaderException, XMLStreamException {
        expect(reader.getAttributeValue(null, "script")).andReturn("foo.mock");
        expect(reader.getAttributeValue(null, "class")).andReturn(null);
        expect(reader.next()).andReturn(XMLStreamConstants.END_ELEMENT);
        expect(deploymentContext.getClassLoader()).andReturn(classLoader);

        replay(reader);
        replay(deploymentContext);

        ScriptImplementationLoader mockLoader = new ScriptImplementationLoader(registry) {
            protected String loadSource(ClassLoader cl, String resource) throws LoaderException {
                assertSame(classLoader, cl);
                assertEquals("foo.mock", resource);
                return "bar";
            }
        };
        mockLoader.load(parent, null, reader, deploymentContext);
        verify(reader);
        verify(deploymentContext);
    }

    public void testLoadNoScriptPresent() throws LoaderException, XMLStreamException {
        expect(reader.getAttributeValue(null, "script")).andReturn("foo.py");
        expect(reader.getAttributeValue(null, "class")).andReturn(null);
        expect(reader.next()).andReturn(XMLStreamConstants.END_ELEMENT);
        expect(deploymentContext.getClassLoader()).andReturn(classLoader);

        replay(reader);
        replay(deploymentContext);

        ScriptImplementationLoader mockLoader = new ScriptImplementationLoader(registry) {
            protected String loadSource(ClassLoader cl, String resource) throws LoaderException {
                assertSame(classLoader, cl);
                assertEquals("foo.py", resource);
                throw new MissingResourceException(resource);
            }
        };
        try {
            mockLoader.load(parent, null, reader, deploymentContext);
            fail();
        } catch (MissingResourceException e) {
            assertEquals("foo.py", e.getMessage());
        }
        verify(reader);
        verify(deploymentContext);
    }

    public void testGetXMLType() throws LoaderException {
        assertEquals(XML_NAMESPACE_1_0, loader.getXMLType().getNamespaceURI());
        assertEquals("implementation.script", loader.getXMLType().getLocalPart());
    }

    protected void setUp() throws Exception {
        super.setUp();
        registry = createMock(LoaderRegistry.class);
        loader = new ScriptImplementationLoader(registry);

        parent = createMock(CompositeComponent.class);
        reader = createMock(XMLStreamReader.class);
        deploymentContext = createMock(DeploymentContext.class);
        classLoader = createMock(ClassLoader.class);
    }
}
