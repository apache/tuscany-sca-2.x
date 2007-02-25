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
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import static org.osoa.sca.Constants.SCA_NS;

import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.model.BindingDefinition;
import org.apache.tuscany.spi.model.ComponentType;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.model.ServiceDefinition;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

/**
 * Verifies loading of a service definition from an XML-based assembly
 *
 * @version $Rev$ $Date$
 */
public class ServiceLoaderTestCase extends TestCase {
    private static final QName SERVICE = new QName(SCA_NS, "service");
    //private static final QName REFERENCE = new QName(SCA_NS, "reference");
    private static final QName INTERFACE_JAVA = new QName(SCA_NS, "interface.java");
    private static final String PARENT_NAME = "sca://localhost/parent/";
    private ServiceLoader loader;
    private DeploymentContext deploymentContext;
    private XMLStreamReader mockReader;
    private LoaderRegistry mockRegistry;
    private URI componentId;

    public void testWithNoInterface() throws LoaderException, XMLStreamException {
        String name = "serviceDefinition";
        expect(mockReader.getName()).andReturn(SERVICE).anyTimes();
        expect(mockReader.getAttributeValue(null, "name")).andReturn(name);
        expect(mockReader.getAttributeValue(null, "promote")).andReturn(null);
        expect(mockReader.next()).andReturn(END_ELEMENT);
        expect(mockReader.getName()).andReturn(SERVICE).anyTimes();
        replay(mockReader);
        ServiceDefinition serviceDefinition = loader.load(null, mockReader, deploymentContext);
        assertNotNull(serviceDefinition);
        assertEquals(PARENT_NAME + "#" + name, serviceDefinition.getUri().toString());
    }

    public void testComponentTypeService() throws LoaderException, XMLStreamException {
        String name = "service";
        expect(mockReader.getName()).andReturn(SERVICE).anyTimes();
        expect(mockReader.getAttributeValue(null, "name")).andReturn(name);
        expect(mockReader.getAttributeValue(null, "promote")).andReturn(null);
        expect(mockReader.next()).andReturn(END_ELEMENT);
        expect(mockReader.getName()).andReturn(SERVICE).anyTimes();
        replay(mockReader);
        ComponentType<ServiceDefinition, ReferenceDefinition, Property<?>> type =
            new ComponentType<ServiceDefinition, ReferenceDefinition, Property<?>>();
        ServiceDefinition serviceDefinition = loader.load(type, mockReader, deploymentContext);
        assertTrue(ServiceDefinition.class.equals(serviceDefinition.getClass()));
    }

    public void testMultipleBindings() throws LoaderException, XMLStreamException {
        String name = "serviceDefinition";
        expect(mockReader.getName()).andReturn(SERVICE).anyTimes();
        expect(mockReader.getAttributeValue(null, "name")).andReturn(name);
        expect(mockReader.getAttributeValue(null, "promote")).andReturn("component/target");
        expect(mockReader.next()).andReturn(START_ELEMENT);
        expect(mockReader.next()).andReturn(START_ELEMENT);
        expect(mockReader.next()).andReturn(END_ELEMENT);
        expect(mockReader.getName()).andReturn(SERVICE).anyTimes();
        replay(mockReader);

        BindingDefinition binding = new BindingDefinition() {
        };
        expect(mockRegistry.load(null, mockReader, deploymentContext)).andReturn(binding).times(2);
        replay(mockRegistry);

        ServiceDefinition serviceDefinition = loader.load(null, mockReader, deploymentContext);
        assertEquals(2, serviceDefinition.getBindings().size());
    }

    public void testWithInterface() throws LoaderException, XMLStreamException {
        String name = "serviceDefinition";
        String target = "target";
        ServiceContract sc = new ServiceContract() {
        };
        expect(mockReader.getName()).andReturn(SERVICE).anyTimes();
        expect(mockReader.getAttributeValue(null, "name")).andReturn(name);
        expect(mockReader.getAttributeValue(null, "promote")).andReturn(target);
        expect(mockReader.next()).andReturn(START_ELEMENT);
        expect(mockRegistry.load(null, mockReader, deploymentContext)).andReturn(sc);
        expect(mockReader.next()).andReturn(END_ELEMENT);
        expect(mockReader.getName()).andReturn(SERVICE);

        replay(mockReader);
        replay(mockRegistry);

        ServiceDefinition serviceDefinition = loader.load(null, mockReader, deploymentContext);
        assertNotNull(serviceDefinition);
        assertEquals(PARENT_NAME + "#" + name, serviceDefinition.getUri().toString());
        assertSame(sc, serviceDefinition.getServiceContract());
    }

    public void testWithNoReference() throws LoaderException, XMLStreamException {
        String name = "serviceDefinition";
        ServiceContract sc = new ServiceContract() {
        };
        expect(mockReader.getName()).andReturn(SERVICE).anyTimes();
        expect(mockReader.getAttributeValue(null, "name")).andReturn(name);
        expect(mockReader.getAttributeValue(null, "promote")).andReturn(null);
        expect(mockReader.next()).andReturn(START_ELEMENT);
        expect(mockRegistry.load(null, mockReader, deploymentContext)).andReturn(sc);
        expect(mockReader.next()).andReturn(END_ELEMENT);
        expect(mockReader.getName()).andReturn(SERVICE);

        replay(mockReader);
        replay(mockRegistry);

        ServiceDefinition serviceDefinition = loader.load(null, mockReader, deploymentContext);
        assertNotNull(serviceDefinition);
        assertEquals(PARENT_NAME + "#" + name, serviceDefinition.getUri().toString());
        assertSame(sc, serviceDefinition.getServiceContract());
    }

    protected void setUp() throws Exception {
        super.setUp();
        mockReader = EasyMock.createStrictMock(XMLStreamReader.class);
        mockRegistry = EasyMock.createMock(LoaderRegistry.class);
        loader = new ServiceLoader(mockRegistry);
        componentId = URI.create(PARENT_NAME);
        deploymentContext = EasyMock.createMock(DeploymentContext.class);
        EasyMock.expect(deploymentContext.getComponentId()).andReturn(componentId);
        EasyMock.replay(deploymentContext);
    }
}
