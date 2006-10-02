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

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.SystemAtomicComponent;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class CompositeComponentImplTestCase extends TestCase {

    public void testSetUri() throws Exception {
        CompositeComponentImpl component = new CompositeComponentImpl("foo", "foo/bar", null, null, null);
        assertEquals("foo/bar", component.getURI());
    }

    public void testRegisterSystemService() throws Exception {
        List<Class<?>> services = new ArrayList<Class<?>>();
        services.add(Foo.class);
        CompositeComponent parent = new CompositeComponentImpl("foo", "foo", null, null, null);
        SystemAtomicComponent component = EasyMock.createMock(SystemAtomicComponent.class);
        EasyMock.expect(component.getName()).andReturn("bar").atLeastOnce();
        EasyMock.expect(component.getServiceInterfaces()).andReturn(services);
        EasyMock.expect(component.isSystem()).andReturn(true).atLeastOnce();
        EasyMock.replay(component);
        parent.register(component);
        assertNull(parent.getChild("bar"));
        assertNotNull(parent.getSystemChild("bar"));
        EasyMock.verify(component);
    }

    public void testRegister() throws Exception {
        List<Class<?>> services = new ArrayList<Class<?>>();
        services.add(Foo.class);
        CompositeComponent parent = new CompositeComponentImpl("foo", "foo", null, null, null);
        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(component.getName()).andReturn("bar").atLeastOnce();
        EasyMock.expect(component.getServiceInterfaces()).andReturn(services);
        EasyMock.expect(component.isSystem()).andReturn(false).atLeastOnce();
        EasyMock.replay(component);
        parent.register(component);
        assertNull(parent.getSystemChild("bar"));
        assertNotNull(parent.getChild("bar"));
        EasyMock.verify(component);
    }

    /**
     * Verifies a system service and application component can be registered with the same name in a composite
     */
    public void testSystemServiceApplicationNamespaceIsolation() throws Exception {
        List<Class<?>> services = new ArrayList<Class<?>>();
        services.add(Foo.class);
        CompositeComponent parent = new CompositeComponentImpl("foo", "foo", null, null, null);
        SystemAtomicComponent component = EasyMock.createMock(SystemAtomicComponent.class);
        EasyMock.expect(component.getName()).andReturn("bar").atLeastOnce();
        EasyMock.expect(component.getServiceInterfaces()).andReturn(services);
        EasyMock.expect(component.isSystem()).andReturn(true).atLeastOnce();
        EasyMock.replay(component);
        parent.register(component);
        AtomicComponent component2 = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(component2.getName()).andReturn("bar").atLeastOnce();
        EasyMock.expect(component2.getServiceInterfaces()).andReturn(services);
        EasyMock.expect(component2.isSystem()).andReturn(false).atLeastOnce();
        EasyMock.replay(component2);
        parent.register(component2);
        EasyMock.verify(component);
        EasyMock.verify(component2);
    }

    public void testSystemServiceLifecycle() throws Exception {
        List<Class<?>> services = new ArrayList<Class<?>>();
        services.add(Foo.class);
        CompositeComponent parent = new CompositeComponentImpl("foo", "foo", null, null, null);
        SystemAtomicComponent component = EasyMock.createMock(SystemAtomicComponent.class);
        component.start();
        EasyMock.expect(component.getName()).andReturn("bar").atLeastOnce();
        EasyMock.expect(component.getServiceInterfaces()).andReturn(services);
        EasyMock.expect(component.isSystem()).andReturn(true).atLeastOnce();
        component.stop();
        EasyMock.replay(component);
        parent.register(component);
        parent.start();
        parent.stop();
        EasyMock.verify(component);
    }

    public void testComponentLifecycle() throws Exception {
        List<Class<?>> services = new ArrayList<Class<?>>();
        services.add(Foo.class);
        CompositeComponent parent = new CompositeComponentImpl("foo", "foo", null, null, null);
        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        component.start();
        EasyMock.expect(component.getName()).andReturn("bar").atLeastOnce();
        EasyMock.expect(component.getServiceInterfaces()).andReturn(services);
        EasyMock.expect(component.isSystem()).andReturn(false).atLeastOnce();
        component.stop();
        EasyMock.replay(component);
        parent.register(component);
        parent.start();
        parent.stop();
        EasyMock.verify(component);
    }

    public void testSystemServiceAutowire() throws Exception {
        List<Class<?>> services = new ArrayList<Class<?>>();
        services.add(Foo.class);
        CompositeComponent parent = new CompositeComponentImpl("foo", "foo", null, null, null);
        SystemAtomicComponent component = EasyMock.createMock(SystemAtomicComponent.class);
        component.start();
        EasyMock.expect(component.getName()).andReturn("bar").atLeastOnce();
        EasyMock.expect(component.getServiceInterfaces()).andReturn(services);
        EasyMock.expect(component.getServiceInstance()).andReturn(new Foo() {
        });
        EasyMock.expect(component.isSystem()).andReturn(true).atLeastOnce();
        component.stop();
        EasyMock.replay(component);
        parent.register(component);
        parent.start();
        assertNull(parent.resolveSystemExternalInstance(Foo.class));
        assertNotNull(parent.resolveSystemInstance(Foo.class));
        parent.stop();
        EasyMock.verify(component);
    }


    public void testAutowire() throws Exception {
        List<Class<?>> services = new ArrayList<Class<?>>();
        services.add(Foo.class);
        CompositeComponent parent = new CompositeComponentImpl("foo", "foo", null, null, null);
        SystemAtomicComponent component = EasyMock.createMock(SystemAtomicComponent.class);
        component.start();
        EasyMock.expect(component.getName()).andReturn("bar").atLeastOnce();
        EasyMock.expect(component.getServiceInterfaces()).andReturn(services);
        EasyMock.expect(component.getServiceInstance()).andReturn(new Foo() {
        });
        EasyMock.expect(component.isSystem()).andReturn(false).atLeastOnce();
        component.stop();
        EasyMock.replay(component);
        parent.register(component);
        parent.start();
        assertNull(parent.resolveExternalInstance(Foo.class));
        assertNotNull(parent.resolveInstance(Foo.class));
        parent.stop();
        EasyMock.verify(component);
    }

    private class Foo {

    }
}
