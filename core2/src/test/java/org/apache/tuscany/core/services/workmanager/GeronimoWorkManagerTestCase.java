package org.apache.tuscany.core.services.workmanager;

import javax.resource.spi.work.Work;
import javax.resource.spi.work.WorkEvent;
import javax.resource.spi.work.WorkListener;

import junit.framework.TestCase;
import org.apache.geronimo.connector.work.GeronimoWorkManager;
import org.apache.geronimo.transaction.context.TransactionContextManager;

/**
 * Tests integration of the Geronimo work manager
 *
 * @version $Rev$ $Date$
 */
public class GeronimoWorkManagerTestCase extends TestCase {

    private GeronimoWorkManager workManager;

    protected void setUp() throws Exception {
        TransactionContextManager transactionContextManager = new TransactionContextManager();

        workManager = new GeronimoWorkManager(2, transactionContextManager);
        workManager.doStart();
    }

    public void testScheduleWork() throws Exception {
        TestThread threads[] = startTestThreads(5, 10000, 100);
        int accepted = 0;
        for (TestThread thread : threads) {
            if (null != thread.listener.acceptedEvent) {
                accepted++;
            } else {
                fail("incorrect state, expecting accepted or started");
            }
        }
        assertTrue(accepted > 0);
    }

    private TestThread[] startTestThreads(int count, int timeout, int delay) throws Exception {
        TestThread threads[] = new TestThread[count];
        for (int i = 0; i < count; i++) {
            TestWorkListener listener = new TestWorkListener();
            threads[i] = new TestThread(listener, timeout, delay);
        }
        for (int i = 0; i < count; i++) {
            threads[i].start();
        }
        for (int i = 0; i < count; i++) {
            threads[i].join();
        }
        return threads;
    }

    private class TestThread extends Thread {
        public final TestWorkListener listener;
        private final int timeout;
        private final int delay;

        public TestThread(TestWorkListener listener, int timeout, int delay) {
            this.listener = listener;
            this.timeout = timeout;
            this.delay = delay;
        }

        public void run() {
            try {
                workManager.scheduleWork(new TestWorker(delay), timeout, null, listener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public class TestWorker implements Work {
        private final int delay;

        public TestWorker(int delay) {
            this.delay = delay;
        }

        public void release() {
        }

        public void run() {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public class TestWorkListener implements WorkListener {
        public WorkEvent acceptedEvent;
        public WorkEvent rejectedEvent;
        public WorkEvent startedEvent;
        public WorkEvent completedEvent;

        public void workAccepted(WorkEvent e) {
            acceptedEvent = e;
        }

        public void workRejected(WorkEvent e) {
            rejectedEvent = e;
        }

        public void workStarted(WorkEvent e) {
            startedEvent = e;
        }

        public void workCompleted(WorkEvent e) {
            completedEvent = e;
        }
    }
}
