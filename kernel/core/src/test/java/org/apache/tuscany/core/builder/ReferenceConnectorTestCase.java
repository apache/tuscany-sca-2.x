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

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;

import org.apache.tuscany.core.implementation.composite.ReferenceImpl;
import org.apache.tuscany.core.wire.InboundInvocationChainImpl;
import org.apache.tuscany.core.wire.InboundWireImpl;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.core.wire.OutboundInvocationChainImpl;
import org.apache.tuscany.core.wire.OutboundWireImpl;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class ReferenceConnectorTestCase extends AbstractConnectorImplTestCase {

    public void testConnectReferenceWiresNoInboundInterceptors() throws Exception {
        URI referenceUri = URI.create("foo");
        InboundInvocationChain inboundChain = new InboundInvocationChainImpl(operation);
        InboundWire inboundWire = new InboundWireImpl();
        inboundWire.setServiceContract(contract);
        inboundWire.addInvocationChain(operation, inboundChain);
        inboundWire.setUri(referenceUri);

        OutboundInvocationChain outboundChain = new OutboundInvocationChainImpl(operation);
        // Outbound chains always contains at least one interceptor
        outboundChain.addInterceptor(new InvokerInterceptor());
        OutboundWire outboundWire = new OutboundWireImpl();
        outboundWire.setServiceContract(contract);
        outboundWire.setTargetUri(TARGET_NAME);
        outboundWire.addInvocationChain(operation, outboundChain);
        outboundWire.setUri(referenceUri);

        ReferenceBinding referenceBinding = EasyMock.createMock(ReferenceBinding.class);
        referenceBinding.setReference(EasyMock.isA(Reference.class));
        EasyMock.expect(referenceBinding.createTargetInvoker(contract, operation)).andReturn(null);
        EasyMock.expect(referenceBinding.getInboundWire()).andReturn(inboundWire);
        EasyMock.expect(referenceBinding.getOutboundWire()).andReturn(outboundWire);
        EasyMock.replay(referenceBinding);

        Reference reference = new ReferenceImpl(referenceUri, contract);
        reference.addReferenceBinding(referenceBinding);

        connector.connect(reference);

        EasyMock.verify(referenceBinding);
        Interceptor interceptor = inboundChain.getHeadInterceptor();
        MessageImpl msg = new MessageImpl();
        msg.setTargetInvoker(new MockInvoker());
        Message resp = interceptor.invoke(msg);
        assertEquals(RESPONSE, resp.getBody());
    }

    public void testConnectReferenceWiresWithInboundInterceptors() throws Exception {
        URI referenceUri = URI.create("foo");
        MockInterceptor inboundInterceptor = new MockInterceptor();
        InboundInvocationChain inboundChain = new InboundInvocationChainImpl(operation);
        inboundChain.addInterceptor(inboundInterceptor);
        InboundWire inboundWire = new InboundWireImpl();
        inboundWire.setServiceContract(contract);
        inboundWire.addInvocationChain(operation, inboundChain);
        inboundWire.setUri(referenceUri);
        OutboundInvocationChain outboundChain = new OutboundInvocationChainImpl(operation);
        // Outbound always contains at lease one interceptor
        outboundChain.addInterceptor(new InvokerInterceptor());
        OutboundWire outboundWire = new OutboundWireImpl();
        outboundWire.setServiceContract(contract);
        outboundWire.setUri(referenceUri);
        outboundWire.setTargetUri(TARGET_NAME);
        outboundWire.addInvocationChain(operation, outboundChain);

        ReferenceBinding referenceBinding = EasyMock.createMock(ReferenceBinding.class);
        referenceBinding.setReference(EasyMock.isA(Reference.class));
        EasyMock.expect(referenceBinding.createTargetInvoker(contract, operation)).andReturn(null);
        EasyMock.expect(referenceBinding.getInboundWire()).andReturn(inboundWire);
        EasyMock.expect(referenceBinding.getOutboundWire()).andReturn(outboundWire);
        EasyMock.replay(referenceBinding);

        Reference reference = new ReferenceImpl(referenceUri, contract);
        reference.addReferenceBinding(referenceBinding);

        connector.connect(reference);

        EasyMock.verify(referenceBinding);
        Interceptor interceptor = inboundChain.getHeadInterceptor();
        MessageImpl msg = new MessageImpl();
        msg.setTargetInvoker(new MockInvoker());
        Message resp = interceptor.invoke(msg);
        assertEquals(RESPONSE, resp.getBody());
        assertTrue(inboundInterceptor.isInvoked());
    }

    public void testOutboundWireToInboundReferenceTarget() throws Exception {
        AtomicComponent source = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(source.getUri()).andReturn(URI.create("source"));
        EasyMock.replay(source);

        ReferenceBinding target = EasyMock.createMock(ReferenceBinding.class);
        EasyMock.expect(target.createTargetInvoker(EasyMock.isA(ServiceContract.class), EasyMock.isA(Operation.class)))
            .andReturn(new MockInvoker());
        EasyMock.replay(target);

        InboundInvocationChain inboundChain = new InboundInvocationChainImpl(operation);
        inboundChain.addInterceptor(new InvokerInterceptor());
        InboundWire targetWire = new InboundWireImpl();
        targetWire.setServiceContract(contract);
        targetWire.addInvocationChain(operation, inboundChain);
        targetWire.setUri(TARGET_NAME);
        // create the outbound wire and chain from the source component
        OutboundInvocationChain sourceChain = new OutboundInvocationChainImpl(operation);
        OutboundWire sourceWire = new OutboundWireImpl();
        sourceWire.setServiceContract(contract);
        sourceWire.setTargetUri(TARGET_NAME);
        sourceWire.addInvocationChain(operation, sourceChain);

        connector.connect(source, sourceWire, target, targetWire, false);
        Interceptor interceptor = sourceChain.getHeadInterceptor();
        MessageImpl msg = new MessageImpl();
        msg.setTargetInvoker(new MockInvoker());
        Message resp = interceptor.invoke(msg);
        assertEquals(RESPONSE, resp.getBody());
    }

    protected void setUp() throws Exception {
        super.setUp();
        AtomicComponent source = EasyMock.createNiceMock(AtomicComponent.class);
        EasyMock.replay(source);
    }

}
