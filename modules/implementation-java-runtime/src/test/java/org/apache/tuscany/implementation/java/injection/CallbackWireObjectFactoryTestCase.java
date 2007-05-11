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
package org.apache.tuscany.implementation.java.injection;

import static org.easymock.EasyMock.createMock;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.tuscany.sca.core.RuntimeWire;
import org.apache.tuscany.sca.core.invocation.CallbackWireObjectFactory;
import org.apache.tuscany.sca.invocation.ProxyFactory;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class CallbackWireObjectFactoryTestCase extends TestCase {

    @SuppressWarnings({"unchecked"})
    public void testCreateInstance() throws Exception {
        ProxyFactory service = createMock(ProxyFactory.class);
        Foo foo = new Foo() {
        };
        EasyMock.expect(service.createCallbackProxy(EasyMock.eq(Foo.class), EasyMock.isA(List.class))).andReturn(foo);
        EasyMock.replay(service);
        List<RuntimeWire> wires = new ArrayList<RuntimeWire>();
        CallbackWireObjectFactory factory = new CallbackWireObjectFactory(Foo.class, service, wires);
        assertEquals(foo, factory.getInstance());
        EasyMock.verify(service);
    }

    private interface Foo {

    }
}
