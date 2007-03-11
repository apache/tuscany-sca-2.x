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
package org.apache.tuscany.core.integration.wire.oneway;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.concurrent.CountDownLatch;

import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.extension.ExecutionMonitor;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.services.work.WorkScheduler;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.TargetInvoker;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.implementation.java.JavaAtomicComponent;
import org.apache.tuscany.core.implementation.java.JavaTargetInvoker;
import org.apache.tuscany.core.services.work.jsr237.Jsr237WorkScheduler;
import org.apache.tuscany.core.services.work.jsr237.workmanager.ThreadPoolWorkManager;
import org.apache.tuscany.core.wire.InvocationChainImpl;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.core.wire.NonBlockingInterceptor;
import org.easymock.classextension.EasyMock;

/**
 * Verifies non-blocking invocations are properly made through a wire to a Java component
 *
 * @version $Rev$ $Date$
 */
public class OneWayWireToJavaInvocationTestCase extends TestCase {
    private WorkScheduler scheduler;
    private WorkContext context;
    private CountDownLatch latch;
    private InvocationChain ochain;
    private JavaTargetInvoker invoker;
    private JavaAtomicComponent component;
    private AsyncTarget target;

    public void testOneWay() throws Exception {
        MessageImpl msg = new MessageImpl();
        msg.setTargetInvoker(invoker);
        ochain.getHeadInterceptor().invoke(msg);
        latch.await();
        EasyMock.verify(target);
        EasyMock.verify(component);
    }

    protected void setUp() throws Exception {
        super.setUp();
        latch = new CountDownLatch(1);
        context = new WorkContextImpl();
        scheduler = new Jsr237WorkScheduler(new ThreadPoolWorkManager(1));
        target = EasyMock.createMock(AsyncTarget.class);
        target.invoke();
        EasyMock.expectLastCall().once();
        EasyMock.replay(target);
        component = EasyMock.createMock(JavaAtomicComponent.class);
        EasyMock.expect(component.getScope()).andReturn(Scope.COMPOSITE);
        EasyMock.expect(component.getTargetInstance()).andReturn(target);
        EasyMock.replay(component);
        Method method = AsyncTarget.class.getMethod("invoke");
        method.setAccessible(true);
        ExecutionMonitor monitor = EasyMock.createNiceMock(ExecutionMonitor.class);
        invoker = new JavaTargetInvoker(method, component, null, context);
        Operation<Type> operation = new Operation<Type>("invoke", null, null, null, false, null, TargetInvoker.NONE);
        ochain = new InvocationChainImpl(operation);
        NonBlockingInterceptor bridgeInterceptor = new NonBlockingInterceptor(scheduler, context);
        ochain.addInterceptor(bridgeInterceptor);
        InvocationChain ichain = new InvocationChainImpl(operation);
        WaitInterceptor waitInterceptor = new WaitInterceptor();
        InvokerInterceptor invokerInterceptor = new InvokerInterceptor();
        ichain.addInterceptor(waitInterceptor);
        ichain.addInterceptor(invokerInterceptor);
        bridgeInterceptor.setNext(waitInterceptor);
        ochain.setTargetInvoker(invoker);
    }

    public interface AsyncTarget {
        void invoke();
    }


    private class WaitInterceptor implements Interceptor {
        private Interceptor next;

        public Message invoke(Message msg) {
            msg = next.invoke(msg);
            latch.countDown();
            return msg;
        }

        public Interceptor getNext() {
            return next;
        }

        public void setNext(Interceptor next) {
            this.next = next;
        }

        public boolean isOptimizable() {
            return false;
        }
    }
}
