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

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.spi.model.ComponentType;
import org.apache.tuscany.spi.model.ModelObject;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ServiceDefinition;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class ComponentTypeElementLoaderTestCase extends TestCase {

    public void testSpecializedComponentTypePassedIn() throws Exception {
        ComponentType<ServiceDefinition, ReferenceDefinition, Property<?>> type =
            new ComponentType<ServiceDefinition, ReferenceDefinition, Property<?>>();
        ComponentTypeElementLoader loader = new ComponentTypeElementLoader(null);
        XMLStreamReader reader = EasyMock.createMock(XMLStreamReader.class);
        EasyMock.expect(reader.getName()).andReturn(ComponentTypeElementLoader.COMPONENT_TYPE);
        EasyMock.expect(reader.next()).andReturn(XMLStreamConstants.END_ELEMENT);
        EasyMock.replay(reader);
        // verify that the exact component type instance is returned. Some StAXElementLoader implementations may chose
        // to copy the original instance but ComponentTypeElementLoader does not since it has no knowledge of the
        // specialized instance 
        ModelObject object = loader.load(null, type, reader, null);
        assertEquals(object, type);
    }

    public void testNonSpecializedComponentTypePassedIn() throws Exception {
        ComponentTypeElementLoader loader = new ComponentTypeElementLoader(null);
        XMLStreamReader reader = EasyMock.createMock(XMLStreamReader.class);
        EasyMock.expect(reader.getName()).andReturn(ComponentTypeElementLoader.COMPONENT_TYPE);
        EasyMock.expect(reader.next()).andReturn(XMLStreamConstants.END_ELEMENT);
        EasyMock.replay(reader);
        ModelObject object = loader.load(null, null, reader, null);
        assertEquals(ComponentType.class, object.getClass());
    }


}
