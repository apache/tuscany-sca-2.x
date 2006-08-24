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
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import static org.osoa.sca.Version.XML_NAMESPACE_1_0;

import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.StAXPropertyFactory;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ServiceDefinition;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;

import org.apache.tuscany.core.implementation.java.JavaImplementation;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * @version $Rev$ $Date$
 */
public class ComponentLoaderTestCase extends MockObjectTestCase {
    private static final QName COMPONENT = new QName(XML_NAMESPACE_1_0, "component");
    private static final String NAME = "testComponent";
    private static final Implementation IMPL = new JavaImplementation();

    private Mock mockReader;
    private Mock mockRegistry;
    private Mock mockPropertyFactory;
    private ComponentLoader loader;

    public void testEmptyComponent() throws LoaderException, XMLStreamException {
        mockReader.expects(once()).method("getName").will(returnValue(COMPONENT));
        mockReader.expects(atLeastOnce()).method("getAttributeValue")
            .with(ANYTHING, ANYTHING)
            .will(onConsecutiveCalls(returnValue(NAME), returnValue(null)));
        mockReader.expects(once()).method("nextTag").will(returnValue(0));
        mockReader.expects(once()).method("next").will(returnValue(XMLStreamConstants.END_ELEMENT));
        mockRegistry.expects(once()).method("loadComponentType");
        mockRegistry.expects(once()).method("load").will(returnValue(IMPL));
        ComponentDefinition component = loader.load(null, (XMLStreamReader) mockReader.proxy(), null);
        assertEquals(NAME, component.getName());
        assertNull(component.getInitLevel());
    }

    public void testInitValue20() throws LoaderException, XMLStreamException {
        mockReader.expects(once()).method("getName").will(returnValue(COMPONENT));
        mockReader.expects(atLeastOnce()).method("getAttributeValue")
            .with(ANYTHING, ANYTHING)
            .will(onConsecutiveCalls(returnValue(NAME), returnValue("20")));
        mockReader.expects(once()).method("nextTag").will(returnValue(0));
        mockReader.expects(once()).method("next").will(returnValue(XMLStreamConstants.END_ELEMENT));
        mockRegistry.expects(once()).method("loadComponentType");
        mockRegistry.expects(once()).method("load").will(returnValue(IMPL));
        ComponentDefinition component = loader.load(null, (XMLStreamReader) mockReader.proxy(), null);
        assertEquals(NAME, component.getName());
        assertEquals(Integer.valueOf(20), component.getInitLevel());
    }

    public void testLoadPropertyWithSource() throws LoaderException, XMLStreamException {
        PojoComponentType<?, ?, Property<?>> type =
            new PojoComponentType<ServiceDefinition, ReferenceDefinition, Property<?>>();
        Property property = new Property();
        property.setName("name");
        type.add(property);
        JavaImplementation impl = new JavaImplementation();
        impl.setComponentType(type);
        ComponentDefinition<?> defn = new ComponentDefinition<JavaImplementation>(impl);
        XMLStreamReader reader = createMock(XMLStreamReader.class);
        expect(reader.getAttributeValue(null, "name")).andReturn("name");
        expect(reader.getAttributeValue(null, "source")).andReturn("$source");
        replay(reader);
        loader.loadProperty(reader, null, defn);
        assertEquals("$source", defn.getPropertyValues().get("name").getSource());
        EasyMock.verify(reader);
    }

    protected void setUp() throws Exception {
        super.setUp();
        mockReader = mock(XMLStreamReader.class);
        mockRegistry = mock(LoaderRegistry.class);
        mockPropertyFactory = mock(StAXPropertyFactory.class);
        loader = new ComponentLoader((LoaderRegistry) mockRegistry.proxy(),
            (StAXPropertyFactory) mockPropertyFactory.proxy());
    }
}
