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

import static org.osoa.sca.Constants.SCA_NS;

import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.UndefinedReferenceException;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.ModelObject;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ServiceDefinition;

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.java.JavaImplementation;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.isNull;

/**
 * @version $Rev$ $Date$
 */
public class ComponentLoaderNoReferenceTestCase extends TestCase {
    private static final QName COMPONENT = new QName(SCA_NS, "component");
    private static final QName REFERENCE = new QName(SCA_NS, "reference");
    private static final String NAME = "testComponent";
    private ComponentLoader loader;
    private XMLStreamReader reader;
    private DeploymentContext ctx;

    /**
     * Verifies an error is thrown when an attempt to configure a non-existent reference in SCDL is made
     */
    public void testNoReferenceOnComponentType() throws LoaderException, XMLStreamException {
        try {
            loader.load(null, reader, ctx);
            fail();
        } catch (UndefinedReferenceException e) {
            // expected
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        URI componentId = URI.create("sca://localhost/parent/");
        PojoComponentType<ServiceDefinition, ReferenceDefinition, Property<?>> type =
            new PojoComponentType<ServiceDefinition, ReferenceDefinition, Property<?>>();
        JavaImplementation impl = new JavaImplementation(null, type);
        reader = EasyMock.createMock(XMLStreamReader.class);
        EasyMock.expect(reader.getName()).andReturn(COMPONENT);
        EasyMock.expect(reader.getAttributeValue((String) EasyMock.isNull(), EasyMock.isA(String.class)))
            .andReturn(NAME);
        EasyMock.expect(reader.getAttributeValue((String) EasyMock.isNull(), EasyMock.eq("initLevel")))
            .andReturn(null);
        EasyMock.expect(reader.getAttributeValue((String) EasyMock.isNull(), EasyMock.eq("autowire")))
            .andReturn(null);
        EasyMock.expect(reader.getAttributeValue(EasyMock.isA(String.class), EasyMock.isA(String.class)))
            .andReturn(null);
        EasyMock.expect(reader.nextTag()).andReturn(0);
        EasyMock.expect(reader.next()).andReturn(XMLStreamConstants.START_ELEMENT);
        EasyMock.expect(reader.getName()).andReturn(REFERENCE);
        EasyMock.expect(reader.getAttributeValue((String) isNull(), EasyMock.eq("name")))
            .andReturn("noreference");
        EasyMock.expect(reader.getAttributeValue((String) EasyMock.isNull(), EasyMock.eq("autowire")))
            .andReturn(null);
        EasyMock.expect(reader.getAttributeValue(null, "runtimeId")).andReturn(null);
        EasyMock.expect(reader.getAttributeValue(null, "target")).andReturn("text");

        EasyMock.expect(reader.next()).andReturn(XMLStreamConstants.END_ELEMENT);
        EasyMock.replay(reader);
        LoaderRegistry mockRegistry = EasyMock.createMock(LoaderRegistry.class);
        mockRegistry.loadComponentType(
            EasyMock.isA(Implementation.class),
            EasyMock.isA(DeploymentContext.class));
        EasyMock.expect(mockRegistry.load(
            (ModelObject) isNull(),
            EasyMock.isA(XMLStreamReader.class),
            EasyMock.isA(DeploymentContext.class))).andReturn(impl);
        EasyMock.replay(mockRegistry);
        loader = new ComponentLoader(mockRegistry, null);
        ctx = EasyMock.createMock(DeploymentContext.class);
        EasyMock.expect(ctx.getClassLoader()).andReturn(null);
        EasyMock.expect(ctx.isAutowire()).andReturn(false);
        EasyMock.expect(ctx.getScdlLocation()).andReturn(null);
        EasyMock.expect(ctx.getComponentId()).andReturn(componentId);
        EasyMock.replay(ctx);
    }

}
