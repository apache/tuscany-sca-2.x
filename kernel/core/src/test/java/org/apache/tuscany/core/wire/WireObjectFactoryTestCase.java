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

import java.lang.reflect.Type;
import java.util.Map;

import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.WireService;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class WireObjectFactoryTestCase extends TestCase {

    @SuppressWarnings({"unchecked"})
    public void testCreateInstance() throws Exception {
        OutboundWire wire = new OutboundWireImpl();
        Operation<Type> op = new Operation<Type>("hello", null, null, null);
        OutboundInvocationChain chain = new OutboundInvocationChainImpl(op);
        wire.addInvocationChain(op, chain);

        WireService service = EasyMock.createMock(WireService.class);
        service.createProxy(EasyMock.eq(Foo.class), EasyMock.eq(wire), EasyMock.isA(Map.class));
        EasyMock.expectLastCall().andReturn(new Foo() {
            public void hello() {

            }
        });
        EasyMock.replay(service);

        WireObjectFactory<Foo> factory = new WireObjectFactory<Foo>(Foo.class, wire, service);
        factory.getInstance();
        EasyMock.verify(service);
    }


    private interface Foo {
        void hello();
    }

}
