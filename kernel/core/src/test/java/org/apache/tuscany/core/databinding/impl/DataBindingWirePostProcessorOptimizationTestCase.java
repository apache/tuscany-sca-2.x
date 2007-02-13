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
package org.apache.tuscany.core.databinding.impl;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.databinding.Mediator;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * Verifies that data binding interceptor is not added to invocation chains when the data binding types are not set on
 * service contracts
 *
 * @version $Rev$ $Date$
 */
public class DataBindingWirePostProcessorOptimizationTestCase extends TestCase {
    private DataBindingWirePostProcessor processor;
    private OutboundInvocationChain outboundChain;
    private OutboundWire outboundWire;
    private InboundInvocationChain inboundChain;
    private InboundWire inboundWire;
    private SCAObject container;

    public void testNoInterceptorInterposedOutboundToInbound() {
        processor.process(container, outboundWire, container, inboundWire);
        EasyMock.verify(outboundChain);
        EasyMock.verify(inboundChain);
        EasyMock.verify(outboundWire);
        EasyMock.verify(inboundWire);
    }

    public void testNoInterceptorInterposedInboundToOutbound() {
        processor.process(container, inboundWire, container, outboundWire);
        EasyMock.verify(outboundChain);
        EasyMock.verify(inboundChain);
        EasyMock.verify(outboundWire);
        EasyMock.verify(inboundWire);
    }


    protected void setUp() throws Exception {
        super.setUp();
        container = EasyMock.createMock(SCAObject.class);
        EasyMock.replay(container);

        Mediator mediator = new MediatorImpl();
        processor = new DataBindingWirePostProcessor(mediator);

        ServiceContract<Type> contract = new JavaServiceContract(null);
        Operation<Type> operation = new Operation<Type>("test", null, null, null);
        operation.setServiceContract(contract);
        Map<String, Operation<Type>> operations = new HashMap<String, Operation<Type>>();
        operations.put("test", operation);
        contract.setOperations(operations);
        contract.setCallbackOperations(operations);

        inboundChain = EasyMock.createMock(InboundInvocationChain.class);
        EasyMock.replay(inboundChain);
        Map<Operation<?>, InboundInvocationChain> inboundChains = new HashMap<Operation<?>, InboundInvocationChain>();
        inboundChains.put(operation, inboundChain);

        outboundChain = EasyMock.createMock(OutboundInvocationChain.class);
        EasyMock.replay(outboundChain);
        Map<Operation<?>, OutboundInvocationChain> outboundChains =
            new HashMap<Operation<?>, OutboundInvocationChain>();
        outboundChains.put(operation, outboundChain);

        outboundWire = EasyMock.createMock(OutboundWire.class);
        EasyMock.expect(outboundWire.getOutboundInvocationChains()).andReturn(outboundChains);
        EasyMock.expect(outboundWire.getServiceContract()).andReturn(contract).anyTimes();
        EasyMock.expect(outboundWire.getTargetCallbackInvocationChains()).andReturn(inboundChains).anyTimes();
        URI uri = URI.create("foo");
        EasyMock.expect(outboundWire.getSourceUri()).andReturn(uri).anyTimes();

        EasyMock.replay(outboundWire);

        inboundWire = EasyMock.createMock(InboundWire.class);
        EasyMock.expect(inboundWire.getInboundInvocationChains()).andReturn(inboundChains);
        EasyMock.expect(inboundWire.getSourceCallbackInvocationChains(EasyMock.eq(uri))).andReturn(outboundChains)
            .anyTimes();
        EasyMock.replay(inboundWire);

    }
}
