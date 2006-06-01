package org.apache.tuscany.container.java.integration.context;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import junit.framework.TestCase;
import org.apache.tuscany.container.java.mock.MockContextFactory;
import org.apache.tuscany.container.java.mock.components.Target;
import org.apache.tuscany.container.java.mock.components.TargetImpl;
import org.apache.tuscany.core.builder.Connector;
import org.apache.tuscany.core.builder.ConnectorImpl;
import org.apache.tuscany.core.context.WorkContextImpl;
import org.apache.tuscany.core.context.event.HttpSessionEnd;
import org.apache.tuscany.core.context.event.HttpSessionStart;
import org.apache.tuscany.core.context.event.ModuleStart;
import org.apache.tuscany.core.context.event.ModuleStop;
import org.apache.tuscany.core.context.event.RequestEnd;
import org.apache.tuscany.core.context.event.RequestStart;
import org.apache.tuscany.core.context.scope.HttpSessionScopeContext;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.core.context.scope.RequestScopeContext;
import org.apache.tuscany.core.context.scope.StatelessScopeContext;
import org.apache.tuscany.core.util.MethodHashMap;
import org.apache.tuscany.core.wire.OutboundInvocationChainImpl;
import org.apache.tuscany.core.wire.OutboundWireImpl;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.ScopeContext;
import org.apache.tuscany.spi.context.WorkContext;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.WireService;
import org.apache.tuscany.test.ArtifactFactory;

/**
 * Validates wiring from a service context to Java atomic contexts by scope
 *
 * @version $$Rev$$ $$Date$$
 */
public class OutboundWireToJavaTestCase extends TestCase {
    private WorkContext workContext;
    private WireService wireService = ArtifactFactory.createWireService();

    public void testToStatelessScope() throws Exception {
        StatelessScopeContext scope = new StatelessScopeContext(workContext);
        scope.start();
        final OutboundWire<Target> wire = getWire(scope);
        Target service = wireService.createProxy(wire);
        assertNotNull(service);
        service.setString("foo");
        assertEquals(null, service.getString());
        scope.stop();
    }

    public void testToRequestScope() throws Exception {
        final RequestScopeContext scope = new RequestScopeContext(workContext);
        scope.start();

        scope.onEvent(new RequestStart(this));

        final OutboundWire<Target> wire = getWire(scope);
        Target service = wireService.createProxy(wire);
        assertNotNull(service);
        service.setString("foo");

        // another request
        Executor executor = Executors.newSingleThreadExecutor();
        FutureTask<Void> future = new FutureTask<Void>(new Runnable() {
            public void run() {
                scope.onEvent(new RequestStart(this));
                Target service2 = wireService.createProxy(wire);
                Target target2 = wireService.createProxy(wire);
                assertEquals(null, service2.getString());
                service2.setString("bar");
                assertEquals("bar", service2.getString());
                assertEquals("bar", target2.getString());
                scope.onEvent(new RequestEnd(this));
            }
        }, null);
        executor.execute(future);
        future.get();

        assertEquals("foo", service.getString());
        scope.onEvent(new RequestEnd(this));
        scope.stop();
    }

    public void testToSessionScope() throws Exception {
        HttpSessionScopeContext scope = new HttpSessionScopeContext(workContext);
        scope.start();
        Object session1 = new Object();
        workContext.setIdentifier(HttpSessionScopeContext.HTTP_IDENTIFIER, session1);
        scope.onEvent(new HttpSessionStart(this, session1));

        final OutboundWire<Target> wire = getWire(scope);
        Target service = wireService.createProxy(wire);
        Target target = wireService.createProxy(wire);
        assertNotNull(service);
        service.setString("foo");
        assertEquals("foo", service.getString());
        assertEquals("foo", target.getString());

        workContext.clearIdentifier(HttpSessionScopeContext.HTTP_IDENTIFIER);

        //second session
        Object session2 = new Object();
        workContext.setIdentifier(HttpSessionScopeContext.HTTP_IDENTIFIER, session2);
        scope.onEvent(new HttpSessionStart(this, session2));

        Target service2 = wireService.createProxy(wire);
        assertNotNull(service2);
        assertNull(service2.getString());
        Target target2 = wireService.createProxy(wire);
        service2.setString("bar");
        assertEquals("bar", service2.getString());
        assertEquals("bar", target2.getString());

        scope.onEvent(new HttpSessionEnd(this, session2));
        workContext.clearIdentifier(HttpSessionScopeContext.HTTP_IDENTIFIER);

        workContext.setIdentifier(HttpSessionScopeContext.HTTP_IDENTIFIER, session1);
        assertEquals("foo", service.getString());

        scope.onEvent(new HttpSessionEnd(this, session1));

        scope.stop();
    }

    public void testToModuleScope() throws Exception {

        ModuleScopeContext scope = new ModuleScopeContext(workContext);
        scope.start();
        scope.onEvent(new ModuleStart(this, null));
        final OutboundWire<Target> wire = getWire(scope);
        Target service = wireService.createProxy(wire);
        Target target = wireService.createProxy(wire);
        assertNotNull(service);
        service.setString("foo");
        assertEquals("foo", service.getString());
        assertEquals("foo", target.getString());
        scope.onEvent(new ModuleStop(this, null));
        scope.stop();
    }

    @SuppressWarnings("unchecked")
    private OutboundWire<Target> getWire(ScopeContext scope) throws NoSuchMethodException {
        Connector connector = new ConnectorImpl();
        OutboundWire<Target> wire = createOutboundWire(new QualifiedName("target/Target"), Target.class);

        AtomicContext<?> atomicContext = MockContextFactory.createJavaAtomicContext("target", scope, TargetImpl.class, Target.class, scope.getScope());
        InboundWire targetWire = MockContextFactory.createTargetWire("Target", Target.class);
        atomicContext.addInboundWire(targetWire);
        connector.connect(wire, atomicContext.getInboundWire("Target"), atomicContext, false);
        atomicContext.start();
        return wire;
    }

    protected void setUp() throws Exception {
        super.setUp();
        workContext = new WorkContextImpl();
    }

    public static <T> OutboundWire<T> createOutboundWire(QualifiedName targetName, Class<T> interfaze) {
        OutboundWire<T> wire = new OutboundWireImpl<T>();
        wire.setBusinessInterface(interfaze);
        wire.setTargetName(targetName);
        wire.addInvocationChains(createInvocationChains(interfaze));
        return wire;
    }

    private static Map<Method, OutboundInvocationChain> createInvocationChains(Class<?> interfaze) {
        Map<Method, OutboundInvocationChain> invocations = new MethodHashMap<OutboundInvocationChain>();
        Method[] methods = interfaze.getMethods();
        for (Method method : methods) {
            OutboundInvocationChain chain = new OutboundInvocationChainImpl(method);
            invocations.put(method, chain);
        }
        return invocations;
    }

}
