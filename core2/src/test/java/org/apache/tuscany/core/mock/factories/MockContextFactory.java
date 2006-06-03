package org.apache.tuscany.core.mock.factories;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.system.component.SystemAtomicComponent;
import org.apache.tuscany.core.system.component.SystemAtomicComponentImpl;
import org.apache.tuscany.core.system.wire.SystemInboundWireImpl;
import org.apache.tuscany.core.system.wire.SystemOutboundWireImpl;
import org.apache.tuscany.core.system.wire.SystemOutboundWire;
import org.apache.tuscany.core.util.MethodHashMap;
import org.apache.tuscany.core.wire.InboundInvocationChainImpl;
import org.apache.tuscany.core.wire.InboundWireImpl;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.context.AtomicComponent;
import org.apache.tuscany.spi.context.ScopeContext;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;

/**
 * @version $$Rev$$ $$Date$$
 */
public class MockContextFactory {

    private MockContextFactory() {
    }

    public static Map<String, AtomicComponent> createWiredContexts(String source, Class<?> sourceClass, ScopeContext sourceScopeCtx,
                                                                 String target, Class<?> targetClass, ScopeContext targetScopeCtx) throws NoSuchMethodException {
        List<Class<?>> sourceClasses = new ArrayList<Class<?>>();
        sourceClasses.add(sourceClass);
        return createWiredContexts(source, sourceClasses, sourceClass, sourceScopeCtx, target, targetClass, targetScopeCtx);
    }

    /**
     * Creates source and target {@link AtomicComponent}s whose instances are wired together. The wiring
     * algorithm searches for the first method on the source with a single parameter type matching an
     * interface implemented by the target.
     *
     * @throws NoSuchMethodException
     */
    public static Map<String, AtomicComponent> createWiredContexts(String source,
                                                                 List<Class<?>> sourceInterfaces,
                                                                 Class<?> sourceClass,
                                                                 ScopeContext sourceScopeCtx,
                                                                 String target,
                                                                 Class<?> targetClass,
                                                                 ScopeContext targetScopeCtx) throws NoSuchMethodException {

        Map<String, AtomicComponent> contexts = new HashMap<String, AtomicComponent>();
        SystemAtomicComponent targetCtx = createSystemAtomicContext(target, targetScopeCtx, targetClass);//, targetEager, targetInitInvoker, targetDestroyInvoker, null);

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

        Map<String, Member> members = new HashMap<String, Member>();
        members.put(setter.getName(), setter);
        SystemAtomicComponent sourceCtx = createSystemAtomicContext(source, sourceScopeCtx, sourceInterfaces, sourceClass, null, members);//, sourceEager, sourceInitInvoker, sourceDestroyInvoker, injectors);
        QualifiedName targetName = new QualifiedName(target);
        SystemOutboundWire wire = new SystemOutboundWireImpl(setter.getName(), targetName, targetClass);
        InboundWire inboundWire = new SystemInboundWireImpl(targetName.getPortName(), targetClass, targetCtx);
        wire.setTargetWire(inboundWire);

        sourceCtx.addOutboundWire(wire);
        contexts.put(source, sourceCtx);
        contexts.put(target, targetCtx);
        return contexts;
    }

    public static SystemAtomicComponent createSystemAtomicContext(String name, ScopeContext scopeContext, Class<?> clazz) throws NoSuchMethodException {
        List<Class<?>> serviceInterfaces = new ArrayList<Class<?>>();
        serviceInterfaces.add(clazz);
        return createSystemAtomicContext(name, scopeContext, serviceInterfaces, clazz);
    }

    public static SystemAtomicComponent createSystemAtomicContext(String name, ScopeContext scopeContext, List<Class<?>> interfaces, Class<?> clazz) throws NoSuchMethodException {
        return createSystemAtomicContext(name, scopeContext, interfaces, clazz, null, null);
    }

    public static SystemAtomicComponent createSystemAtomicContext(String name,
                                                                ScopeContext scopeContext,
                                                                List<Class<?>> serviceInterfaces,
                                                                Class<?> clazz,
                                                                List<Injector> injectors,
                                                                Map<String, Member> members) throws NoSuchMethodException {
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
        return createSystemAtomicContext(name, scopeContext, serviceInterfaces, clazz, eager, initInvoker, destroyInvoker, injectors, members);
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
    public static SystemAtomicComponentImpl createSystemAtomicContext(String name,
                                                                    ScopeContext scopeContext,
                                                                    List<Class<?>> serviceInterfaces,
                                                                    Class<?> clazz,
                                                                    boolean eagerInit,
                                                                    EventInvoker<Object> initInvoker,
                                                                    EventInvoker<Object> destroyInvoker,
                                                                    List<Injector> injectors,
                                                                    Map<String, Member> members) throws NoSuchMethodException {
        return new SystemAtomicComponentImpl(name, null, scopeContext, serviceInterfaces, createObjectFactory(clazz), eagerInit, initInvoker, destroyInvoker, injectors, members);
    }

    public static <T> InboundWire<T> createTargetWireFactory(String serviceName, Class<T> interfaze) {
        InboundWire<T> wire = new InboundWireImpl<T>();
        wire.setServiceName(serviceName);
        wire.setBusinessInterface(interfaze);
        wire.addInvocationChains(createTargetInvocationConfigurations(interfaze));
        return wire;
    }

    public static Map<Method, InboundInvocationChain> createTargetInvocationConfigurations(Class<?> interfaze) {
        Map<Method, InboundInvocationChain> invocations = new MethodHashMap<InboundInvocationChain>();
        Method[] methods = interfaze.getMethods();
        for (Method method : methods) {
            InboundInvocationChain iConfig = new InboundInvocationChainImpl(method);
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

}
