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

import javax.xml.stream.XMLStreamException;

import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.MissingRequiredPropertyException;
import org.apache.tuscany.spi.loader.PropertyObjectFactory;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ServiceDefinition;

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.java.JavaImplementation;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class ComponentLoaderPropertyTestCase extends TestCase {

    private TestLoader loader;

    /**
     * Verifies that an optional property not cofigured in an assembly will avoid having a PropertyValue created for it
     * so that the runtime does not erroneously inject null values
     */
    public void testOptionalPropertyNotConfigured() throws LoaderException, XMLStreamException {
        PojoComponentType<?, ?, Property<?>> type =
            new PojoComponentType<ServiceDefinition, ReferenceDefinition, Property<?>>();
        Property property = new Property();
        property.setName("name");
        type.add(property);
        JavaImplementation impl = new JavaImplementation(null, type);
        impl.setComponentType(type);
        ComponentDefinition<Implementation<?>> defn = new ComponentDefinition<Implementation<?>>(impl);
        loader.populatePropertyValues(defn);
        assertTrue(defn.getPropertyValues().isEmpty());
    }

    protected void setUp() throws Exception {
        super.setUp();
        LoaderRegistry mockRegistry = EasyMock.createMock(LoaderRegistry.class);
        PropertyObjectFactory mockPropertyFactory = EasyMock.createMock(PropertyObjectFactory.class);
        loader = new TestLoader(mockRegistry, mockPropertyFactory);
    }

    private class TestLoader extends ComponentLoader {

        public TestLoader(LoaderRegistry registry, PropertyObjectFactory propertyFactory) {
            super(registry, propertyFactory);
        }

        @Override
        public void populatePropertyValues(ComponentDefinition<Implementation<?>> componentDefinition)
            throws MissingRequiredPropertyException {
            super.populatePropertyValues(componentDefinition);
        }
    }
}
