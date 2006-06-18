package org.apache.tuscany.core.component.scope;

import java.lang.reflect.Constructor;

import junit.framework.TestCase;
import org.apache.tuscany.spi.component.PojoConfiguration;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.component.event.RequestEnd;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.apache.tuscany.spi.injection.PojoObjectFactory;
import org.apache.tuscany.core.system.component.SystemAtomicComponent;
import org.apache.tuscany.core.system.component.SystemAtomicComponentImpl;
import org.apache.tuscany.spi.component.WorkContext;

/**
 * Verifies the scope container properly disposes resources and canbe restarted
 *
 * @version $$Rev$$ $$Date$$
 */
public class RequestScopeRestartTestCase extends TestCase {

    public void testRestart() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        RequestScopeContainer scope = new RequestScopeContainer(ctx);
        scope.start();
        MethodEventInvoker<Object> initInvoker =
            new MethodEventInvoker<Object>(InitDestroyOnce.class.getMethod("init"));
        MethodEventInvoker<Object> destroyInvoker =
            new MethodEventInvoker<Object>(InitDestroyOnce.class.getMethod("destroy"));
        PojoConfiguration configuration = new PojoConfiguration();
        configuration.setScopeContainer(scope);
        configuration.addServiceInterface(InitDestroyOnce.class);
        configuration.setInitInvoker(initInvoker);
        configuration.setDestroyInvoker(destroyInvoker);
        Constructor<InitDestroyOnce> ctr = InitDestroyOnce.class.getConstructor((Class<?>[]) null);
        configuration.setObjectFactory(new PojoObjectFactory<InitDestroyOnce>(ctr));
        SystemAtomicComponent context = new SystemAtomicComponentImpl("InitDestroy", configuration);
        context.start();

        Object instance = context.getServiceInstance();
        assertSame(instance, context.getServiceInstance());

        scope.onEvent(new RequestEnd(this));
        scope.stop();
        context.stop();

        scope.start();
        context.start();
        assertNotSame(instance, context.getServiceInstance());
        scope.onEvent(new RequestEnd(this));
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
