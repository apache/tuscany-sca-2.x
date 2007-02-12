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

import org.osoa.sca.annotations.EagerInit;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.builder.BindingBuilder;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.builder.BuilderInstantiationException;
import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.builder.ComponentBuilder;
import org.apache.tuscany.spi.builder.MissingWireTargetException;
import org.apache.tuscany.spi.builder.ScopeNotFoundException;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.RegistrationException;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.model.BindingDefinition;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.ComponentType;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.InteractionScope;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.model.ServiceDefinition;
import org.apache.tuscany.spi.wire.WireService;

import org.apache.tuscany.core.binding.local.LocalBindingDefinition;
import org.apache.tuscany.core.component.ComponentManager;
import org.apache.tuscany.core.implementation.composite.ReferenceImpl;
import org.apache.tuscany.core.implementation.composite.ServiceImpl;

/**
 * The default builder registry in the runtime
 *
 * @version $Rev$ $Date$
 */
@EagerInit
public class BuilderRegistryImpl implements BuilderRegistry {
    private WireService wireService;
    private ScopeRegistry scopeRegistry;
    private ComponentManager componentManager;

    private final Map<Class<? extends Implementation<?>>, ComponentBuilder<? extends Implementation<?>>>
    componentBuilders =
        new HashMap<Class<? extends Implementation<?>>, ComponentBuilder<? extends Implementation<?>>>();
    private final Map<Class<? extends BindingDefinition>, BindingBuilder<? extends BindingDefinition>> bindingBuilders =
        new HashMap<Class<? extends BindingDefinition>, BindingBuilder<? extends BindingDefinition>>();

    public BuilderRegistryImpl(@Autowire ScopeRegistry scopeRegistry,
                               @Autowire WireService wireService,
                               @Autowire ComponentManager componentManager) {
        this.scopeRegistry = scopeRegistry;
        this.wireService = wireService;
        this.componentManager = componentManager;
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
        // noinspection SuspiciousMethodCalls
        ComponentBuilder<I> componentBuilder = (ComponentBuilder<I>) componentBuilders.get(implClass);
        try {
            if (componentBuilder == null) {
                String name = implClass.getName();
                throw new NoRegisteredBuilderException("No builder registered for implementation", name);
            }

            Component component = componentBuilder.build(parent, componentDefinition, context);
            if (component != null) {
                component.setDefaultPropertyValues(componentDefinition.getPropertyValues());
                Scope scope = componentDefinition.getImplementation().getComponentType().getImplementationScope();
                if (scope == Scope.SYSTEM || scope == Scope.COMPOSITE) {
                    component.setScopeContainer(context.getCompositeScope());
                } else {
                    // Check for conversational contract if conversational scope
                    if (scope == Scope.CONVERSATION) {
                        boolean hasConversationalContract = false;
                        ComponentType<ServiceDefinition, ReferenceDefinition, ?> componentType =
                            componentDefinition.getImplementation().getComponentType();
                        Map<String, ServiceDefinition> services = componentType.getServices();
                        for (ServiceDefinition serviceDef : services.values()) {
                            InteractionScope intScope = serviceDef.getServiceContract().getInteractionScope();
                            if (intScope == InteractionScope.CONVERSATIONAL) {
                                hasConversationalContract = true;
                                break;
                            }
                        }
                        if (!hasConversationalContract) {
                            String name = implClass.getName();
                            throw new NoConversationalContractException(
                                "No conversational contract for conversational implementation", name);
                        }
                    }
                    // Now it's ok to set the scope container
                    ScopeContainer scopeContainer = scopeRegistry.getScopeContainer(scope);
                    if (scopeContainer == null) {
                        throw new ScopeNotFoundException(scope.toString());
                    }
                    component.setScopeContainer(scopeContainer);
                }
            }
            ComponentType<?, ?, ?> componentType = componentDefinition.getImplementation().getComponentType();
            assert componentType != null : "Component type must be set";
            // create wires for the component
            if (wireService != null && component instanceof AtomicComponent) {
                wireService.createWires((AtomicComponent) component, componentDefinition);
            }
            componentManager.register(component);
            return component;
        } catch (BuilderException e) {
            e.addContextName(componentDefinition.getUri().toString());
            throw e;
        } catch (RegistrationException e) {
            BuilderInstantiationException bie = new BuilderInstantiationException("Error registering component", e);
            bie.addContextName(componentDefinition.getUri().toString());
            throw bie;
        }
    }

    @SuppressWarnings({"unchecked"})
    public Service build(CompositeComponent parent,
                         ServiceDefinition serviceDefinition,
                         DeploymentContext deploymentContext) throws BuilderException {
        URI uri = serviceDefinition.getUri();
        ServiceContract<?> serviceContract = serviceDefinition.getServiceContract();
        if (serviceDefinition.getBindings().isEmpty()) {
            // if no bindings are configured, default to the local binding.
            // this should be changed to allow runtime selection
            if (serviceDefinition.getBindings().isEmpty()) {
                // TODO JFM implement capability for the runtime to choose a binding
                serviceDefinition.addBinding(new LocalBindingDefinition());
            }
        }
        URI targetUri = serviceDefinition.getTarget();
        Service service = new ServiceImpl(uri, serviceContract, targetUri);
        for (BindingDefinition definition : serviceDefinition.getBindings()) {
            Class<?> bindingClass = definition.getClass();
            // noinspection SuspiciousMethodCalls
            BindingBuilder bindingBuilder = bindingBuilders.get(bindingClass);
            if (bindingBuilder == null) {
                throw new NoRegisteredBuilderException("No builder registered for type", bindingClass.getName());
            }
            ServiceBinding binding =
                bindingBuilder.build(parent, serviceDefinition, definition, deploymentContext);
            if (wireService != null) {
                if (targetUri == null) {
                    throw new MissingWireTargetException("Service target uri not specified");
                }
                //String path = uri.getPath();
                ServiceContract<?> contract = serviceDefinition.getServiceContract();
                wireService.createWires(binding, contract, targetUri.toString());
            }
            service.addServiceBinding(binding);
        }
        return service;
    }

    @SuppressWarnings("unchecked")
    public Reference build(CompositeComponent parent,
                           ReferenceDefinition referenceDefinition,
                           DeploymentContext context) throws BuilderException {
        URI uri = referenceDefinition.getUri();
        ServiceContract<?> contract = referenceDefinition.getServiceContract();
        if (referenceDefinition.getBindings().isEmpty()) {
            // if no bindings are configured, default to the local binding.
            // this should be changed to allow runtime selection
            if (referenceDefinition.getBindings().isEmpty()) {
                // TODO JFM implement capability for the runtime to choose a binding
                referenceDefinition.addBinding(new LocalBindingDefinition());
            }
        }

        Reference reference = new ReferenceImpl(uri, contract);
        for (BindingDefinition bindingDefinition : referenceDefinition.getBindings()) {
            Class<?> bindingClass = bindingDefinition.getClass();
            // noinspection SuspiciousMethodCalls
            BindingBuilder bindingBuilder = bindingBuilders.get(bindingClass);
            ReferenceBinding binding = bindingBuilder.build(parent, referenceDefinition, bindingDefinition, context);
            // create wires for the component
            if (wireService != null) {
                URI targetUri = bindingDefinition.getTargetUri();
                // it is possible for a binding to not have a URI
                wireService.createWires(binding, contract, targetUri);

            }
            reference.addReferenceBinding(binding);

        }
        return reference;
    }

}
