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
package org.apache.tuscany.core.wire.jdk;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.model.DataType;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.InvocationRuntimeException;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.Wire;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.wire.InvocationChainImpl;
import org.apache.tuscany.core.wire.WireImpl;

/**
 * @version $Rev$ $Date$
 */
public class JDKInvocationHandlerTestCase extends TestCase {

    public void testToString() {
        Wire wire = new WireImpl();
        ServiceContract contract = new JavaServiceContract(Foo.class);
        contract.setConversational(false);
        wire.setSourceContract(contract);
        wire.setSourceUri(URI.create("foo#bar"));
        JDKInvocationHandler handler = new JDKInvocationHandler(Foo.class, wire, null);
        Foo foo = (Foo) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Foo.class}, handler);
        assertNotNull(foo.toString());
    }

    public void testHashCode() {
        Wire wire = new WireImpl();
        ServiceContract contract = new JavaServiceContract(Foo.class);
        contract.setConversational(false);
        wire.setSourceContract(contract);
        wire.setSourceUri(URI.create("foo#bar"));
        JDKInvocationHandler handler = new JDKInvocationHandler(Foo.class, wire, null);
        Foo foo = (Foo) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Foo.class}, handler);
        assertNotNull(foo.hashCode());
    }

    public void testConversational() throws Throwable {
        Wire wire = new WireImpl();
        DataType<Type> type1 = new DataType<Type>(String.class, String.class);
        List<DataType<Type>> types = new ArrayList<DataType<Type>>();
        types.add(type1);
        DataType<List<DataType<Type>>> inputType1 = new DataType<List<DataType<Type>>>(Object[].class, types);
        DataType<Type> outputType1 = new DataType<Type>(String.class, String.class);
        Operation<Type> op1 = new Operation<Type>("test", inputType1, outputType1, null);
        ServiceContract<Type> contract = new JavaServiceContract(Foo.class);
        contract.setConversational(true);
        op1.setServiceContract(contract);

        WorkContext wc = new WorkContextImpl();
        MockInvoker invoker = new MockInvoker(wc);

        InvocationChain chain = new InvocationChainImpl(op1);
        chain.setTargetInvoker(invoker);
        wire.addInvocationChain(op1, chain);
        URI uri = URI.create("fooRef");
        wire.setSourceUri(uri);
        wire.setSourceContract(contract);

        String convID = UUID.randomUUID().toString();
        wc.setIdentifier(Scope.CONVERSATION, convID);
        invoker.setCurrentConversationID(convID);

        JDKInvocationHandler handler = new JDKInvocationHandler(Foo.class, wire, wc);
        handler.invoke(Foo.class.getMethod("test", String.class), new Object[]{"bar"});
        String currentConvID = (String) wc.getIdentifier(Scope.CONVERSATION);
        assertSame(convID, currentConvID);

        JDKInvocationHandler handler2 = new JDKInvocationHandler(Foo.class, wire, wc);
        handler2.invoke(Foo.class.getMethod("test", String.class), new Object[]{"bar"});
        currentConvID = (String) wc.getIdentifier(Scope.CONVERSATION);
        assertSame(convID, currentConvID);
    }

    private interface Foo {
        String test(String s);
    }

    private class MockInvoker implements TargetInvoker {

        private WorkContext wc;
        private String currentConversationID;

        public MockInvoker(WorkContext wc) {
            this.wc = wc;
        }

        public void setCurrentConversationID(String id) {
            currentConversationID = id;
        }

        public Object invokeTarget(final Object payload, final short sequence, WorkContext workContext) throws InvocationTargetException {
            assertEquals("bar", Array.get(payload, 0));
            String convID = (String) wc.getIdentifier(Scope.CONVERSATION);
            assertSame(convID, currentConversationID);
            return "response";
        }

        public Message invoke(Message msg) throws InvocationRuntimeException {
            fail();
            return null;
        }

        public boolean isCacheable() {
            return false;
        }

        public void setCacheable(boolean cacheable) {

        }

        public boolean isOptimizable() {
            return false;
        }

        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }
}
