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
package org.apache.tuscany.core.async.invocation;

import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.core.async.wire.mock.MockHandler;
import org.apache.tuscany.core.async.wire.mock.MockStaticInvoker;
import org.apache.tuscany.core.async.wire.mock.MockSyncInterceptor;
import org.apache.tuscany.core.async.wire.mock.SimpleTarget;
import org.apache.tuscany.core.async.wire.mock.SimpleTargetImpl;
import org.apache.tuscany.core.async.work.DefaultWorkManager;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.core.message.impl.MessageFactoryImpl;
import org.apache.tuscany.core.wire.SourceInvocationConfiguration;
import org.apache.tuscany.core.wire.TargetInvocationConfiguration;
import org.apache.tuscany.core.wire.impl.InvokerInterceptor;
import org.apache.tuscany.core.wire.impl.MessageChannelImpl;

public class AsyncInvocationConfigurationTestCase extends TestCase {

    private DefaultWorkManager workManager;
    private Method hello;

    private MessageFactory factory = new MessageFactoryImpl();

    public AsyncInvocationConfigurationTestCase() {
        super();
    }

    public AsyncInvocationConfigurationTestCase(String arg0) {
        super(arg0);
    }

    protected void setUp() throws Exception {
        hello = SimpleTarget.class.getMethod("hello", String.class);

        workManager=new DefaultWorkManager();
        workManager.setScheduledMaximumPoolSize(5);
        workManager.init();
    }

    protected void tearDown() throws Exception {
        workManager.destroy();

        super.tearDown();
    }

    /**
     * Tests basic wiring of a source to a target, including handlers and interceptors
     */
    public void testInvokeWithHandlers() throws Exception {
        SourceInvocationConfiguration source = new SourceInvocationConfiguration(hello);

        source.addInterceptor(new AsyncInterceptor(workManager, factory));

        MockHandler sourceRequestHandler = new MockHandler();
        MockHandler sourceResponseHandler = new MockHandler();
        MockSyncInterceptor sourceInterceptor = new MockSyncInterceptor();
        source.addRequestHandler(sourceRequestHandler);
        source.addResponseHandler(sourceResponseHandler);
        source.addInterceptor(sourceInterceptor);

        TargetInvocationConfiguration target = new TargetInvocationConfiguration(hello);
        MockHandler targetRequestHandler = new MockHandler();
        MockHandler targetResponseHandler = new MockHandler();
        MockSyncInterceptor targetInterceptor = new MockSyncInterceptor();
        target.addRequestHandler(targetRequestHandler);
        target.addResponseHandler(targetResponseHandler);
        target.addInterceptor(targetInterceptor);
        target.addInterceptor(new InvokerInterceptor());

        // connect the source to the target
        source.setTargetRequestChannel(new MessageChannelImpl(target.getRequestHandlers()));
        source.setTargetResponseChannel(new MessageChannelImpl(target.getResponseHandlers()));
        source.build();
        target.build();

        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal = new CountDownLatch(1);
        MockStaticInvoker invoker = new MockStaticInvoker(hello, new SimpleTargetImpl(startSignal, doneSignal));
        source.setTargetInvoker(invoker);

        Message msg = factory.createMessage();
        msg.setBody("foo");
        msg.setTargetInvoker(invoker);
        Message response = source.getHeadInterceptor().invoke(msg);
        startSignal.countDown();
        doneSignal.await();

        Assert.assertEquals(null, response.getBody());
        Assert.assertEquals(1, sourceRequestHandler.getCount());
        //FIXME why isn't the responseHandler invoked?
        //Assert.assertEquals(1, sourceResponseHandler.getCount());
        Assert.assertEquals(1, sourceInterceptor.getCount());
        Assert.assertEquals(1, targetRequestHandler.getCount());
        //FIXME
        //Assert.assertEquals(1, targetResponseHandler.getCount());
        Assert.assertEquals(1, targetInterceptor.getCount());
    }

    public void testInvokeWithRequestHandlers() throws Exception {
        SourceInvocationConfiguration source = new SourceInvocationConfiguration(hello);

        source.addInterceptor(new AsyncInterceptor(workManager, factory));

        MockHandler sourceRequestHandler = new MockHandler();
        MockSyncInterceptor sourceInterceptor = new MockSyncInterceptor();
        source.addRequestHandler(sourceRequestHandler);
        source.addInterceptor(sourceInterceptor);

        TargetInvocationConfiguration target = new TargetInvocationConfiguration(hello);
        MockHandler targetRequestHandler = new MockHandler();
        MockSyncInterceptor targetInterceptor = new MockSyncInterceptor();
        target.addRequestHandler(targetRequestHandler);
        target.addInterceptor(targetInterceptor);
        target.addInterceptor(new InvokerInterceptor());

        // connect the source to the target
        source.setTargetRequestChannel(new MessageChannelImpl(target.getRequestHandlers()));
        source.setTargetResponseChannel(new MessageChannelImpl(target.getResponseHandlers()));
        source.build();
        target.build();

        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal = new CountDownLatch(1);
        MockStaticInvoker invoker = new MockStaticInvoker(hello, new SimpleTargetImpl(startSignal, doneSignal));
        source.setTargetInvoker(invoker);

        Message msg = factory.createMessage();
        msg.setBody("foo");
        msg.setTargetInvoker(invoker);
        Message response = source.getHeadInterceptor().invoke(msg);
        startSignal.countDown();
        doneSignal.await();

        Assert.assertEquals(null, response.getBody());
        Assert.assertEquals(1, sourceRequestHandler.getCount());
        Assert.assertEquals(1, sourceInterceptor.getCount());
        Assert.assertEquals(1, targetRequestHandler.getCount());
        Assert.assertEquals(1, targetInterceptor.getCount());
    }

    /**
     * Tests basic wiring of a source to a target, including handlers and interceptors
     */
    public void testInvokeWithInterceptorsOnly() throws Exception {
        SourceInvocationConfiguration source = new SourceInvocationConfiguration(hello);

        source.addInterceptor(new AsyncInterceptor(workManager, factory));

        MockSyncInterceptor sourceInterceptor = new MockSyncInterceptor();
        source.addInterceptor(sourceInterceptor);

        TargetInvocationConfiguration target = new TargetInvocationConfiguration(hello);
        MockSyncInterceptor targetInterceptor = new MockSyncInterceptor();
        target.addInterceptor(targetInterceptor);
        target.addInterceptor(new InvokerInterceptor());

        // connect the source to the target
        source.setTargetInterceptor(target.getHeadInterceptor());
        source.build();
        target.build();

        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal = new CountDownLatch(1);
        MockStaticInvoker invoker = new MockStaticInvoker(hello, new SimpleTargetImpl(startSignal, doneSignal));
        source.setTargetInvoker(invoker);

        Message msg = factory.createMessage();
        msg.setBody("foo");
        msg.setTargetInvoker(invoker);
        Message response = source.getHeadInterceptor().invoke(msg);
        startSignal.countDown();
        doneSignal.await();

        Assert.assertEquals(null, response.getBody());
        Assert.assertEquals(1, sourceInterceptor.getCount());
        Assert.assertEquals(1, targetInterceptor.getCount());
    }
}
