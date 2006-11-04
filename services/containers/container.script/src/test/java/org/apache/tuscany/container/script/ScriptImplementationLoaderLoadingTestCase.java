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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.MissingResourceException;
import org.apache.tuscany.spi.model.ModelObject;

import junit.framework.TestCase;
import static org.easymock.classextension.EasyMock.createMock;

/**
 * 
 */
public class ScriptImplementationLoaderLoadingTestCase extends TestCase {

    private LoaderRegistry registry;

    private ScriptImplementationLoader loader;

    public void testLoadSource() throws LoaderException {
        String script =
            loader.loadSource(getClass().getClassLoader(), "org/apache/tuscany/container/script/helper/foo.mock");
        assertTrue(script.startsWith("hello"));
    }

    public void testLoadSourceMissingResource() throws LoaderException {
        try {
            loader.loadSource(getClass().getClassLoader(), "doesnt.exist");
            fail();
        } catch (MissingResourceException e) {
            // expected
        }
    }

    public void testGetXMLType() throws LoaderException {
        assertEquals("http://foo", loader.getXMLType().getNamespaceURI());
        assertEquals("bar", loader.getXMLType().getLocalPart());
    }

    protected void setUp() throws Exception {
        super.setUp();
        registry = createMock(LoaderRegistry.class);
        loader = new ScriptImplementationLoader(registry) {
            public QName getXMLType() {
                return new QName("http://foo", "bar");
            }

            public ScriptImplementation load(CompositeComponent arg0, ModelObject arg1, XMLStreamReader arg2,
                                             DeploymentContext arg3) throws XMLStreamException, LoaderException {
                return null;
            }
        };
    }
}
