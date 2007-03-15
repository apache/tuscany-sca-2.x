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
package org.apache.tuscany.hessian.integration;

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.spi.model.physical.PhysicalOperationDefinition;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.Wire;

import junit.framework.TestCase;
import org.apache.tuscany.hessian.channel.LocalChannel;
import org.apache.tuscany.hessian.destination.LocalDestination;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class HessianLocalSendReceiveTestCase extends TestCase {

    public void testInvokeVoidReturn() throws Exception {
        Interceptor interceptor = EasyMock.createMock(Interceptor.class);
        EasyMock.expect(interceptor.invoke(EasyMock.isA(Message.class))).andReturn(new MessageImpl());
        EasyMock.replay(interceptor);

        PhysicalOperationDefinition operation = new PhysicalOperationDefinition();
        operation.setName("hello");

        InvocationChain chain = EasyMock.createMock(InvocationChain.class);
        EasyMock.expect(chain.getHeadInterceptor()).andReturn(interceptor);
        EasyMock.replay(chain);

        Map<PhysicalOperationDefinition, InvocationChain> chains =
            new HashMap<PhysicalOperationDefinition, InvocationChain>();
        chains.put(operation, chain);

        Wire wire = EasyMock.createMock(Wire.class);
        EasyMock.expect(wire.getPhysicalInvocationChains()).andReturn(chains);
        EasyMock.replay(wire);
        ClassLoader loader = getClass().getClassLoader();
        LocalDestination destination = new LocalDestination(wire, loader);
        LocalChannel channel = new LocalChannel(destination);
        channel.send("hello", null, new MessageImpl());
        EasyMock.verify(interceptor);
    }

}
