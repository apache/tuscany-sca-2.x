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
package org.apache.tuscany.core.implementation.composite;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.spi.model.Operation;
import static org.apache.tuscany.spi.model.Operation.NO_CONVERSATION;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class CompositeReferenceTestCase extends TestCase {

    public void testCreateTargetInvoker() throws Exception {

        MyServiceContract serviceContract = new MyServiceContract();
        CompositeReference compositeReference = new CompositeReference("testCompositeReferemce", null);
        Operation operation = new Operation<Type>("sayHi", null, null, null, false, null, NO_CONVERSATION);
        OutboundInvocationChain chain = EasyMock.createMock(OutboundInvocationChain.class);
        EasyMock.replay(chain);
        Map<Operation<?>, OutboundInvocationChain> chains = new HashMap<Operation<?>, OutboundInvocationChain>();
        chains.put(operation, chain);
        OutboundWire wire = EasyMock.createMock(OutboundWire.class);
        EasyMock.expect(wire.getInvocationChains()).andReturn(chains);
        EasyMock.expect(wire.getContainer()).andReturn(null);
        EasyMock.expect(wire.getServiceContract()).andReturn(serviceContract);
        EasyMock.replay(wire);
        compositeReference.setOutboundWire(wire);
        TargetInvoker targetInvoker = compositeReference.createTargetInvoker(serviceContract, operation);
        assertNotNull(targetInvoker);
    }

    public void testCreateCallbackTargetInvoker() throws Exception {
        MyServiceContract serviceContract = new MyServiceContract();
        CompositeReference compositeReference = new CompositeReference("testCompositeReferemce", null);
        Operation operation = new Operation<Type>("sayHi", null, null, null, false, null, NO_CONVERSATION);
        TargetInvoker targetInvoker = compositeReference.createCallbackTargetInvoker(serviceContract, operation);
        assertNotNull(targetInvoker);
    }

    class MyServiceContract extends ServiceContract {

    }
}
