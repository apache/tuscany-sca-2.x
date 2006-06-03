package org.apache.tuscany.container.java.mock;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.container.java.JavaAtomicComponent;
import org.apache.tuscany.container.java.JavaTargetInvoker;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.util.MethodHashMap;
import org.apache.tuscany.core.wire.InboundInvocationChainImpl;
import org.apache.tuscany.core.wire.InboundWireImpl;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.core.wire.MessageChannelImpl;
import org.apache.tuscany.core.wire.OutboundInvocationChainImpl;
import org.apache.tuscany.core.wire.OutboundWireImpl;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ScopeContext;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.MessageHandler;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireService;
import org.apache.tuscany.test.ArtifactFactory;

/**
 * @version $$Rev$$ $$Date$$
 */
public class MockContextFactory {

    private static final WireService wireService = ArtifactFactory.createWireService();

    public static JavaAtomicComponent<?> createJavaAtomicContext(String name, ScopeContext scopeContext, Class<?> clazz, Scope scope) throws NoSuchMethodException {
        return createJavaAtomicContext(name, null, scopeContext, clazz, clazz, scope, false, null, null, null, null);

    }

    public static JavaAtomicComponent<?> createJavaAtomicContext(String name, ScopeContext scopeContext, Class<?> clazz, Class<?> service, Scope scope) throws NoSuchMethodException {
        return createJavaAtomicContext(name, null, scopeContext, clazz, service, scope, false, null, null, null, null);

    }

    public static JavaAtomicComponent<?> createJavaAtomicContext(String name,
                                                                 CompositeComponent<?> parent,
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
        return new JavaAtomicComponent(name, parent, scopeContext, serviceInterfaces, createObjectFactory(clazz),
                scope, eagerInit, initInvoker, destroyInvoker, injectors, members, wireService);
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
    public static Map<String, AtomicComponent> createWiredContexts(String sourceName, Class<?> sourceClass,
                                                                   ScopeContext sourceScope,
                                                                   Map<String, Member> members, String targetName, Class<?> targetService, Class<?> targetClass,
                                                                   ScopeContext targetScope) throws Exception {
        return createWiredContexts(sourceName, sourceClass, targetService, sourceScope, members, targetName, targetService, targetClass, targetScope);

    }

    /**
     * Wires two contexts together where the reference interface may be different from the target service
     */
    public static Map<String, AtomicComponent> createWiredContexts(String sourceName, Class<?> sourceClass, Class<?> sourceReferenceClass,
                                                                   ScopeContext sourceScope,
                                                                   Map<String, Member> members, String targetName,
                                                                   Class<?> targetService, Class<?> targetClass,
                                                                   ScopeContext targetScope) throws Exception {
        return createWiredContexts(sourceName, sourceClass, sourceReferenceClass, sourceScope, null, null, null, members, targetName, targetService,
                targetClass, targetScope, null, null, null);
    }

    public static Map<String, AtomicComponent> createWiredContexts(String sourceName, Class<?> sourceClass, Class<?> sourceReferenceClass,
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
        JavaAtomicComponent targetContext = createJavaAtomicContext(targetName, targetScope, targetClass, targetScope.getScope());
        InboundWire inboundWire = createServiceWire(targetService.getName().substring(
                targetService.getName().lastIndexOf('.') + 1), targetService, targetHeadInterceptor, targetRequestHeadHandler, targetResponseHeadHandler);
        targetContext.addInboundWire(inboundWire);

        JavaAtomicComponent sourceContext = createJavaAtomicContext(sourceName, null, sourceScope, sourceClass, sourceClass, sourceScope.getScope(), false, null, null, null, members);
        OutboundWire outboundWire = createReferenceWire(targetName, sourceReferenceClass, sourceHeadInterceptor,
                sourceHeadRequestHandler, sourceHeadResponseHandler);
        sourceContext.addOutboundWire(outboundWire);
        targetScope.register(targetContext);
        sourceScope.register(sourceContext);
        connect(outboundWire, inboundWire, targetContext, false);
        Map<String, AtomicComponent> contexts = new HashMap<String, AtomicComponent>();
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
    public static Map<String, AtomicComponent> createWiredMultiplicity(String sourceName, Class<?> sourceClass, Class<?> sourceReferenceClass,
                                                                       ScopeContext sourceScope,
                                                                       String targetName, Class<?> targetService, Class<?> targetClass,
                                                                       Map<String, Member> members, ScopeContext targetScope) throws Exception {
        JavaAtomicComponent targetContext = createJavaAtomicContext(targetName, targetScope, targetClass, targetScope.getScope());
        InboundWire inboundWire = createServiceWire(targetService.getName().substring(
                targetService.getName().lastIndexOf('.') + 1), targetService, null, null, null);
        targetContext.addInboundWire(inboundWire);

        JavaAtomicComponent sourceContext = createJavaAtomicContext(sourceName, null, sourceScope, sourceClass, sourceClass, sourceScope.getScope(), false, null, null, null, members);
        OutboundWire outboundWire = createReferenceWire(targetName, sourceReferenceClass, null, null, null);
        List<OutboundWire> factories = new ArrayList<OutboundWire>();
        factories.add(outboundWire);
        sourceContext.addOutboundWires(sourceReferenceClass, factories);
        targetScope.register(targetContext);
        sourceScope.register(sourceContext);
        connect(outboundWire, inboundWire, targetContext, false);
        Map<String, AtomicComponent> contexts = new HashMap<String, AtomicComponent>();
        contexts.put(sourceName, sourceContext);
        contexts.put(targetName, targetContext);
        return contexts;
    }

    public static <T> InboundWire<T> createTargetWire(String serviceName, Class<T> interfaze) {
        return createServiceWire(serviceName, interfaze, null, null, null);
    }


    public static <T> InboundWire<T> createServiceWire(String serviceName, Class<T> interfaze,
                                                       Interceptor headInterceptor,
                                                       MessageHandler headRequestHandler,
                                                       MessageHandler headResponseHandler) {
        InboundWire<T> wire = new InboundWireImpl<T>();
        wire.setBusinessInterface(interfaze);
        wire.setServiceName(serviceName);
        wire.addInvocationChains(createServiceInvocationChains(interfaze, headInterceptor, headRequestHandler, headResponseHandler));
        return wire;
    }

    public static <T> OutboundWire<T> createReferenceWire(String refName, Class<T> interfaze,
                                                          Interceptor headInterceptor,
                                                          MessageHandler headRequestHandler,
                                                          MessageHandler headResponseHandler) {

        OutboundWire<T> wire = new OutboundWireImpl<T>();
        wire.setReferenceName(refName);
        wire.addInvocationChains(createReferenceInvocationChains(interfaze, headInterceptor, headRequestHandler, headResponseHandler));
        wire.setBusinessInterface(interfaze);
        return wire;
    }

    public static <T> OutboundWire<T> createReferenceWire(String refName, Class<T> interfaze) {
        OutboundWire<T> wire = new OutboundWireImpl<T>();
        wire.setReferenceName(refName);
        wire.addInvocationChains(createReferenceInvocationChains(interfaze));
        wire.setBusinessInterface(interfaze);
        return wire;
    }


    /**
     * @param outboundWire
     * @param inboundWire
     * @param targetContext
     * @param cacheable
     * @throws Exception
     */
    public static void connect(OutboundWire<?> outboundWire, InboundWire<?> inboundWire, JavaAtomicComponent targetContext, boolean cacheable) throws Exception {
        if (inboundWire != null) {
            // if null, the target side has no interceptors or handlers
            Map<Method, InboundInvocationChain> targetInvocationConfigs = inboundWire.getInvocationChains();
            for (OutboundInvocationChain outboundInvocationConfig : outboundWire.getInvocationChains().values())
            {
                // match wire chains
                InboundInvocationChain inboundInvocationConfig = targetInvocationConfigs.get(outboundInvocationConfig.getMethod());
                if (inboundInvocationConfig == null) {
                    BuilderConfigException e = new BuilderConfigException("Incompatible source and target interface types for reference");
                    e.setIdentifier(outboundWire.getReferenceName());
                    throw e;
                }
                // if handler is configured, add that
                if (inboundInvocationConfig.getRequestHandlers() != null) {
                    outboundInvocationConfig.setTargetRequestChannel(new MessageChannelImpl(inboundInvocationConfig
                            .getRequestHandlers()));
                    outboundInvocationConfig.setTargetResponseChannel(new MessageChannelImpl(inboundInvocationConfig
                            .getResponseHandlers()));
                } else {
                    // no handlers, just connect interceptors
                    if (inboundInvocationConfig.getHeadInterceptor() == null) {
                        BuilderConfigException e = new BuilderConfigException("No target handler or interceptor for operation");
                        e.setIdentifier(inboundInvocationConfig.getMethod().getName());
                        throw e;
                    }
                    if (!(outboundInvocationConfig.getTailInterceptor() instanceof InvokerInterceptor && inboundInvocationConfig
                            .getHeadInterceptor() instanceof InvokerInterceptor)) {
                        // check that we do not have the case where the only interceptors are invokers since we just need one
                        outboundInvocationConfig.setTargetInterceptor(inboundInvocationConfig.getHeadInterceptor());
                    }
                }
            }

            for (OutboundInvocationChain outboundInvocationConfig : outboundWire.getInvocationChains()
                    .values()) {
                //FIXME should use target method, not outboundInvocationConfig.getMethod()
                TargetInvoker invoker = new JavaTargetInvoker(outboundInvocationConfig.getMethod(), targetContext);
                invoker.setCacheable(cacheable);
                outboundInvocationConfig.setTargetInvoker(invoker);
            }
        }
    }

    private static Map<Method, OutboundInvocationChain> createReferenceInvocationChains(Class<?> interfaze) {
        return createReferenceInvocationChains(interfaze, null, null, null);
    }

    private static Map<Method, OutboundInvocationChain> createReferenceInvocationChains(Class<?> interfaze,
                                                                                        Interceptor headInterceptor, MessageHandler headRequestHandler,
                                                                                        MessageHandler headResponseHandler) {
        Map<Method, OutboundInvocationChain> invocations = new HashMap<Method, OutboundInvocationChain>();
        Method[] methods = interfaze.getMethods();
        for (Method method : methods) {
            OutboundInvocationChain chain = new OutboundInvocationChainImpl(method);
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

    private static Map<Method, InboundInvocationChain> createServiceInvocationChains(Class<?> interfaze,
                                                                                     Interceptor headInterceptor, MessageHandler headRequestHandler,
                                                                                     MessageHandler headResponseHandler) {
        Map<Method, InboundInvocationChain> invocations = new MethodHashMap<InboundInvocationChain>();
        Method[] methods = interfaze.getMethods();
        for (Method method : methods) {
            InboundInvocationChain chain = new InboundInvocationChainImpl(method);
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
