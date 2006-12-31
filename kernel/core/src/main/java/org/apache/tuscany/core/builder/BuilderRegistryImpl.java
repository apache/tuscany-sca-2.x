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

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.core.implementation.composite.ServiceImpl;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.builder.BindingBuilder;
import org.apache.tuscany.spi.builder.BindlessBuilder;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.builder.ComponentBuilder;
import org.apache.tuscany.spi.builder.MissingWireTargetException;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.model.BindingDefinition;
import org.apache.tuscany.spi.model.BoundReferenceDefinition;
import org.apache.tuscany.spi.model.BoundServiceDefinition;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.ComponentType;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.WireService;
import org.osoa.sca.annotations.Init;

/**
 * The default builder registry in the runtime
 *
 * @version $Rev$ $Date$
 */
public class BuilderRegistryImpl implements BuilderRegistry {
    
    /**
     * Wire service used by the builder.
     */
    protected WireService wireService;
    
    /**
     * Scope registry used by the builder.
     */
    protected ScopeRegistry scopeRegistry;

    /**
     * Map of component builders.
     */
    private final Map<Class<? extends Implementation<?>>, ComponentBuilder<? extends Implementation<?>>> componentBuilders =
        new HashMap<Class<? extends Implementation<?>>, ComponentBuilder<? extends Implementation<?>>>();
    
    /**
     * Map of binding builders.
     */
    private final Map<Class<? extends BindingDefinition>, BindingBuilder<? extends BindingDefinition>> bindingBuilders =
        new HashMap<Class<? extends BindingDefinition>, BindingBuilder<? extends BindingDefinition>>();
    
    /**
     * Bindless builder.
     */
    private BindlessBuilder bindlessBuilder;

    /**
     * Default constructor.
     *
     */
    public BuilderRegistryImpl() {
    }

    /**
     * Initializes the scope registry.
     * 
     * @param scopeRegistry Scope registry to use.
     */
    public BuilderRegistryImpl(ScopeRegistry scopeRegistry) {
        this.scopeRegistry = scopeRegistry;
    }

    /**
     * Initiakization method.
     *
     */
    @Init(eager = true)
    public void init() {
    }

    /**
     * Method for auto-wiring scope registry.
     * @param scopeRegistry Scope registry to use.
     */
    @Autowire
    public void setScopeRegistry(ScopeRegistry scopeRegistry) {
        this.scopeRegistry = scopeRegistry;
    }

    /**
     * Method for auto-wiring wire service.
     * @param scopeRegistry Wire service to use.
     */
    @Autowire
    public void setWireService(WireService wireService) {
        this.wireService = wireService;
    }

    /**
     * @see org.apache.tuscany.spi.builder.BuilderRegistry#register(java.lang.Class, org.apache.tuscany.spi.builder.ComponentBuilder)
     */
    public <I extends Implementation<?>> void register(Class<I> implClass, ComponentBuilder<I> builder) {
        componentBuilders.put(implClass, builder);
    }

    /**
     * @see org.apache.tuscany.spi.builder.BuilderRegistry#unregister(java.lang.Class)
     */
    public <I extends Implementation<?>> void unregister(Class<I> implClass) {
        componentBuilders.remove(implClass);
    }

    /**
     * @see org.apache.tuscany.spi.builder.BuilderRegistry#register(java.lang.Class, org.apache.tuscany.spi.builder.BindingBuilder)
     */
    public <B extends BindingDefinition> void register(Class<B> implClass, BindingBuilder<B> builder) {
        bindingBuilders.put(implClass, builder);
    }

    /**
     * @see org.apache.tuscany.spi.builder.BuilderRegistry#register(org.apache.tuscany.spi.builder.BindlessBuilder)
     */
    public void register(BindlessBuilder builder) {
        bindlessBuilder = builder;
    }

    /**
     * @see org.apache.tuscany.spi.builder.Builder#build(org.apache.tuscany.spi.component.CompositeComponent, org.apache.tuscany.spi.model.ComponentDefinition, org.apache.tuscany.spi.deployer.DeploymentContext)
     */
    @SuppressWarnings("unchecked")
    public <I extends Implementation<?>> Component build(CompositeComponent parent,
                                                         ComponentDefinition<I> componentDefinition,
                                                         DeploymentContext deploymentContext) throws BuilderException {
        Class<?> implClass = componentDefinition.getImplementation().getClass();
        ComponentBuilder<I> componentBuilder = (ComponentBuilder<I>)componentBuilders.get(implClass);
        try {
            if (componentBuilder == null) {
                String name = implClass.getName();
                throw new NoRegisteredBuilderException("No builder registered for implementation", name);
            }

            Component component = componentBuilder.build(parent, componentDefinition, deploymentContext);
            if (component != null) {
                component.setComponentDefinition(componentDefinition);
            }

            ComponentType<?, ?, ?> componentType = componentDefinition.getImplementation().getComponentType();
            assert componentType != null : "Component type must be set";
            // create wires for the component
            if (wireService != null) {
                wireService.createWires(component, componentDefinition);
            }
            return component;
        } catch (BuilderException e) {
            e.addContextName(componentDefinition.getName());
            throw e;
        }
    }

    /**
     * @see org.apache.tuscany.spi.builder.Builder#build(org.apache.tuscany.spi.component.CompositeComponent, org.apache.tuscany.spi.model.BoundServiceDefinition, org.apache.tuscany.spi.deployer.DeploymentContext)
     */
    @SuppressWarnings( {"unchecked"})
    public Service build(CompositeComponent parent,
                         BoundServiceDefinition boundServiceDefinition,
                         DeploymentContext deploymentContext) throws BuilderException {
        String name = boundServiceDefinition.getName();
        ServiceContract<?> serviceContract = boundServiceDefinition.getServiceContract();
        boolean system = parent.isSystem();
        URI targetUri = boundServiceDefinition.getTarget();
        Service service = new ServiceImpl(name, parent, serviceContract, targetUri, system);
        for (BindingDefinition definition : boundServiceDefinition.getBindings()) {
            Class<?> bindingClass = definition.getClass();
            BindingBuilder bindingBuilder = bindingBuilders.get(bindingClass);
            if (bindingBuilder == null) {
                throw new NoRegisteredBuilderException("No builder registered for type", bindingClass.getName());
            }
            ServiceBinding binding =
                bindingBuilder.build(parent, boundServiceDefinition, definition, deploymentContext);
            if (wireService != null) {
                URI uri = boundServiceDefinition.getTarget();
                if (uri == null) {
                    throw new MissingWireTargetException("Service uri not specified");
                }
                String path = uri.getPath();
                ServiceContract<?> contract = boundServiceDefinition.getServiceContract();
                wireService.createWires(binding, path, contract);
            }
            service.addServiceBinding(binding);
        }
        return service;
    }

    /**
     * @see org.apache.tuscany.spi.builder.Builder#build(org.apache.tuscany.spi.component.CompositeComponent, org.apache.tuscany.spi.model.BoundReferenceDefinition, org.apache.tuscany.spi.deployer.DeploymentContext)
     */
    @SuppressWarnings("unchecked")
    public <B extends BindingDefinition> SCAObject build(CompositeComponent parent,
                                                         BoundReferenceDefinition<B> boundReferenceDefinition,
                                                         DeploymentContext deploymentContext) throws BuilderException {
        Class<B> bindingClass = (Class<B>)boundReferenceDefinition.getBinding().getClass();
        BindingBuilder<B> bindingBuilder = (BindingBuilder<B>)bindingBuilders.get(bindingClass);
        SCAObject object;
        object = bindingBuilder.build(parent, boundReferenceDefinition, deploymentContext);
        // create wires for the component
        if (wireService != null) {
            wireService.createWires((Reference)object, boundReferenceDefinition.getServiceContract());
        }
        return object;
    }

    /**
     * @see org.apache.tuscany.spi.builder.Builder#build(org.apache.tuscany.spi.component.CompositeComponent, org.apache.tuscany.spi.model.ReferenceDefinition, org.apache.tuscany.spi.deployer.DeploymentContext)
     */
    public SCAObject build(CompositeComponent parent,
                           ReferenceDefinition referenceDefinition,
                           DeploymentContext deploymentContext) {
        SCAObject object = bindlessBuilder.build(parent, referenceDefinition, deploymentContext);
        if (wireService != null) {
            wireService.createWires((Reference)object, referenceDefinition.getServiceContract());
        }
        return object;
    }

}
