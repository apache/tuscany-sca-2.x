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
package org.apache.tuscany.container.groovy;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.MissingResourceException;

import junit.framework.TestCase;
import static org.easymock.classextension.EasyMock.expect;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import org.easymock.classextension.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class ImplementationLoaderTestCase extends TestCase {
    private CompositeComponent parent;
    private XMLStreamReader reader;
    private DeploymentContext deploymentContext;
    private ClassLoader classLoader;
    private LoaderRegistry registry;
    private ImplementationLoader loader;

    public void testNoScriptAttribute() throws LoaderException, XMLStreamException {
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

    public void testNoScriptPresent() throws LoaderException, XMLStreamException {
        expect(reader.getAttributeValue(null, "script")).andReturn("foo.groovy");
        expect(deploymentContext.getClassLoader()).andReturn(classLoader);

        replay(reader);
        replay(deploymentContext);

        ImplementationLoader mockLoader = new ImplementationLoader(registry) {
            protected String loadSource(ClassLoader cl, String resource) throws LoaderException {
                assertSame(classLoader, cl);
                assertEquals("foo.groovy", resource);
                throw new MissingResourceException(resource);
            }
        };
        try {
            mockLoader.load(parent, null, reader, deploymentContext);
            fail();
        } catch (MissingResourceException e) {
            assertEquals("foo.groovy", e.getIdentifier());
        }
        verify(reader);
        verify(deploymentContext);
    }

    public void testLoadScript() throws LoaderException {
        String script = loader.loadSource(getClass().getClassLoader(),
            "org/apache/tuscany/container/groovy/mock/TestScript.groovy");
        assertEquals("Test Script", script);
    }

    protected void setUp() throws Exception {
        super.setUp();
        registry = EasyMock.createMock(LoaderRegistry.class);
        loader = new ImplementationLoader(registry);

        parent = EasyMock.createMock(CompositeComponent.class);
        reader = EasyMock.createMock(XMLStreamReader.class);
        deploymentContext = EasyMock.createMock(DeploymentContext.class);
        classLoader = EasyMock.createMock(ClassLoader.class);
    }
}
