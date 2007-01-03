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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.PrepareException;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;

import junit.framework.TestCase;
import org.apache.tuscany.core.builder.ConnectorImpl;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class CompositeComponentImplSystemWireTestCase extends TestCase {

    /**
     * Verifies system services in a CompositeComponentImpl are wired during the parent composite's prepare callback
     */
    public void testSystemServiceWire() throws Exception {
        InboundWire inbound = EasyMock.createMock(InboundWire.class);
        EasyMock.expect(inbound.getServiceContract()).andReturn(new JavaServiceContract(Foo.class)).atLeastOnce();
        inbound.getInvocationChains();
        EasyMock.expectLastCall().andReturn(Collections.emptyMap()).atLeastOnce();

        QualifiedName qName = new QualifiedName("target/bar");
        OutboundWire outbound = EasyMock.createMock(OutboundWire.class);
        EasyMock.expect(outbound.getTargetName()).andReturn(qName).atLeastOnce();
        EasyMock.expect(outbound.isAutowire()).andReturn(false);

        outbound.setTargetWire(EasyMock.eq(inbound));
        EasyMock.expect(outbound.getServiceContract()).andReturn(new JavaServiceContract(Foo.class)).atLeastOnce();
        List<OutboundWire> wires = new ArrayList<OutboundWire>();
        wires.add(outbound);
        Map<String, List<OutboundWire>> wireMap = new HashMap<String, List<OutboundWire>>();
        wireMap.put("ref", wires);
        CompositeComponent parent = new CompositeComponentImpl("foo", "foo", null, new ConnectorImpl(), null);
        AtomicComponent source = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(source.getScope()).andReturn(Scope.COMPOSITE).atLeastOnce();
        EasyMock.expect(source.getName()).andReturn("source").atLeastOnce();
        EasyMock.expect(source.getOutboundWires()).andReturn(wireMap);
        source.getInboundWires();
        EasyMock.expectLastCall().andReturn(Collections.emptyList()).atLeastOnce();
        EasyMock.expect(source.isSystem()).andReturn(true).atLeastOnce();
        EasyMock.expect(source.getParent()).andReturn(parent).atLeastOnce();

        source.prepare();
        EasyMock.replay(source);

        EasyMock.expect(outbound.getContainer()).andReturn(source).atLeastOnce();
        EasyMock.replay(outbound);

        parent.register(source);

        AtomicComponent target = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(target.getName()).andReturn("target").atLeastOnce();
        EasyMock.expect(target.getInboundWire("bar")).andReturn(inbound).atLeastOnce();
        List<InboundWire> inboundWires = new ArrayList<InboundWire>();
        inboundWires.add(inbound);
        EasyMock.expect(target.getInboundWires()).andReturn(inboundWires).atLeastOnce();
        EasyMock.expect(target.getScope()).andReturn(Scope.COMPOSITE).atLeastOnce();
        EasyMock.expect(target.getParent()).andReturn(parent).atLeastOnce();

        target.prepare();
        target.getOutboundWires();
        EasyMock.expectLastCall().andReturn(Collections.emptyMap());
        EasyMock.expect(target.isSystem()).andReturn(true).atLeastOnce();
        EasyMock.replay(target);

        EasyMock.expect(inbound.getContainer()).andReturn(target);
        EasyMock.replay(inbound);

        parent.register(target);
        parent.prepare();
        EasyMock.verify(source);
        EasyMock.verify(target);
        EasyMock.verify(inbound);
        EasyMock.verify(outbound);
    }


    /**
     * Verifies an application component cannot be wired to a system service in the same composite
     */
    public void testSystemServiceIsolationWire() throws Exception {
        InboundWire inbound = EasyMock.createMock(InboundWire.class);
        EasyMock.expect(inbound.getServiceContract()).andReturn(new JavaServiceContract(Foo.class)).atLeastOnce();
        EasyMock.replay(inbound);

        QualifiedName qName = new QualifiedName("target/bar");
        OutboundWire outbound = EasyMock.createMock(OutboundWire.class);
        EasyMock.expect(outbound.isAutowire()).andReturn(false);
        EasyMock.expect(outbound.getContainer()).andReturn(EasyMock.createNiceMock(SCAObject.class));
        EasyMock.expect(outbound.getReferenceName()).andReturn("foo");
        EasyMock.expect(outbound.getTargetName()).andReturn(qName).atLeastOnce();
        EasyMock.replay(outbound);

        List<OutboundWire> wires = new ArrayList<OutboundWire>();
        wires.add(outbound);
        Map<String, List<OutboundWire>> wireMap = new HashMap<String, List<OutboundWire>>();
        wireMap.put("ref", wires);
        CompositeComponent parent = new CompositeComponentImpl("foo", "foo", null, new ConnectorImpl(), null);
        AtomicComponent source = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(source.getName()).andReturn("source").atLeastOnce();
        List<InboundWire> inboundWires = new ArrayList<InboundWire>();
        inboundWires.add(inbound);
        source.getInboundWires();
        EasyMock.expectLastCall().andReturn(inboundWires).atLeastOnce();
        EasyMock.expect(source.getOutboundWires()).andReturn(wireMap);
        EasyMock.expect(source.isSystem()).andReturn(true).atLeastOnce();
        EasyMock.expect(source.getParent()).andReturn(parent).atLeastOnce();
        EasyMock.replay(source);

        parent.register(source);

        AtomicComponent target = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(target.getName()).andReturn("target").atLeastOnce();
        inboundWires.add(inbound);
        EasyMock.expect(target.getInboundWires()).andReturn(inboundWires).atLeastOnce();
        EasyMock.expect(target.isSystem()).andReturn(false).atLeastOnce();
        EasyMock.replay(target);

        parent.register(target);
        try {
            parent.prepare();
            fail();
        } catch (PrepareException e) {
            //expected
        }
        EasyMock.verify(source);
        EasyMock.verify(target);
        EasyMock.verify(inbound);
        EasyMock.verify(outbound);
    }


    private class Foo {

    }

}
