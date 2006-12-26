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
package org.apache.tuscany.spi.extension;

import static org.apache.tuscany.spi.model.Operation.NO_CONVERSATION;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;

import junit.framework.TestCase;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;

/**
 * @version $Rev$ $Date$
 */
public class ReferenceTestCase extends TestCase {

    public void testScope() throws Exception {
        TestReference ref = new TestReference<Object>(Object.class);
        assertEquals(Scope.SYSTEM, ref.getScope());
    }

    public void testSetGetInterface() throws Exception {
        TestReference<TestReference> ref = new TestReference<TestReference>(TestReference.class);
        assertEquals(TestReference.class, ref.getInterface());

    }

    public void testPrepare() throws Exception {
        InboundInvocationChain chain = createMock(InboundInvocationChain.class);
        Operation<Type> operation = new Operation<Type>("test", null, null, null, false, null, NO_CONVERSATION);
        chain.setTargetInvoker(null);
        expectLastCall();
        chain.getOperation();
        expectLastCall().andReturn(operation);
        chain.prepare();
        expectLastCall();
        InboundWire wire = createMock(InboundWire.class);
        wire.getInvocationChains();
        Map<Operation, InvocationChain> chains = new HashMap<Operation, InvocationChain>();
        chains.put(operation, chain);
        expectLastCall().andReturn(chains);
        OutboundWire outboundWire = createMock(OutboundWire.class);
        outboundWire.getTargetName();
        expectLastCall().andReturn(new QualifiedName("foo/bar"));
        replay(chain);
        replay(wire);
        replay(outboundWire);
        TestReference<Object> ref = new TestReference<Object>(Object.class);
        ref.setInboundWire(wire);
        ref.setOutboundWire(outboundWire);
        ref.prepare();
    }

    private class TestReference<T> extends ReferenceExtension {
        public TestReference(Class<T> clazz) {
            super(null, clazz, null);
        }

        public TargetInvoker createTargetInvoker(ServiceContract contract, Operation operation) {
            return null;
        }


    }
}
