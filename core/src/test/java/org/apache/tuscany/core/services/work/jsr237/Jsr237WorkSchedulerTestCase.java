package org.apache.tuscany.core.services.work.jsr237;

import org.apache.tuscany.spi.services.work.NotificationListener;
import org.apache.tuscany.spi.services.work.WorkSchedulerException;

import commonj.work.Work;
import commonj.work.WorkItem;
import commonj.work.WorkListener;
import commonj.work.WorkManager;
import commonj.work.WorkRejectedException;
import junit.framework.TestCase;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

/**
 * @version $Rev$ $Date$
 */
public class Jsr237WorkSchedulerTestCase extends TestCase {

    public void testSchedule() throws Exception {
        WorkItem item = createMock(WorkItem.class);
        WorkManager mgr = createMock(WorkManager.class);
        mgr.schedule(isA(Work.class));
        expectLastCall().andReturn(item);
        replay(mgr);
        Jsr237WorkScheduler scheduler = new Jsr237WorkScheduler(mgr);
        Work work = createMock(Work.class);
        scheduler.scheduleWork(work);
        verify(mgr);
    }

    @SuppressWarnings("unchecked")
    public void testListener() throws Exception {
        WorkItem item = createMock(WorkItem.class);
        WorkManager mgr = createMock(WorkManager.class);
        mgr.schedule(isA(Work.class), isA(WorkListener.class));
        expectLastCall().andReturn(item);
        replay(mgr);
        Jsr237WorkScheduler scheduler = new Jsr237WorkScheduler(mgr);
        Work work = createMock(Work.class);
        NotificationListener<Runnable> listener = createMock(NotificationListener.class);
        scheduler.scheduleWork(work, listener);
        verify(mgr);
    }

    @SuppressWarnings("unchecked")
    public void testWorkRejectedListener() throws Exception {
        WorkManager mgr = createMock(WorkManager.class);
        mgr.schedule(isA(Work.class), isA(WorkListener.class));
        expectLastCall().andThrow(new WorkRejectedException());
        replay(mgr);
        Jsr237WorkScheduler scheduler = new Jsr237WorkScheduler(mgr);
        Work work = createMock(Work.class);
        NotificationListener<Runnable> listener = createMock(NotificationListener.class);
        listener.workRejected(isA(Runnable.class));
        expectLastCall();
        replay(listener);
        scheduler.scheduleWork(work, listener);
        verify(mgr);
    }

    @SuppressWarnings("unchecked")
    public void testWorkRejectedNoListener() throws Exception {
        WorkManager mgr = createMock(WorkManager.class);
        mgr.schedule(isA(Work.class));
        expectLastCall().andThrow(new WorkRejectedException());
        replay(mgr);
        Jsr237WorkScheduler scheduler = new Jsr237WorkScheduler(mgr);
        Work work = createMock(Work.class);
        try {
            scheduler.scheduleWork(work);
            fail();
        } catch (WorkSchedulerException e) {
            // expected
        }
        verify(mgr);
    }

}
