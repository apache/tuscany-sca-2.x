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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.ServiceBinding;
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
    public void testAtomicAutowire() throws Exception {
        CompositeComponent parent = new CompositeComponentImpl(URI.create("parent"), null, null, null);
        parent.start();

        List<Class<?>> interfaces = new ArrayList<Class<?>>();
        interfaces.add(Source.class);
        interfaces.add(Source2.class);
        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(component.getUri()).andReturn(URI.create("source")).atLeastOnce();
        List<InboundWire> wires = TestUtils.createInboundWires(interfaces);
        EasyMock.expect(component.getInboundWires()).andReturn(wires).atLeastOnce();
        TestUtils.populateInboundWires(component, wires);

        EasyMock.replay(component);
        parent.register(component);

        InboundWire source = parent.resolveAutowire(Source.class);
        assertNotNull(source);
        InboundWire sourceBase = parent.resolveAutowire(SourceBase.class);
        assertSame(source, sourceBase);
        InboundWire source2 = parent.resolveAutowire(Source2.class);
        assertSame(source.getContainer(), source2.getContainer());
        assertNull(parent.resolveExternalAutowire(Source.class));
        EasyMock.verify(component);
    }

    /**
     * Tests autowiring to a service
     */
    public void testServiceAutowire() throws Exception {
        CompositeComponent parent = new CompositeComponentImpl(URI.create("parent"), null, null);
        parent.start();

        ServiceBinding serviceBinding = EasyMock.createMock(ServiceBinding.class);
        InboundWire wire = TestUtils.createInboundWire(Source.class);
        wire.setContainer(serviceBinding);
        EasyMock.expect(serviceBinding.getInboundWire()).andReturn(wire).atLeastOnce();
        EasyMock.replay(serviceBinding);

        List<ServiceBinding> bindings = new ArrayList<ServiceBinding>();
        bindings.add(serviceBinding);
        Service service = EasyMock.createMock(Service.class);
        EasyMock.expect(service.getUri()).andReturn(URI.create("service")).atLeastOnce();
        EasyMock.expect(service.getServiceBindings()).andReturn(bindings).atLeastOnce();
        EasyMock.replay(service);
        parent.register(service);

        InboundWire source = parent.resolveExternalAutowire(Source.class);
        assertSame(serviceBinding, source.getContainer());
        InboundWire sourceBase = parent.resolveExternalAutowire(SourceBase.class);
        assertSame(source, sourceBase);
        InboundWire source2 = parent.resolveExternalAutowire(Source2.class);
        assertNull(source2);
        EasyMock.verify(serviceBinding);
    }


    /**
     * Tests autowiring to a reference
     */
    public void testReferenceAutowire() throws Exception {
        CompositeComponent parent = new CompositeComponentImpl(URI.create("parent"), null, null);
        parent.start();

        ReferenceBinding binding = EasyMock.createMock(ReferenceBinding.class);
        binding.setReference(EasyMock.isA(Reference.class));
        InboundWire wire = TestUtils.createInboundWire(Source.class);
        wire.setContainer(binding);
        EasyMock.expect(binding.getInboundWire()).andReturn(wire).atLeastOnce();
        EasyMock.replay(binding);

        Reference reference = new ReferenceImpl(URI.create("foo"), null, wire.getServiceContract());
        reference.addReferenceBinding(binding);
        parent.register(reference);

        InboundWire source = parent.resolveAutowire(Source.class);
        assertNotNull(source);
        InboundWire sourceBase = parent.resolveAutowire(SourceBase.class);
        assertSame(source, sourceBase);
        EasyMock.verify(binding);
    }


    public static class SourceImpl implements Source, Source2 {
        public SourceImpl() {
        }
    }

    public static interface SourceBase {
    }

    public static interface Source extends SourceBase {
    }

    public static interface Source2 {
    }

}
