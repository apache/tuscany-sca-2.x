package org.apache.tuscany.core.policy.async;

import java.util.ArrayList;
import java.util.List;

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
import org.apache.tuscany.core.monitor.NullMonitorFactory;
import org.apache.tuscany.spi.context.WorkContext;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.spi.wire.TargetWire;

/**
 * @version $$Rev$$ $$Date$$
 */
public class AsyncInterceptorTestCase extends TestCase {


    @SuppressWarnings("unchecked")
    public void testInvocation() throws Exception {
        TransactionContextManager transactionContextManager = new TransactionContextManager();
        GeronimoWorkManager workManager = new GeronimoWorkManager(2, transactionContextManager);
        workManager.doStart();

        ConnectorImpl connector = new ConnectorImpl();
        WorkContext workContext = new WorkContextImpl();
        ModuleScopeContext scopeContext = new ModuleScopeContext(workContext);
        scopeContext.start();

        MockAtomicContext<AsyncSource> sourceContext = MockWireFactory.setupAsyncSource(scopeContext, null, null, null);

        AsyncInterceptor interceptor = new AsyncInterceptor(workManager, new NullMonitorFactory().getMonitor(AsyncMonitor.class));
        List<Interceptor> interceptors = new ArrayList<Interceptor>();
        interceptors.add(interceptor);
        MockAtomicContext<AsyncTarget> targetContext = MockWireFactory.setupAsyncTarget(scopeContext, interceptors, null, null);
        for (SourceWire<?> sourceWire : sourceContext.getSourceWires()) {
            TargetWire<SimpleTarget> targetWire = targetContext.getTargetWire(sourceWire.getTargetName().getPortName());
            connector.connect((SourceWire<SimpleTarget>) sourceWire, targetWire, targetContext, false);
        }
        targetContext.prepare();
        scopeContext.onEvent(new ModuleStart(this, null));
        AsyncTarget target = targetContext.getService();
        assertNull(target.getString());
        AsyncSource source = sourceContext.getService();
        source.getTarget().setString("foo");
        assertEquals("foo",target.getString());
        scopeContext.onEvent(new ModuleStop(this, null));
        scopeContext.stop();

    }
}
