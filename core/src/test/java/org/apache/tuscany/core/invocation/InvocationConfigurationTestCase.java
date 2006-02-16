/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.invocation;

import java.lang.reflect.Method;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.core.invocation.impl.InvokerInterceptor;
import org.apache.tuscany.core.invocation.mock.MockHandler;
import org.apache.tuscany.core.invocation.mock.MockJavaOperationType;
import org.apache.tuscany.core.invocation.mock.MockStaticInvoker;
import org.apache.tuscany.core.invocation.mock.MockSyncInterceptor;
import org.apache.tuscany.core.invocation.mock.SimpleTarget;
import org.apache.tuscany.core.invocation.mock.SimpleTargetImpl;
import org.apache.tuscany.core.message.Message;
import org.apache.tuscany.core.message.channel.impl.MessageChannelImpl;
import org.apache.tuscany.core.message.impl.PojoMessageImpl;
import org.apache.tuscany.model.types.OperationType;

public class InvocationConfigurationTestCase extends TestCase {

    private Method hello;

    private Method goodbye;

    public InvocationConfigurationTestCase() {
        super();
    }

    public InvocationConfigurationTestCase(String arg0) {
        super(arg0);
    }

    public void setUp() throws Exception {
        hello = SimpleTarget.class.getMethod("hello", new Class[] { String.class });
        goodbye = SimpleTarget.class.getMethod("goodbye", new Class[] { String.class });
    }

    /**
     * Tests basic wiring of a source to a target, including handlers and interceptors
     */
    public void testInvokeWithHandlers() throws Exception {
        OperationType operation = new MockJavaOperationType(hello);
        InvocationConfiguration source = new InvocationConfiguration(operation);
        MockHandler sourceRequestHandler = new MockHandler();
        MockHandler sourceResponseHandler = new MockHandler();
        MockSyncInterceptor sourceInterceptor = new MockSyncInterceptor();
        source.addRequestHandler(sourceRequestHandler);
        source.addResponseHandler(sourceResponseHandler);
        source.addSourceInterceptor(sourceInterceptor);

        InvocationConfiguration target = new InvocationConfiguration(operation);
        MockHandler targetRequestHandler = new MockHandler();
        MockHandler targetResponseHandler = new MockHandler();
        MockSyncInterceptor targetInterceptor = new MockSyncInterceptor();
        target.addRequestHandler(targetRequestHandler);
        target.addResponseHandler(targetResponseHandler);
        target.addTargetInterceptor(targetInterceptor);
        target.addTargetInterceptor(new InvokerInterceptor());

        // connect the source to the target
        source.setTargetRequestChannel(new MessageChannelImpl(target.getRequestHandlers()));
        source.setTargetResponseChannel(new MessageChannelImpl(target.getResponseHandlers()));
        source.build();
        target.build();
        MockStaticInvoker invoker = new MockStaticInvoker(hello, new SimpleTargetImpl());
        source.setTargetInvoker(invoker);

        Message msg = new PojoMessageImpl();
        msg.setPayload("foo");
        msg.setTargetInvoker(invoker);
        Message response = (Message) source.getSourceInterceptor().invoke(msg);
        Assert.assertEquals("foo", response.getPayload());
        Assert.assertEquals(1, sourceRequestHandler.getCount());
        Assert.assertEquals(1, sourceResponseHandler.getCount());
        Assert.assertEquals(1, sourceInterceptor.getCount());
        Assert.assertEquals(1, targetRequestHandler.getCount());
        Assert.assertEquals(1, targetResponseHandler.getCount());
        Assert.assertEquals(1, targetInterceptor.getCount());
    }

    public void testInvokeWithRequestHandlers() throws Exception {
        OperationType operation = new MockJavaOperationType(hello);
        InvocationConfiguration source = new InvocationConfiguration(operation);
        MockHandler sourceRequestHandler = new MockHandler();
        MockSyncInterceptor sourceInterceptor = new MockSyncInterceptor();
        source.addRequestHandler(sourceRequestHandler);
        source.addSourceInterceptor(sourceInterceptor);

        InvocationConfiguration target = new InvocationConfiguration(operation);
        MockHandler targetRequestHandler = new MockHandler();
        MockSyncInterceptor targetInterceptor = new MockSyncInterceptor();
        target.addRequestHandler(targetRequestHandler);
        target.addTargetInterceptor(targetInterceptor);
        target.addTargetInterceptor(new InvokerInterceptor());

        // connect the source to the target
        source.setTargetRequestChannel(new MessageChannelImpl(target.getRequestHandlers()));
        source.setTargetResponseChannel(new MessageChannelImpl(target.getResponseHandlers()));
        source.build();
        target.build();
        MockStaticInvoker invoker = new MockStaticInvoker(hello, new SimpleTargetImpl());
        source.setTargetInvoker(invoker);

        Message msg = new PojoMessageImpl();
        msg.setPayload("foo");
        msg.setTargetInvoker(invoker);
        Message response = (Message) source.getSourceInterceptor().invoke(msg);
        Assert.assertEquals("foo", response.getPayload());
        Assert.assertEquals(1, sourceRequestHandler.getCount());
        Assert.assertEquals(1, sourceInterceptor.getCount());
        Assert.assertEquals(1, targetRequestHandler.getCount());
        Assert.assertEquals(1, targetInterceptor.getCount());
    }

    /**
     * Tests basic wiring of a source to a target, including handlers and interceptors
     */
    public void testInvokeWithInterceptorsOnly() throws Exception {
        OperationType operation = new MockJavaOperationType(hello);
        InvocationConfiguration source = new InvocationConfiguration(operation);
        MockSyncInterceptor sourceInterceptor = new MockSyncInterceptor();
        source.addSourceInterceptor(sourceInterceptor);

        InvocationConfiguration target = new InvocationConfiguration(operation);
        MockSyncInterceptor targetInterceptor = new MockSyncInterceptor();
        target.addTargetInterceptor(targetInterceptor);
        target.addTargetInterceptor(new InvokerInterceptor());

        // connect the source to the target
        source.addTargetInterceptor(target.getTargetInterceptor());
        source.build();
        target.build();
        MockStaticInvoker invoker = new MockStaticInvoker(hello, new SimpleTargetImpl());
        source.setTargetInvoker(invoker);

        Message msg = new PojoMessageImpl();
        msg.setPayload("foo");
        msg.setTargetInvoker(invoker);
        Message response = (Message) source.getSourceInterceptor().invoke(msg);
        Assert.assertEquals("foo", response.getPayload());
        Assert.assertEquals(1, sourceInterceptor.getCount());
        Assert.assertEquals(1, targetInterceptor.getCount());
    }
}
