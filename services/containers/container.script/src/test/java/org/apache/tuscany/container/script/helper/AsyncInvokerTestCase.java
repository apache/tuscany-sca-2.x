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
package org.apache.tuscany.container.script.helper;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.getCurrentArguments;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.apache.tuscany.container.script.helper.AsyncInvoker;
import org.apache.tuscany.container.script.helper.AsyncMonitor;
import org.apache.tuscany.container.script.helper.ScriptHelperComponent;
import org.apache.tuscany.container.script.helper.ScriptHelperInstance;
import org.apache.tuscany.container.script.helper.AsyncInvoker.ContextBinder;
import org.apache.tuscany.container.script.helper.AsyncInvoker.ImmutableMessage;
import org.apache.tuscany.container.script.helper.mock.AsyncTarget;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.services.work.WorkScheduler;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.easymock.IAnswer;
import org.easymock.classextension.EasyMock;

/**
 */
public class AsyncInvokerTestCase extends TestCase {
    
    @SuppressWarnings("unchecked")
    public void testInvoke() throws Exception {
        ScriptHelperInstance instance = createMock(ScriptHelperInstance.class);
        expect(instance.invokeTarget("invoke", null)).andReturn(null).once();
        replay(instance);
        ScriptHelperComponent component = EasyMock.createMock(ScriptHelperComponent.class);
        expect(component.getTargetInstance()).andReturn(instance);
        EasyMock.replay(component);
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
        AsyncInvoker invoker = new AsyncInvoker("invoke", wire, component, scheduler, monitor, context);
        Message msg = new MessageImpl();
        invoker.invoke(msg);
        verify(instance);
    }
    
    public void testClone() {
        AsyncInvoker invoker = new AsyncInvoker(null, null, null,null,null,null);
        assertNotNull(invoker.clone());
    }

    public void testGetInstance() {
        ScriptHelperComponent component = EasyMock.createMock(ScriptHelperComponent.class);
        expect(component.getTargetInstance()).andReturn("petra");
        EasyMock.replay(component);
        AsyncInvoker invoker = new AsyncInvoker(null, null, component,null,null,null);
        assertEquals("petra", invoker.getInstance());
    }

    public void testGetInstanceCacheable() {
        ScriptHelperComponent component = EasyMock.createMock(ScriptHelperComponent.class);
        expect(component.getTargetInstance()).andReturn("petra");
        EasyMock.replay(component);
        AsyncInvoker invoker = new AsyncInvoker(null, null, component,null,null,null);
        invoker.setCacheable(true);
        assertEquals("petra", invoker.getInstance());
    }

    public void testGetBody() {
        ImmutableMessage message = new AsyncInvoker.ImmutableMessage();
        assertNull(message.getBody());
    }

    public void testSetBody() {
        ImmutableMessage message = new AsyncInvoker.ImmutableMessage();
        try {
            message.setBody(null);
            fail();
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

    public void testGetTargetInvoker() {
        ImmutableMessage message = new AsyncInvoker.ImmutableMessage();
        assertNull(message.getTargetInvoker());
    }

    public void testSetTargetInvoker() {
        ImmutableMessage message = new AsyncInvoker.ImmutableMessage();
        try {
            message.setTargetInvoker(null);
            fail();
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

    public void testGetFromAddress() {
        ImmutableMessage message = new AsyncInvoker.ImmutableMessage();
        assertNull(message.getFromAddress());
    }

    public void testSetFromAddress() {
        ImmutableMessage message = new AsyncInvoker.ImmutableMessage();
        try {
            message.setFromAddress(null);
            fail();
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

    public void testGetMessageId() {
        ImmutableMessage message = new AsyncInvoker.ImmutableMessage();
        assertNull(message.getMessageId());
    }

    public void testSetMessageId() {
        ImmutableMessage message = new AsyncInvoker.ImmutableMessage();
        try {
            message.setMessageId(null);
            fail();
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

    public void testGetCorrelationId() {
        ImmutableMessage message = new AsyncInvoker.ImmutableMessage();
        assertNull(message.getCorrelationId());
    }

    public void testSetCorrelationId() {
        ImmutableMessage message = new AsyncInvoker.ImmutableMessage();
        try {
            message.setCorrelationId(null);
            fail();
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

    public void testIsFault() {
        ImmutableMessage message = new AsyncInvoker.ImmutableMessage();
        assertFalse(message.isFault());
    }

    public void testSetBodyWithFault() {
        ImmutableMessage message = new AsyncInvoker.ImmutableMessage();
        try {
            message.setBodyWithFault(null);
            fail();
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

    public void testContextBinder() {
        ContextBinder contextBinder = new AsyncInvoker.ContextBinder();
        contextBinder.setContext(null);
        try {
            contextBinder.start();
            fail();
        } catch (AssertionError e) {
            // expected
        }
        try {
            contextBinder.stop();
            fail();
        } catch (AssertionError e) {
            // expected
        }
    }
}
