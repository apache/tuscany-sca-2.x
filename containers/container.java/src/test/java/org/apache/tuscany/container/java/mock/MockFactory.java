package org.apache.tuscany.container.java.mock;

import java.lang.reflect.Constructor;
import java.util.List;

import org.apache.tuscany.common.ObjectCreationException;
import org.apache.tuscany.common.ObjectFactory;
import org.apache.tuscany.container.java.context.JavaAtomicContext;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.spi.context.AtomicContext;

/**
 * @version $$Rev$$ $$Date$$
 */
public class MockFactory {


    public static JavaAtomicContext createJavaAtomicContext(String name, Class<?> clazz, boolean eagerInit, EventInvoker<Object> initInvoker,
                                                            EventInvoker<Object> destroyInvoker, List<Injector> injectors) throws NoSuchMethodException {
        return new JavaAtomicContext(name, createObjectFactory(clazz, null), false, null, null, null, null);
    }


    private static <T> ObjectFactory<T> createObjectFactory(Class<T> clazz, List<Injector> injectors) throws NoSuchMethodException {
        Constructor<T> ctr = clazz.getConstructor((Class<T>[]) null);
        return new PojoObjectFactory<T>(ctr, null, injectors);
    }


    /**
     * Used for injecting references
     */
    private static class AtomicContextInstanceFactory implements ObjectFactory {
        private AtomicContext ctx;

        public AtomicContextInstanceFactory(AtomicContext ctx) {
            this.ctx = ctx;
        }

        public Object getInstance() throws ObjectCreationException {
            return ctx.getTargetInstance();
        }
    }

}
