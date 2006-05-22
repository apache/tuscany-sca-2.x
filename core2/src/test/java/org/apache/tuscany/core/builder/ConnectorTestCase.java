package org.apache.tuscany.core.builder;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import org.apache.tuscany.core.context.WorkContextImpl;
import org.apache.tuscany.core.context.event.ModuleStart;
import org.apache.tuscany.core.context.event.ModuleStop;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.core.mock.component.SimpleSource;
import org.apache.tuscany.core.mock.component.SimpleTarget;
import org.apache.tuscany.core.mock.context.MockAtomicContext;
import org.apache.tuscany.core.mock.wire.MockHandler;
import org.apache.tuscany.core.mock.wire.MockSyncInterceptor;
import org.apache.tuscany.core.mock.factories.MockWireFactory;
import org.apache.tuscany.spi.context.WorkContext;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.MessageHandler;
import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.spi.wire.TargetWire;

/**
 * @version $$Rev$$ $$Date$$
 */
public class ConnectorTestCase extends TestCase {

    @SuppressWarnings("unchecked")
    public void testNoInterceptorsNoHandlers() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        WorkContext workContext = new WorkContextImpl();
        ModuleScopeContext scopeContext = new ModuleScopeContext(workContext);
        scopeContext.start();

        MockAtomicContext<SimpleSource> sourceContext = MockWireFactory.setupSource(scopeContext, null, null, null);
        MockAtomicContext<SimpleTarget> targetContext = MockWireFactory.setupTarget(scopeContext, null, null, null);
        for (SourceWire<?> sourceWire : sourceContext.getSourceWires()) {
            TargetWire<SimpleTarget> targetWire = targetContext.getTargetWire(sourceWire.getTargetName().getPortName());
            connector.connect((SourceWire<SimpleTarget>) sourceWire, targetWire, targetContext, false);
        }
        targetContext.prepare();
        scopeContext.onEvent(new ModuleStart(this, null));
        SimpleSource source = sourceContext.getService();
        assertEquals("foo", source.getTarget().echo("foo"));
        scopeContext.onEvent(new ModuleStop(this, null));
        scopeContext.stop();
    }


    /**
     * Verifies an invocation with a single source interceptor
     */
    @SuppressWarnings("unchecked")
    public void testSourceInterceptor() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        WorkContext workContext = new WorkContextImpl();
        ModuleScopeContext scopeContext = new ModuleScopeContext(workContext);
        scopeContext.start();

        MockSyncInterceptor interceptor = new MockSyncInterceptor();
        List<Interceptor> interceptors = new ArrayList<Interceptor>();
        interceptors.add(interceptor);
        MockAtomicContext<SimpleSource> sourceContext = MockWireFactory.setupSource(scopeContext, interceptors, null, null);
        MockAtomicContext<SimpleTarget> targetContext = MockWireFactory.setupTarget(scopeContext, null, null, null);
        for (SourceWire<?> sourceWire : sourceContext.getSourceWires()) {
            TargetWire<SimpleTarget> targetWire = targetContext.getTargetWire(sourceWire.getTargetName().getPortName());
            connector.connect((SourceWire<SimpleTarget>) sourceWire, targetWire, targetContext, false);
        }
        targetContext.prepare();
        scopeContext.onEvent(new ModuleStart(this, null));
        assertEquals(0, interceptor.getCount());
        SimpleSource source = sourceContext.getService();
        assertEquals("foo", source.getTarget().echo("foo"));
        assertEquals(1, interceptor.getCount());
        scopeContext.onEvent(new ModuleStop(this, null));
        scopeContext.stop();
    }

    /**
     * Verifies an invocation with a single target interceptor
     */
    @SuppressWarnings("unchecked")
    public void testTargetInterceptor() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        WorkContext workContext = new WorkContextImpl();
        ModuleScopeContext scopeContext = new ModuleScopeContext(workContext);
        scopeContext.start();

        MockSyncInterceptor interceptor = new MockSyncInterceptor();
        List<Interceptor> interceptors = new ArrayList<Interceptor>();
        interceptors.add(interceptor);
        MockAtomicContext<SimpleSource> sourceContext = MockWireFactory.setupSource(scopeContext, null, null, null);
        MockAtomicContext<SimpleTarget> targetContext = MockWireFactory.setupTarget(scopeContext, interceptors, null, null);
        for (SourceWire<?> sourceWire : sourceContext.getSourceWires()) {
            TargetWire<SimpleTarget> targetWire = targetContext.getTargetWire(sourceWire.getTargetName().getPortName());
            connector.connect((SourceWire<SimpleTarget>) sourceWire, targetWire, targetContext, false);
        }
        targetContext.prepare();
        scopeContext.onEvent(new ModuleStart(this, null));
        assertEquals(0, interceptor.getCount());
        SimpleSource source = sourceContext.getService();
        assertEquals("foo", source.getTarget().echo("foo"));
        assertEquals(1, interceptor.getCount());
        scopeContext.onEvent(new ModuleStop(this, null));
        scopeContext.stop();
    }

    /**
     * Verifies an invocation with a source and target interceptor
     */
    @SuppressWarnings("unchecked")
    public void testSourceTargetInterceptor() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        WorkContext workContext = new WorkContextImpl();
        ModuleScopeContext scopeContext = new ModuleScopeContext(workContext);
        scopeContext.start();

        MockSyncInterceptor sourceInterceptor = new MockSyncInterceptor();
        List<Interceptor> sourceInterceptors = new ArrayList<Interceptor>();
        sourceInterceptors.add(sourceInterceptor);
        MockSyncInterceptor targetInterceptor = new MockSyncInterceptor();
        List<Interceptor> targetInterceptors = new ArrayList<Interceptor>();
        targetInterceptors.add(targetInterceptor);
        MockAtomicContext<SimpleSource> sourceContext = MockWireFactory.setupSource(scopeContext, sourceInterceptors, null, null);
        MockAtomicContext<SimpleTarget> targetContext = MockWireFactory.setupTarget(scopeContext, targetInterceptors, null, null);
        for (SourceWire<?> sourceWire : sourceContext.getSourceWires()) {
            TargetWire<SimpleTarget> targetWire = targetContext.getTargetWire(sourceWire.getTargetName().getPortName());
            connector.connect((SourceWire<SimpleTarget>) sourceWire, targetWire, targetContext, false);
        }
        targetContext.prepare();
        scopeContext.onEvent(new ModuleStart(this, null));
        assertEquals(0, sourceInterceptor.getCount());
        assertEquals(0, targetInterceptor.getCount());
        SimpleSource source = sourceContext.getService();
        assertEquals("foo", source.getTarget().echo("foo"));
        assertEquals(1, sourceInterceptor.getCount());
        assertEquals(1, targetInterceptor.getCount());
        scopeContext.onEvent(new ModuleStop(this, null));
        scopeContext.stop();
    }

    /**
     * Verifies an invocation with a source interceptor and a request handler
     */
    @SuppressWarnings("unchecked")
    public void testSourceInterceptorSourceRequestHandler() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        WorkContext workContext = new WorkContextImpl();
        ModuleScopeContext scopeContext = new ModuleScopeContext(workContext);
        scopeContext.start();

        MockSyncInterceptor interceptor = new MockSyncInterceptor();
        List<Interceptor> interceptors = new ArrayList<Interceptor>();
        interceptors.add(interceptor);
        MockHandler handler = new MockHandler();
        List<MessageHandler> handlers = new ArrayList<MessageHandler>();
        handlers.add(handler);
        MockAtomicContext<SimpleSource> sourceContext = MockWireFactory.setupSource(scopeContext, interceptors, handlers, null);
        MockAtomicContext<SimpleTarget> targetContext = MockWireFactory.setupTarget(scopeContext, null, null, null);
        for (SourceWire<?> sourceWire : sourceContext.getSourceWires()) {
            TargetWire<SimpleTarget> targetWire = targetContext.getTargetWire(sourceWire.getTargetName().getPortName());
            connector.connect((SourceWire<SimpleTarget>) sourceWire, targetWire, targetContext, false);
        }
        targetContext.prepare();
        scopeContext.onEvent(new ModuleStart(this, null));
        assertEquals(0, interceptor.getCount());
        assertEquals(0, handler.getCount());
        SimpleSource source = sourceContext.getService();
        assertEquals("foo", source.getTarget().echo("foo"));
        assertEquals(1, handler.getCount());
        assertEquals(1, interceptor.getCount());
        scopeContext.onEvent(new ModuleStop(this, null));
        scopeContext.stop();
    }

    /**
     * Verifies an invocation with a target interceptor and a request handler
     */
    @SuppressWarnings("unchecked")
    public void testTargetInterceptorTargetRequestHandler() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        WorkContext workContext = new WorkContextImpl();
        ModuleScopeContext scopeContext = new ModuleScopeContext(workContext);
        scopeContext.start();

        MockSyncInterceptor interceptor = new MockSyncInterceptor();
        List<Interceptor> interceptors = new ArrayList<Interceptor>();
        interceptors.add(interceptor);
        MockHandler handler = new MockHandler();
        List<MessageHandler> handlers = new ArrayList<MessageHandler>();
        handlers.add(handler);
        MockAtomicContext<SimpleSource> sourceContext = MockWireFactory.setupSource(scopeContext, null,null, null);
        MockAtomicContext<SimpleTarget> targetContext = MockWireFactory.setupTarget(scopeContext, interceptors, handlers, null);
        for (SourceWire<?> sourceWire : sourceContext.getSourceWires()) {
            TargetWire<SimpleTarget> targetWire = targetContext.getTargetWire(sourceWire.getTargetName().getPortName());
            connector.connect((SourceWire<SimpleTarget>) sourceWire, targetWire, targetContext, false);
        }
        targetContext.prepare();
        scopeContext.onEvent(new ModuleStart(this, null));
        assertEquals(0, interceptor.getCount());
        assertEquals(0, handler.getCount());
        SimpleSource source = sourceContext.getService();
        assertEquals("foo", source.getTarget().echo("foo"));
        assertEquals(1, handler.getCount());
        assertEquals(1, interceptor.getCount());
        scopeContext.onEvent(new ModuleStop(this, null));
        scopeContext.stop();
    }



    /**
     * Verifies an invocation with a source interceptor and response handler
     */
    @SuppressWarnings("unchecked")
    public void testSourceInterceptorSourceResponseHandler() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        WorkContext workContext = new WorkContextImpl();
        ModuleScopeContext scopeContext = new ModuleScopeContext(workContext);
        scopeContext.start();

        MockSyncInterceptor interceptor = new MockSyncInterceptor();
        List<Interceptor> interceptors = new ArrayList<Interceptor>();
        interceptors.add(interceptor);
        MockHandler handler = new MockHandler();
        List<MessageHandler> handlers = new ArrayList<MessageHandler>();
        handlers.add(handler);
        MockAtomicContext<SimpleSource> sourceContext = MockWireFactory.setupSource(scopeContext, interceptors, null, handlers);
        MockAtomicContext<SimpleTarget> targetContext = MockWireFactory.setupTarget(scopeContext, null, null, null);
        for (SourceWire<?> sourceWire : sourceContext.getSourceWires()) {
            TargetWire<SimpleTarget> targetWire = targetContext.getTargetWire(sourceWire.getTargetName().getPortName());
            connector.connect((SourceWire<SimpleTarget>) sourceWire, targetWire, targetContext, false);
        }
        targetContext.prepare();
        scopeContext.onEvent(new ModuleStart(this, null));
        assertEquals(0, interceptor.getCount());
        assertEquals(0, handler.getCount());
        SimpleSource source = sourceContext.getService();
        assertEquals("foo", source.getTarget().echo("foo"));
        assertEquals(1, handler.getCount());
        assertEquals(1, interceptor.getCount());
        scopeContext.onEvent(new ModuleStop(this, null));
        scopeContext.stop();
    }

    /**
     * Verifies an invocation with a source interceptor and response handler
     */
    @SuppressWarnings("unchecked")
    public void testTargetInterceptorTargetResponseHandler() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        WorkContext workContext = new WorkContextImpl();
        ModuleScopeContext scopeContext = new ModuleScopeContext(workContext);
        scopeContext.start();

        MockSyncInterceptor interceptor = new MockSyncInterceptor();
        List<Interceptor> interceptors = new ArrayList<Interceptor>();
        interceptors.add(interceptor);
        MockHandler handler = new MockHandler();
        List<MessageHandler> handlers = new ArrayList<MessageHandler>();
        handlers.add(handler);
        MockAtomicContext<SimpleSource> sourceContext = MockWireFactory.setupSource(scopeContext, null, null, null);
        MockAtomicContext<SimpleTarget> targetContext = MockWireFactory.setupTarget(scopeContext, interceptors, null, handlers);
        for (SourceWire<?> sourceWire : sourceContext.getSourceWires()) {
            TargetWire<SimpleTarget> targetWire = targetContext.getTargetWire(sourceWire.getTargetName().getPortName());
            connector.connect((SourceWire<SimpleTarget>) sourceWire, targetWire, targetContext, false);
        }
        targetContext.prepare();
        scopeContext.onEvent(new ModuleStart(this, null));
        assertEquals(0, interceptor.getCount());
        assertEquals(0, handler.getCount());
        SimpleSource source = sourceContext.getService();
        assertEquals("foo", source.getTarget().echo("foo"));
        assertEquals(1, handler.getCount());
        assertEquals(1, interceptor.getCount());
        scopeContext.onEvent(new ModuleStop(this, null));
        scopeContext.stop();
    }

    /**
     * Verifies an invocation with a source interceptor, request handler, and response handler
     */
    @SuppressWarnings("unchecked")
    public void testSourceInterceptorSourceRequestResponseHandler() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        WorkContext workContext = new WorkContextImpl();
        ModuleScopeContext scopeContext = new ModuleScopeContext(workContext);
        scopeContext.start();

        MockSyncInterceptor interceptor = new MockSyncInterceptor();
        List<Interceptor> interceptors = new ArrayList<Interceptor>();
        interceptors.add(interceptor);
        MockHandler handler = new MockHandler();
        List<MessageHandler> handlers = new ArrayList<MessageHandler>();
        handlers.add(handler);
        MockAtomicContext<SimpleSource> sourceContext = MockWireFactory.setupSource(scopeContext, interceptors, handlers, handlers);
        MockAtomicContext<SimpleTarget> targetContext = MockWireFactory.setupTarget(scopeContext, null, null, null);
        for (SourceWire<?> sourceWire : sourceContext.getSourceWires()) {
            TargetWire<SimpleTarget> targetWire = targetContext.getTargetWire(sourceWire.getTargetName().getPortName());
            connector.connect((SourceWire<SimpleTarget>) sourceWire, targetWire, targetContext, false);
        }
        targetContext.prepare();
        scopeContext.onEvent(new ModuleStart(this, null));
        assertEquals(0, interceptor.getCount());
        assertEquals(0, handler.getCount());
        SimpleSource source = sourceContext.getService();
        assertEquals("foo", source.getTarget().echo("foo"));
        assertEquals(2, handler.getCount());
        assertEquals(1, interceptor.getCount());
        scopeContext.onEvent(new ModuleStop(this, null));
        scopeContext.stop();
    }

    /**
     * Verifies an invocation with a target interceptor, request handler, and response handler
     */
    @SuppressWarnings("unchecked")
    public void testTargetInterceptorTargetRequestResponseHandler() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        WorkContext workContext = new WorkContextImpl();
        ModuleScopeContext scopeContext = new ModuleScopeContext(workContext);
        scopeContext.start();

        MockSyncInterceptor interceptor = new MockSyncInterceptor();
        List<Interceptor> interceptors = new ArrayList<Interceptor>();
        interceptors.add(interceptor);
        MockHandler handler = new MockHandler();
        List<MessageHandler> handlers = new ArrayList<MessageHandler>();
        handlers.add(handler);
        MockAtomicContext<SimpleSource> sourceContext = MockWireFactory.setupSource(scopeContext, null, null, handlers);
        MockAtomicContext<SimpleTarget> targetContext = MockWireFactory.setupTarget(scopeContext, interceptors, handlers, null);
        for (SourceWire<?> sourceWire : sourceContext.getSourceWires()) {
            TargetWire<SimpleTarget> targetWire = targetContext.getTargetWire(sourceWire.getTargetName().getPortName());
            connector.connect((SourceWire<SimpleTarget>) sourceWire, targetWire, targetContext, false);
        }
        targetContext.prepare();
        scopeContext.onEvent(new ModuleStart(this, null));
        assertEquals(0, interceptor.getCount());
        assertEquals(0, handler.getCount());
        SimpleSource source = sourceContext.getService();
        assertEquals("foo", source.getTarget().echo("foo"));
        assertEquals(2, handler.getCount());
        assertEquals(1, interceptor.getCount());
        scopeContext.onEvent(new ModuleStop(this, null));
        scopeContext.stop();
    }

    /**
     * Verifies an invocation with a source request handler and response handler
     */
    @SuppressWarnings("unchecked")
    public void testSourceRequestResponseHandler() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        WorkContext workContext = new WorkContextImpl();
        ModuleScopeContext scopeContext = new ModuleScopeContext(workContext);
        scopeContext.start();

        MockHandler handler = new MockHandler();
        List<MessageHandler> handlers = new ArrayList<MessageHandler>();
        handlers.add(handler);
        MockAtomicContext<SimpleSource> sourceContext = MockWireFactory.setupSource(scopeContext, null, handlers, handlers);
        MockAtomicContext<SimpleTarget> targetContext = MockWireFactory.setupTarget(scopeContext, null, null, null);
        for (SourceWire<?> sourceWire : sourceContext.getSourceWires()) {
            TargetWire<SimpleTarget> targetWire = targetContext.getTargetWire(sourceWire.getTargetName().getPortName());
            connector.connect((SourceWire<SimpleTarget>) sourceWire, targetWire, targetContext, false);
        }
        targetContext.prepare();
        scopeContext.onEvent(new ModuleStart(this, null));
        assertEquals(0, handler.getCount());
        SimpleSource source = sourceContext.getService();
        assertEquals("foo", source.getTarget().echo("foo"));
        assertEquals(2, handler.getCount());
        scopeContext.onEvent(new ModuleStop(this, null));
        scopeContext.stop();
    }

    /**
     * Verifies an invocation with a target request handler and response handler
     */
    @SuppressWarnings("unchecked")
    public void testTargetRequestResponseHandler() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        WorkContext workContext = new WorkContextImpl();
        ModuleScopeContext scopeContext = new ModuleScopeContext(workContext);
        scopeContext.start();

        MockHandler handler = new MockHandler();
        List<MessageHandler> handlers = new ArrayList<MessageHandler>();
        handlers.add(handler);
        MockAtomicContext<SimpleSource> sourceContext = MockWireFactory.setupSource(scopeContext, null, null,null);
        MockAtomicContext<SimpleTarget> targetContext = MockWireFactory.setupTarget(scopeContext, null, handlers, handlers);
        for (SourceWire<?> sourceWire : sourceContext.getSourceWires()) {
            TargetWire<SimpleTarget> targetWire = targetContext.getTargetWire(sourceWire.getTargetName().getPortName());
            connector.connect((SourceWire<SimpleTarget>) sourceWire, targetWire, targetContext, false);
        }
        targetContext.prepare();
        scopeContext.onEvent(new ModuleStart(this, null));
        assertEquals(0, handler.getCount());
        SimpleSource source = sourceContext.getService();
        assertEquals("foo", source.getTarget().echo("foo"));
        assertEquals(2, handler.getCount());
        scopeContext.onEvent(new ModuleStop(this, null));
        scopeContext.stop();
    }

    /**
     * Verifies an invocation with a single source request handler
     */
    @SuppressWarnings("unchecked")
    public void testSourceRequestHandler() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        WorkContext workContext = new WorkContextImpl();
        ModuleScopeContext scopeContext = new ModuleScopeContext(workContext);
        scopeContext.start();

        MockHandler handler = new MockHandler();
        List<MessageHandler> handlers = new ArrayList<MessageHandler>();
        handlers.add(handler);
        MockAtomicContext<SimpleSource> sourceContext = MockWireFactory.setupSource(scopeContext, null, handlers, null);
        MockAtomicContext<SimpleTarget> targetContext = MockWireFactory.setupTarget(scopeContext, null, null, null);
        for (SourceWire<?> sourceWire : sourceContext.getSourceWires()) {
            TargetWire<SimpleTarget> targetWire = targetContext.getTargetWire(sourceWire.getTargetName().getPortName());
            connector.connect((SourceWire<SimpleTarget>) sourceWire, targetWire, targetContext, false);
        }
        targetContext.prepare();
        scopeContext.onEvent(new ModuleStart(this, null));
        assertEquals(0, handler.getCount());
        SimpleSource source = sourceContext.getService();
        assertEquals("foo", source.getTarget().echo("foo"));
        assertEquals(1, handler.getCount());
        scopeContext.onEvent(new ModuleStop(this, null));
        scopeContext.stop();
    }

    /**
     * Verifies an invocation with a single target request handler
     */
    @SuppressWarnings("unchecked")
    public void testTargetRequestHandler() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        WorkContext workContext = new WorkContextImpl();
        ModuleScopeContext scopeContext = new ModuleScopeContext(workContext);
        scopeContext.start();

        MockHandler handler = new MockHandler();
        List<MessageHandler> handlers = new ArrayList<MessageHandler>();
        handlers.add(handler);
        MockAtomicContext<SimpleSource> sourceContext = MockWireFactory.setupSource(scopeContext, null, null, null);
        MockAtomicContext<SimpleTarget> targetContext = MockWireFactory.setupTarget(scopeContext, null, handlers, null);
        for (SourceWire<?> sourceWire : sourceContext.getSourceWires()) {
            TargetWire<SimpleTarget> targetWire = targetContext.getTargetWire(sourceWire.getTargetName().getPortName());
            connector.connect((SourceWire<SimpleTarget>) sourceWire, targetWire, targetContext, false);
        }
        targetContext.prepare();
        scopeContext.onEvent(new ModuleStart(this, null));
        assertEquals(0, handler.getCount());
        SimpleSource source = sourceContext.getService();
        assertEquals("foo", source.getTarget().echo("foo"));
        assertEquals(1, handler.getCount());
        scopeContext.onEvent(new ModuleStop(this, null));
        scopeContext.stop();
    }

    /**
     * Verifies an invocation with a single source response handler
     */
    @SuppressWarnings("unchecked")
    public void testSourceResponseHandler() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        WorkContext workContext = new WorkContextImpl();
        ModuleScopeContext scopeContext = new ModuleScopeContext(workContext);
        scopeContext.start();

        MockHandler handler = new MockHandler();
        List<MessageHandler> handlers = new ArrayList<MessageHandler>();
        handlers.add(handler);
        MockAtomicContext<SimpleSource> sourceContext = MockWireFactory.setupSource(scopeContext, null,null, handlers);
        MockAtomicContext<SimpleTarget> targetContext = MockWireFactory.setupTarget(scopeContext, null, null, null);
        for (SourceWire<?> sourceWire : sourceContext.getSourceWires()) {
            TargetWire<SimpleTarget> targetWire = targetContext.getTargetWire(sourceWire.getTargetName().getPortName());
            connector.connect((SourceWire<SimpleTarget>) sourceWire, targetWire, targetContext, false);
        }
        targetContext.prepare();
        scopeContext.onEvent(new ModuleStart(this, null));
        assertEquals(0, handler.getCount());
        SimpleSource source = sourceContext.getService();
        assertEquals("foo", source.getTarget().echo("foo"));
        assertEquals(1, handler.getCount());
        scopeContext.onEvent(new ModuleStop(this, null));
        scopeContext.stop();
    }

    /**
     * Verifies an invocation with a single target response handler
     */
    @SuppressWarnings("unchecked")
    public void testTargetResponseHandler() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        WorkContext workContext = new WorkContextImpl();
        ModuleScopeContext scopeContext = new ModuleScopeContext(workContext);
        scopeContext.start();

        MockHandler handler = new MockHandler();
        List<MessageHandler> handlers = new ArrayList<MessageHandler>();
        handlers.add(handler);
        MockAtomicContext<SimpleSource> sourceContext = MockWireFactory.setupSource(scopeContext, null,null, null);
        MockAtomicContext<SimpleTarget> targetContext = MockWireFactory.setupTarget(scopeContext, null, null, handlers);
        for (SourceWire<?> sourceWire : sourceContext.getSourceWires()) {
            TargetWire<SimpleTarget> targetWire = targetContext.getTargetWire(sourceWire.getTargetName().getPortName());
            connector.connect((SourceWire<SimpleTarget>) sourceWire, targetWire, targetContext, false);
        }
        targetContext.prepare();
        scopeContext.onEvent(new ModuleStart(this, null));
        assertEquals(0, handler.getCount());
        SimpleSource source = sourceContext.getService();
        assertEquals("foo", source.getTarget().echo("foo"));
        assertEquals(1, handler.getCount());
        scopeContext.onEvent(new ModuleStop(this, null));
        scopeContext.stop();
    }

}
