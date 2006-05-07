package org.apache.tuscany.core.mock;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.common.ObjectCreationException;
import org.apache.tuscany.common.ObjectFactory;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.apache.tuscany.core.injection.MethodInjector;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.system.context.SystemAtomicContext;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.ScopeContext;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;

/**
 * @version $$Rev$$ $$Date$$
 */
public class MockFactory {

    /**
     * Creates source and target {@link AtomicContext}s whose instances are wired together. The wiring
     * algorithm searches for the first method on the source with a single parameter type matching an
     * interface implemented by the target.
     *
     * @throws NoSuchMethodException
     */
    public static Map<String, AtomicContext> createWiredContexts(String source, Class<?> sourceClass, ScopeContext<AtomicContext> sourceScopeCtx,
                                                                 String target, Class<?> targetClass,ScopeContext<AtomicContext> targetScopeCtx) throws NoSuchMethodException {

        Map<String, AtomicContext> contexts = new HashMap<String, AtomicContext>();
        SystemAtomicContext targetCtx = createSystemAtomicContext(target, targetClass);//, targetEager, targetInitInvoker, targetDestroyInvoker, null);
        targetCtx.setScopeContext(targetScopeCtx);

        //create target wire
        Method[] sourceMethods = sourceClass.getMethods();
        Class[] interfaces = targetClass.getInterfaces();
        Method setter = null;
        for (Class interfaze : interfaces) {
            for (Method method : sourceMethods) {
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
        SystemAtomicContext sourceCtx = createSystemAtomicContext(source, sourceClass,injectors);//, sourceEager, sourceInitInvoker, sourceDestroyInvoker, injectors);
        sourceCtx.setScopeContext(sourceScopeCtx);
        contexts.put(source, sourceCtx);
        contexts.put(target, targetCtx);
        return contexts;
    }


    public static SystemAtomicContext createSystemAtomicContext(String name, Class<?> clazz) throws NoSuchMethodException {
       return  createSystemAtomicContext(name, clazz, null);
    }

    public static SystemAtomicContext createSystemAtomicContext(String name, Class<?> clazz, List<Injector> injectors) throws NoSuchMethodException {
        Method[] methods = clazz.getMethods();
        EventInvoker<Object> initInvoker = null;
        EventInvoker<Object> destroyInvoker = null;
        boolean eager = false;
        for (Method method : methods) {
            Init init;
            if ((init = method.getAnnotation(Init.class)) != null) {
                eager = init.eager();
                initInvoker = new MethodEventInvoker<Object>(method);

            } else if (method.getAnnotation(Destroy.class) != null) {
                destroyInvoker = new MethodEventInvoker<Object>(method);
            }
        }
        return createSystemAtomicContext(name, clazz, eager, initInvoker, destroyInvoker, injectors);
    }

    /**
     * Creates a system atomic context
     *
     * @param name           the name of the context
     * @param clazz          the component implementation class
     * @param eagerInit      if the component eager initializes
     * @param initInvoker    the invoker for {@link org.osoa.sca.annotations.Init}
     * @param destroyInvoker the invoker for {@link org.osoa.sca.annotations.Destroy}
     * @param injectors      the injectors responsible for injecting on an instance
     * @throws NoSuchMethodException
     */
    public static SystemAtomicContext createSystemAtomicContext(String name, Class<?> clazz, boolean eagerInit, EventInvoker<Object> initInvoker,
                                                                EventInvoker<Object> destroyInvoker, List<Injector> injectors) throws NoSuchMethodException {
        return new SystemAtomicContext(name, createObjectFactory(clazz, injectors), eagerInit, initInvoker, destroyInvoker);
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
