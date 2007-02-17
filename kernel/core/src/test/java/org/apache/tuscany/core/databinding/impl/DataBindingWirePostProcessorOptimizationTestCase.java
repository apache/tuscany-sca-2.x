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

import org.apache.tuscany.spi.databinding.Mediator;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.Wire;

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
    private InvocationChain outboundChain;
    private InvocationChain callbackChain;
    private Wire wire;

    public void testNoInterceptorInterposed() {
        processor.process(wire);
        EasyMock.verify(outboundChain);
        EasyMock.verify(callbackChain);
    }

    protected void setUp() throws Exception {
        super.setUp();

        Mediator mediator = new MediatorImpl();
        processor = new DataBindingWirePostProcessor(mediator);

        ServiceContract<Type> contract = new JavaServiceContract(null);
        Operation<Type> operation = new Operation<Type>("test", null, null, null);
        operation.setServiceContract(contract);
        Map<String, Operation<Type>> operations = new HashMap<String, Operation<Type>>();
        operations.put("test", operation);
        contract.setOperations(operations);
        contract.setCallbackOperations(operations);

        outboundChain = EasyMock.createMock(InvocationChain.class);
        EasyMock.replay(outboundChain);
        Map<Operation<?>, InvocationChain> outboundChains = new HashMap<Operation<?>, InvocationChain>();
        outboundChains.put(operation, outboundChain);

        callbackChain = EasyMock.createMock(InvocationChain.class);
        EasyMock.replay(callbackChain);
        Map<Operation<?>, InvocationChain> callbackChains = new HashMap<Operation<?>, InvocationChain>();
        callbackChains.put(operation, callbackChain);

        wire = EasyMock.createMock(Wire.class);
        EasyMock.expect(wire.getInvocationChains()).andReturn(outboundChains);
        EasyMock.expect(wire.getSourceContract()).andReturn(contract).anyTimes();
        EasyMock.expect(wire.getTargetContract()).andReturn(contract).anyTimes();
        EasyMock.expect(wire.getCallbackInvocationChains()).andReturn(callbackChains).anyTimes();
        URI uri = URI.create("foo");
        EasyMock.expect(wire.getSourceUri()).andReturn(uri).anyTimes();

        EasyMock.replay(wire);

    }
}
