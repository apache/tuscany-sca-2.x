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
package org.apache.tuscany.core.launcher;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.WireService;

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.composite.ServiceImpl;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class CompositeContextImplTestCase extends TestCase {

    public void testGetName() throws Exception {
        CompositeComponent composite = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(composite.getName()).andReturn("foo");
        EasyMock.replay(composite);
        CompositeContextImpl context = new CompositeContextImpl(composite, null);
        assertEquals("foo", context.getCompositeName());
    }

    public void testAtomicLocate() throws Exception {
        InboundWire wire = EasyMock.createMock(InboundWire.class);
        EasyMock.replay(wire);
        AtomicComponent child = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(child.getInboundWire(FooService.class.getName())).andReturn(wire);
        EasyMock.replay(child);
        CompositeComponent composite = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(composite.getChild("Foo")).andReturn(child);
        EasyMock.replay(composite);

        WireService service = EasyMock.createMock(WireService.class);
        EasyMock.expect(service.createProxy(EasyMock.eq(FooService.class), EasyMock.eq(wire)))
            .andReturn(new FooService() {
            });
        EasyMock.replay(service);
        CompositeContextImpl context = new CompositeContextImpl(composite, service);
        context.locateService(FooService.class, "Foo");
        EasyMock.verify(service);
        EasyMock.verify(composite);
        EasyMock.verify(wire);
        EasyMock.verify(child);
    }

    public void testServiceLocate() throws Exception {
        InboundWire wire = EasyMock.createMock(InboundWire.class);
        EasyMock.expect(wire.getBindingType()).andReturn(InboundWire.LOCAL_BINDING);
        EasyMock.replay(wire);
        ServiceBinding binding = EasyMock.createMock(ServiceBinding.class);
        binding.setService(EasyMock.isA(Service.class));
        EasyMock.expect(binding.getInboundWire()).andReturn(wire).atLeastOnce();
        EasyMock.replay(binding);
        Service child = new ServiceImpl("Foo", null, null);
        child.addServiceBinding(binding);
        CompositeComponent composite = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(composite.getChild("Foo")).andReturn(child);
        EasyMock.replay(composite);

        WireService service = EasyMock.createMock(WireService.class);
        EasyMock.expect(service.createProxy(EasyMock.eq(FooService.class), EasyMock.eq(wire)))
            .andReturn(new FooService() {
            });
        EasyMock.replay(service);
        CompositeContextImpl context = new CompositeContextImpl(composite, service);
        context.locateService(FooService.class, "Foo");
        EasyMock.verify(service);
        EasyMock.verify(composite);
        EasyMock.verify(wire);
        EasyMock.verify(binding);
    }

    public void testReferenceLocate() throws Exception {
        InboundWire wire = EasyMock.createMock(InboundWire.class);
        EasyMock.replay(wire);
        ReferenceBinding child = EasyMock.createMock(ReferenceBinding.class);
        EasyMock.expect(child.getInboundWire()).andReturn(wire);
        EasyMock.replay(child);
        CompositeComponent composite = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(composite.getChild("Foo")).andReturn(child);
        EasyMock.replay(composite);

        WireService service = EasyMock.createMock(WireService.class);
        EasyMock.expect(service.createProxy(EasyMock.eq(FooService.class), EasyMock.eq(wire)))
            .andReturn(new FooService() {
            });
        EasyMock.replay(service);
        CompositeContextImpl context = new CompositeContextImpl(composite, service);
        context.locateService(FooService.class, "Foo");
        EasyMock.verify(service);
        EasyMock.verify(composite);
        EasyMock.verify(wire);
        EasyMock.verify(child);
    }


    public void testCompositeLocate() throws Exception {
        InboundWire wire = EasyMock.createMock(InboundWire.class);
        EasyMock.expect(wire.getBindingType()).andReturn(InboundWire.LOCAL_BINDING);
        EasyMock.replay(wire);
        ServiceBinding serviceBinding = EasyMock.createMock(ServiceBinding.class);
        serviceBinding.setService(EasyMock.isA(Service.class));
        EasyMock.expect(serviceBinding.getInboundWire()).andReturn(wire).atLeastOnce();
        EasyMock.replay(serviceBinding);
        Service service = new ServiceImpl("Foo", null, null);
        service.addServiceBinding(serviceBinding);
        CompositeComponent child = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(child.getChild("Bar")).andReturn(service);
        EasyMock.replay(child);
        CompositeComponent composite = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(composite.getChild("Foo")).andReturn(child);
        EasyMock.replay(composite);

        WireService wireService = EasyMock.createMock(WireService.class);
        EasyMock.expect(wireService.createProxy(EasyMock.eq(FooService.class), EasyMock.eq(wire)))
            .andReturn(new FooService() {
            });
        EasyMock.replay(wireService);
        CompositeContextImpl context = new CompositeContextImpl(composite, wireService);
        context.locateService(FooService.class, "Foo/Bar");
        EasyMock.verify(wireService);
        EasyMock.verify(composite);
        EasyMock.verify(wire);
        EasyMock.verify(child);
        EasyMock.verify(serviceBinding);
    }

    private class FooService {

    }

}
