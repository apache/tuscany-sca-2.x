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
package org.apache.tuscany.implementation.java.proxy;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import junit.framework.TestCase;

import org.apache.tuscany.assembly.Contract;
import org.apache.tuscany.core.component.SimpleWorkContext;
import org.apache.tuscany.core.wire.InvocationChainImpl;
import org.apache.tuscany.core.wire.WireImpl;
import org.apache.tuscany.implementation.java.context.ModelHelper;
import org.apache.tuscany.implementation.java.context.PojoWorkContextTunnel;
import org.apache.tuscany.implementation.java.proxy.JDKInvocationHandler;
import org.apache.tuscany.interfacedef.DataType;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.interfacedef.impl.DataTypeImpl;
import org.apache.tuscany.interfacedef.impl.OperationImpl;
import org.apache.tuscany.spi.Scope;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.InvocationRuntimeException;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.Wire;

/**
 * @version $Rev$ $Date$
 */
public class JDKInvocationHandlerTestCase extends TestCase {

    public void testToString() {
        Wire wire = new WireImpl();
        Contract contract = ModelHelper.createReference("foo", Foo.class);
        wire.setSourceContract(contract);
        wire.setSourceUri(URI.create("foo#bar"));
        JDKInvocationHandler handler = new JDKInvocationHandler(Foo.class, wire, null);
        Foo foo = (Foo) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Foo.class}, handler);
        assertNotNull(foo.toString());
    }

    public void testHashCode() {
        Wire wire = new WireImpl();
        Contract contract = ModelHelper.createReference("foo", Foo.class);
        wire.setSourceContract(contract);
        wire.setSourceUri(URI.create("foo#bar"));
        JDKInvocationHandler handler = new JDKInvocationHandler(Foo.class, wire, null);
        Foo foo = (Foo) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Foo.class}, handler);
        assertNotNull(foo.hashCode());
    }

    public void testConversational() throws Throwable {
        Wire wire = new WireImpl();
        DataType<Class> type1 = new DataTypeImpl<Class>(String.class, String.class);
        List<DataType> types = new ArrayList<DataType>();
        types.add(type1);
        DataType<List<DataType>> inputType1 = new DataTypeImpl<List<DataType>>(Object[].class, types);
        DataType<Class> outputType1 = new DataTypeImpl<Class>(String.class, String.class);
        Operation op1 = new OperationImpl("test");
        op1.setInputType(inputType1);
        op1.setOutputType(outputType1);
        Contract contract = ModelHelper.createReference("foo", Foo.class);
        op1.setInterface(contract.getInterfaceContract().getInterface());

        WorkContext wc = new SimpleWorkContext();
        PojoWorkContextTunnel.setThreadWorkContext(wc);
        try {
            MockInvoker invoker = new MockInvoker();

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
            handler.invoke(null, Foo.class.getMethod("test", String.class), new Object[]{"bar"});
            String currentConvID = (String) wc.getIdentifier(Scope.CONVERSATION);
            assertSame(convID, currentConvID);

            JDKInvocationHandler handler2 = new JDKInvocationHandler(Foo.class, wire, wc);
            handler2.invoke(null, Foo.class.getMethod("test", String.class), new Object[]{"bar"});
            currentConvID = (String) wc.getIdentifier(Scope.CONVERSATION);
            assertSame(convID, currentConvID);
        } finally {
            PojoWorkContextTunnel.setThreadWorkContext(null);
        }
    }

    private interface Foo {
        String test(String s);
    }

    private class MockInvoker implements TargetInvoker {

        private String currentConversationID;

        public void setCurrentConversationID(String id) {
            currentConversationID = id;
        }

        public Object invokeTarget(final Object payload, final short sequence, WorkContext workContext) throws InvocationTargetException {
            assertEquals("bar", Array.get(payload, 0));
            String convID = (String) workContext.getIdentifier(Scope.CONVERSATION);
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
