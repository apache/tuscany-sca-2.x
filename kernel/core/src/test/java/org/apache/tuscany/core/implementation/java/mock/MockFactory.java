/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.apache.tuscany.core.implementation.java.mock;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.idl.InvalidServiceContractException;
import org.apache.tuscany.spi.idl.java.JavaIDLUtils;
import org.apache.tuscany.spi.idl.java.JavaInterfaceProcessorRegistry;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireService;

import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.idl.java.JavaInterfaceProcessorRegistryImpl;
import org.apache.tuscany.core.implementation.PojoConfiguration;
import org.apache.tuscany.core.implementation.java.JavaAtomicComponent;
import org.apache.tuscany.core.implementation.java.JavaTargetInvoker;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.wire.InboundInvocationChainImpl;
import org.apache.tuscany.core.wire.InboundWireImpl;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.core.wire.OutboundInvocationChainImpl;
import org.apache.tuscany.core.wire.OutboundWireImpl;
import org.apache.tuscany.core.wire.jdk.JDKWireService;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;

/**
 * @version $$Rev$$ $$Date$$
 */
public final class MockFactory {

    private static final WireService WIRE_SERVICE = new JDKWireService(new WorkContextImpl(), null);
    private static final JavaInterfaceProcessorRegistry REGISTRY = new JavaInterfaceProcessorRegistryImpl();

    private MockFactory() {
    }

    /**
     * Creates a JavaAtomicComponent which returns the given instance
     */
    @SuppressWarnings("unchecked")
    public static <T> JavaAtomicComponent createJavaComponent(T instance) {
        ScopeContainer scope = createMock(ScopeContainer.class);
        scope.getScope();
        expectLastCall().andReturn(Scope.MODULE);
        scope.getInstance(isA(JavaAtomicComponent.class));
        expectLastCall().andReturn(instance).anyTimes();
        replay(scope);
        PojoConfiguration configuration = new PojoConfiguration();
        configuration.setScopeContainer(scope);
        try {
            configuration.setInstanceFactory(new PojoObjectFactory(DummyImpl.class.getConstructor()));
        } catch (NoSuchMethodException e) {
            throw new AssertionError(e);
        }
        configuration.addServiceInterface(DummyImpl.class);
        configuration.setWireService(WIRE_SERVICE);
        configuration.setWorkContext(new WorkContextImpl());
        configuration.setName(instance.getClass().getName());

        return new JavaAtomicComponent(configuration);
    }

    @SuppressWarnings("unchecked")
    public static <T> JavaAtomicComponent createJavaComponent(String name, ScopeContainer scope, Class<T> clazz)
        throws NoSuchMethodException {
        PojoConfiguration configuration = new PojoConfiguration();
        configuration.setScopeContainer(scope);
        configuration.setInstanceFactory(new PojoObjectFactory(clazz.getConstructor()));
        configuration.addServiceInterface(clazz);
        configuration.setWireService(WIRE_SERVICE);
        configuration.setWorkContext(new WorkContextImpl());
        configuration.setName(name);
        return new JavaAtomicComponent(configuration);

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
    public static Map<String, AtomicComponent> createWiredComponents(String sourceName,
                                                                     Class<?> sourceClass,
                                                                     ScopeContainer sourceScope,
                                                                     Map<String, Member> members,
                                                                     String targetName,
                                                                     Class<?> targetService,
                                                                     Class<?> targetClass,
                                                                     ScopeContainer targetScope) throws Exception {
        return createWiredComponents(sourceName, sourceClass, targetService, sourceScope, members, targetName,
            targetService, targetClass, targetScope);

    }

    /**
     * Wires two contexts together where the reference interface may be different from the target service
     */
    public static Map<String, AtomicComponent> createWiredComponents(String sourceName, Class<?> sourceClass,
                                                                     Class<?> sourceReferenceClass,
                                                                     ScopeContainer sourceScope,
                                                                     Map<String, Member> members,
                                                                     String targetName,
                                                                     Class<?> targetService,
                                                                     Class<?> targetClass,
                                                                     ScopeContainer targetScope) throws Exception {
        return createWiredComponents(sourceName,
            sourceClass,
            sourceReferenceClass,
            sourceScope,
            null,
            members,
            targetName,
            targetService,
            targetClass,
            targetScope,
            null);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, AtomicComponent> createWiredComponents(String sourceName, Class<?> sourceClass,
                                                                     Class<?> sourceReferenceClass,
                                                                     ScopeContainer sourceScope,
                                                                     Interceptor sourceHeadInterceptor,
                                                                     Map<String, Member> members,
                                                                     String targetName, Class<?> targetService,
                                                                     Class<?> targetClass,
                                                                     ScopeContainer targetScope,
                                                                     Interceptor targetHeadInterceptor)
        throws Exception {

        JavaAtomicComponent targetContext =
            createJavaComponent(targetName, targetScope, targetClass);
        String serviceName = targetService.getName().substring(targetService.getName().lastIndexOf('.') + 1);
        InboundWire inboundWire = createServiceWire(serviceName, targetService, targetHeadInterceptor);
        targetContext.addInboundWire(inboundWire);

        PojoConfiguration configuration = new PojoConfiguration();
        configuration.setScopeContainer(sourceScope);
        configuration.setInstanceFactory(new PojoObjectFactory(sourceClass.getConstructor()));
        configuration.addServiceInterface(sourceClass);
        configuration.setWireService(WIRE_SERVICE);
        for (Map.Entry<String, Member> entry : members.entrySet()) {
            configuration.addReferenceSite(entry.getKey(), entry.getValue());
        }
        configuration.setWorkContext(new WorkContextImpl());
        configuration.setName(sourceName);
        JavaAtomicComponent sourceContext = new JavaAtomicComponent(configuration);
        OutboundWire outboundWire = createReferenceWire(targetName, sourceReferenceClass, sourceHeadInterceptor);
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
    @SuppressWarnings("unchecked")
    public static Map<String, AtomicComponent> createWiredMultiplicity(String sourceName, Class<?> sourceClass,
                                                                       Class<?> sourceReferenceClass,
                                                                       ScopeContainer sourceScope,
                                                                       String targetName, Class<?> targetService,
                                                                       Class<?> targetClass,
                                                                       Map<String, Member> members,
                                                                       ScopeContainer targetScope) throws Exception {
        JavaAtomicComponent targetContext =
            createJavaComponent(targetName, targetScope, targetClass);
        String serviceName = targetService.getName().substring(targetService.getName().lastIndexOf('.') + 1);
        InboundWire inboundWire = createServiceWire(serviceName, targetService, null);
        targetContext.addInboundWire(inboundWire);

        PojoConfiguration configuration = new PojoConfiguration();
        configuration.setScopeContainer(sourceScope);
        configuration.setInstanceFactory(new PojoObjectFactory(sourceClass.getConstructor()));
        configuration.addServiceInterface(sourceClass);
        configuration.setWireService(WIRE_SERVICE);
        for (Map.Entry<String, Member> entry : members.entrySet()) {
            configuration.addReferenceSite(entry.getKey(), entry.getValue());
        }
        configuration.setWorkContext(new WorkContextImpl());
        configuration.setName(sourceName);

        JavaAtomicComponent sourceContext = new JavaAtomicComponent(configuration);
        OutboundWire outboundWire = createReferenceWire(targetName, sourceReferenceClass, null);
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

    public static <T> InboundWire createTargetWire(String serviceName, Class<T> interfaze)
        throws InvalidServiceContractException {
        return createServiceWire(serviceName, interfaze, null);
    }


    public static <T> InboundWire createServiceWire(String serviceName, Class<T> interfaze, Interceptor interceptor)
        throws InvalidServiceContractException {
        InboundWire wire = new InboundWireImpl();
        ServiceContract<?> contract = REGISTRY.introspect(interfaze);
        wire.setServiceContract(contract);
        wire.setServiceName(serviceName);
        wire.addInvocationChains(
            createInboundChains(interfaze, interceptor));
        return wire;
    }

    public static <T> OutboundWire createReferenceWire(String refName, Class<T> interfaze, Interceptor interceptor)
        throws InvalidServiceContractException {

        OutboundWire wire = new OutboundWireImpl();
        wire.setReferenceName(refName);
        Map<Operation<?>, OutboundInvocationChain> outboundChains = createOutboundChains(interfaze, interceptor);
        wire.addInvocationChains(outboundChains);
        ServiceContract<?> contract = REGISTRY.introspect(interfaze);
        wire.setServiceContract(contract);
        return wire;
    }

    public static <T> OutboundWire createReferenceWire(String refName, Class<T> interfaze)
        throws InvalidServiceContractException {
        OutboundWire wire = new OutboundWireImpl();
        wire.setReferenceName(refName);
        wire.addInvocationChains(createOutboundChains(interfaze));
        ServiceContract<?> contract = REGISTRY.introspect(interfaze);
        wire.setServiceContract(contract);
        return wire;
    }


    /**
     * @param outboundWire
     * @param inboundWire
     * @param targetContext
     * @param cacheable
     * @throws Exception
     */
    public static void connect(OutboundWire outboundWire,
                               InboundWire inboundWire,
                               JavaAtomicComponent targetContext,
                               boolean cacheable) throws Exception {
        if (inboundWire != null) {
            // if null, the target side has no interceptors or handlers
            Map<Operation<?>, InboundInvocationChain> targetInvocationConfigs = inboundWire.getInvocationChains();
            for (OutboundInvocationChain outboundInvocationConfig : outboundWire.getInvocationChains().values()) {
                // match wire chains
                InboundInvocationChain inboundInvocationConfig =
                    targetInvocationConfigs.get(outboundInvocationConfig.getOperation());
                if (inboundInvocationConfig == null) {
                    BuilderConfigException e =
                        new BuilderConfigException("Incompatible source and target interface types for reference");
                    e.setIdentifier(outboundWire.getReferenceName());
                    throw e;
                }
                if (inboundInvocationConfig.getHeadInterceptor() == null) {
                    BuilderConfigException e =
                        new BuilderConfigException("No target handler or interceptor for operation");
                    e.setIdentifier(inboundInvocationConfig.getOperation().getName());
                    throw e;
                }
                if (!(outboundInvocationConfig.getTailInterceptor() instanceof InvokerInterceptor
                    && inboundInvocationConfig.getHeadInterceptor() instanceof InvokerInterceptor)) {
                    // check that we do not have the case where the only interceptors are invokers since we just
                    // need one
                    outboundInvocationConfig.setTargetInterceptor(inboundInvocationConfig.getHeadInterceptor());
                }
            }

            for (OutboundInvocationChain chain : outboundWire.getInvocationChains().values()) {
                //FIXME should use target method, not outboundInvocationConfig.getMethod()
                Method[] methods = outboundWire.getServiceContract().getInterfaceClass().getMethods();
                Method m = JavaIDLUtils.findMethod(chain.getOperation(), methods);
                TargetInvoker invoker = new JavaTargetInvoker(m, targetContext, null, new WorkContextImpl(), null);
                invoker.setCacheable(cacheable);
                chain.setTargetInvoker(invoker);
            }
        }
    }

    private static Map<Operation<?>, OutboundInvocationChain> createOutboundChains(Class<?> interfaze)
        throws InvalidServiceContractException {
        return createOutboundChains(interfaze, null);
    }

    private static Map<Operation<?>, OutboundInvocationChain> createOutboundChains(Class<?> interfaze,
                                                                                   Interceptor interceptor)
        throws InvalidServiceContractException {
        Map<Operation<?>, OutboundInvocationChain> invocations = new HashMap<Operation<?>, OutboundInvocationChain>();
        ServiceContract<?> contract = REGISTRY.introspect(interfaze);
        for (Operation<?> operation : contract.getOperations().values()) {
            OutboundInvocationChain chain = new OutboundInvocationChainImpl(operation);
            if (interceptor != null) {
                chain.addInterceptor(interceptor);
            }
            invocations.put(operation, chain);
        }
        return invocations;
    }

    private static Map<Operation<?>, InboundInvocationChain> createInboundChains(Class<?> interfaze,
                                                                                 Interceptor interceptor)
        throws InvalidServiceContractException {

        Map<Operation<?>, InboundInvocationChain> invocations = new HashMap<Operation<?>, InboundInvocationChain>();
        ServiceContract<?> contract = REGISTRY.introspect(interfaze);
        for (Operation<?> method : contract.getOperations().values()) {
            InboundInvocationChain chain = new InboundInvocationChainImpl(method);
            if (interceptor != null) {
                chain.addInterceptor(interceptor);
            }
            // add tail interceptor
            chain.addInterceptor(new InvokerInterceptor());
            invocations.put(method, chain);
        }
        return invocations;
    }

    private static class DummyImpl {
        public DummyImpl() {
        }
    }

}
