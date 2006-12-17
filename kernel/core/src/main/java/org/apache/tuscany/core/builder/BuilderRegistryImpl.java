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
package org.apache.tuscany.core.builder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Scope;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.builder.BindingBuilder;
import org.apache.tuscany.spi.builder.BindlessBuilder;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.builder.ComponentBuilder;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.SystemAtomicComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.model.Binding;
import org.apache.tuscany.spi.model.BindlessServiceDefinition;
import org.apache.tuscany.spi.model.BoundReferenceDefinition;
import org.apache.tuscany.spi.model.BoundServiceDefinition;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.ComponentType;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.WireService;

import org.apache.tuscany.core.implementation.system.component.SystemService;

/**
 * The default builder registry in the runtime
 *
 * @version $Rev$ $Date$
 */
@Scope("MODULE")
public class BuilderRegistryImpl implements BuilderRegistry {
    protected WireService wireService;
    protected ScopeRegistry scopeRegistry;

    private final Map<Class<? extends Implementation<?>>,
        ComponentBuilder<? extends Implementation<?>>> componentBuilders =
        new HashMap<Class<? extends Implementation<?>>, ComponentBuilder<? extends Implementation<?>>>();
    private final Map<Class<? extends Binding>,
        BindingBuilder<? extends Binding>> bindingBuilders =
        new HashMap<Class<? extends Binding>, BindingBuilder<? extends Binding>>();
    private BindlessBuilder bindlessBuilder;

    public BuilderRegistryImpl() {
    }

    public BuilderRegistryImpl(ScopeRegistry scopeRegistry) {
        this.scopeRegistry = scopeRegistry;
    }

    @Init(eager = true)
    public void init() {
    }

    @Autowire
    public void setScopeRegistry(ScopeRegistry scopeRegistry) {
        this.scopeRegistry = scopeRegistry;
    }

    @Autowire
    public void setWireService(WireService wireService) {
        this.wireService = wireService;
    }

    public <I extends Implementation<?>> void register(Class<I> implClass, ComponentBuilder<I> builder) {
        componentBuilders.put(implClass, builder);
    }

    public <I extends Implementation<?>> void unregister(Class<I> implClass) {
        componentBuilders.remove(implClass);
    }

    @SuppressWarnings("unchecked")
    public <B extends Binding> void register(BindingBuilder<B> builder) {
        Type[] interfaces = builder.getClass().getGenericInterfaces();
        for (Type type : interfaces) {
            if (!(type instanceof ParameterizedType)) {
                continue;
            }
            ParameterizedType interfaceType = (ParameterizedType) type;
            if (!BindingBuilder.class.equals(interfaceType.getRawType())) {
                continue;
            }
            Class<B> implClass = (Class<B>) interfaceType.getActualTypeArguments()[0];
            register(implClass, builder);
            return;
        }
        throw new IllegalArgumentException("builder is not generified");
    }

    public <B extends Binding> void register(Class<B> implClass, BindingBuilder<B> builder) {
        bindingBuilders.put(implClass, builder);
    }

    public void register(BindlessBuilder builder) {
        bindlessBuilder = builder;
    }

    @SuppressWarnings("unchecked")
    public <I extends Implementation<?>> Component build(CompositeComponent parent,
                                                         ComponentDefinition<I> componentDefinition,
                                                         DeploymentContext deploymentContext) throws BuilderException {
        Class<?> implClass = componentDefinition.getImplementation().getClass();
        ComponentBuilder<I> componentBuilder = (ComponentBuilder<I>) componentBuilders.get(implClass);
        try {
            if (componentBuilder == null) {
                String name = implClass.getName();
                throw new NoRegisteredBuilderException("No builder registered for implementation", name);
            }

            Component component = componentBuilder.build(parent, componentDefinition, deploymentContext);
            ComponentType<?, ?, ?> componentType = componentDefinition.getImplementation().getComponentType();
            assert componentType != null : "Component type must be set";
            // create wires for the component
            if (wireService != null && !(component instanceof SystemAtomicComponent)) {
                wireService.createWires(component, componentDefinition);
            }
            return component;
        } catch (BuilderException e) {
            e.addContextName(componentDefinition.getName());
//            e.addContextName(parent.getName());
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    public <B extends Binding> SCAObject build(CompositeComponent parent,
                                               BoundServiceDefinition<B> boundServiceDefinition,
                                               DeploymentContext deploymentContext) throws BuilderException {
        Class<?> bindingClass = boundServiceDefinition.getBinding().getClass();
        BindingBuilder<B> bindingBuilder = (BindingBuilder<B>) bindingBuilders.get(bindingClass);
        if (bindingBuilder == null) {
            throw new NoRegisteredBuilderException("No builder registered for type", bindingClass.getName());
        }
        SCAObject object = bindingBuilder.build(parent, boundServiceDefinition, deploymentContext);
        if (wireService != null && !(object instanceof SystemService)) {
            String path = boundServiceDefinition.getTarget().getPath();
            ServiceContract<?> contract = boundServiceDefinition.getServiceContract();
            wireService.createWires((Service) object, path, contract);
        }
        return object;
    }

    @SuppressWarnings("unchecked")
    public <B extends Binding> SCAObject build(CompositeComponent parent,
                                               BoundReferenceDefinition<B> boundReferenceDefinition,
                                               DeploymentContext deploymentContext) throws BuilderException {
        Class<B> bindingClass = (Class<B>) boundReferenceDefinition.getBinding().getClass();
        BindingBuilder<B> bindingBuilder = (BindingBuilder<B>) bindingBuilders.get(bindingClass);
        SCAObject object;
        object = bindingBuilder.build(parent, boundReferenceDefinition, deploymentContext);
        // create wires for the component
        if (wireService != null) {
            wireService.createWires((Reference) object, boundReferenceDefinition.getServiceContract());
        }
        return object;
    }

    public SCAObject build(CompositeComponent parent,
                           BindlessServiceDefinition serviceDefinition,
                           DeploymentContext deploymentContext) {
        SCAObject object = bindlessBuilder.build(parent, serviceDefinition, deploymentContext);
        if (wireService != null) {
            String path = serviceDefinition.getTarget().getPath();
            ServiceContract<?> contract = serviceDefinition.getServiceContract();
            wireService.createWires((Service) object, path, contract);
        }
        return object;
    }

    public SCAObject build(CompositeComponent parent,
                           ReferenceDefinition referenceDefinition,
                           DeploymentContext deploymentContext) {
        SCAObject object = bindlessBuilder.build(parent, referenceDefinition, deploymentContext);
        if (wireService != null) {
            wireService.createWires((Reference) object, referenceDefinition.getServiceContract());
        }
        return object;
    }

}
