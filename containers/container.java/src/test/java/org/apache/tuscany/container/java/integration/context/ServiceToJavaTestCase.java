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
import org.apache.tuscany.core.system.context.SystemCompositeContext;
import org.apache.tuscany.core.system.context.SystemCompositeContextImpl;
import org.apache.tuscany.core.util.MethodHashMap;
import org.apache.tuscany.core.wire.InboundInvocationChainImpl;
import org.apache.tuscany.core.wire.jdk.JDKInboundWire;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.ScopeContext;
import org.apache.tuscany.spi.context.WorkContext;
import org.apache.tuscany.spi.extension.ServiceContextExtension;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;

/**
 * Validates wiring from a service context to Java atomic contexts by scope
 *
 * @version $$Rev$$ $$Date$$
 */
public class ServiceToJavaTestCase extends TestCase {
    private WorkContext workContext;
    private SystemCompositeContext parent;


    public void testToStatelessScope() throws Exception {
        StatelessScopeContext scope = new StatelessScopeContext(workContext);
        scope.start();
        setupComposite(parent, scope);
        parent.start();
        Target service = (Target) parent.getContext("service").getService();
        assertNotNull(service);
        Target target = (Target) parent.getContext("target").getService();
        service.setString("foo");
        assertEquals(null, service.getString());
        assertEquals(null, target.getString());
        parent.stop();
        scope.stop();
    }

    public void testToRequestScope() throws Exception {
        final RequestScopeContext scope = new RequestScopeContext(workContext);
        scope.start();
        setupComposite(parent, scope);
        parent.start();

        scope.onEvent(new RequestStart(this));
        Target service = (Target) parent.getContext("service").getService();
        assertNotNull(service);
        Target target = (Target) parent.getContext("target").getService();
        service.setString("foo");

        // another request
        Executor executor = Executors.newSingleThreadExecutor();
        FutureTask<Void> future = new FutureTask<Void>(new Runnable() {
            public void run() {
                scope.onEvent(new RequestStart(this));
                Target service2 = (Target) parent.getContext("service").getService();
                Target target2 = (Target) parent.getContext("target").getService();
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
        assertEquals("foo", target.getString());
        scope.onEvent(new RequestEnd(this));
        parent.stop();
        scope.stop();
    }

    public void testToSessionScope() throws Exception {
        HttpSessionScopeContext scope = new HttpSessionScopeContext(workContext);
        scope.start();
        setupComposite(parent, scope);
        parent.start();
        Object session1 = new Object();
        workContext.setIdentifier(HttpSessionScopeContext.HTTP_IDENTIFIER, session1);
        scope.onEvent(new HttpSessionStart(this, session1));

        Target service = (Target) parent.getContext("service").getService();
        assertNotNull(service);
        Target target = (Target) parent.getContext("target").getService();
        service.setString("foo");
        assertEquals("foo", service.getString());
        assertEquals("foo", target.getString());

        workContext.clearIdentifier(HttpSessionScopeContext.HTTP_IDENTIFIER);

        //second session
        Object session2 = new Object();
        workContext.setIdentifier(HttpSessionScopeContext.HTTP_IDENTIFIER, session2);
        scope.onEvent(new HttpSessionStart(this, session2));

        Target service2 = (Target) parent.getContext("service").getService();
        assertNotNull(service2);
        assertNull(service2.getString());
        Target target2 = (Target) parent.getContext("target").getService();
        service2.setString("bar");
        assertEquals("bar", service2.getString());
        assertEquals("bar", target2.getString());

        scope.onEvent(new HttpSessionEnd(this, session2));
        workContext.clearIdentifier(HttpSessionScopeContext.HTTP_IDENTIFIER);

        workContext.setIdentifier(HttpSessionScopeContext.HTTP_IDENTIFIER, session1);
        assertEquals("foo", service.getString());

        scope.onEvent(new HttpSessionEnd(this, session1));

        parent.stop();
        scope.stop();
    }

    public void testToModuleScope() throws Exception {

        ModuleScopeContext scope = new ModuleScopeContext(workContext);
        scope.start();
        setupComposite(parent, scope);
        parent.start();
        scope.onEvent(new ModuleStart(this, parent));
        Target service = (Target) parent.getContext("service").getService();
        assertNotNull(service);
        Target target = (Target) parent.getContext("target").getService();
        service.setString("foo");
        assertEquals("foo", service.getString());
        assertEquals("foo", target.getString());
        scope.onEvent(new ModuleStop(this, parent));
        parent.stop();
        scope.stop();
    }

    @SuppressWarnings("unchecked")
    private void setupComposite(CompositeContext<?> parent, ScopeContext scope) throws NoSuchMethodException {
        Connector connector = new ConnectorImpl();
        InboundWire<Target> sourceWire = createServiceWire("target", Target.class);
        sourceWire.setServiceName("Target");
        ServiceContextExtension<Target> serviceContext = new ServiceContextExtension<Target>("service", sourceWire, parent);
        AtomicContext<?> atomicContext = MockContextFactory.createJavaAtomicContext("target", scope, TargetImpl.class, Target.class, scope.getScope());
        InboundWire targetWire = MockContextFactory.createTargetWire("Target", Target.class);
        atomicContext.addServiceWire(targetWire);
        parent.registerContext(serviceContext);
        parent.registerContext(atomicContext);
        connector.connect(serviceContext.getInboundWire(), atomicContext);
    }

    protected void setUp() throws Exception {
        super.setUp();
        workContext = new WorkContextImpl();
        parent = new SystemCompositeContextImpl(null, null, null);
    }

    public static <T> InboundWire<T> createServiceWire(String serviceName, Class<T> interfaze) {
        InboundWire<T> wire = new JDKInboundWire<T>();
        wire.setBusinessInterface(interfaze);
        wire.setServiceName(serviceName);
        wire.addInvocationChains(createServiceInvocationChains(interfaze));
        return wire;
    }

    private static Map<Method, InboundInvocationChain> createServiceInvocationChains(Class<?> interfaze) {
        Map<Method, InboundInvocationChain> invocations = new MethodHashMap<InboundInvocationChain>();
        Method[] methods = interfaze.getMethods();
        for (Method method : methods) {
            InboundInvocationChain chain = new InboundInvocationChainImpl(method);
            invocations.put(method, chain);
        }
        return invocations;
    }

}
