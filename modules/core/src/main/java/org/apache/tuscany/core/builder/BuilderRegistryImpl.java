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

import org.apache.tuscany.assembly.Binding;
import org.apache.tuscany.assembly.ComponentType;
import org.apache.tuscany.assembly.CompositeReference;
import org.apache.tuscany.assembly.CompositeService;
import org.apache.tuscany.assembly.Implementation;
import org.apache.tuscany.assembly.Property;
import org.apache.tuscany.assembly.Service;
import org.apache.tuscany.core.binding.local.LocalBindingDefinition;
import org.apache.tuscany.core.implementation.composite.ReferenceImpl;
import org.apache.tuscany.core.implementation.composite.ServiceImpl;
import org.apache.tuscany.core.util.JavaIntrospectionHelper;
import org.apache.tuscany.spi.Scope;
import org.apache.tuscany.spi.builder.BindingBuilder;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.builder.ComponentBuilder;
import org.apache.tuscany.spi.builder.ScopeNotFoundException;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.osoa.sca.annotations.EagerInit;

/**
 * The default builder registry in the runtime
 * 
 * @version $Rev$ $Date$
 */
@EagerInit
public class BuilderRegistryImpl implements BuilderRegistry {
    private ScopeRegistry scopeRegistry;

    private final Map<Class<? extends Implementation>, ComponentBuilder> componentBuilders = new HashMap<Class<? extends Implementation>, ComponentBuilder>();
    private final Map<Class<? extends Binding>, BindingBuilder<? extends Binding>> bindingBuilders = new HashMap<Class<? extends Binding>, BindingBuilder<? extends Binding>>();

    public BuilderRegistryImpl(@org.osoa.sca.annotations.Reference
    ScopeRegistry scopeRegistry) {
        this.scopeRegistry = scopeRegistry;
    }

    public <I extends Implementation> void register(Class<I> implClass, ComponentBuilder builder) {
        componentBuilders.put(implClass, builder);
    }

    public <I extends Implementation> void unregister(Class<I> implClass) {
        componentBuilders.remove(implClass);
    }

    public <B extends Binding> void register(Class<B> implClass, BindingBuilder<B> builder) {
        bindingBuilders.put(implClass, builder);
    }
    
    // FIXME: Hack to get the registry working
    private <T extends Implementation> Class<T> getImplementationType(Class<?> implClass) {
        for(Class<?> interfaze: JavaIntrospectionHelper.getAllInterfaces(implClass)) {
            if(interfaze!=Implementation.class && Implementation.class.isAssignableFrom(interfaze)) {
                return (Class<T>) interfaze;
            }
        }
        return (Class<T>) implClass;
    }

    @SuppressWarnings("unchecked")
    public Component build(org.apache.tuscany.assembly.Component componentDef, DeploymentContext context)
        throws BuilderException {
        Class<?> implClass = getImplementationType(componentDef.getImplementation().getClass());
        // noinspection SuspiciousMethodCalls
        ComponentBuilder componentBuilder = componentBuilders.get(implClass);
        if (componentBuilder == null) {
            String name = implClass.getName();
            throw new NoRegisteredBuilderException("No builder registered for implementation", name);
        }
        Component component = componentBuilder.build(componentDef, context);
        assert component != null;
        Map<String, Property> properties = new HashMap<String, Property>();
        for (Property p : componentDef.getProperties()) {
            properties.put(p.getName(), p);
        }
        component.setDefaultPropertyValues(properties);
        
        // FIXME: How to deal scopes?
        // Scope scope = componentDef.getImplementation().getScope();
        Scope scope = Scope.STATELESS;

        if (scope == Scope.SYSTEM || scope == Scope.COMPOSITE) {
            component.setScopeContainer(context.getCompositeScope());
        } else {
            // Check for conversational contract if conversational scope
            if (scope == Scope.CONVERSATION) {
                boolean hasConversationalContract = false;
                ComponentType componentType = componentDef.getImplementation();
                for (Service serviceDef : componentType.getServices()) {
                    if (serviceDef.getInterface().isConversational()) {
                        hasConversationalContract = true;
                        break;
                    }
                }
                if (!hasConversationalContract) {
                    String name = implClass.getName();
                    throw new NoConversationalContractException(
                                                                "No conversational contract for conversational implementation",
                                                                name);
                }
            }
            // Now it's ok to set the scope container
            ScopeContainer scopeContainer = scopeRegistry.getScopeContainer(scope);
            if (scopeContainer == null) {
                throw new ScopeNotFoundException(scope.toString());
            }
            component.setScopeContainer(scopeContainer);
        }
        context.getComponents().put(component.getUri(), component);
        ComponentType componentType = componentDef.getImplementation();
        assert componentType != null : "Component type must be set";
        return component;
    }

    @SuppressWarnings( {"unchecked"})
    public org.apache.tuscany.spi.component.Service build(CompositeService serviceDefinition, DeploymentContext context)
        throws BuilderException {
        URI uri = URI.create("#" + serviceDefinition.getName());
        if (serviceDefinition.getBindings().isEmpty()) {
            // if no bindings are configured, default to the local binding.
            // this should be changed to allow runtime selection
            if (serviceDefinition.getBindings().isEmpty()) {
                // TODO JFM implement capability for the runtime to choose a
                // binding
                serviceDefinition.getBindings().add(new LocalBindingDefinition());
            }
        }
        // FIXME:
        URI targetUri = URI.create("#" + serviceDefinition.getPromotedService().getName());
        org.apache.tuscany.spi.component.Service service = new ServiceImpl(uri, serviceDefinition, targetUri);
        for (Binding definition : serviceDefinition.getBindings()) {
            Class<?> bindingClass = definition.getClass();
            // noinspection SuspiciousMethodCalls
            BindingBuilder bindingBuilder = bindingBuilders.get(bindingClass);
            if (bindingBuilder == null) {
                throw new NoRegisteredBuilderException("No builder registered for type", bindingClass.getName());
            }
            ServiceBinding binding = bindingBuilder.build(serviceDefinition, definition, context);
            service.addServiceBinding(binding);
        }
        return service;
    }

    @SuppressWarnings("unchecked")
    public Reference build(CompositeReference referenceDefinition, DeploymentContext context) throws BuilderException {
        URI uri = URI.create("#" + referenceDefinition.getName());
        if (referenceDefinition.getBindings().isEmpty()) {
            // if no bindings are configured, default to the local binding.
            // this should be changed to allow runtime selection
            if (referenceDefinition.getBindings().isEmpty()) {
                // TODO JFM implement capability for the runtime to choose a
                // binding
                referenceDefinition.getBindings().add(new LocalBindingDefinition());
            }
        }

        Reference reference = new ReferenceImpl(uri, referenceDefinition);
        for (Binding bindingDefinition : referenceDefinition.getBindings()) {
            Class<?> bindingClass = bindingDefinition.getClass();
            // noinspection SuspiciousMethodCalls
            BindingBuilder bindingBuilder = bindingBuilders.get(bindingClass);
            ReferenceBinding binding = bindingBuilder.build(referenceDefinition, bindingDefinition, context);
            reference.addReferenceBinding(binding);

        }
        return reference;
    }

}
