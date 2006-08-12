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
package org.apache.tuscany.core.policy.async;

import java.util.concurrent.CountDownLatch;

import org.apache.tuscany.spi.services.work.WorkScheduler;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;

import org.apache.tuscany.core.monitor.NullMonitorFactory;
import org.apache.tuscany.core.services.work.jsr237.Jsr237WorkScheduler;
import org.apache.tuscany.core.services.work.jsr237.workmanager.ThreadPoolWorkManager;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.jmock.core.Invocation;
import org.jmock.core.Stub;

/**
 * Verfies basic async invocations
 *
 * @version $$Rev$$ $$Date$$
 */
public class AsyncInterceptorTestCase extends MockObjectTestCase {

    private WorkScheduler workScheduler;
    private ThreadPoolWorkManager workManager;

    @SuppressWarnings("unchecked")
    public void testInvocation() throws Exception {
        AsyncInterceptor asyncInterceptor =
            new AsyncInterceptor(workScheduler, new NullMonitorFactory().getMonitor(AsyncMonitor.class));
        Message msg = new MessageImpl();
        msg.setBody("foo");
        final CountDownLatch startSignal = new CountDownLatch(1);
        final CountDownLatch doneSignal = new CountDownLatch(1);
        Mock mock = mock(Interceptor.class);
        mock.expects(once()).method("invoke").with(eq(msg)).will(new Stub() {
            public Object invoke(Invocation invocation) throws Throwable {
                startSignal.await();
                doneSignal.countDown();
                return null;
            }

            public StringBuffer describeTo(StringBuffer stringBuffer) {
                return null;
            }
        });
        asyncInterceptor.setNext((Interceptor) mock.proxy());
        asyncInterceptor.invoke(msg);
        startSignal.countDown();
        doneSignal.await();
    }

    protected void setUp() throws Exception {
        super.setUp();
        workManager = new ThreadPoolWorkManager(2);
        workScheduler = new Jsr237WorkScheduler(workManager);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        workManager.destroy();
    }
}
