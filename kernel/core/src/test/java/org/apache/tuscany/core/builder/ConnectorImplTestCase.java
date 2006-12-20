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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;

import junit.framework.TestCase;
import org.apache.tuscany.core.wire.SynchronousBridgingInterceptor;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class ConnectorImplTestCase extends TestCase {

    private ConnectorImpl connector;
    private ServiceContract contract;
    private Operation<Type> operation;
    private Interceptor headInterceptor;
    private Interceptor tailInterceptor;

    public void testConnectReferenceWires() throws Exception {

        // create the inbound wire and chain
        InboundInvocationChain inboundChain = EasyMock.createMock(InboundInvocationChain.class);
        EasyMock.expect(inboundChain.getOperation()).andReturn(operation).atLeastOnce();
        inboundChain.addInterceptor(EasyMock.isA(SynchronousBridgingInterceptor.class));
        inboundChain.setTargetInvoker(null);
        inboundChain.prepare();
        EasyMock.replay(inboundChain);
        Map<Operation<?>, InboundInvocationChain> inboundChains = new HashMap<Operation<?>, InboundInvocationChain>();
        inboundChains.put(operation, inboundChain);
        InboundWire inboundWire = EasyMock.createMock(InboundWire.class);
        EasyMock.expect(inboundWire.getServiceContract()).andReturn(contract).anyTimes();
        EasyMock.expect(inboundWire.getInvocationChains()).andReturn(inboundChains).atLeastOnce();
        EasyMock.replay(inboundWire);

        OutboundInvocationChain outboundChain = EasyMock.createMock(OutboundInvocationChain.class);
        EasyMock.expect(outboundChain.getHeadInterceptor()).andReturn(headInterceptor);
        EasyMock.replay(outboundChain);
        Map<Operation<?>, OutboundInvocationChain> outboundChains =
            new HashMap<Operation<?>, OutboundInvocationChain>();
        outboundChains.put(operation, outboundChain);
        OutboundWire outboundWire = EasyMock.createMock(OutboundWire.class);
        EasyMock.expect(outboundWire.getServiceContract()).andReturn(contract).anyTimes();
        EasyMock.expect(outboundWire.getTargetName()).andReturn(new QualifiedName("target/FooService")).anyTimes();
        EasyMock.expect(outboundWire.getInvocationChains()).andReturn(outboundChains).anyTimes();
        EasyMock.replay(outboundWire);

        Reference reference = EasyMock.createMock(Reference.class);
        EasyMock.expect(reference.getParent()).andReturn(null);
        EasyMock.expect(reference.createTargetInvoker(contract, operation)).andReturn(null);
        EasyMock.expect(reference.getInboundWire()).andReturn(inboundWire);
        EasyMock.expect(reference.getOutboundWire()).andReturn(outboundWire);
        EasyMock.replay(reference);

        connector.connect(reference);

        EasyMock.verify(reference);
        EasyMock.verify(inboundWire);
        EasyMock.verify(outboundWire);
        EasyMock.verify(inboundChain);
        EasyMock.verify(outboundChain);
    }

    public void testConnectServiceWires() throws Exception {
        // create the inbound wire and chain for the target
        InboundInvocationChain targetChain = EasyMock.createMock(InboundInvocationChain.class);
        EasyMock.expect(targetChain.getOperation()).andReturn(operation).atLeastOnce();
        EasyMock.expect(targetChain.getHeadInterceptor()).andReturn(headInterceptor);
        targetChain.prepare();
        EasyMock.replay(targetChain);
        Map<Operation<?>, InboundInvocationChain> targetChains = new HashMap<Operation<?>, InboundInvocationChain>();
        targetChains.put(operation, targetChain);
        InboundWire targetWire = EasyMock.createMock(InboundWire.class);
        EasyMock.expect(targetWire.getServiceContract()).andReturn(contract).anyTimes();
        EasyMock.expect(targetWire.getInvocationChains()).andReturn(targetChains);
        targetWire.getSourceCallbackInvocationChains("source");
        EasyMock.expectLastCall().andReturn(Collections.emptyMap());

        // create the target
        AtomicComponent target = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(target.getScope()).andReturn(Scope.COMPOSITE);
        EasyMock.expect(target.isSystem()).andReturn(false).atLeastOnce();
        target.getInboundWire(EasyMock.eq("FooService"));
        EasyMock.expectLastCall().andReturn(targetWire).atLeastOnce();
        target.createTargetInvoker(EasyMock.eq("FooService"), EasyMock.eq(operation), EasyMock.eq(targetWire));
        EasyMock.expectLastCall().andReturn(null);
        EasyMock.replay(target);

        EasyMock.expect(targetWire.getContainer()).andReturn(target);
        EasyMock.replay(targetWire);

        // create the parent composite
        CompositeComponent parent = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(parent.getChild("target")).andReturn(target);
        EasyMock.replay(parent);

        // create the inbound wire and chain for the source service
        InboundInvocationChain inboundChain = EasyMock.createMock(InboundInvocationChain.class);
        EasyMock.expect(inboundChain.getOperation()).andReturn(operation).atLeastOnce();
        inboundChain.addInterceptor(EasyMock.isA(SynchronousBridgingInterceptor.class));
        inboundChain.setTargetInvoker(null);
        EasyMock.replay(inboundChain);
        Map<Operation<?>, InboundInvocationChain> inboundChains = new HashMap<Operation<?>, InboundInvocationChain>();
        inboundChains.put(operation, inboundChain);
        InboundWire inboundWire = EasyMock.createMock(InboundWire.class);
        EasyMock.expect(inboundWire.getServiceContract()).andReturn(contract).anyTimes();
        EasyMock.expect(inboundWire.getInvocationChains()).andReturn(inboundChains).atLeastOnce();
        EasyMock.replay(inboundWire);

        // create the outbound wire and chain for the source service
        OutboundInvocationChain outboundChain = EasyMock.createMock(OutboundInvocationChain.class);
        EasyMock.expect(outboundChain.getHeadInterceptor()).andReturn(headInterceptor);
        outboundChain.setTargetInterceptor(EasyMock.isA(SynchronousBridgingInterceptor.class));
        outboundChain.prepare();
        outboundChain.setTargetInvoker(null);
        EasyMock.expect(outboundChain.getOperation()).andReturn(operation);
        EasyMock.replay(outboundChain);
        Map<Operation<?>, OutboundInvocationChain> outboundChains =
            new HashMap<Operation<?>, OutboundInvocationChain>();
        outboundChains.put(operation, outboundChain);
        OutboundWire outboundWire = EasyMock.createMock(OutboundWire.class);
        outboundWire.getTargetCallbackInvocationChains();
        EasyMock.expectLastCall().andReturn(Collections.emptyMap());
        EasyMock.expect(outboundWire.getServiceContract()).andReturn(contract).anyTimes();
        EasyMock.expect(outboundWire.getTargetName()).andReturn(new QualifiedName("target/FooService")).anyTimes();
        EasyMock.expect(outboundWire.getInvocationChains()).andReturn(outboundChains).anyTimes();

        // create the service
        Service service = EasyMock.createMock(Service.class);
        EasyMock.expect(service.getName()).andReturn("source");
        EasyMock.expect(service.isSystem()).andReturn(false).atLeastOnce();
        EasyMock.expect(service.getParent()).andReturn(parent).atLeastOnce();
        EasyMock.expect(service.getInboundWire()).andReturn(inboundWire).atLeastOnce();
        EasyMock.expect(service.getScope()).andReturn(Scope.SYSTEM);
        EasyMock.expect(service.getOutboundWire()).andReturn(outboundWire);
        EasyMock.replay(service);

        EasyMock.expect(outboundWire.getContainer()).andReturn(service);
        EasyMock.replay(outboundWire);

        connector.connect(service);

        EasyMock.verify(service);
        EasyMock.verify(inboundWire);
        EasyMock.verify(outboundWire);
        EasyMock.verify(inboundChain);
        EasyMock.verify(outboundChain);
    }

    /**
     * Verifies connecting a wire from an atomic component to a target atomic component with one synchronous operation
     */
    public void testConnectAtomicComponentToAtomicComponentSyncWire() throws Exception {

        // create the inbound wire and chain
        InboundInvocationChain inboundChain = EasyMock.createMock(InboundInvocationChain.class);
        EasyMock.expect(inboundChain.getOperation()).andReturn(operation).atLeastOnce();
        EasyMock.expect(inboundChain.getHeadInterceptor()).andReturn(headInterceptor);
        EasyMock.replay(inboundChain);
        Map<Operation<?>, InboundInvocationChain> inboundChains = new HashMap<Operation<?>, InboundInvocationChain>();
        inboundChains.put(operation, inboundChain);
        InboundWire targetWire = EasyMock.createMock(InboundWire.class);
        EasyMock.expect(targetWire.getServiceContract()).andReturn(contract).anyTimes();
        EasyMock.expect(targetWire.getInvocationChains()).andReturn(inboundChains);
        targetWire.getSourceCallbackInvocationChains("source");
        EasyMock.expectLastCall().andReturn(Collections.emptyMap());

        // create the target
        AtomicComponent target = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(target.getScope()).andReturn(Scope.COMPOSITE);
        target.getInboundWire(EasyMock.eq("FooService"));
        EasyMock.expectLastCall().andReturn(targetWire);
        target.createTargetInvoker(EasyMock.eq("FooService"), EasyMock.eq(operation), EasyMock.eq(targetWire));
        EasyMock.expectLastCall().andReturn(null);
        EasyMock.replay(target);

        EasyMock.expect(targetWire.getContainer()).andReturn(target);
        EasyMock.replay(targetWire);

        // create the parent composite
        CompositeComponent parent = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(parent.getChild("target")).andReturn(target);
        EasyMock.replay(parent);

        // create the outbound wire and chain from the source component
        OutboundInvocationChain outboundChain = EasyMock.createMock(OutboundInvocationChain.class);
        EasyMock.expect(outboundChain.getOperation()).andReturn(operation).atLeastOnce();
        outboundChain.setTargetInterceptor(EasyMock.isA(SynchronousBridgingInterceptor.class));
        outboundChain.setTargetInvoker(null);
        outboundChain.prepare();
        EasyMock.replay(outboundChain);
        Map<Operation<?>, OutboundInvocationChain> outboundChains =
            new HashMap<Operation<?>, OutboundInvocationChain>();
        outboundChains.put(operation, outboundChain);
        OutboundWire outboundWire = EasyMock.createMock(OutboundWire.class);
        EasyMock.expect(outboundWire.getServiceContract()).andReturn(contract).anyTimes();
        EasyMock.expect(outboundWire.getTargetName()).andReturn(new QualifiedName("target/FooService")).anyTimes();
        EasyMock.expect(outboundWire.getInvocationChains()).andReturn(outboundChains).anyTimes();
        outboundWire.getTargetCallbackInvocationChains();
        EasyMock.expectLastCall().andReturn(Collections.emptyMap());

        Map<String, List<OutboundWire>> outboundWires = new HashMap<String, List<OutboundWire>>();
        List<OutboundWire> list = new ArrayList<OutboundWire>();
        list.add(outboundWire);
        outboundWires.put("fooService", list);

        // create the source
        AtomicComponent source = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(source.isSystem()).andReturn(false).atLeastOnce();
        EasyMock.expect(source.getScope()).andReturn(Scope.COMPOSITE);
        EasyMock.expect(source.getParent()).andReturn(parent).atLeastOnce();
        EasyMock.expect(source.getOutboundWires()).andReturn(outboundWires);
        EasyMock.expect(source.getName()).andReturn("source");
        source.getInboundWires();
        EasyMock.expectLastCall().andReturn(Collections.emptyMap());
        EasyMock.replay(source);

        EasyMock.expect(outboundWire.getContainer()).andReturn(source);
        EasyMock.replay(outboundWire);

        connector.connect(source);
        EasyMock.verify(outboundWire);
        EasyMock.verify(targetWire);
        EasyMock.verify(outboundChain);
        EasyMock.verify(inboundChain);
        EasyMock.verify(source);
        EasyMock.verify(target);
    }

    public void testConnectInboundAtomicComponentWires() throws Exception {
        // create the inbound wire and chain
        InboundInvocationChain chain = EasyMock.createMock(InboundInvocationChain.class);
        EasyMock.expect(chain.getOperation()).andReturn(operation).atLeastOnce();
        chain.setTargetInvoker(null);
        chain.prepare();
        EasyMock.replay(chain);
        Map<Operation<?>, InboundInvocationChain> inboundChains = new HashMap<Operation<?>, InboundInvocationChain>();
        inboundChains.put(operation, chain);
        InboundWire wire = EasyMock.createMock(InboundWire.class);
        EasyMock.expect(wire.getServiceName()).andReturn("FooService");
        EasyMock.expect(wire.getServiceContract()).andReturn(contract).anyTimes();
        EasyMock.expect(wire.getInvocationChains()).andReturn(inboundChains);
        EasyMock.replay(wire);

        Map<String, InboundWire> wires = new HashMap<String, InboundWire>();
        wires.put("FooService", wire);

        AtomicComponent source = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(source.getParent()).andReturn(null);
        source.getOutboundWires();
        EasyMock.expectLastCall().andReturn(Collections.emptyMap());
        source.getInboundWires();
        EasyMock.expectLastCall().andReturn(wires);
        source.createTargetInvoker(EasyMock.eq("FooService"), EasyMock.eq(operation), (InboundWire) EasyMock.isNull());
        EasyMock.expectLastCall().andReturn(null);
        EasyMock.replay(source);

        connector.connect(source);

        EasyMock.verify(source);
        EasyMock.verify(wire);
        EasyMock.verify(chain);
    }

    public void testConnectTargetNotFound() throws Exception {
        CompositeComponent parent = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(parent.getName()).andReturn("parent");
        parent.getChild(EasyMock.isA(String.class));
        EasyMock.expectLastCall().andReturn(null);
        EasyMock.replay(parent);
        OutboundWire outboundWire = EasyMock.createMock(OutboundWire.class);
        EasyMock.expect(outboundWire.getServiceContract()).andReturn(contract).anyTimes();
        EasyMock.expect(outboundWire.getTargetName()).andReturn(new QualifiedName("target/FooService")).anyTimes();
        EasyMock.expect(outboundWire.getInvocationChains()).andReturn(null).anyTimes();
        EasyMock.expect(outboundWire.getReferenceName()).andReturn("nothtere");
        EasyMock.expect(outboundWire.getContainer()).andReturn(EasyMock.createNiceMock(SCAObject.class));
        outboundWire.getTargetCallbackInvocationChains();
        EasyMock.expectLastCall().andReturn(Collections.emptyMap());
        EasyMock.replay(outboundWire);
        Map<String, List<OutboundWire>> outboundWires = new HashMap<String, List<OutboundWire>>();
        List<OutboundWire> list = new ArrayList<OutboundWire>();
        list.add(outboundWire);
        outboundWires.put("fooService", list);

        // create the source
        AtomicComponent source = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(source.getOutboundWires()).andReturn(outboundWires);
        EasyMock.expect(source.isSystem()).andReturn(false);
        EasyMock.expect(source.getName()).andReturn("foo");
        EasyMock.expect(source.getParent()).andReturn(parent);
        EasyMock.replay(source);
        try {
            connector.connect(source);
            fail();
        } catch (TargetServiceNotFoundException e) {
            // expected
        }

        EasyMock.verify(source);
    }

    public void testOutboundToInboundOptimization() throws Exception {
        InboundWire inboundWire = EasyMock.createMock(InboundWire.class);
        EasyMock.expect(inboundWire.getContainer()).andReturn(null);
        inboundWire.getInvocationChains();
        EasyMock.expectLastCall().andReturn(Collections.emptyMap());
        EasyMock.replay(inboundWire);

        OutboundWire outboundWire = EasyMock.createMock(OutboundWire.class);
        outboundWire.getInvocationChains();
        EasyMock.expectLastCall().andReturn(Collections.emptyMap());
        outboundWire.setTargetWire(inboundWire);
        EasyMock.expect(outboundWire.getContainer()).andReturn(null);
        EasyMock.expect(outboundWire.getServiceContract()).andReturn(null);
        EasyMock.replay(outboundWire);

        connector.connect(outboundWire, inboundWire, true);
        EasyMock.verify(inboundWire);
        EasyMock.verify(outboundWire);
    }

    public void testOutboundToInboundChainConnect() {

        TargetInvoker invoker = EasyMock.createMock(TargetInvoker.class);
        EasyMock.replay(invoker);

        InboundInvocationChain inboundChain = EasyMock.createMock(InboundInvocationChain.class);
        EasyMock.expect(inboundChain.getHeadInterceptor()).andReturn(headInterceptor);
        EasyMock.replay(inboundChain);

        OutboundInvocationChain outboundChain = EasyMock.createMock(OutboundInvocationChain.class);
        outboundChain.prepare();
        outboundChain.setTargetInterceptor(EasyMock.isA(SynchronousBridgingInterceptor.class));
        outboundChain.setTargetInvoker(invoker);
        EasyMock.replay(outboundChain);
        connector.connect(outboundChain, inboundChain, invoker, false);
        EasyMock.verify(outboundChain);
    }

    public void testInboundToOutboundChainConnect() {
        InboundInvocationChain inboundChain = EasyMock.createMock(InboundInvocationChain.class);
        inboundChain.addInterceptor(EasyMock.isA(SynchronousBridgingInterceptor.class));
        EasyMock.replay(inboundChain);

        OutboundInvocationChain outboundChain = EasyMock.createMock(OutboundInvocationChain.class);
        EasyMock.expect(outboundChain.getHeadInterceptor()).andReturn(headInterceptor);
        outboundChain.prepare();
        outboundChain.setTargetInterceptor(headInterceptor);
        EasyMock.replay(outboundChain);
        connector.connect(inboundChain, outboundChain);
        EasyMock.verify(inboundChain);
    }

    public void testOutboundWireToInboundReferenceTarget() throws Exception {
        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(component.getName()).andReturn("foo");
        EasyMock.replay(component);
        // create the inbound wire and chain
        InboundInvocationChain inboundChain = EasyMock.createMock(InboundInvocationChain.class);
        EasyMock.expect(inboundChain.getOperation()).andReturn(operation).atLeastOnce();
        EasyMock.expect(inboundChain.getHeadInterceptor()).andReturn(headInterceptor);
        EasyMock.replay(inboundChain);
        Map<Operation<?>, InboundInvocationChain> inboundChains = new HashMap<Operation<?>, InboundInvocationChain>();
        inboundChains.put(operation, inboundChain);
        InboundWire targetWire = EasyMock.createMock(InboundWire.class);
        EasyMock.expect(targetWire.getServiceContract()).andReturn(contract).anyTimes();
        EasyMock.expect(targetWire.getInvocationChains()).andReturn(inboundChains);
        targetWire.getSourceCallbackInvocationChains("foo");
        EasyMock.expectLastCall().andReturn(Collections.emptyMap());
        Reference target = EasyMock.createMock(Reference.class);
        EasyMock.expect(target.createTargetInvoker(EasyMock.isA(ServiceContract.class), EasyMock.isA(Operation.class)))
            .andReturn(null);
        EasyMock.replay(target);

        EasyMock.expect(targetWire.getContainer()).andReturn(target);
        EasyMock.replay(targetWire);

        // create the outbound wire and chain from the source component
        OutboundInvocationChain outboundChain = EasyMock.createMock(OutboundInvocationChain.class);
        EasyMock.expect(outboundChain.getOperation()).andReturn(operation).atLeastOnce();
        outboundChain.setTargetInterceptor(EasyMock.isA(SynchronousBridgingInterceptor.class));
        outboundChain.setTargetInvoker(null);
        outboundChain.prepare();
        EasyMock.replay(outboundChain);
        Map<Operation<?>, OutboundInvocationChain> outboundChains =
            new HashMap<Operation<?>, OutboundInvocationChain>();
        outboundChains.put(operation, outboundChain);
        OutboundWire sourceWire = EasyMock.createMock(OutboundWire.class);
        EasyMock.expect(sourceWire.getContainer()).andReturn(component);
        EasyMock.expect(sourceWire.getServiceContract()).andReturn(contract).anyTimes();
        EasyMock.expect(sourceWire.getTargetName()).andReturn(new QualifiedName("target/FooService")).anyTimes();
        EasyMock.expect(sourceWire.getInvocationChains()).andReturn(outboundChains).anyTimes();
        sourceWire.getTargetCallbackInvocationChains();
        EasyMock.expectLastCall().andReturn(Collections.emptyMap());
        EasyMock.replay(sourceWire);

        connector.connect(sourceWire, targetWire, false);
        EasyMock.verify(inboundChain);
        EasyMock.verify(targetWire);
        EasyMock.verify(target);
    }

    protected void setUp() throws Exception {
        super.setUp();
        connector = new ConnectorImpl();
        contract = new JavaServiceContract(String.class);
        operation = new Operation<Type>("bar", null, null, null);
        headInterceptor = EasyMock.createMock(Interceptor.class);
        EasyMock.replay(headInterceptor);
        tailInterceptor = EasyMock.createMock(Interceptor.class);
        EasyMock.replay(tailInterceptor);
    }

}
