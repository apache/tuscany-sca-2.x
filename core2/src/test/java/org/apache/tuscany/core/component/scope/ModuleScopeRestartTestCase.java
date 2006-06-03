package org.apache.tuscany.core.component.scope;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.component.scope.ModuleScopeContext;
import org.apache.tuscany.core.component.event.CompositeStart;
import org.apache.tuscany.core.component.event.CompositeStop;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.apache.tuscany.core.mock.factories.MockContextFactory;
import org.apache.tuscany.core.system.component.SystemAtomicComponent;
import org.apache.tuscany.spi.context.WorkContext;

/**
 * @version $$Rev$$ $$Date$$
 */
public class ModuleScopeRestartTestCase extends TestCase {

    public void testRestart() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ModuleScopeContext scope = new ModuleScopeContext(ctx);
        scope.start();
        MethodEventInvoker<Object> initInvoker = new MethodEventInvoker<Object>(InitDestroyOnce.class.getMethod("init"));
        MethodEventInvoker<Object> destroyInvoker = new MethodEventInvoker<Object>(InitDestroyOnce.class.getMethod("destroy"));
        List<Class<?>> interfaces = new ArrayList<Class<?>>();
        interfaces.add(InitDestroyOnce.class);
        SystemAtomicComponent context = MockContextFactory.createSystemAtomicContext("InitDestroy", scope, interfaces,
                InitDestroyOnce.class, false, initInvoker, destroyInvoker, null,null);
        context.start();

        scope.onEvent(new CompositeStart(this, null));
        Object instance = context.getService();
        assertSame(instance, context.getService());

        scope.onEvent(new CompositeStop(this, null));
        scope.stop();
        context.stop();

        scope.start();
        scope.onEvent(new CompositeStart(this, null));
        scope.register(context);
        context.start();
        assertNotSame(instance, context.getService());
        scope.onEvent(new CompositeStop(this, null));
        scope.stop();
        context.stop();
    }

    public static class InitDestroyOnce {

        private boolean initialized;
        private boolean destroyed;

        public InitDestroyOnce() {
        }

        public void init() {
            if (!initialized) {
                initialized = true;
            } else {
                fail("Scope did not clean up properly - Init called more than once");
            }
        }

        public void destroy() {
            if (!destroyed) {
                destroyed = true;
            } else {
                fail("Scope did not clean up properly - Destroyed called more than once");
            }
        }

    }
}
