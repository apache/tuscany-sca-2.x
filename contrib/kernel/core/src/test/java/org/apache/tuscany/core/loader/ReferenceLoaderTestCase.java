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
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.Constants;

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
import org.easymock.EasyMock;

/**
 * Verifies loading of a reference definition from an XML-based assembly
 *
 * @version $Rev$ $Date$
 */
public class ReferenceLoaderTestCase extends TestCase {
    private static final QName REFERENCE = new QName(Constants.SCA_NS, "reference");
    private static final String COMPONENT_NAME = "sca://someComponent";
    private URI componentId;
    private ReferenceLoader loader;
    private XMLStreamReader mockReader;
    private LoaderRegistry mockRegistry;
    private DeploymentContext ctx;

    public void testWithNoInterface() throws LoaderException, XMLStreamException {
        String name = "referenceDefinition";
        EasyMock.expect(mockReader.getName()).andReturn(REFERENCE).anyTimes();
        EasyMock.expect(mockReader.getAttributeValue(null, "name")).andReturn(name);
        EasyMock.expect(mockReader.getAttributeValue(null, "multiplicity")).andReturn("0..1");
        EasyMock.expect(mockReader.next()).andReturn(XMLStreamConstants.END_ELEMENT);
        EasyMock.expect(mockReader.getName()).andReturn(REFERENCE).anyTimes();
        EasyMock.replay(mockReader);
        ReferenceDefinition referenceDefinition = loader.load(null, mockReader, ctx);
        assertNotNull(referenceDefinition);
        assertEquals(COMPONENT_NAME + "#" + name, referenceDefinition.getUri().toString());
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
        ReferenceDefinition referenceDefinition = loader.load(type, mockReader, ctx);
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
        EasyMock.expect(mockRegistry.load(
            (ModelObject) EasyMock.isNull(),
            EasyMock.eq(mockReader),
            EasyMock.isA(DeploymentContext.class)))
            .andReturn(binding).times(2);
        EasyMock.replay(mockRegistry);

        ReferenceDefinition referenceDefinition = loader.load(null, mockReader, ctx);
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
        EasyMock.expect(mockRegistry.load(null, mockReader, ctx)).andReturn(sc);
        EasyMock.expect(mockReader.next()).andReturn(XMLStreamConstants.END_ELEMENT);

        EasyMock.replay(mockReader);
        EasyMock.replay(mockRegistry);

        ReferenceDefinition referenceDefinition = loader.load(null, mockReader, ctx);
        assertNotNull(referenceDefinition);
        assertEquals(COMPONENT_NAME + "#" + name, referenceDefinition.getUri().toString());
        assertSame(sc, referenceDefinition.getServiceContract());
    }

    protected void setUp() throws Exception {
        super.setUp();
        componentId = URI.create(COMPONENT_NAME);
        mockReader = EasyMock.createStrictMock(XMLStreamReader.class);
        mockRegistry = EasyMock.createMock(LoaderRegistry.class);
        loader = new ReferenceLoader(mockRegistry);
        ctx = EasyMock.createMock(DeploymentContext.class);
        EasyMock.expect(ctx.getComponentId()).andReturn(componentId);
        EasyMock.replay(ctx);
    }
}
