package org.apache.tuscany.container.java.mock;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.container.java.JavaAtomicContext;
import org.apache.tuscany.container.java.JavaTargetInvoker;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.util.MethodHashMap;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.core.wire.MessageChannelImpl;
import org.apache.tuscany.core.wire.ReferenceInvocationChainImpl;
import org.apache.tuscany.core.wire.ServiceInvocationChainImpl;
import org.apache.tuscany.core.wire.jdk.JDKReferenceWire;
import org.apache.tuscany.core.wire.jdk.JDKServiceWire;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.ScopeContext;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.MessageHandler;
import org.apache.tuscany.spi.wire.ReferenceInvocationChain;
import org.apache.tuscany.spi.wire.ReferenceWire;
import org.apache.tuscany.spi.wire.ServiceInvocationChain;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.ServiceWire;

/**
 * @version $$Rev$$ $$Date$$
 */
public class MockContextFactory {

    public static JavaAtomicContext<?> createJavaAtomicContext(String name, ScopeContext scopeContext, Class<?> clazz, Scope scope) throws NoSuchMethodException {
        return createJavaAtomicContext(name, null, scopeContext, clazz, clazz, scope, false, null, null, null, null);

    }

    public static JavaAtomicContext<?> createJavaAtomicContext(String name, ScopeContext scopeContext, Class<?> clazz, Class<?> service, Scope scope) throws NoSuchMethodException {
        return createJavaAtomicContext(name, null, scopeContext, clazz, service, scope, false, null, null, null, null);

    }

    public static JavaAtomicContext<?> createJavaAtomicContext(String name,
                                                               CompositeContext<?> parent,
                                                               ScopeContext scopeContext,
                                                               Class<?> clazz,
                                                               Class<?> service,
                                                               Scope scope,
                                                               boolean eagerInit,
                                                               EventInvoker<Object> initInvoker,
                                                               EventInvoker<Object> destroyInvoker,
                                                               List<Injector> injectors,
                                                               Map<String, Member> members)
            throws NoSuchMethodException {
        List<Class<?>> serviceInterfaces = new ArrayList<Class<?>>();
        serviceInterfaces.add(service);
        return new JavaAtomicContext(name, parent, scopeContext, serviceInterfaces, createObjectFactory(clazz), scope, eagerInit, initInvoker, destroyInvoker, injectors, members);
    }

    /**
     * Wires two contexts together where the reference interface is the same as target service
     *
     * @param sourceName
     * @param sourceClass
     * @param sourceScope
     * @param members
     * @param targetName
     * @param targetService
     * @param targetClass
     * @param targetScope
     * @return
     * @throws Exception
     */
    public static Map<String, AtomicContext> createWiredContexts(String sourceName, Class<?> sourceClass,
                                                                 ScopeContext sourceScope,
                                                                 Map<String, Member> members, String targetName, Class<?> targetService, Class<?> targetClass,
                                                                 ScopeContext targetScope) throws Exception {
        return createWiredContexts(sourceName, sourceClass, targetService, sourceScope, members, targetName, targetService, targetClass, targetScope);

    }

    /**
     * Wires two contexts together where the reference interface may be different from the target service
     */
    public static Map<String, AtomicContext> createWiredContexts(String sourceName, Class<?> sourceClass, Class<?> sourceReferenceClass,
                                                                 ScopeContext sourceScope,
                                                                 Map<String, Member> members, String targetName,
                                                                 Class<?> targetService, Class<?> targetClass,
                                                                 ScopeContext targetScope) throws Exception {
        return createWiredContexts(sourceName, sourceClass, sourceReferenceClass, sourceScope, null, null, null, members, targetName, targetService,
                targetClass, targetScope, null, null, null);
    }

    public static Map<String, AtomicContext> createWiredContexts(String sourceName, Class<?> sourceClass, Class<?> sourceReferenceClass,
                                                                 ScopeContext sourceScope,
                                                                 Interceptor sourceHeadInterceptor,
                                                                 MessageHandler sourceHeadRequestHandler,
                                                                 MessageHandler sourceHeadResponseHandler,
                                                                 Map<String, Member> members,
                                                                 String targetName, Class<?> targetService, Class<?> targetClass,
                                                                 ScopeContext targetScope,
                                                                 Interceptor targetHeadInterceptor,
                                                                 MessageHandler targetRequestHeadHandler,
                                                                 MessageHandler targetResponseHeadHandler) throws Exception {
        JavaAtomicContext targetContext = createJavaAtomicContext(targetName, targetScope, targetClass, targetScope.getScope());
        ServiceWire serviceWire = createServiceWire(targetService.getName().substring(
                targetService.getName().lastIndexOf('.') + 1), targetService, targetHeadInterceptor, targetRequestHeadHandler, targetResponseHeadHandler);
        targetContext.addServiceWire(serviceWire);

        JavaAtomicContext sourceContext = createJavaAtomicContext(sourceName, null, sourceScope, sourceClass, sourceClass, sourceScope.getScope(), false, null, null, null, members);
        ReferenceWire referenceWire = createReferenceWire(targetName, sourceReferenceClass, sourceHeadInterceptor,
                sourceHeadRequestHandler, sourceHeadResponseHandler);
        sourceContext.addReferenceWire(referenceWire);
        targetScope.register(targetContext);
        sourceScope.register(sourceContext);
        connect(referenceWire, serviceWire, targetContext, false);
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
                                                                     ScopeContext sourceScope,
                                                                     String targetName, Class<?> targetService, Class<?> targetClass,
                                                                     Map<String, Member> members, ScopeContext targetScope) throws Exception {
        JavaAtomicContext targetContext = createJavaAtomicContext(targetName, targetScope, targetClass, targetScope.getScope());
        ServiceWire serviceWire = createServiceWire(targetService.getName().substring(
                targetService.getName().lastIndexOf('.') + 1), targetService, null, null, null);
        targetContext.addServiceWire(serviceWire);

        JavaAtomicContext sourceContext = createJavaAtomicContext(sourceName, null, sourceScope, sourceClass, sourceClass, sourceScope.getScope(), false, null, null, null, members);
        ReferenceWire referenceWire = createReferenceWire(targetName, sourceReferenceClass, null, null, null);
        List<ReferenceWire> factories = new ArrayList<ReferenceWire>();
        factories.add(referenceWire);
        sourceContext.addReferenceWires(sourceReferenceClass, factories);
        targetScope.register(targetContext);
        sourceScope.register(sourceContext);
        connect(referenceWire, serviceWire, targetContext, false);
        Map<String, AtomicContext> contexts = new HashMap<String, AtomicContext>();
        contexts.put(sourceName, sourceContext);
        contexts.put(targetName, targetContext);
        return contexts;
    }

    public static <T> ServiceWire<T> createTargetWire(String serviceName, Class<T> interfaze) {
        return createServiceWire(serviceName, interfaze, null, null, null);
    }


    public static <T> ServiceWire<T> createServiceWire(String serviceName, Class<T> interfaze,
                                                       Interceptor headInterceptor,
                                                       MessageHandler headRequestHandler,
                                                       MessageHandler headResponseHandler) {
        ServiceWire<T> wire = new JDKServiceWire<T>();
        wire.setBusinessInterface(interfaze);
        wire.setServiceName(serviceName);
        wire.addInvocationChains(createServiceInvocationChains(interfaze, headInterceptor, headRequestHandler, headResponseHandler));
        return wire;
    }

    public static <T> ReferenceWire<T> createReferenceWire(String refName, Class<T> interfaze,
                                                           Interceptor headInterceptor,
                                                           MessageHandler headRequestHandler,
                                                           MessageHandler headResponseHandler) {

        ReferenceWire<T> wire = new JDKReferenceWire<T>();
        wire.setReferenceName(refName);
        wire.addInvocationChains(createReferenceInvocationChains(interfaze, headInterceptor, headRequestHandler, headResponseHandler));
        wire.setBusinessInterface(interfaze);
        return wire;
    }

    public static <T> ReferenceWire<T> createReferenceWire(String refName, Class<T> interfaze) {
        ReferenceWire<T> wire = new JDKReferenceWire<T>();
        wire.setReferenceName(refName);
        wire.addInvocationChains(createReferenceInvocationChains(interfaze));
        wire.setBusinessInterface(interfaze);
        return wire;
    }


    /**
     * @param referenceWire
     * @param serviceWire
     * @param targetContext
     * @param cacheable
     * @throws Exception
     */
    public static void connect(ReferenceWire<?> referenceWire, ServiceWire<?> serviceWire, JavaAtomicContext targetContext, boolean cacheable) throws Exception {
        if (serviceWire != null) {
            // if null, the target side has no interceptors or handlers
            Map<Method, ServiceInvocationChain> targetInvocationConfigs = serviceWire.getInvocationChains();
            for (ReferenceInvocationChain referenceInvocationConfig : referenceWire.getInvocationChains().values()) {
                // match wire chains
                ServiceInvocationChain serviceInvocationConfig = targetInvocationConfigs.get(referenceInvocationConfig.getMethod());
                if (serviceInvocationConfig == null) {
                    BuilderConfigException e = new BuilderConfigException("Incompatible source and target interface types for reference");
                    e.setIdentifier(referenceWire.getReferenceName());
                    throw e;
                }
                // if handler is configured, add that
                if (serviceInvocationConfig.getRequestHandlers() != null) {
                    referenceInvocationConfig.setTargetRequestChannel(new MessageChannelImpl(serviceInvocationConfig
                            .getRequestHandlers()));
                    referenceInvocationConfig.setTargetResponseChannel(new MessageChannelImpl(serviceInvocationConfig
                            .getResponseHandlers()));
                } else {
                    // no handlers, just connect interceptors
                    if (serviceInvocationConfig.getHeadInterceptor() == null) {
                        BuilderConfigException e = new BuilderConfigException("No target handler or interceptor for operation");
                        e.setIdentifier(serviceInvocationConfig.getMethod().getName());
                        throw e;
                    }
                    if (!(referenceInvocationConfig.getTailInterceptor() instanceof InvokerInterceptor && serviceInvocationConfig
                            .getHeadInterceptor() instanceof InvokerInterceptor)) {
                        // check that we do not have the case where the only interceptors are invokers since we just need one
                        referenceInvocationConfig.setTargetInterceptor(serviceInvocationConfig.getHeadInterceptor());
                    }
                }
            }

            for (ReferenceInvocationChain referenceInvocationConfig : referenceWire.getInvocationChains()
                    .values()) {
                //FIXME should use target method, not referenceInvocationConfig.getMethod()
                TargetInvoker invoker = new JavaTargetInvoker(referenceInvocationConfig.getMethod(), targetContext);
                invoker.setCacheable(cacheable);
                referenceInvocationConfig.setTargetInvoker(invoker);
            }
        }
    }

    private static Map<Method, ReferenceInvocationChain> createReferenceInvocationChains(Class<?> interfaze) {
        return createReferenceInvocationChains(interfaze, null, null, null);
    }

    private static Map<Method, ReferenceInvocationChain> createReferenceInvocationChains(Class<?> interfaze,
                                                                                         Interceptor headInterceptor, MessageHandler headRequestHandler,
                                                                                         MessageHandler headResponseHandler) {
        Map<Method, ReferenceInvocationChain> invocations = new HashMap<Method, ReferenceInvocationChain>();
        Method[] methods = interfaze.getMethods();
        for (Method method : methods) {
            ReferenceInvocationChain chain = new ReferenceInvocationChainImpl(method);
            if (headInterceptor != null) {
                chain.addInterceptor(headInterceptor);
            }
            if (headRequestHandler != null) {
                chain.addRequestHandler(headRequestHandler);
            }
            if (headResponseHandler != null) {
                chain.addRequestHandler(headResponseHandler);
            }
            invocations.put(method, chain);
        }
        return invocations;
    }

    private static Map<Method, ServiceInvocationChain> createServiceInvocationChains(Class<?> interfaze,
                                                                                     Interceptor headInterceptor, MessageHandler headRequestHandler,
                                                                                     MessageHandler headResponseHandler) {
        Map<Method, ServiceInvocationChain> invocations = new MethodHashMap<ServiceInvocationChain>();
        Method[] methods = interfaze.getMethods();
        for (Method method : methods) {
            ServiceInvocationChain chain = new ServiceInvocationChainImpl(method);
            if (headInterceptor != null) {
                chain.addInterceptor(headInterceptor);
            }
            if (headRequestHandler != null) {
                chain.addRequestHandler(headRequestHandler);
            }
            if (headResponseHandler != null) {
                chain.addRequestHandler(headResponseHandler);
            }
            // add tail interceptor
            chain.addInterceptor(new InvokerInterceptor());
            invocations.put(method, chain);
        }
        return invocations;
    }


    private static <T> ObjectFactory<T> createObjectFactory(Class<T> clazz) throws NoSuchMethodException {
        Constructor<T> ctr = clazz.getConstructor((Class<T>[]) null);
        return new PojoObjectFactory<T>(ctr);
    }


}
