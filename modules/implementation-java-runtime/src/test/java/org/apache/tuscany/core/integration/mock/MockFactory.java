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

import org.apache.tuscany.assembly.Contract;
import org.apache.tuscany.assembly.impl.DefaultAssemblyFactory;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.implementation.PojoConfiguration;
import org.apache.tuscany.core.implementation.java.JavaAtomicComponent;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.wire.InvocationChainImpl;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.core.wire.WireImpl;
import org.apache.tuscany.core.wire.jdk.JDKProxyService;
import org.apache.tuscany.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.interfacedef.java.introspection.JavaInterfaceProcessorRegistry;
import org.apache.tuscany.interfacedef.java.introspection.impl.JavaInterfaceProcessorRegistryImpl;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.ProxyService;
import org.apache.tuscany.spi.wire.Wire;

/**
 * @version $$Rev$$ $$Date$$
 */
public final class MockFactory {

    private static final ProxyService PROXY_SERVICE = new JDKProxyService(new WorkContextImpl());
    private static final JavaInterfaceProcessorRegistry REGISTRY = new JavaInterfaceProcessorRegistryImpl();

    private MockFactory() {
    }

    /**
     * Wires two components together where the reference interface is the same as target service
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
            targetClass,
            targetScope
        );

    }

    @SuppressWarnings("unchecked")
    public static Map<String, AtomicComponent> createWiredComponents(String sourceName, Class<?> sourceClass,
                                                                     Class<?> sourceReferenceClass,
                                                                     ScopeContainer sourceScope,
                                                                     Interceptor sourceHeadInterceptor,
                                                                     Map<String, Member> members,
                                                                     String targetName,
                                                                     Class<?> targetClass,
                                                                     ScopeContainer targetScope
    )
        throws Exception {

        JavaAtomicComponent targetComponent =
            createJavaComponent(targetName, targetScope, targetClass);
        PojoConfiguration configuration = new PojoConfiguration();
        configuration.setInstanceFactory(new PojoObjectFactory(sourceClass.getConstructor()));
        configuration.setProxyService(PROXY_SERVICE);
        for (Map.Entry<String, Member> entry : members.entrySet()) {
            configuration.addReferenceSite(entry.getKey(), entry.getValue());
        }
        configuration.setWorkContext(new WorkContextImpl());
        configuration.setName(new URI(sourceName));
        configuration.setGroupId(URI.create("composite"));
        JavaAtomicComponent sourceComponent = new JavaAtomicComponent(configuration);
        sourceComponent.setScopeContainer(sourceScope);
        Wire wire = createWire(targetName, sourceReferenceClass, sourceHeadInterceptor);
        for (InvocationChain chain : wire.getInvocationChains().values()) {
            chain.setTargetInvoker(targetComponent.createTargetInvoker(targetName, chain.getOperation(), false));
        }
        sourceComponent.attachWire(wire);
        targetScope.register(targetComponent, configuration.getGroupId());
        sourceScope.register(sourceComponent, configuration.getGroupId());
        Map<String, AtomicComponent> components = new HashMap<String, AtomicComponent>();
        components.put(sourceName, sourceComponent);
        components.put(targetName, targetComponent);
        return components;
    }


    /**
     * Wires two components using a multiplicity reference
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
        PojoConfiguration configuration = new PojoConfiguration();
        configuration.setInstanceFactory(new PojoObjectFactory(sourceClass.getConstructor()));
        configuration.setProxyService(PROXY_SERVICE);
        for (Map.Entry<String, Member> entry : members.entrySet()) {
            configuration.addReferenceSite(entry.getKey(), entry.getValue());
        }
        configuration.setWorkContext(new WorkContextImpl());
        configuration.setName(new URI(sourceName));
        configuration.setGroupId(URI.create("composite"));

        JavaAtomicComponent sourceComponent = new JavaAtomicComponent(configuration);
        sourceComponent.setScopeContainer(sourceScope);
        Wire wire = createWire(targetName, sourceReferenceClass, null);
        wire.setTargetUri(URI.create(targetName + "#" + serviceName));
        for (InvocationChain chain : wire.getInvocationChains().values()) {
            chain.setTargetInvoker(targetComponent.createTargetInvoker("target", chain.getOperation(), false));
        }
        List<Wire> wires = new ArrayList<Wire>();
        wires.add(wire);
        sourceComponent.attachWires(wires);
        targetScope.register(targetComponent, configuration.getGroupId());
        sourceScope.register(sourceComponent, configuration.getGroupId());

        Map<String, AtomicComponent> components = new HashMap<String, AtomicComponent>();
        components.put(sourceName, sourceComponent);
        components.put(targetName, targetComponent);
        return components;
    }

    public static <T> Wire createWire(String serviceName, Class<T> interfaze)
        throws InvalidInterfaceException {
        return createWire(serviceName, interfaze, null);
    }

    public static <T> Wire createWire(String serviceName, Class<T> interfaze, Interceptor interceptor)
        throws InvalidInterfaceException {
        Wire wire = new WireImpl();
        Contract contract = new DefaultAssemblyFactory().createComponentService();
        REGISTRY.introspect(contract, interfaze);
        wire.setSourceContract(contract);
        wire.setSourceUri(URI.create("#" + serviceName));
        createChains(interfaze, interceptor, wire);
        return wire;
    }

    @SuppressWarnings("unchecked")
    private static <T> JavaAtomicComponent createJavaComponent(String name, ScopeContainer scope, Class<T> clazz)
        throws NoSuchMethodException, URISyntaxException {
        PojoConfiguration configuration = new PojoConfiguration();
        configuration.setImplementationClass(clazz);
        configuration.setInstanceFactory(new PojoObjectFactory(clazz.getConstructor()));
        configuration.setProxyService(PROXY_SERVICE);
        configuration.setWorkContext(new WorkContextImpl());
        configuration.setName(new URI(name));
        configuration.setGroupId(URI.create("composite"));
        JavaAtomicComponent component = new JavaAtomicComponent(configuration);
        component.setScopeContainer(scope);
        return component;
    }

    private static void createChains(Class<?> interfaze, Interceptor interceptor, Wire wire)
        throws InvalidInterfaceException {

        Contract contract = new DefaultAssemblyFactory().createComponentService();
        REGISTRY.introspect(contract, interfaze);
        for (Operation method : contract.getInterface().getOperations()) {
            InvocationChain chain = new InvocationChainImpl(method);
            if (interceptor != null) {
                chain.addInterceptor(interceptor);
            }
            // add tail interceptor
            chain.addInterceptor(new InvokerInterceptor());
            wire.addInvocationChain(method, chain);
        }
    }

}
