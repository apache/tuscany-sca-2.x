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
        Work work = createMock(Work.class);
        work.run();
        expectLastCall();
        replay(work);
        ThreadPoolWorkManager mgr = new ThreadPoolWorkManager(1);
        mgr.schedule(work);
        verify(work);
    }

    public void testListener() throws Exception {
        WorkListener listener = createStrictMock(WorkListener.class);
        listener.workAccepted(isA(WorkEvent.class));
        listener.workStarted(isA(WorkEvent.class));
        listener.workCompleted(isA(WorkEvent.class));
        expectLastCall();
        replay(listener);
        Work work = createMock(Work.class);
        work.run();
        expectLastCall();
        replay(work);
        ThreadPoolWorkManager mgr = new ThreadPoolWorkManager(1);
        mgr.schedule(work, listener);
        verify(work);
    }

    public void testDelayListener() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);
        WorkListener listener = createStrictMock(WorkListener.class);
        listener.workAccepted(isA(WorkEvent.class));
        listener.workStarted(isA(WorkEvent.class));
        listener.workCompleted(isA(WorkEvent.class));
        expectLastCall().andStubAnswer(new IAnswer() {
            public Object answer() throws Throwable {
                latch2.countDown();
                return null;
            }
        });
        replay(listener);
        Work work = createMock(Work.class);
        work.run();
        expectLastCall().andStubAnswer(new IAnswer() {
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
        WorkListener listener = createStrictMock(WorkListener.class);
        listener.workAccepted(isA(WorkEvent.class));
        listener.workStarted(isA(WorkEvent.class));
        listener.workCompleted(isA(WorkEvent.class));
        replay(listener);
        Work work = createMock(Work.class);
        work.run();
        expectLastCall().andThrow(new RuntimeException());
        replay(work);
        ThreadPoolWorkManager mgr = new ThreadPoolWorkManager(1);
        mgr.schedule(work, listener);
        verify(work);
    }

}

