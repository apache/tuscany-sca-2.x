package org.apache.tuscany.core.mock;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.common.ObjectCreationException;
import org.apache.tuscany.common.ObjectFactory;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.MethodInjector;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.system.context.SystemAtomicContext;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.ScopeContext;

/**
 * @version $$Rev$$ $$Date$$
 */
public class MockFactory {

    /**
     * Creates source and target {@link AtomicContext}s whose instances are wired together. The wiring
     * algorithm searches for the first method on the source with a single parameter type matching an
     * interface implemented by the target.
     *
     * @param source
     * @param sourceClass
     * @param target
     * @param targetClass
     * @param sourceScope
     * @param targetScope
     * @return
     * @throws NoSuchMethodException
     */
    public static List<AtomicContext> createWiredContexts(String source, Class<?> sourceClass, String target,
                                                          Class<?> targetClass,
                                                          ScopeContext<AtomicContext> sourceScope,
                                                          ScopeContext<AtomicContext> targetScope) throws NoSuchMethodException {

        List<AtomicContext> contexts = new ArrayList<AtomicContext>();
        SystemAtomicContext targetCtx = createSystemAtomicContext(target, targetClass, false, null, null, null);
        targetCtx.setScopeContext(targetScope);

        //wire the contexts
        Class[] interfaces = targetClass.getInterfaces();
        Method setter = null;
        for (Class interfaze : interfaces) {
            Method[] methods = sourceClass.getMethods();
            for (Method method : methods) {
                if (method.getParameterTypes().length == 1) {
                    if (interfaze.isAssignableFrom(method.getParameterTypes()[0])) {
                        setter = method;
                        break;
                    }
                }
            }
            if (setter != null) {
                break;
            }
        }
        if (setter == null) {
            throw new IllegalArgumentException("No setter found on source for target");
        }
        MethodInjector injector = new MethodInjector(setter, new AtomicContextInstanceFactory(targetCtx));
        List<Injector> injectors = new ArrayList<Injector>();
        injectors.add(injector);
        SystemAtomicContext sourceCtx = createSystemAtomicContext(source, sourceClass, false, null, null, injectors);
        sourceCtx.setScopeContext(sourceScope);

        contexts.add(sourceCtx);
        contexts.add(targetCtx);
        return contexts;
    }

    public static SystemAtomicContext createSystemAtomicContext(String name, Class<?> clazz, boolean eagerInit, EventInvoker<Object> initInvoker,
                                                                EventInvoker<Object> destroyInvoker, List<Injector> injectors) throws NoSuchMethodException {
        return new SystemAtomicContext(name, createObjectFactory(clazz, injectors), eagerInit, initInvoker, destroyInvoker);
    }

    private static <T> ObjectFactory<T> createObjectFactory(Class<T> clazz, List<Injector> injectors) throws NoSuchMethodException {
        Constructor<T> ctr = clazz.getConstructor((Class<T>[]) null);
        return new PojoObjectFactory<T>(ctr, null, injectors);
    }


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

//ObjectFactory<?> objectFactory, boolean eagerInit, EventInvoker<Object> initInvoker,
//                               EventInvoker<Object> destroyInvoker