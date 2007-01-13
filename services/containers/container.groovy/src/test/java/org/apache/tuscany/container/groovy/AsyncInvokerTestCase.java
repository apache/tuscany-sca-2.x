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
package org.apache.tuscany.container.groovy;

import java.lang.reflect.Method;

import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.services.work.WorkScheduler;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.extension.ExecutionMonitor;

import groovy.lang.GroovyObject;
import junit.framework.TestCase;
import org.apache.tuscany.container.groovy.mock.AsyncTarget;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.getCurrentArguments;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import org.easymock.IAnswer;
import org.easymock.classextension.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class AsyncInvokerTestCase extends TestCase {

    @SuppressWarnings("unchecked")
    public void testInvoke() throws Exception {
        GroovyObject instance = createMock(GroovyObject.class);
        expect(instance.invokeMethod("invoke", null)).andReturn(null).once();
        replay(instance);
        GroovyAtomicComponent component = EasyMock.createMock(GroovyAtomicComponent.class);
        expect(component.getTargetInstance()).andReturn(instance);
        EasyMock.replay(component);
        ExecutionMonitor monitor = createMock(ExecutionMonitor.class);
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
        GroovyInvoker invoker = new GroovyInvoker("invoke", component, wire, context, monitor);
        Message msg = new MessageImpl();
        invoker.invoke(msg);
        verify(instance);
    }

}
