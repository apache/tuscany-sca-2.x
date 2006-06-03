package org.apache.tuscany.core.policy.async;

import java.util.concurrent.CountDownLatch;

import org.apache.geronimo.connector.work.GeronimoWorkManager;
import org.apache.geronimo.transaction.context.TransactionContextManager;
import org.apache.tuscany.core.monitor.NullMonitorFactory;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.jmock.core.Invocation;
import org.jmock.core.Stub;

/**
 * @version $$Rev$$ $$Date$$
 */
public class AsyncInterceptorTestCase extends MockObjectTestCase {

    private GeronimoWorkManager workManager;

    @SuppressWarnings("unchecked")
    public void testInvocation() throws Exception {
        AsyncInterceptor asyncInterceptor = new AsyncInterceptor(workManager, new NullMonitorFactory().getMonitor(AsyncMonitor.class));
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
        TransactionContextManager transactionContextManager = new TransactionContextManager();
        workManager = new GeronimoWorkManager(2, transactionContextManager);
        workManager.doStart();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        workManager.doStop();
    }
}
