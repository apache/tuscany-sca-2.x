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
import java.net.URI;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.wire.InboundWire;

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.TestUtils;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class CompositeComponentImplTestCase extends TestCase {

    public void testRegisterSystemService() throws Exception {
        List<Class<?>> services = new ArrayList<Class<?>>();
        services.add(Foo.class);
        CompositeComponent parent = new CompositeComponentImpl(URI.create("foo"), null, null, null);
        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(component.getUri()).andReturn(URI.create("bar")).atLeastOnce();
        List<InboundWire> wires = TestUtils.createInboundWires(services);
        EasyMock.expect(component.getInboundWires()).andReturn(wires).atLeastOnce();
        EasyMock.replay(component);
        parent.register(component);
        assertNotNull(parent.getChild("bar"));
        EasyMock.verify(component);
    }

    public void testRegister() throws Exception {
        List<Class<?>> services = new ArrayList<Class<?>>();
        services.add(Foo.class);
        CompositeComponent parent = new CompositeComponentImpl(URI.create("foo"), null, null, null);
        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(component.getUri()).andReturn(URI.create("bar")).atLeastOnce();
        List<InboundWire> wires = TestUtils.createInboundWires(services);
        EasyMock.expect(component.getInboundWires()).andReturn(wires).atLeastOnce();
        EasyMock.replay(component);
        parent.register(component);
        assertNotNull(parent.getChild("bar"));
        EasyMock.verify(component);
    }

    public void testAutowire() throws Exception {
        List<Class<?>> services = new ArrayList<Class<?>>();
        services.add(Foo.class);
        CompositeComponent parent = new CompositeComponentImpl(URI.create("foo"), null, null, null);
        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(component.getUri()).andReturn(URI.create("component")).atLeastOnce();
        List<InboundWire> wires = TestUtils.createInboundWires(services);
        TestUtils.populateInboundWires(component, wires);
        EasyMock.expect(component.getInboundWires()).andReturn(wires).atLeastOnce();
        EasyMock.replay(component);
        parent.register(component);
        parent.start();
        assertNull(parent.resolveExternalAutowire(Foo.class));
        assertNotNull(parent.resolveAutowire(Foo.class));
        parent.stop();
        EasyMock.verify(component);
    }

    private class Foo {

    }
}
