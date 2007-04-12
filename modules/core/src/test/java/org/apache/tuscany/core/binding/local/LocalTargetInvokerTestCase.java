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
package org.apache.tuscany.core.binding.local;

import java.net.URI;

import junit.framework.TestCase;

import org.apache.tuscany.assembly.Contract;
import org.apache.tuscany.assembly.impl.ComponentServiceImpl;
import org.apache.tuscany.core.wire.InvocationChainImpl;
import org.apache.tuscany.core.wire.WireImpl;
import org.apache.tuscany.interfacedef.InterfaceContract;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.interfacedef.impl.OperationImpl;
import org.apache.tuscany.interfacedef.java.JavaInterface;
import org.apache.tuscany.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.interfacedef.java.impl.DefaultJavaFactory;
import org.apache.tuscany.interfacedef.java.impl.JavaInterfaceContractImpl;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.Wire;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class LocalTargetInvokerTestCase extends TestCase {
    private InterfaceContract serviceContract;
    private Operation operation;

    public void testInvoke() {
        TargetInvoker targetInvoker = EasyMock.createMock(TargetInvoker.class);
        EasyMock.expect(targetInvoker.invoke(EasyMock.isA(Message.class))).andReturn(new MessageImpl());
        EasyMock.replay(targetInvoker);
        InvocationChain chain = new InvocationChainImpl(operation);
        chain.setTargetInvoker(targetInvoker);
        Wire wire = new WireImpl();
        wire.addInvocationChain(chain);
        wire.setSourceContract(serviceContract);
        TargetInvoker invoker = new LocalTargetInvoker(operation, wire);
        Message msg = invoker.invoke(new MessageImpl());
        assertFalse(msg.isFault());
        EasyMock.verify(targetInvoker);
    }

    public void testCallbackSetInvoke() {
        Contract contract = new ComponentServiceImpl();
        JavaInterface javaInterface = new DefaultJavaFactory().createJavaInterface();
        javaInterface.setJavaClass(Object.class);
        JavaInterfaceContract javaInterfaceContract = new DefaultJavaFactory().createJavaInterfaceContract();
        contract.setInterfaceContract(javaInterfaceContract);
        javaInterfaceContract.setCallbackInterface(javaInterface);
        TargetInvoker targetInvoker = EasyMock.createMock(TargetInvoker.class);
        EasyMock.expect(targetInvoker.invoke(EasyMock.isA(Message.class))).andReturn(new MessageImpl());
        EasyMock.replay(targetInvoker);

        InvocationChain chain = new InvocationChainImpl(operation);
        chain.setTargetInvoker(targetInvoker);
        InvocationChain callbackChain = new InvocationChainImpl(operation);
        Wire wire = new WireImpl();
        wire.addInvocationChain(chain);
        wire.addCallbackInvocationChain(callbackChain);
        wire.setSourceContract(serviceContract);
        URI uri = URI.create("foo");
        wire.setSourceUri(uri);
        wire.addInvocationChain(chain);
        TargetInvoker invoker = new LocalTargetInvoker(operation, wire);
        Message msg = EasyMock.createMock(Message.class);
        msg.pushCallbackUri(EasyMock.eq(uri));
        EasyMock.replay(msg);
        invoker.invoke(msg);
        EasyMock.verify(msg);
        EasyMock.verify(targetInvoker);
    }

    public void testFaultInvoke() {
        TargetInvoker targetInvoker = EasyMock.createMock(TargetInvoker.class);
        EasyMock.expect(targetInvoker.invoke(EasyMock.isA(Message.class))).andThrow(new TestException());
        EasyMock.replay(targetInvoker);

        InvocationChain chain = new InvocationChainImpl(operation);
        chain.setTargetInvoker(targetInvoker);
        Wire wire = new WireImpl();
        wire.setSourceContract(serviceContract);
        wire.addInvocationChain(chain);
        TargetInvoker invoker = new LocalTargetInvoker(operation, wire);
        Message msg = invoker.invoke(new MessageImpl());
        assertTrue(msg.isFault());
        assertTrue(msg.getBody() instanceof TestException);
        EasyMock.verify(targetInvoker);
    }

    protected void setUp() throws Exception {
        super.setUp();
        serviceContract = new JavaInterfaceContractImpl();
        operation = new OperationImpl();
        operation.setName("foo");
    }

    private class TestException extends RuntimeException {

    }
}
