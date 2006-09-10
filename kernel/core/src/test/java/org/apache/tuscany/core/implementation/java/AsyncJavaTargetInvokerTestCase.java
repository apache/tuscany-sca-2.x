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
package org.apache.tuscany.core.implementation.java;

import java.lang.reflect.Method;

import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.services.work.WorkScheduler;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;

import junit.framework.TestCase;
import static org.apache.tuscany.core.implementation.java.mock.MockFactory.createJavaComponent;
import org.apache.tuscany.core.implementation.java.mock.components.AsyncTarget;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.getCurrentArguments;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import org.easymock.IAnswer;

/**
 * @version $Rev$ $Date$
 */
public class AsyncJavaTargetInvokerTestCase extends TestCase {

    public void testInvoke() throws Exception {
        AsyncTarget target = createMock(AsyncTarget.class);
        target.invoke();
        expectLastCall().once();
        replay(target);
        JavaAtomicComponent component = createJavaComponent(target);
        AsyncMonitor monitor = createMock(AsyncMonitor.class);
        replay(monitor);

        WorkScheduler scheduler = createMock(WorkScheduler.class);
        scheduler.scheduleWork(isA(Runnable.class));
        expectLastCall().andStubAnswer(new IAnswer() {
            public Object answer() throws Throwable {
                Runnable runnable = (Runnable) getCurrentArguments()[0];
                runnable.run();
                return null;
            }
        });
        replay(scheduler);
        WorkContext context = createMock(WorkContext.class);
        Method method = AsyncTarget.class.getMethod("invoke");
        method.setAccessible(true);
        InboundWire wire = createMock(InboundWire.class);
        AsyncJavaTargetInvoker invoker =
            new AsyncJavaTargetInvoker(method, wire, component, scheduler, monitor, context);
        Message msg = new MessageImpl();
        invoker.invoke(msg);
        verify(target);
    }

    public void testClone() throws Exception {
        AsyncTarget target = createMock(AsyncTarget.class);
        target.invoke();
        expectLastCall().once();
        replay(target);
        JavaAtomicComponent component = createJavaComponent(target);
        AsyncMonitor monitor = createMock(AsyncMonitor.class);
        replay(monitor);
        WorkScheduler scheduler = createMock(WorkScheduler.class);
        scheduler.scheduleWork(isA(Runnable.class));
        expectLastCall().andStubAnswer(new IAnswer() {
            public Object answer() throws Throwable {
                Runnable runnable = (Runnable) getCurrentArguments()[0];
                runnable.run();
                return null;
            }
        });
        replay(scheduler);
        WorkContext context = createMock(WorkContext.class);
        InboundWire wire = createMock(InboundWire.class);
        Method method = AsyncTarget.class.getMethod("invoke");
        method.setAccessible(true);
        AsyncJavaTargetInvoker invoker =
            new AsyncJavaTargetInvoker(method, wire, component, scheduler, monitor, context);
        AsyncJavaTargetInvoker clone = invoker.clone();
        Message msg = new MessageImpl();
        clone.invoke(msg);
        verify(target);
    }

}
