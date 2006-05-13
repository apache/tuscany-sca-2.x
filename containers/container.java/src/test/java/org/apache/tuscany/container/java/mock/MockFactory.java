package org.apache.tuscany.container.java.mock;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.common.ObjectFactory;
import org.apache.tuscany.container.java.context.JavaAtomicContext;
import org.apache.tuscany.container.java.invocation.ScopedJavaComponentInvoker;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.util.MethodHashMap;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.core.wire.MessageChannelImpl;
import org.apache.tuscany.core.wire.SourceInvocationChainImpl;
import org.apache.tuscany.core.wire.TargetInvocationChainImpl;
import org.apache.tuscany.core.wire.jdk.JDKSourceWire;
import org.apache.tuscany.core.wire.jdk.JDKTargetWire;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.ScopeContext;
import org.apache.tuscany.spi.wire.SourceInvocationChain;
import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.spi.wire.TargetInvocationChain;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.TargetWire;
import org.apache.tuscany.spi.wire.WireFactoryInitException;

/**
 * @version $$Rev$$ $$Date$$
 */
public class MockFactory {

    public static JavaAtomicContext createJavaAtomicContext(String name, Class<?> clazz) throws NoSuchMethodException {
        return createJavaAtomicContext(name, clazz, false, null, null, null, null);

    }

    public static JavaAtomicContext createJavaAtomicContext(String name, Class<?> clazz, boolean eagerInit, EventInvoker<Object> initInvoker,
                                                            EventInvoker<Object> destroyInvoker, List<Injector> injectors, Map<String, Member> members) throws NoSuchMethodException {
        List<Class<?>> serviceInterfaces = new ArrayList<Class<?>>();
        serviceInterfaces.add(clazz);
        return new JavaAtomicContext(name, serviceInterfaces, createObjectFactory(clazz, null), eagerInit, initInvoker, destroyInvoker, injectors, members);
    }

    public static <T> TargetWire<T> createTargetWireFactory(String serviceName, Class<T> interfaze) throws WireFactoryInitException {
        TargetWire<T> wire = new JDKTargetWire<T>();
        wire.setBusinessInterface(interfaze);
        wire.setServiceName(serviceName);
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

    public static <T> SourceWire<T> createSourceWireFactory(String refName, Class<T> interfaze) throws WireFactoryInitException {
        SourceWire<T> wire = new JDKSourceWire<T>();
        wire.setReferenceName(refName);
        wire.setInvocationChains(createSourceInvocationConfigurations(interfaze));
        wire.setBusinessInterface(interfaze);
        wire.initialize();
        return wire;
    }

    /**
     * Wires two contexts together where the reference interface is the same as target service
     *
     * @param sourceName
     * @param sourceClass
     * @param sourceScope
     * @param targetName
     * @param targetService
     * @param targetClass
     * @param members
     * @param targetScope
     * @return
     * @throws Exception
     */
    public static Map<String, AtomicContext> createWiredContexts(String sourceName, Class<?> sourceClass,
                                                                 ScopeContext<AtomicContext> sourceScope,
                                                                 String targetName, Class<?> targetService, Class<?> targetClass,
                                                                 Map<String, Member> members, ScopeContext<AtomicContext> targetScope) throws Exception {
        return createWiredContexts(sourceName, sourceClass, targetService, sourceScope, targetName, targetService, targetClass, members, targetScope);

    }

    /**
     * Wires two contexts together where the reference interface may be different from the target service
     *
     * @param sourceName
     * @param sourceClass
     * @param sourceReferenceClass
     * @param sourceScope
     * @param targetName
     * @param targetService
     * @param targetClass
     * @param members
     * @param targetScope
     * @return
     * @throws Exception
     */
    public static Map<String, AtomicContext> createWiredContexts(String sourceName, Class<?> sourceClass, Class<?> sourceReferenceClass,
                                                                 ScopeContext<AtomicContext> sourceScope,
                                                                 String targetName, Class<?> targetService, Class<?> targetClass,
                                                                 Map<String, Member> members, ScopeContext<AtomicContext> targetScope) throws Exception {
        JavaAtomicContext targetContext = createJavaAtomicContext(targetName, targetClass);
        TargetWire targetWire = createTargetWireFactory(targetService.getName().substring(
                targetService.getName().lastIndexOf('.') + 1), targetService);
        targetContext.addTargetWire(targetWire);

        JavaAtomicContext sourceContext = createJavaAtomicContext(sourceName, sourceClass, false, null, null, null, members);
        SourceWire sourceWire = createSourceWireFactory(targetName, sourceReferenceClass);
        sourceContext.addSourceWire(sourceWire);
        targetScope.register(targetContext);
        sourceContext.setScopeContext(sourceScope);
        sourceScope.register(sourceContext);
        targetContext.setScopeContext(targetScope);
        connect(sourceWire, targetWire, targetContext, false);
        Map<String, AtomicContext> contexts = new HashMap<String, AtomicContext>();
        contexts.put(sourceName, sourceContext);
        contexts.put(targetName, targetContext);
        return contexts;
    }


    /**
     * Wires two contexts using a multiplicity reference
     *
     * @param sourceName
     * @param sourceClass
     * @param sourceReferenceClass
     * @param sourceScope
     * @param targetName
     * @param targetService
     * @param targetClass
     * @param members
     * @param targetScope
     * @return
     * @throws Exception
     */
    public static Map<String, AtomicContext> createWiredMultiplicity(String sourceName, Class<?> sourceClass, Class<?> sourceReferenceClass,
                                                                     ScopeContext<AtomicContext> sourceScope,
                                                                     String targetName, Class<?> targetService, Class<?> targetClass,
                                                                     Map<String, Member> members, ScopeContext<AtomicContext> targetScope) throws Exception {
        JavaAtomicContext targetContext = createJavaAtomicContext(targetName, targetClass);
        TargetWire targetWire = createTargetWireFactory(targetService.getName().substring(
                targetService.getName().lastIndexOf('.') + 1), targetService);
        targetContext.addTargetWire(targetWire);

        JavaAtomicContext sourceContext = createJavaAtomicContext(sourceName, sourceClass, false, null, null, null, members);
        SourceWire sourceWire = createSourceWireFactory(targetName, sourceReferenceClass);
        List<SourceWire> factories = new ArrayList<SourceWire>();
        factories.add(sourceWire);
        sourceContext.addSourceWires(sourceReferenceClass, factories);
        targetScope.register(targetContext);
        sourceContext.setScopeContext(sourceScope);
        sourceScope.register(sourceContext);
        targetContext.setScopeContext(targetScope);
        connect(sourceWire, targetWire, targetContext, false);
        Map<String, AtomicContext> contexts = new HashMap<String, AtomicContext>();
        contexts.put(sourceName, sourceContext);
        contexts.put(targetName, targetContext);
        return contexts;
    }


    /**
     * TODO refactor
     *
     * @param sourceWire
     * @param targetWire
     * @param targetContext
     * @param cacheable
     * @throws Exception
     */
    public static void connect(SourceWire<?> sourceWire, TargetWire<?> targetWire, AtomicContext targetContext, boolean cacheable) throws Exception {
        if (targetWire != null) {
            // if null, the target side has no interceptors or handlers
            Map<Method, TargetInvocationChain> targetInvocationConfigs = targetWire.getInvocationChains();
            for (SourceInvocationChain sourceInvocationConfig : sourceWire.getInvocationChains().values())
            {
                // match wire chains
                TargetInvocationChain targetInvocationConfig = targetInvocationConfigs.get(sourceInvocationConfig.getMethod());
                if (targetInvocationConfig == null) {
                    BuilderConfigException e = new BuilderConfigException("Incompatible source and target interface types for reference");
                    e.setIdentifier(sourceWire.getReferenceName());
                    throw e;
                }
                // if handler is configured, add that
                if (targetInvocationConfig.getRequestHandlers() != null) {
                    sourceInvocationConfig.setTargetRequestChannel(new MessageChannelImpl(targetInvocationConfig
                            .getRequestHandlers()));
                    sourceInvocationConfig.setTargetResponseChannel(new MessageChannelImpl(targetInvocationConfig
                            .getResponseHandlers()));
                } else {
                    // no handlers, just connect interceptors
                    if (targetInvocationConfig.getHeadInterceptor() == null) {
                        BuilderConfigException e = new BuilderConfigException("No target handler or interceptor for operation");
                        e.setIdentifier(targetInvocationConfig.getMethod().getName());
                        throw e;
                    }
                    if (!(sourceInvocationConfig.getTailInterceptor() instanceof InvokerInterceptor && targetInvocationConfig
                            .getHeadInterceptor() instanceof InvokerInterceptor)) {
                        // check that we do not have the case where the only interceptors are invokers since we just need one
                        sourceInvocationConfig.setTargetInterceptor(targetInvocationConfig.getHeadInterceptor());
                    }
                }
            }

            for (SourceInvocationChain sourceInvocationConfig : sourceWire.getInvocationChains()
                    .values()) {
                //FIXME should use target method, not sourceInvocationConfig.getMethod()
                TargetInvoker invoker = new ScopedJavaComponentInvoker(sourceInvocationConfig.getMethod(), targetContext);
                invoker.setCacheable(cacheable);
                sourceInvocationConfig.setTargetInvoker(invoker);
            }
        }
    }


    public static Map<Method, SourceInvocationChain> createSourceInvocationConfigurations(Class<?> interfaze) {
        Map<Method, SourceInvocationChain> invocations = new HashMap<Method, SourceInvocationChain>();
        Method[] methods = interfaze.getMethods();
        for (Method method : methods) {
            invocations.put(method, new SourceInvocationChainImpl(method));
        }
        return invocations;
    }

    private static <T> ObjectFactory<T> createObjectFactory(Class<T> clazz, List<Injector> injectors) throws NoSuchMethodException {
        Constructor<T> ctr = clazz.getConstructor((Class<T>[]) null);
        return new PojoObjectFactory<T>(ctr, null, injectors);
    }


}
