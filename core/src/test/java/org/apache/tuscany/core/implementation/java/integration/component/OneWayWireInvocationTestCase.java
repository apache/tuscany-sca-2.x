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
package org.apache.tuscany.core.implementation.java.integration.component;

import java.lang.reflect.Method;
import java.util.Map;

import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.services.work.WorkScheduler;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.WireService;

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.java.AsyncJavaTargetInvoker;
import org.apache.tuscany.core.implementation.java.JavaAtomicComponent;
import org.apache.tuscany.core.implementation.java.mock.MockFactory;
import static org.apache.tuscany.core.implementation.java.mock.MockFactory.createServiceWire;
import org.apache.tuscany.core.implementation.java.mock.components.AsyncTarget;
import org.apache.tuscany.core.wire.jdk.JDKWireService;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.getCurrentArguments;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import org.easymock.IAnswer;

/**
 * Verifies non-blocking invocations are properly made through a wire
 *
 * @version $Rev$ $Date$
 */
public class OneWayWireInvocationTestCase extends TestCase {

    WireService wireService = new JDKWireService();

    public void testNoInterceptors() throws Exception {
        AsyncTarget target = createMock(AsyncTarget.class);
        target.invoke();
        expectLastCall().once();
        replay(target);
        JavaAtomicComponent component = MockFactory.createJavaComponent(target);
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
        AsyncJavaTargetInvoker invoker = new AsyncJavaTargetInvoker(method, null, component, scheduler, null, context);
        InboundWire<AsyncTarget> wire =
            createServiceWire("foo", AsyncTarget.class, null, null, null);
        Map<Operation, InboundInvocationChain> chains = wire.getInvocationChains();
        InboundInvocationChain chain = chains.get(wire.getServiceContract().getOperations().get("invoke"));
        chain.setTargetInvoker(invoker);
        chain.prepare();
        MessageImpl msg = new MessageImpl();
        msg.setTargetInvoker(invoker);
        chain.getHeadInterceptor().invoke(msg);
        verify(target);
    }
}
