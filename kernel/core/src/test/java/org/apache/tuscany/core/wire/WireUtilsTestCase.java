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
import java.util.Map;

import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundChainHolder;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class WireUtilsTestCase extends TestCase {
    private Method m;

    public void testCreateInterfaceToWireMapping() throws Exception {
        OutboundWire wire = new OutboundWireImpl();
        Operation<Type> op = new Operation<Type>("hello", null, null, null);
        OutboundInvocationChain chain = new OutboundInvocationChainImpl(op);
        wire.addOutboundInvocationChain(op, chain);
        Map<Method, OutboundChainHolder> chains = WireUtils.createInterfaceToWireMapping(Foo.class, wire);
        assertEquals(1, chains.size());
        assertNotNull(chains.get(m));
    }

    public void testCreateInterfaceToWireMappingNoOperation() throws Exception {
        OutboundWire wire = new OutboundWireImpl();
        Operation<Type> op = new Operation<Type>("goodbye", null, null, null);
        OutboundInvocationChain chain = new OutboundInvocationChainImpl(op);
        wire.addOutboundInvocationChain(op, chain);
        try {
            WireUtils.createInterfaceToWireMapping(Foo.class, wire);
            fail();
        } catch (NoMethodForOperationException e) {
            // expected
        }
    }

    public void testCreateInboundMapping() throws Exception {
        InboundWire wire = new InboundWireImpl();
        Operation<Type> op = new Operation<Type>("hello", null, null, null);
        InboundInvocationChain chain = new InboundInvocationChainImpl(op);
        wire.addInboundInvocationChain(op, chain);
        Map<Method, InboundInvocationChain> chains = WireUtils.createInboundMapping(wire, new Method[]{m});
        assertEquals(1, chains.size());
        assertNotNull(chains.get(m));
    }

    public void testCreateInboundMappingNoOperation() throws Exception {
        InboundWire wire = new InboundWireImpl();
        Operation<Type> op = new Operation<Type>("goodbye", null, null, null);
        InboundInvocationChain chain = new InboundInvocationChainImpl(op);
        wire.addInboundInvocationChain(op, chain);
        try {
            WireUtils.createInboundMapping(wire, new Method[]{m});
            fail();
        } catch (NoMethodForOperationException e) {
            // expected
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        m = Foo.class.getMethod("hello");
    }

    private interface Foo {
        void hello();
    }
}
