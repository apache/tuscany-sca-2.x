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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.Version;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.model.BindingDefinition;
import org.apache.tuscany.spi.model.ComponentType;
import org.apache.tuscany.spi.model.ModelObject;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.model.ServiceDefinition;

import junit.framework.TestCase;
import org.apache.tuscany.core.deployer.RootDeploymentContext;
import org.easymock.EasyMock;

/**
 * Verifies loading of a reference definition from an XML-based assembly
 *
 * @version $Rev$ $Date$
 */
public class ReferenceLoaderTestCase extends TestCase {
    private static final QName REFERENCE = new QName(Version.XML_NAMESPACE_1_0, "reference");
    private static final String PARENT_NAME = "parent";
    private ReferenceLoader loader;
    private DeploymentContext deploymentContext;
    private XMLStreamReader mockReader;
    private LoaderRegistry mockRegistry;
    private CompositeComponent parent;
    private DeploymentContext ctx;

    public void testWithNoInterface() throws LoaderException, XMLStreamException {
        String name = "referenceDefinition";
        EasyMock.expect(mockReader.getName()).andReturn(REFERENCE).anyTimes();
        EasyMock.expect(mockReader.getAttributeValue(null, "name")).andReturn(name);
        EasyMock.expect(mockReader.getAttributeValue(null, "multiplicity")).andReturn("0..1");
        EasyMock.expect(mockReader.next()).andReturn(XMLStreamConstants.END_ELEMENT);
        EasyMock.expect(mockReader.getName()).andReturn(REFERENCE).anyTimes();
        EasyMock.replay(mockReader);
        ReferenceDefinition referenceDefinition = loader.load(parent, null, mockReader, ctx);
        assertNotNull(referenceDefinition);
        assertEquals(PARENT_NAME + "#" + name, referenceDefinition.getUri().toString());
    }

    public void testComponentTypeService() throws LoaderException, XMLStreamException {
        String name = "reference";
        EasyMock.expect(mockReader.getName()).andReturn(REFERENCE).anyTimes();
        EasyMock.expect(mockReader.getAttributeValue(null, "name")).andReturn(name);
        EasyMock.expect(mockReader.getAttributeValue(null, "multiplicity")).andReturn("0..1");
        EasyMock.expect(mockReader.next()).andReturn(XMLStreamConstants.END_ELEMENT);
        EasyMock.expect(mockReader.getName()).andReturn(REFERENCE).anyTimes();
        EasyMock.replay(mockReader);
        ComponentType<ServiceDefinition, ReferenceDefinition, Property<?>> type =
            new ComponentType<ServiceDefinition, ReferenceDefinition, Property<?>>();
        ReferenceDefinition referenceDefinition = loader.load(parent, type, mockReader, ctx);
        assertTrue(ReferenceDefinition.class.equals(referenceDefinition.getClass()));
    }

    public void testMultipleBindings() throws LoaderException, XMLStreamException {
        String name = "referenceDefinition";
        EasyMock.expect(mockReader.getName()).andReturn(REFERENCE).anyTimes();
        EasyMock.expect(mockReader.getAttributeValue(null, "name")).andReturn(name);
        EasyMock.expect(mockReader.getAttributeValue(null, "multiplicity")).andReturn("0..1");
        EasyMock.expect(mockReader.next()).andReturn(XMLStreamConstants.START_ELEMENT).times(2);
        EasyMock.expect(mockReader.next()).andReturn(XMLStreamConstants.END_ELEMENT);
        EasyMock.expect(mockReader.getName()).andReturn(REFERENCE).anyTimes();
        EasyMock.replay(mockReader);

        BindingDefinition binding = new BindingDefinition() {
        };
        EasyMock.expect(mockRegistry.load(EasyMock.eq(parent),
            (ModelObject) EasyMock.isNull(),
            EasyMock.eq(mockReader),
            EasyMock.isA(DeploymentContext.class)))
            .andReturn(binding).times(2);
        EasyMock.replay(mockRegistry);

        ReferenceDefinition referenceDefinition = loader.load(parent, null, mockReader, ctx);
        assertEquals(2, referenceDefinition.getBindings().size());
    }

    public void testWithInterface() throws LoaderException, XMLStreamException {
        String name = "referenceDefinition";
        ServiceContract sc = new ServiceContract() {
        };
        EasyMock.expect(mockReader.getName()).andReturn(REFERENCE).anyTimes();
        EasyMock.expect(mockReader.getAttributeValue(null, "name")).andReturn(name);
        EasyMock.expect(mockReader.getAttributeValue(null, "multiplicity")).andReturn("0..1");
        EasyMock.expect(mockReader.next()).andReturn(XMLStreamConstants.START_ELEMENT);
        EasyMock.expect(mockRegistry.load(parent, null, mockReader, deploymentContext)).andReturn(sc);
        EasyMock.expect(mockReader.next()).andReturn(XMLStreamConstants.END_ELEMENT);

        EasyMock.replay(mockReader);
        EasyMock.replay(mockRegistry);

        ReferenceDefinition referenceDefinition = loader.load(parent, null, mockReader, deploymentContext);
        assertNotNull(referenceDefinition);
        assertEquals(PARENT_NAME + "#" + name, referenceDefinition.getUri().toString());
        assertSame(sc, referenceDefinition.getServiceContract());
    }

    protected void setUp() throws Exception {
        super.setUp();
        mockReader = EasyMock.createStrictMock(XMLStreamReader.class);
        mockRegistry = EasyMock.createMock(LoaderRegistry.class);
        loader = new ReferenceLoader(mockRegistry);
        deploymentContext = new RootDeploymentContext(null, null, null, null);
        deploymentContext.getPathNames().add("parent");
        parent = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(parent.getUri()).andReturn(URI.create(PARENT_NAME));
        EasyMock.replay(parent);
        ctx = EasyMock.createMock(DeploymentContext.class);
        List<String> names = new ArrayList<String>();
        names.add("parent");
        EasyMock.expect(ctx.getPathNames()).andReturn(names).atLeastOnce();
        EasyMock.replay(ctx);
    }
}
