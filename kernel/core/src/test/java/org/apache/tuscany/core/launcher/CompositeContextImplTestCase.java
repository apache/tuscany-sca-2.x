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
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.WireService;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class CompositeContextImplTestCase extends TestCase {

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
        EasyMock.replay(wire);
        Service child = EasyMock.createMock(Service.class);
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

    public void testReferenceLocate() throws Exception {
        InboundWire wire = EasyMock.createMock(InboundWire.class);
        EasyMock.replay(wire);
        Reference child = EasyMock.createMock(Reference.class);
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

    private class FooService {

    }

}
