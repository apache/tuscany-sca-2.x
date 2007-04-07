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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.tuscany.assembly.ComponentService;
import org.apache.tuscany.assembly.impl.ComponentServiceImpl;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.interfacedef.impl.OperationImpl;
import org.apache.tuscany.interfacedef.java.JavaInterface;
import org.apache.tuscany.interfacedef.java.impl.JavaInterfaceImpl;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.ProxyService;
import org.apache.tuscany.spi.wire.Wire;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class WireObjectFactoryTestCase extends TestCase {

    @SuppressWarnings({"unchecked"})
    public void testCreateInstance() throws Exception {
        Operation op = new OperationImpl("hello");
        InvocationChain chain = new InvocationChainImpl(op);
        Wire wire = EasyMock.createMock(Wire.class);
        Map<Operation, InvocationChain> chains = new HashMap<Operation, InvocationChain>();
        chains.put(op, chain);
        EasyMock.expect(wire.getInvocationChains()).andReturn(chains);
        EasyMock.expect(wire.isOptimizable()).andReturn(false);
        EasyMock.replay(wire);
        ProxyService service = EasyMock.createMock(ProxyService.class);
        service.createProxy(EasyMock.eq(Foo.class), EasyMock.eq(wire), EasyMock.isA(Map.class));
        EasyMock.expectLastCall().andReturn(new Foo() {
            public void hello() {

            }
        });
        EasyMock.replay(service);

        WireObjectFactory<Foo> factory = new WireObjectFactory<Foo>(Foo.class, wire, service);
        factory.getInstance();
        EasyMock.verify(service);
        EasyMock.verify(wire);
    }

    @SuppressWarnings("unchecked")
    public void testOptimizedCreateInstance() throws Exception {
        ComponentService service = createContract(Foo.class);
        
        Wire wire = EasyMock.createMock(Wire.class);
        EasyMock.expect(wire.isOptimizable()).andReturn(true);
        EasyMock.expect(wire.getSourceContract()).andReturn(service).atLeastOnce();
        EasyMock.expect(wire.getInvocationChains()).andReturn((Map) Collections.emptyMap());
        EasyMock.expect(wire.getTargetInstance()).andReturn(new Foo() {
            public void hello() {
            }
        });
        EasyMock.replay(wire);
        WireObjectFactory<Foo> factory = new WireObjectFactory<Foo>(Foo.class, wire, null);
        factory.getInstance();
        EasyMock.verify(wire);

    }

    private ComponentService createContract(Class cls) {
        JavaInterface jInterface = new JavaInterfaceImpl();
        jInterface.setJavaClass(cls);
        ComponentService service = new ComponentServiceImpl();
        service.setInterface(jInterface);
        service.setName(cls.getSimpleName());
        return service;
    }

    /**
     * Verifies that a proxy is created when the required client contract is different than the wire contract
     */
    @SuppressWarnings("unchecked")
    public void testCannotOptimizeDifferentContractsCreateInstance() throws Exception {
        ComponentService contract = createContract(Object.class);
        Wire wire = EasyMock.createMock(Wire.class);
        EasyMock.expect(wire.isOptimizable()).andReturn(true);
        EasyMock.expect(wire.getSourceContract()).andReturn(contract).atLeastOnce();
        EasyMock.expect(wire.getInvocationChains()).andReturn((Map) Collections.emptyMap());
        EasyMock.replay(wire);
        ProxyService service = EasyMock.createMock(ProxyService.class);
        service.createProxy(EasyMock.eq(Foo.class), EasyMock.eq(wire), EasyMock.isA(Map.class));
        EasyMock.expectLastCall().andReturn(new Foo() {
            public void hello() {

            }
        });
        EasyMock.replay(service);

        WireObjectFactory<Foo> factory = new WireObjectFactory<Foo>(Foo.class, wire, service);
        factory.getInstance();
        EasyMock.verify(service);
        EasyMock.verify(wire);
    }

    @SuppressWarnings("unchecked")
    public void testNoJavaInterfaceCreateInstance() throws Exception {
        ComponentService contract = createContract(Object.class);
        Wire wire = EasyMock.createMock(Wire.class);
        EasyMock.expect(wire.isOptimizable()).andReturn(true);
        EasyMock.expect(wire.getSourceContract()).andReturn(contract).atLeastOnce();
        EasyMock.expect(wire.getInvocationChains()).andReturn((Map) Collections.emptyMap());
        EasyMock.replay(wire);
        ProxyService service = EasyMock.createMock(ProxyService.class);
        service.createProxy(EasyMock.eq(Foo.class), EasyMock.eq(wire), EasyMock.isA(Map.class));
        EasyMock.expectLastCall().andReturn(new Foo() {
            public void hello() {

            }
        });
        EasyMock.replay(service);

        WireObjectFactory<Foo> factory = new WireObjectFactory<Foo>(Foo.class, wire, service);
        factory.getInstance();
        EasyMock.verify(service);
        EasyMock.verify(wire);
    }

    private interface Foo {
        void hello();
    }


}
