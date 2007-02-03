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
package org.apache.tuscany.spi.extension;

import java.util.ArrayList;
import java.util.List;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.Wire;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class CompositeComponentExtensionAutowireTestCase extends TestCase {
    private CompositeComponent composite;
    private ServiceContract<?> contract;
    private ServiceContract<?> contract2;

    public void testAutowireAtomicComponent() throws Exception {
        InboundWire wire = EasyMock.createMock(InboundWire.class);
        EasyMock.expect(wire.getServiceContract()).andReturn(contract).atLeastOnce();
        EasyMock.expect(wire.getBindingType()).andReturn(Wire.LOCAL_BINDING).atLeastOnce();
        EasyMock.replay(wire);
        ServiceBinding binding = EasyMock.createMock(ServiceBinding.class);
        EasyMock.expect(binding.getInboundWire()).andReturn(wire).atLeastOnce();
        EasyMock.replay(binding);
        List<InboundWire> wires = new ArrayList<InboundWire>();
        wires.add(wire);
        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(component.getInboundWires()).andReturn(wires).atLeastOnce();
        EasyMock.expect(component.isSystem()).andReturn(false).atLeastOnce();
        EasyMock.expect(component.getName()).andReturn("foo").atLeastOnce();
        EasyMock.replay(component);
        composite.register(component);
        assertEquals(wire, composite.resolveAutowire(Foo.class));
    }

    public void testAutowireSystemAtomicComponent() throws Exception {
        InboundWire wire = EasyMock.createMock(InboundWire.class);
        EasyMock.expect(wire.getServiceContract()).andReturn(contract).atLeastOnce();
        EasyMock.expect(wire.getBindingType()).andReturn(Wire.LOCAL_BINDING).atLeastOnce();
        EasyMock.replay(wire);
        ServiceBinding binding = EasyMock.createMock(ServiceBinding.class);
        EasyMock.expect(binding.getInboundWire()).andReturn(wire).atLeastOnce();
        EasyMock.replay(binding);
        List<InboundWire> wires = new ArrayList<InboundWire>();
        wires.add(wire);
        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(component.getInboundWires()).andReturn(wires).atLeastOnce();
        EasyMock.expect(component.isSystem()).andReturn(true).atLeastOnce();
        EasyMock.expect(component.getName()).andReturn("foo").atLeastOnce();
        EasyMock.replay(component);
        composite.register(component);
        assertEquals(wire, composite.resolveSystemAutowire(Foo.class));
    }

    public void testAutowireSystemCompositeComponent() throws Exception {
        // configure service
        InboundWire wire = EasyMock.createMock(InboundWire.class);
        EasyMock.expect(wire.getServiceContract()).andReturn(contract).atLeastOnce();
        EasyMock.expect(wire.getBindingType()).andReturn(Wire.LOCAL_BINDING).atLeastOnce();
        EasyMock.replay(wire);
        ServiceBinding binding = EasyMock.createMock(ServiceBinding.class);
        EasyMock.expect(binding.getInboundWire()).andReturn(wire).atLeastOnce();
        EasyMock.replay(binding);
        Service service = EasyMock.createMock(Service.class);
        EasyMock.expect(service.getName()).andReturn("service").atLeastOnce();
        EasyMock.expect(service.isSystem()).andReturn(false).atLeastOnce();
        service.getServiceBindings();
        List<ServiceBinding> bindings = new ArrayList<ServiceBinding>();
        bindings.add(binding);
        EasyMock.expectLastCall().andReturn(bindings).atLeastOnce();
        EasyMock.replay(service);

        // configure system service
        InboundWire systemWire = EasyMock.createMock(InboundWire.class);
        EasyMock.expect(systemWire.getServiceContract()).andReturn(contract2).atLeastOnce();
        EasyMock.expect(systemWire.getBindingType()).andReturn(Wire.LOCAL_BINDING).atLeastOnce();
        EasyMock.replay(systemWire);
        ServiceBinding systemBinding = EasyMock.createMock(ServiceBinding.class);
        EasyMock.expect(systemBinding.getInboundWire()).andReturn(systemWire).atLeastOnce();
        EasyMock.replay(systemBinding);
        Service systemService = EasyMock.createMock(Service.class);
        EasyMock.expect(systemService.getName()).andReturn("systemService").atLeastOnce();
        EasyMock.expect(systemService.isSystem()).andReturn(true).atLeastOnce();
        systemService.getServiceBindings();
        List<ServiceBinding> systemBindings = new ArrayList<ServiceBinding>();
        systemBindings.add(systemBinding);
        EasyMock.expectLastCall().andReturn(systemBindings).atLeastOnce();
        EasyMock.replay(systemService);

        CompositeComponent child = new MockComposite(true);
        child.register(service);
        child.register(systemService);
        composite.register(child);
        // since the child is registered under the system hierarchy, its services should not be visible from the
        // applicaiton hierarchy
        assertNull(composite.resolveAutowire(Foo.class));
        assertEquals(systemWire, composite.resolveSystemAutowire(Bar.class));
    }

    public void testAutowireCompositeComponent() throws Exception {
        // configure service
        InboundWire wire = EasyMock.createMock(InboundWire.class);
        EasyMock.expect(wire.getServiceContract()).andReturn(contract).atLeastOnce();
        EasyMock.expect(wire.getBindingType()).andReturn(Wire.LOCAL_BINDING).atLeastOnce();
        EasyMock.replay(wire);
        ServiceBinding binding = EasyMock.createMock(ServiceBinding.class);
        EasyMock.expect(binding.getInboundWire()).andReturn(wire).atLeastOnce();
        EasyMock.replay(binding);
        Service service = EasyMock.createMock(Service.class);
        EasyMock.expect(service.getName()).andReturn("service").atLeastOnce();
        EasyMock.expect(service.isSystem()).andReturn(false).atLeastOnce();
        service.getServiceBindings();
        List<ServiceBinding> bindings = new ArrayList<ServiceBinding>();
        bindings.add(binding);
        EasyMock.expectLastCall().andReturn(bindings).atLeastOnce();
        EasyMock.replay(service);

        // configure system service
        InboundWire systemWire = EasyMock.createMock(InboundWire.class);
        EasyMock.expect(systemWire.getServiceContract()).andReturn(contract2).atLeastOnce();
        EasyMock.expect(systemWire.getBindingType()).andReturn(Wire.LOCAL_BINDING).atLeastOnce();
        EasyMock.replay(systemWire);
        ServiceBinding systemBinding = EasyMock.createMock(ServiceBinding.class);
        EasyMock.expect(systemBinding.getInboundWire()).andReturn(systemWire).atLeastOnce();
        EasyMock.replay(systemBinding);
        Service systemService = EasyMock.createMock(Service.class);
        EasyMock.expect(systemService.getName()).andReturn("systemService").atLeastOnce();
        EasyMock.expect(systemService.isSystem()).andReturn(true).atLeastOnce();
        systemService.getServiceBindings();
        List<ServiceBinding> systemBindings = new ArrayList<ServiceBinding>();
        systemBindings.add(systemBinding);
        EasyMock.expectLastCall().andReturn(systemBindings).atLeastOnce();
        EasyMock.replay(systemService);

        CompositeComponent child = new MockComposite();
        child.register(service);
        child.register(systemService);
        composite.register(child);
        // since the child is registered under the application hierarchy, its services should not be visible from the
        // system hierarchy
        assertEquals(wire, composite.resolveAutowire(Foo.class));
        assertNull(composite.resolveSystemAutowire(Bar.class));
    }

    public void testAutowireSystemService() throws Exception {
        InboundWire wire = EasyMock.createMock(InboundWire.class);
        EasyMock.expect(wire.getServiceContract()).andReturn(contract).atLeastOnce();
        EasyMock.expect(wire.getBindingType()).andReturn(Wire.LOCAL_BINDING).atLeastOnce();
        EasyMock.replay(wire);
        ServiceBinding binding = EasyMock.createMock(ServiceBinding.class);
        EasyMock.expect(binding.getInboundWire()).andReturn(wire).atLeastOnce();
        EasyMock.replay(binding);
        Service service = EasyMock.createMock(Service.class);
        EasyMock.expect(service.getName()).andReturn("service").atLeastOnce();
        EasyMock.expect(service.isSystem()).andReturn(true).atLeastOnce();
        service.getServiceBindings();
        List<ServiceBinding> bindings = new ArrayList<ServiceBinding>();
        bindings.add(binding);
        EasyMock.expectLastCall().andReturn(bindings).atLeastOnce();
        EasyMock.replay(service);
        composite.register(service);
        assertEquals(wire, composite.resolveSystemExternalAutowire(Foo.class));
    }

    public void testAutowireService() throws Exception {
        InboundWire wire = EasyMock.createMock(InboundWire.class);
        EasyMock.expect(wire.getServiceContract()).andReturn(contract).atLeastOnce();
        EasyMock.expect(wire.getBindingType()).andReturn(Wire.LOCAL_BINDING).atLeastOnce();
        EasyMock.replay(wire);
        ServiceBinding binding = EasyMock.createMock(ServiceBinding.class);
        EasyMock.expect(binding.getInboundWire()).andReturn(wire).atLeastOnce();
        EasyMock.replay(binding);
        Service service = EasyMock.createMock(Service.class);
        EasyMock.expect(service.getName()).andReturn("service").atLeastOnce();
        EasyMock.expect(service.isSystem()).andReturn(false).atLeastOnce();
        service.getServiceBindings();
        List<ServiceBinding> bindings = new ArrayList<ServiceBinding>();
        bindings.add(binding);
        EasyMock.expectLastCall().andReturn(bindings).atLeastOnce();
        EasyMock.replay(service);
        composite.register(service);
        assertEquals(wire, composite.resolveExternalAutowire(Foo.class));
    }

    public void testAutowireReference() throws Exception {
        InboundWire wire = EasyMock.createMock(InboundWire.class);
        EasyMock.expect(wire.getServiceContract()).andReturn(contract).atLeastOnce();
        EasyMock.expect(wire.getBindingType()).andReturn(Wire.LOCAL_BINDING).atLeastOnce();
        EasyMock.replay(wire);
        ReferenceBinding binding = EasyMock.createMock(ReferenceBinding.class);
        EasyMock.expect(binding.getInboundWire()).andReturn(wire).atLeastOnce();
        EasyMock.replay(binding);
        Reference reference = EasyMock.createMock(Reference.class);
        EasyMock.expect(reference.getName()).andReturn("service").atLeastOnce();
        EasyMock.expect(reference.isSystem()).andReturn(false).atLeastOnce();
        reference.getReferenceBindings();
        List<ReferenceBinding> bindings = new ArrayList<ReferenceBinding>();
        bindings.add(binding);
        EasyMock.expectLastCall().andReturn(bindings).atLeastOnce();
        EasyMock.replay(reference);
        composite.register(reference);
        assertEquals(wire, composite.resolveAutowire(Foo.class));
    }

    public void testAutowireSystemReference() throws Exception {
        InboundWire wire = EasyMock.createMock(InboundWire.class);
        EasyMock.expect(wire.getServiceContract()).andReturn(contract).atLeastOnce();
        EasyMock.expect(wire.getBindingType()).andReturn(Wire.LOCAL_BINDING).atLeastOnce();
        EasyMock.replay(wire);
        ReferenceBinding binding = EasyMock.createMock(ReferenceBinding.class);
        EasyMock.expect(binding.getInboundWire()).andReturn(wire).atLeastOnce();
        EasyMock.replay(binding);
        Reference reference = EasyMock.createMock(Reference.class);
        EasyMock.expect(reference.getName()).andReturn("service").atLeastOnce();
        EasyMock.expect(reference.isSystem()).andReturn(true).atLeastOnce();
        reference.getReferenceBindings();
        List<ReferenceBinding> bindings = new ArrayList<ReferenceBinding>();
        bindings.add(binding);
        EasyMock.expectLastCall().andReturn(bindings).atLeastOnce();
        EasyMock.replay(reference);
        composite.register(reference);
        assertEquals(wire, composite.resolveSystemAutowire(Foo.class));
    }


    protected void setUp() throws Exception {
        super.setUp();
        contract = new ServiceContract<Object>(Foo.class) {

        };
        contract2 = new ServiceContract<Object>(Bar.class) {

        };
        composite = new MockComposite();
    }

    private interface Foo {

    }

    private interface Bar {

    }

    private static class MockComposite extends CompositeComponentExtension {
        private boolean system;

        public MockComposite() throws URISyntaxException {
            super(new URI("foo"), null, null, null);
        }

        public MockComposite(boolean system) throws URISyntaxException {
            super(new URI("foo"), null, null, null);
            this.system = system;
        }

        public boolean isSystem() {
            return system;
        }

        public TargetInvoker createTargetInvoker(String targetName, Operation operation, InboundWire callbackWire)
            throws TargetInvokerCreationException {
            throw new UnsupportedOperationException();
        }

        public void setScopeContainer(ScopeContainer scopeContainer) {
            throw new UnsupportedOperationException();
        }
    }
}
