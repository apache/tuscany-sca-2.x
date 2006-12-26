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
package org.apache.tuscany.core.component.scope;

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
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;

import org.apache.tuscany.core.implementation.PojoConfiguration;
import org.apache.tuscany.core.implementation.system.component.SystemAtomicComponentImpl;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.wire.InboundWireImpl;
import org.apache.tuscany.core.wire.OutboundWireImpl;

/**
 * @version $$Rev$$ $$Date$$
 */
public final class MockFactory {

    private MockFactory() {
    }

    @SuppressWarnings("unchecked")
    public static Map<String, AtomicComponent> createWiredComponents(String source,
                                                                     Class<?> sourceClass,
                                                                     ScopeContainer sourceScopeContainer,
                                                                     String target,
                                                                     Class<?> targetClass,
                                                                     ScopeContainer targetScopeContainer)
        throws NoSuchMethodException {
        List<Class<?>> sourceInterfaces = new ArrayList<Class<?>>();
        sourceInterfaces.add(sourceClass);
        Map<String, AtomicComponent> components = new HashMap<String, AtomicComponent>();
        AtomicComponent targetComponent = createAtomicComponent(target, targetScopeContainer, targetClass);
        PojoConfiguration sourceConfig = new PojoConfiguration();
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
        sourceConfig.setName(source);
        AtomicComponent sourceComponent = new SystemAtomicComponentImpl(sourceConfig);
        QualifiedName targetName = new QualifiedName(target);
        OutboundWire wire = new OutboundWireImpl();
        wire.setReferenceName(setter.getName());
        wire.setServiceContract(new JavaServiceContract(targetClass));
        InboundWire inboundWire = new InboundWireImpl();
        inboundWire.setContainer(targetComponent);
        inboundWire.setServiceContract(new JavaServiceContract(targetClass));
        inboundWire.setServiceName(targetName.getPortName());
        wire.setTargetWire(inboundWire);
        sourceComponent.addOutboundWire(wire);
        components.put(source, sourceComponent);
        components.put(target, targetComponent);
        return components;
    }

    @SuppressWarnings("unchecked")
    public static AtomicComponent createAtomicComponent(String name, ScopeContainer container, Class<?> clazz)
        throws NoSuchMethodException {
        PojoConfiguration configuration = new PojoConfiguration();
        configuration.setScopeContainer(container);
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
        configuration.setName(name);
        return new SystemAtomicComponentImpl(configuration);
    }

}
