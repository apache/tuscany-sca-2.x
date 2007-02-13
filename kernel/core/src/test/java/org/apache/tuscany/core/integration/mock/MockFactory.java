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
package org.apache.tuscany.core.integration.mock;

import java.lang.reflect.Member;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.idl.InvalidServiceContractException;
import org.apache.tuscany.spi.idl.java.JavaInterfaceProcessorRegistry;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.WireService;

import org.apache.tuscany.core.builder.ConnectorImpl;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.idl.java.JavaInterfaceProcessorRegistryImpl;
import org.apache.tuscany.core.implementation.PojoConfiguration;
import org.apache.tuscany.core.implementation.java.JavaAtomicComponent;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.wire.InboundInvocationChainImpl;
import org.apache.tuscany.core.wire.InboundWireImpl;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.core.wire.OutboundInvocationChainImpl;
import org.apache.tuscany.core.wire.OutboundWireImpl;
import org.apache.tuscany.core.wire.jdk.JDKWireService;

/**
 * @version $$Rev$$ $$Date$$
 */
public final class MockFactory {

    private static final WireService WIRE_SERVICE = new JDKWireService(new WorkContextImpl(), null);
    private static final JavaInterfaceProcessorRegistry REGISTRY = new JavaInterfaceProcessorRegistryImpl();
    private static final ConnectorImpl CONNECTOR = new ConnectorImpl(null);

    private MockFactory() {
    }

    /**
     * Wires two components together where the reference interface is the same as target service
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
        return createWiredComponents(sourceName,
            sourceClass,
            targetService,
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

        JavaAtomicComponent targetComponent =
            createJavaComponent(targetName, targetScope, targetClass);
        String serviceName = targetService.getName().substring(targetService.getName().lastIndexOf('.') + 1);
        InboundWire inboundWire = createInboundWire(serviceName, targetService, targetHeadInterceptor);
        targetComponent.addInboundWire(inboundWire);
        PojoConfiguration configuration = new PojoConfiguration();
        configuration.setInstanceFactory(new PojoObjectFactory(sourceClass.getConstructor()));
        configuration.setWireService(WIRE_SERVICE);
        for (Map.Entry<String, Member> entry : members.entrySet()) {
            configuration.addReferenceSite(entry.getKey(), entry.getValue());
        }
        configuration.setWorkContext(new WorkContextImpl());
        configuration.setName(new URI(sourceName));
        JavaAtomicComponent sourceComponent = new JavaAtomicComponent(configuration);
        sourceComponent.setScopeContainer(sourceScope);
        OutboundWire outboundWire = createOutboundWire(targetName, sourceReferenceClass, sourceHeadInterceptor);
        sourceComponent.addOutboundWire(outboundWire);
        outboundWire.setTargetUri(URI.create(targetName + "#" + serviceName));
        targetScope.register(targetComponent);
        sourceScope.register(sourceComponent);
        CONNECTOR.connect(sourceComponent, outboundWire, targetComponent, inboundWire, false);
        Map<String, AtomicComponent> contexts = new HashMap<String, AtomicComponent>();
        contexts.put(sourceName, sourceComponent);
        contexts.put(targetName, targetComponent);
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
        JavaAtomicComponent targetComponent =
            createJavaComponent(targetName, targetScope, targetClass);
        String serviceName = targetService.getName().substring(targetService.getName().lastIndexOf('.') + 1);
        InboundWire inboundWire = createInboundWire(serviceName, targetService, null);
        targetComponent.addInboundWire(inboundWire);
        PojoConfiguration configuration = new PojoConfiguration();
        configuration.setInstanceFactory(new PojoObjectFactory(sourceClass.getConstructor()));
        configuration.setWireService(WIRE_SERVICE);
        for (Map.Entry<String, Member> entry : members.entrySet()) {
            configuration.addReferenceSite(entry.getKey(), entry.getValue());
        }
        configuration.setWorkContext(new WorkContextImpl());
        configuration.setName(new URI(sourceName));

        JavaAtomicComponent sourceComponent = new JavaAtomicComponent(configuration);
        sourceComponent.setScopeContainer(sourceScope);
        OutboundWire outboundWire = createOutboundWire(targetName, sourceReferenceClass, null);
        outboundWire.setTargetUri(URI.create(targetName + "#" + serviceName));
        List<OutboundWire> factories = new ArrayList<OutboundWire>();
        factories.add(outboundWire);
        sourceComponent.addOutboundWires(factories);
        targetScope.register(targetComponent);
        sourceScope.register(sourceComponent);
        CONNECTOR.connect(sourceComponent, outboundWire, targetComponent, inboundWire, false);
        Map<String, AtomicComponent> components = new HashMap<String, AtomicComponent>();
        components.put(sourceName, sourceComponent);
        components.put(targetName, targetComponent);
        return components;
    }

    public static <T> InboundWire createInboundWire(String serviceName, Class<T> interfaze)
        throws InvalidServiceContractException {
        return createInboundWire(serviceName, interfaze, null);
    }

    public static <T> InboundWire createInboundWire(String serviceName, Class<T> interfaze, Interceptor interceptor)
        throws InvalidServiceContractException {
        InboundWire wire = new InboundWireImpl();
        ServiceContract<?> contract = REGISTRY.introspect(interfaze);
        wire.setServiceContract(contract);
        wire.setUri(URI.create("#" + serviceName));
        wire.addInvocationChains(createInboundChains(interfaze, interceptor));
        return wire;
    }

    public static <T> OutboundWire createOutboundWire(String refName, Class<T> interfaze)
        throws InvalidServiceContractException {
        return createOutboundWire(refName, interfaze, null);
    }

    public static <T> OutboundWire createOutboundWire(String refName, Class<T> interfaze, Interceptor interceptor)
        throws InvalidServiceContractException {

        OutboundWire wire = new OutboundWireImpl();
        wire.setUri(URI.create("#" + refName));
        createOutboundChains(interfaze, interceptor, wire);
        ServiceContract<?> contract = REGISTRY.introspect(interfaze);
        wire.setServiceContract(contract);
        return wire;
    }


    @SuppressWarnings("unchecked")
    private static <T> JavaAtomicComponent createJavaComponent(String name, ScopeContainer scope, Class<T> clazz)
        throws NoSuchMethodException, URISyntaxException {
        PojoConfiguration configuration = new PojoConfiguration();
        configuration.setImplementationClass(clazz);
        configuration.setInstanceFactory(new PojoObjectFactory(clazz.getConstructor()));
        configuration.setWireService(WIRE_SERVICE);
        configuration.setWorkContext(new WorkContextImpl());
        configuration.setName(new URI(name));
        JavaAtomicComponent component = new JavaAtomicComponent(configuration);
        component.setScopeContainer(scope);
        return component;
    }

    private static void createOutboundChains(Class<?> interfaze, Interceptor interceptor, OutboundWire wire)
        throws InvalidServiceContractException {
        ServiceContract<?> contract = REGISTRY.introspect(interfaze);
        for (Operation<?> operation : contract.getOperations().values()) {
            OutboundInvocationChain chain = new OutboundInvocationChainImpl(operation);
            if (interceptor != null) {
                chain.addInterceptor(interceptor);
            }
            wire.addInvocationChain(operation, chain);
        }
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

}
