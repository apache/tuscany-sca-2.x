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
package org.apache.tuscany.core.mock.factories;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;

import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.SystemAtomicComponent;
import org.apache.tuscany.spi.idl.InvalidServiceContractException;
import org.apache.tuscany.spi.idl.java.JavaInterfaceProcessorRegistry;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;

import org.apache.tuscany.core.idl.java.JavaInterfaceProcessorRegistryImpl;
import org.apache.tuscany.core.implementation.PojoConfiguration;
import org.apache.tuscany.core.implementation.system.component.SystemAtomicComponentImpl;
import org.apache.tuscany.core.implementation.system.wire.SystemInboundWireImpl;
import org.apache.tuscany.core.implementation.system.wire.SystemOutboundWire;
import org.apache.tuscany.core.implementation.system.wire.SystemOutboundWireImpl;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.wire.InboundInvocationChainImpl;
import org.apache.tuscany.core.wire.InboundWireImpl;
import org.apache.tuscany.core.wire.InvokerInterceptor;

/**
 * @version $$Rev$$ $$Date$$
 */
public final class MockFactory {

    private MockFactory() {
    }

    public static Map<String, AtomicComponent> createWiredComponents(String source,
                                                                     Class<?> sourceClass,
                                                                     ScopeContainer sourceScopeContainer,
                                                                     String target,
                                                                     Class<?> targetClass,
                                                                     ScopeContainer targetScopeContainer)
        throws NoSuchMethodException {
        List<Class<?>> sourceClasses = new ArrayList<Class<?>>();
        sourceClasses.add(sourceClass);
        return createWiredComponents(source, sourceClasses, sourceClass, sourceScopeContainer, target, targetClass,
            targetScopeContainer);
    }

    /**
     * Creates source and target {@link AtomicComponent}s whose instances are wired together. The wiring algorithm
     * searches for the first method on the source with a single parameter type matching an interface implemented by the
     * target.
     *
     * @throws NoSuchMethodException
     */
    @SuppressWarnings("unchecked")
    public static Map<String, AtomicComponent> createWiredComponents(String source,
                                                                     List<Class<?>> sourceInterfaces,
                                                                     Class<?> sourceClass,
                                                                     ScopeContainer sourceScopeContainer,
                                                                     String target,
                                                                     Class<?> targetClass,
                                                                     ScopeContainer targetScopeContainer)
        throws NoSuchMethodException {

        Map<String, AtomicComponent> contexts = new HashMap<String, AtomicComponent>();
        SystemAtomicComponent targetComponent = createAtomicComponent(target, targetScopeContainer, targetClass);
        PojoConfiguration sourceConfig = new PojoConfiguration();
        sourceConfig.getServiceInterfaces().addAll(sourceInterfaces);
        sourceConfig.setScopeContainer(sourceScopeContainer);
        sourceConfig.setInstanceFactory(new PojoObjectFactory(sourceClass.getConstructor()));

        //create target wire
        Method[] sourceMethods = sourceClass.getMethods();
        Class[] interfaces = targetClass.getInterfaces();
        Method setter = null;
        for (Class interfaze : interfaces) {
            for (Method method : sourceMethods) {
                if (method.getParameterTypes().length == 1) {
                    if (interfaze.isAssignableFrom(method.getParameterTypes()[0])) {
                        setter = method;
                    }
                }
                Init init;
                if ((init = method.getAnnotation(Init.class)) != null) {
                    sourceConfig.setInitLevel(init.eager() ? 50 : 0);
                    sourceConfig.setInitInvoker(new MethodEventInvoker<Object>(method));

                } else if (method.getAnnotation(Destroy.class) != null) {
                    sourceConfig.setDestroyInvoker(new MethodEventInvoker<Object>(method));
                }
            }

        }
        if (setter == null) {
            throw new IllegalArgumentException("No setter found on source for target");
        }

        sourceConfig.addReferenceSite(setter.getName(), setter);
        SystemAtomicComponent sourceCtx = new SystemAtomicComponentImpl(source, sourceConfig);
        QualifiedName targetName = new QualifiedName(target);
        SystemOutboundWire wire = new SystemOutboundWireImpl(setter.getName(), targetName, targetClass);
        InboundWire inboundWire = new SystemInboundWireImpl(targetName.getPortName(), targetClass, targetComponent);
        wire.setTargetWire(inboundWire);

        sourceCtx.addOutboundWire(wire);
        contexts.put(source, sourceCtx);
        contexts.put(target, targetComponent);
        return contexts;
    }

    @SuppressWarnings("unchecked")
    public static SystemAtomicComponent createAtomicComponent(String name, ScopeContainer container, Class<?> clazz)
        throws NoSuchMethodException {
        PojoConfiguration configuration = new PojoConfiguration();
        configuration.setScopeContainer(container);
        configuration.addServiceInterface(clazz);
        configuration.setInstanceFactory(new PojoObjectFactory(clazz.getConstructor()));
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            Init init;
            if ((init = method.getAnnotation(Init.class)) != null) {
                configuration.setInitLevel(init.eager() ? 50 : 0);
                configuration.setInitInvoker(new MethodEventInvoker<Object>(method));

            } else if (method.getAnnotation(Destroy.class) != null) {
                configuration.setDestroyInvoker(new MethodEventInvoker<Object>(method));
            }
        }
        return new SystemAtomicComponentImpl(name, configuration);
    }

    public static <T> InboundWire createTargetWireFactory(String serviceName, Class<T> interfaze)
        throws InvalidServiceContractException {
        InboundWire wire = new InboundWireImpl();
        wire.setServiceName(serviceName);
        JavaServiceContract contract = new JavaServiceContract(interfaze);
        wire.setServiceContract(contract);
        wire.addInvocationChains(createInboundChains(interfaze));
        return wire;
    }

    public static Map<Operation<?>, InboundInvocationChain> createInboundChains(Class<?> interfaze)
        throws InvalidServiceContractException {
        JavaInterfaceProcessorRegistry registry = new JavaInterfaceProcessorRegistryImpl();
        Map<Operation<?>, InboundInvocationChain> invocations = new HashMap<Operation<?>, InboundInvocationChain>();
        ServiceContract<?> contract = registry.introspect(interfaze);

        for (Operation<?> operation : contract.getOperations().values()) {
            InboundInvocationChain chain = new InboundInvocationChainImpl(operation);
            // add tail interceptor
            chain.addInterceptor(new InvokerInterceptor());
            invocations.put(operation, chain);
        }
        return invocations;
    }

}
