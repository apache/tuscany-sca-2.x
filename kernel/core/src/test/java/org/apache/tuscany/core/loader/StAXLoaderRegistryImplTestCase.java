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
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.StAXElementLoader;
import org.apache.tuscany.spi.loader.UnrecognizedElementException;
import org.apache.tuscany.spi.model.ModelObject;

import junit.framework.TestCase;
import org.apache.tuscany.core.deployer.RootDeploymentContext;
import static org.easymock.EasyMock.isNull;
import org.easymock.classextension.EasyMock;

/**
 * Verifies the default loader registry
 *
 * @version $Rev$ $Date$
 */
public class StAXLoaderRegistryImplTestCase extends TestCase {
    private LoaderRegistryImpl registry;
    private QName name;
    private LoaderRegistryImpl.Monitor mockMonitor;
    private StAXElementLoader<ModelObject> mockLoader;
    private XMLStreamReader mockReader;
    private DeploymentContext deploymentContext;
    private ModelObject modelObject;

    public void testLoaderRegistration() {
        mockMonitor.registeringLoader(EasyMock.eq(name));
        EasyMock.replay(mockMonitor);
        registry.registerLoader(name, mockLoader);
        EasyMock.verify(mockMonitor);
    }

    public void testLoaderUnregistration() {
        mockMonitor.unregisteringLoader(EasyMock.eq(name));
        EasyMock.replay(mockMonitor);
        registry.unregisterLoader(name, (StAXElementLoader<ModelObject>) mockLoader);
        EasyMock.verify(mockMonitor);
    }

    public void testSuccessfulDispatch() throws LoaderException, XMLStreamException {
        EasyMock.expect(mockReader.getName()).andReturn(name);
        EasyMock.replay(mockReader);
        mockMonitor.registeringLoader(EasyMock.eq(name));
        mockMonitor.elementLoad(EasyMock.eq(name));
        EasyMock.replay(mockMonitor);
        EasyMock.expect(mockLoader.load(
            (ModelObject) isNull(),
            EasyMock.eq(mockReader),
            EasyMock.eq(deploymentContext))).andReturn(modelObject);
        EasyMock.replay(mockLoader);
        registry.registerLoader(name, (StAXElementLoader<ModelObject>) mockLoader);
        Component parent = EasyMock.createNiceMock(Component.class);
        assertSame(modelObject, registry.load(null, mockReader, deploymentContext));
        EasyMock.verify(mockLoader);
        EasyMock.verify(mockMonitor);
        EasyMock.verify(mockReader);

    }

    public void testUnsuccessfulDispatch() throws LoaderException, XMLStreamException {
        EasyMock.expect(mockReader.getName()).andReturn(name);
        EasyMock.replay(mockReader);
        mockMonitor.elementLoad(EasyMock.eq(name));
        EasyMock.replay(mockMonitor);
        try {
            registry.load(null, mockReader, deploymentContext);
            fail();
        } catch (UnrecognizedElementException e) {
            assertSame(name, e.getElement());
        }
        EasyMock.verify(mockReader);
        EasyMock.verify(mockMonitor);
    }

    public void testPregivenModelObject() throws Exception {
        EasyMock.expect(mockReader.getName()).andReturn(name);
        EasyMock.replay(mockReader);
        mockMonitor.registeringLoader(EasyMock.eq(name));
        mockMonitor.elementLoad(EasyMock.eq(name));
        EasyMock.replay(mockMonitor);
        EasyMock.expect(mockLoader.load(
            EasyMock.eq(modelObject),
            EasyMock.eq(mockReader),
            EasyMock.eq(deploymentContext))).andReturn(modelObject);
        EasyMock.replay(mockLoader);
        registry.registerLoader(name, (StAXElementLoader<ModelObject>) mockLoader);
        Component parent = EasyMock.createNiceMock(Component.class);
        assertSame(modelObject, registry.load(modelObject, mockReader, deploymentContext));
        EasyMock.verify(mockLoader);
    }

    @SuppressWarnings("unchecked")
    protected void setUp() throws Exception {
        super.setUp();
        name = new QName("http://mock", "test");
        deploymentContext = new RootDeploymentContext(null, null, null, null, null, false);
        mockMonitor = EasyMock.createMock(LoaderRegistryImpl.Monitor.class);
        registry = new LoaderRegistryImpl(mockMonitor);

        mockLoader = EasyMock.createMock(StAXElementLoader.class);
        mockReader = EasyMock.createMock(XMLStreamReader.class);
        modelObject = new ModelObject() {
        };
    }

}
