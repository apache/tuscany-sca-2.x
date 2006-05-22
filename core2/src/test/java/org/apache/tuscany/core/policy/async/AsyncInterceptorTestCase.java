package org.apache.tuscany.core.policy.async;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import junit.framework.TestCase;
import org.apache.geronimo.connector.work.GeronimoWorkManager;
import org.apache.geronimo.transaction.context.TransactionContextManager;
import org.apache.tuscany.core.builder.ConnectorImpl;
import org.apache.tuscany.core.context.WorkContextImpl;
import org.apache.tuscany.core.context.event.ModuleStart;
import org.apache.tuscany.core.context.event.ModuleStop;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.core.mock.component.AsyncSource;
import org.apache.tuscany.core.mock.component.AsyncTarget;
import org.apache.tuscany.core.mock.component.SimpleTarget;
import org.apache.tuscany.core.mock.context.MockAtomicContext;
import org.apache.tuscany.core.mock.factories.MockWireFactory;
import org.apache.tuscany.core.mock.wire.MockSyncInterceptor;
import org.apache.tuscany.core.mock.wire.MockHandler;
import org.apache.tuscany.core.monitor.NullMonitorFactory;
import org.apache.tuscany.spi.context.WorkContext;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.spi.wire.TargetWire;
import org.apache.tuscany.spi.wire.MessageHandler;

/**
 * @version $$Rev$$ $$Date$$
 */
public class AsyncInterceptorTestCase extends TestCase {

    private GeronimoWorkManager workManager;

    @SuppressWarnings("unchecked")
    public void testInvocation() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        WorkContext workContext = new WorkContextImpl();
        ModuleScopeContext scopeContext = new ModuleScopeContext(workContext);
        scopeContext.start();

        List<Interceptor> sourceInterceptors = new ArrayList<Interceptor>();
        MockSyncInterceptor sourceInterceptor = new MockSyncInterceptor();
        sourceInterceptors.add(sourceInterceptor);

        MockHandler handler = new MockHandler();
        List<MessageHandler> handlers = new ArrayList<MessageHandler>();
        handlers.add(handler);
        MockAtomicContext<AsyncSource> sourceContext = MockWireFactory.setupAsyncSource(scopeContext, sourceInterceptors, handlers, handlers);

        List<Interceptor> targetInterceptors = new ArrayList<Interceptor>();
        AsyncInterceptor asyncInterceptor = new AsyncInterceptor(workManager, new NullMonitorFactory().getMonitor(AsyncMonitor.class));
        targetInterceptors.add(asyncInterceptor);
        MockSyncInterceptor targetInterceptor = new MockSyncInterceptor();
        targetInterceptors.add(targetInterceptor);

        MockAtomicContext<AsyncTarget> targetContext = MockWireFactory.setupAsyncTarget(scopeContext, targetInterceptors, handlers, handlers);
        for (SourceWire<?> sourceWire : sourceContext.getSourceWires()) {
            TargetWire<SimpleTarget> targetWire = targetContext.getTargetWire(sourceWire.getTargetName().getPortName());
            connector.connect((SourceWire<SimpleTarget>) sourceWire, targetWire, targetContext, false);
        }
        targetContext.prepare();

        scopeContext.onEvent(new ModuleStart(this, null));
        AsyncTarget target = targetContext.getService();
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal = new CountDownLatch(1);
        target.setLatches(startSignal,doneSignal);
        assertNull(target.getString());
        AsyncSource source = sourceContext.getService();
        source.getTarget().setString("foo");
        startSignal.countDown();
        doneSignal.await();
        assertEquals("foo",target.getString());
        assertEquals(1,sourceInterceptor.getCount());
        assertEquals(1,targetInterceptor.getCount());
        assertEquals(4,handler.getCount());
        scopeContext.onEvent(new ModuleStop(this, null));
        scopeContext.stop();
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
