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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import org.apache.tuscany.core.deployer.RootDeploymentContext;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.StAXElementLoader;
import org.apache.tuscany.spi.loader.UnrecognizedElementException;
import org.apache.tuscany.spi.model.ModelObject;

/**
 * Verifies the default loader registry
 *
 * @version $Rev$ $Date$
 */
@SuppressWarnings({"CastToIncompatibleInterface"})
public class StAXLoaderRegistryImplTestCase extends MockObjectTestCase {
    private LoaderRegistryImpl registry;
    private QName name;
    private Mock mockMonitor;
    private Mock mockLoader;
    private Mock mockReader;
    private DeploymentContext deploymentContext;
    private ModelObject modelObject;

    public void testLoaderRegistration() {
        mockMonitor.expects(once()).method("registeringLoader").with(eq(name));
        registry.registerLoader(name, (StAXElementLoader<ModelObject>) mockLoader.proxy());
    }

    public void testLoaderUnregistration() {
        mockMonitor.expects(once()).method("unregisteringLoader").with(eq(name));
        registry.unregisterLoader(name, (StAXElementLoader<ModelObject>) mockLoader.proxy());
    }

    public void testSuccessfulDispatch() throws LoaderException, XMLStreamException {
        mockReader.expects(once()).method("getName").will(returnValue(name));
        mockMonitor.expects(once()).method("registeringLoader").with(eq(name));
        mockMonitor.expects(once()).method("elementLoad").with(eq(name));
        mockLoader.expects(once()).method("load").with(eq(null), eq(mockReader.proxy()), eq(deploymentContext))
            .will(returnValue(modelObject));

        registry.registerLoader(name, (StAXElementLoader<ModelObject>) mockLoader.proxy());
        assertSame(modelObject, registry.load(null, (XMLStreamReader) mockReader.proxy(), deploymentContext));
    }

    public void testUnsuccessfulDispatch() throws LoaderException, XMLStreamException {
        mockReader.expects(once()).method("getName").will(returnValue(name));
        mockMonitor.expects(once()).method("elementLoad").with(eq(name));

        try {
            registry.load(null, (XMLStreamReader) mockReader.proxy(), deploymentContext);
            fail();
        } catch (UnrecognizedElementException e) {
            assertSame(name, e.getElement());
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        name = new QName("http://mock", "test");
        deploymentContext = new RootDeploymentContext(null, null, null);
        registry = new LoaderRegistryImpl();
        mockMonitor = mock(LoaderRegistryImpl.Monitor.class);
        registry.setMonitor((LoaderRegistryImpl.Monitor) mockMonitor.proxy());

        mockLoader = mock(StAXElementLoader.class);
        mockReader = mock(XMLStreamReader.class);
        modelObject = new ModelObject() {
        };
    }
}
