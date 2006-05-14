package org.apache.tuscany.core.context.scope;

import java.util.List;
import java.util.ArrayList;

import junit.framework.TestCase;
import org.apache.tuscany.spi.context.WorkContext;
import org.apache.tuscany.core.context.WorkContextImpl;
import org.apache.tuscany.core.context.event.HttpSessionStart;
import org.apache.tuscany.core.context.event.HttpSessionEnd;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.apache.tuscany.core.system.context.SystemAtomicContext;
import org.apache.tuscany.core.mock.MockContextFactory;

/**
 * @version $$Rev$$ $$Date$$
 */
public class HttpSessionScopeRestartTestCase extends TestCase {

    public void testRestart() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        HttpSessionScopeContext scope = new HttpSessionScopeContext(ctx);
        scope.start();
        MethodEventInvoker<Object> initInvoker = new MethodEventInvoker<Object>(HttpSessionScopeRestartTestCase.InitDestroyOnce.class.getMethod("init"));
        MethodEventInvoker<Object> destroyInvoker = new MethodEventInvoker<Object>(HttpSessionScopeRestartTestCase.InitDestroyOnce.class.getMethod("destroy"));
        List<Class<?>> interfaces = new ArrayList<Class<?>>();
        interfaces.add(HttpSessionScopeRestartTestCase.InitDestroyOnce.class);
        SystemAtomicContext context = MockContextFactory.createSystemAtomicContext("InitDestroy", interfaces,
                HttpSessionScopeRestartTestCase.InitDestroyOnce.class, false, initInvoker, destroyInvoker, null);
        context.setScopeContext(scope);
        context.start();

        Object session = new Object();
        ctx.setIdentifier(HttpSessionScopeContext.HTTP_IDENTIFIER,session);
        scope.onEvent(new HttpSessionStart(this,session));
        Object instance = context.getService();
        assertSame(instance, context.getService());

        scope.onEvent(new HttpSessionEnd(this,session));
        scope.stop();
        context.stop();

        scope.start();
        scope.onEvent(new HttpSessionStart(this,session));
        scope.register(context);
        context.start();
        assertNotSame(instance, context.getService());
        scope.onEvent(new HttpSessionEnd(this,session));
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
