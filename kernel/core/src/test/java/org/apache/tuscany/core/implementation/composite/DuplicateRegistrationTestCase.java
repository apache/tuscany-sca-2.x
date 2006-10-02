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
package org.apache.tuscany.core.implementation.composite;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.DuplicateNameException;
import org.apache.tuscany.spi.component.SystemAtomicComponent;

import junit.framework.TestCase;
import org.apache.tuscany.core.mock.component.Source;
import org.easymock.EasyMock;

/**
 * Verfies children with the same name cannot be registered in the same composite
 *
 * @version $Rev$ $Date$
 */
public class DuplicateRegistrationTestCase extends TestCase {

    public void testDuplicateRegistration() throws Exception {
        CompositeComponent parent = new CompositeComponentImpl(null, null, null, null);
        parent.start();

        List<Class<?>> interfaces = new ArrayList<Class<?>>();
        interfaces.add(Source.class);
        SystemAtomicComponent component1 = EasyMock.createMock(SystemAtomicComponent.class);
        EasyMock.expect(component1.getName()).andReturn("source").atLeastOnce();
        EasyMock.expect(component1.isSystem()).andReturn(true).atLeastOnce();
        component1.stop();
        EasyMock.expect(component1.getServiceInterfaces()).andReturn(interfaces);
        EasyMock.replay(component1);

        SystemAtomicComponent component2 = EasyMock.createMock(SystemAtomicComponent.class);
        EasyMock.expect(component2.getName()).andReturn("source").atLeastOnce();
        EasyMock.expect(component2.isSystem()).andReturn(true).atLeastOnce();
        component2.stop();
        EasyMock.expect(component2.getServiceInterfaces()).andReturn(interfaces);
        EasyMock.replay(component2);

        parent.register(component1);
        try {
            parent.register(component2);
            fail();
        } catch (DuplicateNameException e) {
            // ok
        }
        parent.stop();
    }

    public void testDuplicateNameSystemService() throws Exception {
        List<Class<?>> services = new ArrayList<Class<?>>();
        services.add(Source.class);
        CompositeComponent parent = new CompositeComponentImpl("foo", "foo", null, null, null);
        SystemAtomicComponent component = EasyMock.createMock(SystemAtomicComponent.class);
        EasyMock.expect(component.getName()).andReturn("bar").atLeastOnce();
        EasyMock.expect(component.getServiceInterfaces()).andReturn(services);
        EasyMock.expect(component.isSystem()).andReturn(true).atLeastOnce();
        EasyMock.replay(component);
        parent.register(component);
        SystemAtomicComponent component2 = EasyMock.createMock(SystemAtomicComponent.class);
        EasyMock.expect(component2.getName()).andReturn("bar").atLeastOnce();
        EasyMock.expect(component2.getServiceInterfaces()).andReturn(services);
        EasyMock.expect(component2.isSystem()).andReturn(true).atLeastOnce();
        EasyMock.replay(component2);
        try {
            parent.register(component2);
            fail();
        } catch (DuplicateNameException e) {
            // expected
        }
    }


    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
