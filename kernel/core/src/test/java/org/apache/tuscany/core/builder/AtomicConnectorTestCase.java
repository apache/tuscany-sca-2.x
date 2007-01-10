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

import java.util.List;
import java.util.Map;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;

import org.apache.tuscany.core.implementation.composite.ServiceImpl;
import org.apache.tuscany.core.wire.InboundInvocationChainImpl;
import org.apache.tuscany.core.wire.InboundWireImpl;
import org.apache.tuscany.core.wire.OutboundInvocationChainImpl;
import org.apache.tuscany.core.wire.OutboundWireImpl;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class AtomicConnectorTestCase extends AbstractConnectorImplTestCase {

    public void testConnectSynchronousServiceWiresToAtomicTarget() throws Exception {
        AtomicComponent target = createAtomicTarget();

        // create the parent composite
        CompositeComponent parent = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(parent.getChild("target")).andReturn(target);
        EasyMock.replay(parent);

        InboundInvocationChain inboundChain = new InboundInvocationChainImpl(operation);
        InboundWire inboundWire = new InboundWireImpl();
        inboundWire.addInvocationChain(operation, inboundChain);
        inboundWire.setServiceContract(contract);

        OutboundInvocationChain outboundChain = new OutboundInvocationChainImpl(operation);
        OutboundWire outboundWire = new OutboundWireImpl();
        outboundWire.setTargetName(TARGET_SERVICE_NAME);
        outboundWire.addInvocationChain(operation, outboundChain);
        outboundWire.setServiceContract(contract);

        // create the binding
        ServiceBinding binding = EasyMock.createMock(ServiceBinding.class);
        EasyMock.expect(binding.getName()).andReturn("source");
        binding.setService(EasyMock.isA(Service.class));
        EasyMock.expect(binding.isSystem()).andReturn(false).atLeastOnce();
        EasyMock.expect(binding.getInboundWire()).andReturn(inboundWire).atLeastOnce();
        EasyMock.expect(binding.getOutboundWire()).andReturn(outboundWire);
        EasyMock.expect(binding.getScope()).andReturn(Scope.SYSTEM);
        EasyMock.replay(binding);

        outboundWire.setContainer(binding);
        inboundWire.setContainer(binding);

        Service service = new ServiceImpl("foo", parent, null);
        service.addServiceBinding(binding);

        connector.connect(service);
        MessageImpl msg = new MessageImpl();
        msg.setTargetInvoker(inboundChain.getTargetInvoker());
        Message resp = inboundChain.getHeadInterceptor().invoke(msg);
        assertEquals(RESPONSE, resp.getBody());
        EasyMock.verify(binding);
    }

    public void testConnectNonBlockingServiceWiresToAtomicTarget() throws Exception {
        // JFM FIXME
    }

    public void testConnectCallbackServiceWiresToAtomicTarget() throws Exception {
        // JFM FIXME
    }

    /**
     * Verifies connecting a wire from an atomic component to a target atomic component with one synchronous operation
     */
    public void testConnectAtomicComponentToAtomicComponentSyncWire() throws Exception {

        AtomicComponent target = createAtomicTarget();
        // create the parent composite
        CompositeComponent parent = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(parent.getChild("target")).andReturn(target);
        EasyMock.replay(parent);
        AtomicComponent source = createAtomicSource(parent);
        connector.connect(source);

        MessageImpl msg = new MessageImpl();
        Map<String, List<OutboundWire>> wires = source.getOutboundWires();
        OutboundWire wire = wires.get(TARGET_SERVICE).get(0);
        OutboundInvocationChain chain = wire.getInvocationChains().get(operation);
        msg.setTargetInvoker(chain.getTargetInvoker());
        Message resp = chain.getHeadInterceptor().invoke(msg);
        assertEquals(RESPONSE, resp.getBody());
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

}
