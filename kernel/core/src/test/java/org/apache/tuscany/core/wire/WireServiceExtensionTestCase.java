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
package org.apache.tuscany.core.wire;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.model.ServiceDefinition;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundChainHolder;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.ProxyCreationException;
import org.apache.tuscany.spi.wire.Wire;
import org.apache.tuscany.spi.wire.WireInvocationHandler;
import org.apache.tuscany.spi.wire.WireService;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.WorkContextImpl;

/**
 * @version $Rev$ $Date$
 */
public class WireServiceExtensionTestCase extends TestCase {
    private WireService wireService;
    private Operation<Type> operation;

    public void testCreateInboundChain() throws Exception {
        InboundInvocationChain chain = wireService.createInboundChain(operation);
        assertEquals(operation, chain.getOperation());
    }

    public void testCreateOutboundChain() throws Exception {
        OutboundInvocationChain chain = wireService.createOutboundChain(operation);
        assertEquals(operation, chain.getOperation());
    }

    public void testCreateWireServiceDefinition() throws Exception {
        ServiceDefinition definition = new ServiceDefinition();
        definition.setName("foo");
        ServiceContract<Type> contract = new ServiceContract<Type>() {
        };
        Map<String, Operation<Type>> operations = new HashMap<String, Operation<Type>>();
        operations.put("foo", operation);
        contract.setOperations(operations);
        definition.setServiceContract(contract);
        InboundWire wire = wireService.createWire(definition);
        assertEquals("foo", wire.getServiceName());
        assertEquals(1, wire.getInvocationChains().size());
        assertEquals(contract, wire.getServiceContract());
        InboundInvocationChain chain = wire.getInvocationChains().get(operation);
        assertEquals(operation, chain.getOperation());
    }

    protected void setUp() throws Exception {
        super.setUp();
        wireService = new TestWireService(new WorkContextImpl());
        operation = new Operation<Type>("foo", null, null, null);
    }

    private class TestWireService extends WireServiceExtension {
        protected TestWireService(WorkContext context) {
            super(context, null);
        }

        public <T> T createProxy(Class<T> interfaze, Wire wire) throws ProxyCreationException {
            return null;
        }

        public <T> T createProxy(Class<T> interfaze, Wire wire, Map<Method, OutboundChainHolder> mapping)
            throws ProxyCreationException {
            return null;
        }

        public Object createCallbackProxy(Class<?> interfaze, InboundWire wire) throws ProxyCreationException {
            return null;
        }

        public WireInvocationHandler createHandler(Class<?> interfaze, Wire wire) {
            return null;
        }
    }
}
