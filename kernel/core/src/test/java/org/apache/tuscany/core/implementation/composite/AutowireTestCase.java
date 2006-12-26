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
import java.util.Map;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.wire.InboundWire;

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.TestUtils;
import org.easymock.EasyMock;

/**
 * Performs basic autowiring tests to composite artifacts
 *
 * @version $$Rev$$ $$Date$$
 */
public class AutowireTestCase extends TestCase {

    /**
     * Tests autowiring to an system atomic component
     */
    public void testSystemAtomicAutowire() throws Exception {
        CompositeComponent parent = new CompositeComponentImpl("parent", null, null, true);
        parent.start();
        List<Class<?>> interfaces = new ArrayList<Class<?>>();
        interfaces.add(Source.class);
        interfaces.add(Source2.class);
        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(component.getName()).andReturn("source").atLeastOnce();
        EasyMock.expect(component.getServiceInterfaces()).andReturn(interfaces);
        EasyMock.expect(component.isSystem()).andReturn(true).atLeastOnce();
        Map<String, InboundWire> wires = TestUtils.createInboundWires(interfaces);
        EasyMock.expect(component.getInboundWires()).andReturn(wires).atLeastOnce();
        TestUtils.populateInboundWires(component, wires);
        EasyMock.replay(component);
        parent.register(component);
        InboundWire source = parent.resolveSystemAutowire(Source.class);
        assertNotNull(source);
        InboundWire source2 = parent.resolveSystemAutowire(Source2.class);
        assertSame(source.getContainer(), source2.getContainer());
        assertNull(parent.resolveSystemExternalAutowire(Source.class));
        EasyMock.verify(component);
    }

    /**
     * Tests autowiring to an system atomic component
     */
    public void testAtomicAutowire() throws Exception {
        CompositeComponent parent = new CompositeComponentImpl("parent", null, null, null);
        parent.start();

        List<Class<?>> interfaces = new ArrayList<Class<?>>();
        interfaces.add(Source.class);
        interfaces.add(Source2.class);
        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(component.getName()).andReturn("source").atLeastOnce();
        EasyMock.expect(component.getServiceInterfaces()).andReturn(interfaces);
        EasyMock.expect(component.isSystem()).andReturn(false).atLeastOnce();
        Map<String, InboundWire> wires = TestUtils.createInboundWires(interfaces);
        EasyMock.expect(component.getInboundWires()).andReturn(wires).atLeastOnce();
        TestUtils.populateInboundWires(component, wires);

        EasyMock.replay(component);
        parent.register(component);

        InboundWire source = parent.resolveAutowire(Source.class);
        assertNotNull(source);
        InboundWire source2 = parent.resolveAutowire(Source2.class);
        assertSame(source.getContainer(), source2.getContainer());
        assertNull(parent.resolveExternalAutowire(Source.class));
        EasyMock.verify(component);
    }

    /**
     * Tests autowiring to a system service
     */
    public void testSystemServiceAutowire() throws Exception {
        CompositeComponent parent = new CompositeComponentImpl("parent", null, null, true);
        parent.start();

        Service service = EasyMock.createMock(Service.class);
        EasyMock.expect(service.getName()).andReturn("service").atLeastOnce();
        EasyMock.expect(service.isSystem()).andReturn(true).atLeastOnce();
        InboundWire wire = TestUtils.createInboundWire(Source.class);
        wire.setContainer(service);
        EasyMock.expect(service.getInboundWire()).andReturn(wire).atLeastOnce();
        EasyMock.replay(service);
        parent.register(service);

        InboundWire source = parent.resolveSystemExternalAutowire(Source.class);
        assertSame(service, source.getContainer());
        InboundWire source2 = parent.resolveSystemExternalAutowire(Source2.class);
        assertNull(source2);
        EasyMock.verify(service);
    }

    /**
     * Tests autowiring to a service
     */
    public void testServiceAutowire() throws Exception {
        CompositeComponent parent = new CompositeComponentImpl("parent", null, null, true);
        parent.start();

        Service service = EasyMock.createMock(Service.class);
        EasyMock.expect(service.getName()).andReturn("service").atLeastOnce();
        EasyMock.expect(service.isSystem()).andReturn(false).atLeastOnce();
        InboundWire wire = TestUtils.createInboundWire(Source.class);
        wire.setContainer(service);
        EasyMock.expect(service.getInboundWire()).andReturn(wire).atLeastOnce();
        EasyMock.replay(service);
        parent.register(service);

        InboundWire source = parent.resolveExternalAutowire(Source.class);
        assertSame(service, source.getContainer());
        InboundWire source2 = parent.resolveExternalAutowire(Source2.class);
        assertNull(source2);
        EasyMock.verify(service);
    }


    /**
     * Tests autowiring to a system reference
     */
    public void testSystemReferenceAutowire() throws Exception {
        CompositeComponent parent = new CompositeComponentImpl("parent", null, null, true);
        parent.start();

        Reference reference = EasyMock.createMock(Reference.class);
        EasyMock.expect(reference.getName()).andReturn("service").atLeastOnce();
        EasyMock.expect(reference.isSystem()).andReturn(true).atLeastOnce();
        InboundWire wire = TestUtils.createInboundWire(Source.class);
        wire.setContainer(reference);
        EasyMock.expect(reference.getInboundWire()).andReturn(wire).atLeastOnce();
        EasyMock.replay(reference);
        parent.register(reference);

        InboundWire source = parent.resolveSystemAutowire(Source.class);
        assertNotNull(source);
        EasyMock.verify(reference);
    }

    /**
     * Tests autowiring to a reference
     */
    public void testReferenceAutowire() throws Exception {
        CompositeComponent parent = new CompositeComponentImpl("parent", null, null, true);
        parent.start();

        Reference reference = EasyMock.createMock(Reference.class);
        EasyMock.expect(reference.getName()).andReturn("service").atLeastOnce();
        EasyMock.expect(reference.isSystem()).andReturn(false).atLeastOnce();
        InboundWire wire = TestUtils.createInboundWire(Source.class);
        wire.setContainer(reference);
        EasyMock.expect(reference.getInboundWire()).andReturn(wire).atLeastOnce();
        EasyMock.replay(reference);
        parent.register(reference);

        InboundWire source = parent.resolveAutowire(Source.class);
        assertNotNull(source);
        EasyMock.verify(reference);
    }


    public static class SourceImpl implements Source, Source2 {
        public SourceImpl() {
        }
    }

    public static interface Source {

    }

    public static interface Source2 {
    }

}
