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
import java.util.List;
import java.util.Map;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;

import org.apache.tuscany.core.implementation.composite.ServiceImpl;
import org.apache.tuscany.core.mock.binding.MockServiceBinding;
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
        componentManager.register(target);
        // create the parent composite

        URI sourceUri = URI.create("source");
        InboundInvocationChain inboundChain = new InboundInvocationChainImpl(operation);
        InboundWire inboundWire = new InboundWireImpl();
        inboundWire.setSourceUri(sourceUri);
        inboundWire.addInvocationChain(operation, inboundChain);
        inboundWire.setServiceContract(contract);

        OutboundInvocationChain outboundChain = new OutboundInvocationChainImpl(operation);
        OutboundWire outboundWire = new OutboundWireImpl();
        outboundWire.setSourceUri(sourceUri);
        outboundWire.setTargetUri(TARGET_NAME);
        outboundWire.addInvocationChain(operation, outboundChain);
        outboundWire.setServiceContract(contract);

        // create the binding
        ServiceBinding binding = new MockServiceBinding(sourceUri);
        binding.setOutboundWire(outboundWire);
        binding.setInboundWire(inboundWire);

        Service service = new ServiceImpl(sourceUri, null);
        service.addServiceBinding(binding);

        connector.connect(service);
        MessageImpl msg = new MessageImpl();
        msg.setTargetInvoker(inboundChain.getTargetInvoker());
        Message resp = inboundChain.getHeadInterceptor().invoke(msg);
        assertEquals(RESPONSE, resp.getBody());
    }

    /**
     * Verifies connecting a wire from an atomic component to a target atomic component with one synchronous operation
     */
    public void testConnectAtomicComponentToAtomicComponentSyncWire() throws Exception {

        AtomicComponent target = createAtomicTarget();
        componentManager.register(target);
        // create the parent composite
        CompositeComponent parent = EasyMock.createMock(CompositeComponent.class);
        EasyMock.replay(parent);
        AtomicComponent source = createAtomicSource(parent);
        connector.connect(source);

        MessageImpl msg = new MessageImpl();
        Map<String, List<OutboundWire>> wires = source.getOutboundWires();
        OutboundWire wire = wires.get(TARGET_FRAGMENT).get(0);
        OutboundInvocationChain chain = wire.getInvocationChains().get(operation);
        msg.setTargetInvoker(chain.getTargetInvoker());
        Message resp = chain.getHeadInterceptor().invoke(msg);
        assertEquals(RESPONSE, resp.getBody());
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

}
