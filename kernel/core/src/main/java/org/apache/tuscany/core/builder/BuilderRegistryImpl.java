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

import org.osoa.sca.annotations.Init;

import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.builder.BindingBuilder;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.builder.ComponentBuilder;
import org.apache.tuscany.spi.builder.MissingWireTargetException;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.ReferenceBinding;
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
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.WireService;

import org.apache.tuscany.core.implementation.composite.ReferenceImpl;
import org.apache.tuscany.core.implementation.composite.ServiceImpl;

/**
 * The default builder registry in the runtime
 *
 * @version $Rev$ $Date$
 */
public class BuilderRegistryImpl implements BuilderRegistry {
    protected WireService wireService;
    protected ScopeRegistry scopeRegistry;

    private final Map<Class<? extends Implementation<?>>,
        ComponentBuilder<? extends Implementation<?>>> componentBuilders =
        new HashMap<Class<? extends Implementation<?>>, ComponentBuilder<? extends Implementation<?>>>();
    private final Map<Class<? extends BindingDefinition>,
        BindingBuilder<? extends BindingDefinition>> bindingBuilders =
        new HashMap<Class<? extends BindingDefinition>, BindingBuilder<? extends BindingDefinition>>();

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

    public <B extends BindingDefinition> void register(Class<B> implClass, BindingBuilder<B> builder) {
        bindingBuilders.put(implClass, builder);
    }

    @SuppressWarnings("unchecked")
    public <I extends Implementation<?>> Component build(CompositeComponent parent,
                                                         ComponentDefinition<I> componentDefinition,
                                                         DeploymentContext context) throws BuilderException {
        Class<?> implClass = componentDefinition.getImplementation().getClass();
        //noinspection SuspiciousMethodCalls
        ComponentBuilder<I> componentBuilder = (ComponentBuilder<I>) componentBuilders.get(implClass);
        try {
            if (componentBuilder == null) {
                String name = implClass.getName();
                throw new NoRegisteredBuilderException("No builder registered for implementation", name);
            }

            Component component = componentBuilder.build(parent, componentDefinition, context);
            if (component != null) {
                component.setDefaultPropertyValues(componentDefinition.getPropertyValues());
            }
            ComponentType<?, ?, ?> componentType = componentDefinition.getImplementation().getComponentType();
            assert componentType != null : "Component type must be set";
            // create wires for the component
            if (wireService != null && component instanceof AtomicComponent) {
                wireService.createWires((AtomicComponent) component, componentDefinition);
            }
            return component;
        } catch (BuilderException e) {
            e.addContextName(componentDefinition.getName());
            throw e;
        }
    }

    @SuppressWarnings({"unchecked"})
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
            //noinspection SuspiciousMethodCalls
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

    @SuppressWarnings("unchecked")
    public Reference build(CompositeComponent parent,
                           BoundReferenceDefinition referenceDefinition,
                           DeploymentContext context) throws BuilderException {

        String name = referenceDefinition.getName();
        ServiceContract<?> contract = referenceDefinition.getServiceContract();
        Reference reference = new ReferenceImpl(name, parent, contract);
        for (BindingDefinition bindingDefinition : referenceDefinition.getBindings()) {
            Class<?> bindingClass = bindingDefinition.getClass();
            //noinspection SuspiciousMethodCalls
            BindingBuilder bindingBuilder = bindingBuilders.get(bindingClass);
            ReferenceBinding binding = bindingBuilder.build(parent, referenceDefinition, bindingDefinition, context);
            // create wires for the component
            if (wireService != null) {
                URI targetUri = bindingDefinition.getTargetUri();
                // it is possible for a binding to not have a URI
                QualifiedName targetName = null;
                if (targetUri != null) {
                    targetName = new QualifiedName(targetUri.getPath());
                }
                wireService.createWires(binding, contract, targetName);

            }
            reference.addReferenceBinding(binding);

        }
        return reference;
    }

}
