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
package org.apache.tuscany.core.services.work.jsr237.workmanager;

import java.util.concurrent.CountDownLatch;

import commonj.work.Work;
import commonj.work.WorkEvent;
import commonj.work.WorkListener;
import junit.framework.TestCase;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import org.easymock.IAnswer;

/**
 * @version $Rev$ $Date$
 */
public class ThreadPoolWorkManagerTestCase extends TestCase {

    public void testSchedule() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        Work work = createMock(Work.class);
        work.run();
        expectLastCall().andStubAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                latch.countDown();
                return null;
            }
        });
        replay(work);
        ThreadPoolWorkManager mgr = new ThreadPoolWorkManager(1);
        mgr.schedule(work);
        latch.await();
        verify(work);
    }

    public void testListener() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        WorkListener listener = createStrictMock(WorkListener.class);
        listener.workAccepted(isA(WorkEvent.class));
        listener.workStarted(isA(WorkEvent.class));
        listener.workCompleted(isA(WorkEvent.class));
        expectLastCall();
        replay(listener);
        Work work = createMock(Work.class);
        work.run();
        expectLastCall().andStubAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                latch.countDown();
                return null;
            }
        });
        replay(work);
        ThreadPoolWorkManager mgr = new ThreadPoolWorkManager(1);
        mgr.schedule(work, listener);
        latch.await();
        verify(work);
    }

    public void testDelayListener() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);
        WorkListener listener = createStrictMock(WorkListener.class);
        listener.workAccepted(isA(WorkEvent.class));
        listener.workStarted(isA(WorkEvent.class));
        listener.workCompleted(isA(WorkEvent.class));
        expectLastCall().andStubAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                latch2.countDown();
                return null;
            }
        });
        replay(listener);
        Work work = createMock(Work.class);
        work.run();
        expectLastCall().andStubAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                latch.await();
                return null;
            }
        });
        replay(work);
        ThreadPoolWorkManager mgr = new ThreadPoolWorkManager(1);
        mgr.schedule(work, listener);
        latch.countDown();
        verify(work);
    }

    public void testErrorListener() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        WorkListener listener = createStrictMock(WorkListener.class);
        listener.workAccepted(isA(WorkEvent.class));
        listener.workStarted(isA(WorkEvent.class));
        listener.workCompleted(isA(WorkEvent.class));
        replay(listener);
        Work work = createMock(Work.class);
        work.run();
        expectLastCall().andStubAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                latch.countDown();
                throw new RuntimeException();
            }
        });
        replay(work);
        ThreadPoolWorkManager mgr = new ThreadPoolWorkManager(1);
        mgr.schedule(work, listener);
        latch.await();
        verify(work);
    }

}

