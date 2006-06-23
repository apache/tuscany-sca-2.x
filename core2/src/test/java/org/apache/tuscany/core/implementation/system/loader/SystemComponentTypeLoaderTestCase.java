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
package org.apache.tuscany.core.implementation.system.loader;

import junit.framework.TestCase;

import org.apache.tuscany.core.implementation.PojoComponentType;
import org.apache.tuscany.core.implementation.system.model.SystemImplementation;
import org.apache.tuscany.core.mock.component.BasicInterface;
import org.apache.tuscany.core.mock.component.BasicInterfaceImpl;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ServiceDefinition;

/**
 * @version $Rev$ $Date$
 */
public class SystemComponentTypeLoaderTestCase extends TestCase {
    private SystemComponentTypeLoader loader;

    public void testIntrospectUnannotatedClass() {
        SystemImplementation impl = new SystemImplementation(BasicInterfaceImpl.class);
        PojoComponentType<?, ?, ?> componentType = loader.loadByIntrospection(impl);
        ServiceDefinition service = componentType.getServices().get(BasicInterface.class.getName());
        assertEquals(BasicInterface.class, service.getServiceContract().getInterfaceClass());
        Property<?> property = componentType.getProperties().get("publicProperty");
        assertEquals(String.class, property.getJavaType());
        ReferenceDefinition referenceDefinition = componentType.getReferences().get("publicReference");
        assertEquals(BasicInterface.class, referenceDefinition.getServiceContract().getInterfaceClass());
    }

    protected void setUp() throws Exception {
        super.setUp();
        loader = new SystemComponentTypeLoader();
    }
}
