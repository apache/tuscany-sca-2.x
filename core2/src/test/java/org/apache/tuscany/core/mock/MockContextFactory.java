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
import org.apache.tuscany.core.system.context.SystemAtomicContextImpl;
import org.apache.tuscany.core.util.MethodHashMap;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.core.wire.TargetInvocationChainImpl;
import org.apache.tuscany.core.wire.jdk.JDKTargetWire;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.ScopeContext;
import org.apache.tuscany.spi.wire.TargetInvocationChain;
import org.apache.tuscany.spi.wire.TargetWire;
import org.apache.tuscany.spi.wire.WireFactoryInitException;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;

/**
 * @version $$Rev$$ $$Date$$
 */
public class MockContextFactory {

    public static Map<String, AtomicContext> createWiredContexts(String source, Class<?> sourceClass, ScopeContext<AtomicContext> sourceScopeCtx,
                                                                 String target, Class<?> targetClass, ScopeContext<AtomicContext> targetScopeCtx) throws NoSuchMethodException {
        List<Class<?>> sourceClasses = new ArrayList<Class<?>>();
        sourceClasses.add(sourceClass);
        return createWiredContexts(source, sourceClasses, sourceClass, sourceScopeCtx, target, targetClass, targetScopeCtx);
    }

    /**
     * Creates source and target {@link AtomicContext}s whose instances are wired together. The wiring
     * algorithm searches for the first method on the source with a single parameter type matching an
     * interface implemented by the target.
     *
     * @throws NoSuchMethodException
     */
    public static Map<String, AtomicContext> createWiredContexts(String source, List<Class<?>> sourceInterfaces, Class<?> sourceClass, ScopeContext<AtomicContext> sourceScopeCtx,
                                                                 String target, Class<?> targetClass, ScopeContext<AtomicContext> targetScopeCtx) throws NoSuchMethodException {

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
        SystemAtomicContext sourceCtx = createSystemAtomicContext(source, sourceInterfaces, sourceClass, injectors);//, sourceEager, sourceInitInvoker, sourceDestroyInvoker, injectors);
        sourceCtx.setScopeContext(sourceScopeCtx);
        contexts.put(source, sourceCtx);
        contexts.put(target, targetCtx);
        return contexts;
    }


    public static SystemAtomicContext createSystemAtomicContext(String name, Class<?> clazz) throws NoSuchMethodException {
        List<Class<?>> serviceInterfaces = new ArrayList<Class<?>>();
        serviceInterfaces.add(clazz);
        return createSystemAtomicContext(name, serviceInterfaces, clazz, null);
    }

    public static SystemAtomicContext createSystemAtomicContext(String name, List<Class<?>> serviceInterfaces,
                                                                Class<?> clazz, List<Injector> injectors) throws NoSuchMethodException {
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
        return createSystemAtomicContext(name, serviceInterfaces, clazz, eager, initInvoker, destroyInvoker, injectors);
    }

    /**
     * Creates a system atomic context
     *
     * @param name           the name of the context
     * @param clazz          the component implementation class
     * @param eagerInit      if the component eager initializes
     * @param initInvoker    the invoker for {@link org.osoa.sca.annotations.Init}
     * @param destroyInvoker the invoker for {@link org.osoa.sca.annotations.Destroy}
     * @param injectors
     * @throws NoSuchMethodException
     */
    public static SystemAtomicContextImpl createSystemAtomicContext(String name, List<Class<?>> serviceInterfaces, Class<?> clazz, boolean eagerInit, EventInvoker<Object> initInvoker,
                                                                    EventInvoker<Object> destroyInvoker, List<Injector> injectors) throws NoSuchMethodException {
        return new SystemAtomicContextImpl(name, serviceInterfaces, createObjectFactory(clazz), eagerInit, initInvoker, destroyInvoker, injectors);
    }

    public static <T> TargetWire<T> createTargetWireFactory(String serviceName, Class<T> interfaze) throws WireFactoryInitException {
        TargetWire<T> wire = new JDKTargetWire<T>();
        wire.setServiceName(serviceName);
        wire.setBusinessInterface(interfaze);
        wire.setInvocationChains(createTargetInvocationConfigurations(interfaze));
        wire.initialize();
        return wire;
    }

    public static Map<Method, TargetInvocationChain> createTargetInvocationConfigurations(Class<?> interfaze) {
        Map<Method, TargetInvocationChain> invocations = new MethodHashMap<TargetInvocationChain>();
        Method[] methods = interfaze.getMethods();
        for (Method method : methods) {
            TargetInvocationChain iConfig = new TargetInvocationChainImpl(method);
            // add tail interceptor
            iConfig.addInterceptor(new InvokerInterceptor());
            invocations.put(method, iConfig);
        }
        return invocations;
    }


    private static <T> ObjectFactory<T> createObjectFactory(Class<T> clazz) throws NoSuchMethodException {
        Constructor<T> ctr = clazz.getConstructor((Class<T>[]) null);
        return new PojoObjectFactory<T>(ctr);
    }


    /**
     * Used for injecting references
     */
    private static class AtomicContextInstanceFactory implements ObjectFactory {
        private SystemAtomicContext ctx;

        public AtomicContextInstanceFactory(SystemAtomicContext ctx) {
            this.ctx = ctx;
        }

        public Object getInstance() throws ObjectCreationException {
            return ctx.getTargetInstance();
        }
    }

}
