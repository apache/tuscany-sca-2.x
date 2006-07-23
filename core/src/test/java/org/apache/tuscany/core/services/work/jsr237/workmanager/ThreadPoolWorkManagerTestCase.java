package org.apache.tuscany.core.services.work.jsr237.workmanager;

import commonj.work.Work;
import commonj.work.WorkEvent;
import commonj.work.WorkListener;
import junit.framework.TestCase;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

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
        WorkListener listener = createMock(WorkListener.class);
        listener.workStarted(isA(WorkEvent.class));
        listener.workAccepted(isA(WorkEvent.class));
        listener.workCompleted(isA(WorkEvent.class));
        replay(listener);
        Work work = createMock(Work.class);
        work.run();
        expectLastCall();
        replay(work);
        ThreadPoolWorkManager mgr = new ThreadPoolWorkManager(1);
        mgr.schedule(work, listener);
        verify(work);
    }


}

