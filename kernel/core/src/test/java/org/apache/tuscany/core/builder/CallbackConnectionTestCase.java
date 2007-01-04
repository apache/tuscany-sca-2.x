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
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireService;

import junit.framework.TestCase;
import org.apache.tuscany.core.wire.InboundWireImpl;
import org.apache.tuscany.core.wire.OutboundWireImpl;
import org.apache.tuscany.core.wire.jdk.JDKWireService;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class CallbackConnectionTestCase extends TestCase {
    private Operation<Type> operation;
    private ServiceContract<Type> contract;
    private ConnectorImpl connector;

    public void testAtomicOutboundInboundCallbackConnect() throws Exception {
        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(component.isSystem()).andReturn(false).anyTimes();
        EasyMock.expect(component.getName()).andReturn("source").atLeastOnce();
        EasyMock.expect(component.createTargetInvoker(EasyMock.eq("bar"),
            EasyMock.isA(Operation.class),
            (InboundWire) EasyMock.isNull())).andReturn(EasyMock.createNiceMock(TargetInvoker.class));
        EasyMock.replay(component);

        InboundWire inboundWire = new InboundWireImpl();
        inboundWire.setContainer(component);
        inboundWire.setServiceContract(contract);
        OutboundWire outboundWire = new OutboundWireImpl();
        outboundWire.setContainer(component);
        outboundWire.setServiceContract(contract);

        InboundInvocationChain inboundChain = EasyMock.createMock(InboundInvocationChain.class);
        EasyMock.expect(inboundChain.getOperation()).andReturn(operation).anyTimes();
        Interceptor interceptor = EasyMock.createNiceMock(Interceptor.class);
        EasyMock.expect(inboundChain.getHeadInterceptor()).andReturn(interceptor);
        EasyMock.replay(inboundChain);
        Map<Operation<?>, InboundInvocationChain> chains = new HashMap<Operation<?>, InboundInvocationChain>();
        chains.put(operation, inboundChain);
        outboundWire.addTargetCallbackInvocationChains(chains);

        connector.connect(outboundWire, inboundWire, true);
        EasyMock.verify(inboundChain);
        EasyMock.verify(component);
    }

    public void testReferenceOutboundInboundCallbackConnect() throws Exception {
        ReferenceBinding binding = EasyMock.createMock(ReferenceBinding.class);
        EasyMock.expect(binding.isSystem()).andReturn(false).anyTimes();
        EasyMock.expect(binding.getName()).andReturn("source").atLeastOnce();
        EasyMock.expect(binding.createCallbackTargetInvoker(EasyMock.isA(ServiceContract.class),
            EasyMock.isA(Operation.class))).andReturn(EasyMock.createNiceMock(TargetInvoker.class));
        EasyMock.replay(binding);

        InboundWire inboundWire = new InboundWireImpl();
        inboundWire.setContainer(binding);
        inboundWire.setServiceContract(contract);
        OutboundWire outboundWire = new OutboundWireImpl();
        outboundWire.setContainer(binding);
        outboundWire.setServiceContract(contract);

        InboundInvocationChain inboundChain = EasyMock.createMock(InboundInvocationChain.class);
        EasyMock.expect(inboundChain.getOperation()).andReturn(operation).anyTimes();
        Interceptor interceptor = EasyMock.createNiceMock(Interceptor.class);
        EasyMock.expect(inboundChain.getHeadInterceptor()).andReturn(interceptor);
        EasyMock.replay(inboundChain);
        Map<Operation<?>, InboundInvocationChain> chains = new HashMap<Operation<?>, InboundInvocationChain>();
        chains.put(operation, inboundChain);
        outboundWire.addTargetCallbackInvocationChains(chains);

        connector.connect(outboundWire, inboundWire, true);
        EasyMock.verify(inboundChain);
        EasyMock.verify(binding);
    }

    public void testServiceOutboundInboundCallbackConnect() throws Exception {
        ServiceBinding binding = EasyMock.createMock(ServiceBinding.class);
        EasyMock.expect(binding.isSystem()).andReturn(false).anyTimes();
        EasyMock.expect(binding.getName()).andReturn("source").atLeastOnce();
        EasyMock.expect(binding.createCallbackTargetInvoker(EasyMock.isA(ServiceContract.class),
            EasyMock.isA(Operation.class))).andReturn(EasyMock.createNiceMock(TargetInvoker.class));
        EasyMock.replay(binding);

        InboundWire inboundWire = new InboundWireImpl();
        inboundWire.setContainer(binding);
        inboundWire.setServiceContract(contract);
        OutboundWire outboundWire = new OutboundWireImpl();
        outboundWire.setContainer(binding);
        outboundWire.setServiceContract(contract);

        InboundInvocationChain inboundChain = EasyMock.createMock(InboundInvocationChain.class);
        EasyMock.expect(inboundChain.getOperation()).andReturn(operation).anyTimes();
        Interceptor interceptor = EasyMock.createNiceMock(Interceptor.class);
        EasyMock.expect(inboundChain.getHeadInterceptor()).andReturn(interceptor);
        EasyMock.replay(inboundChain);
        Map<Operation<?>, InboundInvocationChain> chains = new HashMap<Operation<?>, InboundInvocationChain>();
        chains.put(operation, inboundChain);
        outboundWire.addTargetCallbackInvocationChains(chains);

        connector.connect(outboundWire, inboundWire, true);
        EasyMock.verify(inboundChain);
        EasyMock.verify(binding);
    }

    protected void setUp() throws Exception {
        super.setUp();
        WireService wireService = new JDKWireService(null, null);
        connector = new ConnectorImpl(wireService, null, null, null);
        operation = new Operation<Type>("bar", null, null, null);
        contract = new JavaServiceContract();
        Map<String, Operation<Type>> ops = new HashMap<String, Operation<Type>>();
        ops.put("bar", operation);
        contract.setCallbackOperations(ops);
    }

}
