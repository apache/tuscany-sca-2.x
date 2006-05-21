package org.apache.tuscany.core.builder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import org.apache.tuscany.core.context.WorkContextImpl;
import org.apache.tuscany.core.context.event.ModuleStart;
import org.apache.tuscany.core.context.event.ModuleStop;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.mock.component.SimpleSource;
import org.apache.tuscany.core.mock.component.SimpleSourceImpl;
import org.apache.tuscany.core.mock.component.SimpleTarget;
import org.apache.tuscany.core.mock.component.SimpleTargetImpl;
import org.apache.tuscany.core.mock.context.MockAtomicContext;
import org.apache.tuscany.core.mock.wire.MockHandler;
import org.apache.tuscany.core.mock.wire.MockSyncInterceptor;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.core.wire.SourceInvocationChainImpl;
import org.apache.tuscany.core.wire.TargetInvocationChainImpl;
import org.apache.tuscany.core.wire.jdk.JDKSourceWire;
import org.apache.tuscany.core.wire.jdk.JDKTargetWire;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.context.ScopeContext;
import org.apache.tuscany.spi.context.WorkContext;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.MessageHandler;
import org.apache.tuscany.spi.wire.SourceInvocationChain;
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

        MockAtomicContext<SimpleSource> sourceContext = setupSource(scopeContext, null, null, null);
        MockAtomicContext<SimpleTarget> targetContext = setupTarget(scopeContext, null, null, null);
        for (SourceWire<?> sourceWire : sourceContext.getSourceWires()) {
            TargetWire<SimpleTarget> targetWire = targetContext.getTargetWire(sourceWire.getTargetName().getPortName());
            connector.connect((SourceWire<SimpleTarget>) sourceWire, targetWire, targetContext, false);
        }

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
        MockAtomicContext<SimpleSource> sourceContext = setupSource(scopeContext, interceptors, null, null);
        MockAtomicContext<SimpleTarget> targetContext = setupTarget(scopeContext, null, null, null);
        for (SourceWire<?> sourceWire : sourceContext.getSourceWires()) {
            TargetWire<SimpleTarget> targetWire = targetContext.getTargetWire(sourceWire.getTargetName().getPortName());
            connector.connect((SourceWire<SimpleTarget>) sourceWire, targetWire, targetContext, false);
        }

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
        MockAtomicContext<SimpleSource> sourceContext = setupSource(scopeContext, null, null, null);
        MockAtomicContext<SimpleTarget> targetContext = setupTarget(scopeContext, interceptors, null, null);
        for (SourceWire<?> sourceWire : sourceContext.getSourceWires()) {
            TargetWire<SimpleTarget> targetWire = targetContext.getTargetWire(sourceWire.getTargetName().getPortName());
            connector.connect((SourceWire<SimpleTarget>) sourceWire, targetWire, targetContext, false);
        }

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
        MockAtomicContext<SimpleSource> sourceContext = setupSource(scopeContext, sourceInterceptors, null, null);
        MockAtomicContext<SimpleTarget> targetContext = setupTarget(scopeContext, targetInterceptors, null, null);
        for (SourceWire<?> sourceWire : sourceContext.getSourceWires()) {
            TargetWire<SimpleTarget> targetWire = targetContext.getTargetWire(sourceWire.getTargetName().getPortName());
            connector.connect((SourceWire<SimpleTarget>) sourceWire, targetWire, targetContext, false);
        }

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
        MockAtomicContext<SimpleSource> sourceContext = setupSource(scopeContext, interceptors, handlers, null);
        MockAtomicContext<SimpleTarget> targetContext = setupTarget(scopeContext, null, null, null);
        for (SourceWire<?> sourceWire : sourceContext.getSourceWires()) {
            TargetWire<SimpleTarget> targetWire = targetContext.getTargetWire(sourceWire.getTargetName().getPortName());
            connector.connect((SourceWire<SimpleTarget>) sourceWire, targetWire, targetContext, false);
        }

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
        MockAtomicContext<SimpleSource> sourceContext = setupSource(scopeContext, interceptors, null, handlers);
        MockAtomicContext<SimpleTarget> targetContext = setupTarget(scopeContext, null, null, null);
        for (SourceWire<?> sourceWire : sourceContext.getSourceWires()) {
            TargetWire<SimpleTarget> targetWire = targetContext.getTargetWire(sourceWire.getTargetName().getPortName());
            connector.connect((SourceWire<SimpleTarget>) sourceWire, targetWire, targetContext, false);
        }

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
        MockAtomicContext<SimpleSource> sourceContext = setupSource(scopeContext, interceptors, handlers, handlers);
        MockAtomicContext<SimpleTarget> targetContext = setupTarget(scopeContext, null, null, null);
        for (SourceWire<?> sourceWire : sourceContext.getSourceWires()) {
            TargetWire<SimpleTarget> targetWire = targetContext.getTargetWire(sourceWire.getTargetName().getPortName());
            connector.connect((SourceWire<SimpleTarget>) sourceWire, targetWire, targetContext, false);
        }

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
        MockAtomicContext<SimpleSource> sourceContext = setupSource(scopeContext, null, handlers, handlers);
        MockAtomicContext<SimpleTarget> targetContext = setupTarget(scopeContext, null, null, null);
        for (SourceWire<?> sourceWire : sourceContext.getSourceWires()) {
            TargetWire<SimpleTarget> targetWire = targetContext.getTargetWire(sourceWire.getTargetName().getPortName());
            connector.connect((SourceWire<SimpleTarget>) sourceWire, targetWire, targetContext, false);
        }

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
        MockAtomicContext<SimpleSource> sourceContext = setupSource(scopeContext, null, handlers, null);
        MockAtomicContext<SimpleTarget> targetContext = setupTarget(scopeContext, null, null, null);
        for (SourceWire<?> sourceWire : sourceContext.getSourceWires()) {
            TargetWire<SimpleTarget> targetWire = targetContext.getTargetWire(sourceWire.getTargetName().getPortName());
            connector.connect((SourceWire<SimpleTarget>) sourceWire, targetWire, targetContext, false);
        }

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
        MockAtomicContext<SimpleSource> sourceContext = setupSource(scopeContext, null,null, handlers);
        MockAtomicContext<SimpleTarget> targetContext = setupTarget(scopeContext, null, null, null);
        for (SourceWire<?> sourceWire : sourceContext.getSourceWires()) {
            TargetWire<SimpleTarget> targetWire = targetContext.getTargetWire(sourceWire.getTargetName().getPortName());
            connector.connect((SourceWire<SimpleTarget>) sourceWire, targetWire, targetContext, false);
        }

        scopeContext.onEvent(new ModuleStart(this, null));
        assertEquals(0, handler.getCount());
        SimpleSource source = sourceContext.getService();
        assertEquals("foo", source.getTarget().echo("foo"));
        assertEquals(1, handler.getCount());
        scopeContext.onEvent(new ModuleStop(this, null));
        scopeContext.stop();
    }

    private MockAtomicContext<SimpleSource> setupSource(ScopeContext scopeContext, List<Interceptor> interceptors,
                                                        List<MessageHandler> requestHandlers,
                                                        List<MessageHandler> responseHandlers) throws NoSuchMethodException {
        Method echo = SimpleTarget.class.getMethod("echo", String.class);

        List<Class<?>> sourceInterfaces = new ArrayList<Class<?>>();
        sourceInterfaces.add(SimpleSource.class);
        Constructor<SimpleSourceImpl> sourceCtr = SimpleSourceImpl.class.getConstructor();
        Map<String, Member> members = new HashMap<String, Member>();
        members.put("target", SimpleSourceImpl.class.getMethod("setTarget", SimpleTarget.class));
        MockAtomicContext<SimpleSource> source = new MockAtomicContext<SimpleSource>("source", sourceInterfaces, new PojoObjectFactory<SimpleSourceImpl>(sourceCtr),
                scopeContext.getScope(), members);
        SourceWire<SimpleTarget> sourceWire = new JDKSourceWire<SimpleTarget>();
        sourceWire.setBusinessInterface(SimpleTarget.class);
        sourceWire.setReferenceName("target");
        sourceWire.setTargetName(new QualifiedName("target/Target"));
        SourceInvocationChain chain = new SourceInvocationChainImpl(echo);
        if (interceptors != null) {
            for (Interceptor interceptor : interceptors) {
                chain.addInterceptor(interceptor);
            }
        }
        if (requestHandlers != null) {
            for (MessageHandler handler : requestHandlers) {
                chain.addRequestHandler(handler);
            }
        }
        if (responseHandlers != null) {
            for (MessageHandler handler : responseHandlers) {
                chain.addResponseHandler(handler);
            }
        }
        sourceWire.addInvocationChain(echo, chain);
        source.addSourceWire(sourceWire);
        source.setScopeContext(scopeContext);
        return source;
    }

    private MockAtomicContext<SimpleTarget> setupTarget(ScopeContext scopeContext, List<Interceptor> interceptors,
                                                        List<MessageHandler> requestHandlers,
                                                        List<MessageHandler> responseHandlers) throws NoSuchMethodException {
        Method echo = SimpleTarget.class.getMethod("echo", String.class);

        List<Class<?>> targetInterfaces = new ArrayList<Class<?>>();
        targetInterfaces.add(SimpleTarget.class);
        Constructor<SimpleTargetImpl> targetCtr = SimpleTargetImpl.class.getConstructor();
        MockAtomicContext<SimpleTarget> target = new MockAtomicContext<SimpleTarget>("target", targetInterfaces, new PojoObjectFactory<SimpleTargetImpl>(targetCtr),
                scopeContext.getScope(), null);
        TargetWire<SimpleTarget> targetWire = new JDKTargetWire<SimpleTarget>();
        targetWire.setBusinessInterface(SimpleTarget.class);
        targetWire.setServiceName("Target");
        TargetInvocationChainImpl chain = new TargetInvocationChainImpl(echo);
        if (interceptors != null) {
            for (Interceptor interceptor : interceptors) {
                chain.addInterceptor(interceptor);
            }
        }
        if (requestHandlers != null) {
            for (MessageHandler handler : requestHandlers) {
                chain.addRequestHandler(handler);
            }
        }
        if (responseHandlers != null) {
            for (MessageHandler handler : responseHandlers) {
                chain.addResponseHandler(handler);
            }
        }
        chain.addInterceptor(new InvokerInterceptor()); // add tail interceptor
        targetWire.addInvocationChain(echo, chain);
        target.addTargetWire(targetWire);
        target.setScopeContext(scopeContext);
        return target;
    }
}
