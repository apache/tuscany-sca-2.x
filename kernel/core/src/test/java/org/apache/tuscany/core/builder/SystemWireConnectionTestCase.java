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
package org.apache.tuscany.core.builder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.ComponentManagerImpl;
import org.apache.tuscany.core.implementation.composite.CompositeComponentImpl;
import org.apache.tuscany.core.wire.InboundWireImpl;
import org.apache.tuscany.core.wire.OutboundWireImpl;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class SystemWireConnectionTestCase extends TestCase {

    /**
     * Verifies system services are wired
     */
    public void testWireOptimization() throws Exception {
        InboundWire inbound = new InboundWireImpl();
        inbound.setServiceContract(new JavaServiceContract(Foo.class));
        inbound.setUri(URI.create("scasystem://target#bar"));

        OutboundWire outbound = EasyMock.createMock(OutboundWire.class);
        EasyMock.expect(outbound.getTargetUri()).andReturn(URI.create("scasystem://target#bar")).atLeastOnce();
        EasyMock.expect(outbound.isAutowire()).andReturn(false);
        EasyMock.expect(outbound.getUri()).andReturn(URI.create("scasystem://target#bar"));
        outbound.getInvocationChains();
        EasyMock.expectLastCall().andReturn(Collections.emptyMap()).atLeastOnce();
        outbound.getTargetCallbackInvocationChains();
        EasyMock.expectLastCall().andReturn(Collections.emptyMap()).atLeastOnce();
        outbound.setTargetWire(EasyMock.eq(inbound));

        EasyMock.expect(outbound.getServiceContract()).andReturn(new JavaServiceContract(Foo.class)).atLeastOnce();
        outbound.setOptimizable(true);
        List<OutboundWire> wires = new ArrayList<OutboundWire>();
        wires.add(outbound);
        Map<String, List<OutboundWire>> wireMap = new HashMap<String, List<OutboundWire>>();
        wireMap.put("ref", wires);

        ComponentManagerImpl componentManager = new ComponentManagerImpl();
        ConnectorImpl connector = new ConnectorImpl(null, null, componentManager, null, null);
        CompositeComponent parent = new CompositeComponentImpl(URI.create("foo"), null, connector, null);

        AtomicComponent source = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(source.getScope()).andReturn(Scope.COMPOSITE).atLeastOnce();
        EasyMock.expect(source.getUri()).andReturn(URI.create("scasystem://source")).atLeastOnce();
        EasyMock.expect(source.getOutboundWires()).andReturn(wireMap);
        EasyMock.expect(source.getParent()).andReturn(parent).atLeastOnce();
        EasyMock.replay(source);

        EasyMock.expect(outbound.getContainer()).andReturn(source).atLeastOnce();
        EasyMock.replay(outbound);

        componentManager.register(source);

        AtomicComponent target = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(target.getUri()).andReturn(URI.create("scasystem://target")).atLeastOnce();
        EasyMock.expect(target.isOptimizable()).andReturn(true);
        EasyMock.expect(target.getTargetWire("bar")).andReturn(inbound).atLeastOnce();

        EasyMock.replay(target);
        inbound.setContainer(target);
        componentManager.register(target);

        connector.connect(source);

        EasyMock.verify(source);
        EasyMock.verify(target);
        EasyMock.verify(outbound);
    }


    /**
     * Verifies an application component cannot be wired to a service in a different scheme in the same composite
     */
    public void testSchemeIsolationWire() throws Exception {
        OutboundWire outbound = new OutboundWireImpl();// EasyMock.createMock(OutboundWire.class);
        outbound.setUri(URI.create("ref"));
        outbound.setTargetUri(URI.create("sca://target#bar"));
        List<OutboundWire> wires = new ArrayList<OutboundWire>();
        wires.add(outbound);
        Map<String, List<OutboundWire>> wireMap = new HashMap<String, List<OutboundWire>>();
        wireMap.put("ref", wires);

        ComponentManagerImpl componentManager = new ComponentManagerImpl();
        ConnectorImpl connector = new ConnectorImpl(null, null, componentManager, null, null);
        CompositeComponent parent = new CompositeComponentImpl(URI.create("foo"), null, connector, null);
        AtomicComponent source = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(source.getUri()).andReturn(URI.create("sca://source")).atLeastOnce();
        EasyMock.expect(source.getOutboundWires()).andReturn(wireMap);
        EasyMock.expect(source.getParent()).andReturn(parent).atLeastOnce();
        EasyMock.replay(source);


        componentManager.register(source);

        AtomicComponent target = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(target.getUri()).andReturn(URI.create("scaystem://target")).atLeastOnce();
        EasyMock.replay(target);

        componentManager.register(target);
        try {
            connector.connect(source);
            fail();
        } catch (TargetComponentNotFoundException e) {
            //expected
        }
        EasyMock.verify(source);
        EasyMock.verify(target);
    }


    private class Foo {

    }

}
