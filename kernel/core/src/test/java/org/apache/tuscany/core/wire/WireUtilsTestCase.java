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
import org.apache.tuscany.spi.wire.ChainHolder;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.Wire;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class WireUtilsTestCase extends TestCase {
    private Method m;

    public void testCreateInterfaceToWireMapping() throws Exception {
        Wire wire = new WireImpl();
        Operation<Type> op = new Operation<Type>("hello", null, null, null);
        InvocationChain chain = new InvocationChainImpl(op);
        wire.addInvocationChain(op, chain);
        Map<Method, ChainHolder> chains = WireUtils.createInterfaceToWireMapping(Foo.class, wire);
        assertEquals(1, chains.size());
        assertNotNull(chains.get(m));
    }

    public void testCreateInterfaceToWireMappingNoOperation() throws Exception {
        Wire wire = new WireImpl();
        Operation<Type> op = new Operation<Type>("goodbye", null, null, null);
        InvocationChain chain = new InvocationChainImpl(op);
        wire.addInvocationChain(op, chain);
        try {
            WireUtils.createInterfaceToWireMapping(Foo.class, wire);
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
