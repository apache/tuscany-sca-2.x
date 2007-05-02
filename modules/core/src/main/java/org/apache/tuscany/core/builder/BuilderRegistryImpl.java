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
import java.util.Hashtable;
import java.util.Map;

import org.apache.tuscany.assembly.Binding;
import org.apache.tuscany.assembly.ComponentReference;
import org.apache.tuscany.assembly.ComponentType;
import org.apache.tuscany.assembly.CompositeReference;
import org.apache.tuscany.assembly.CompositeService;
import org.apache.tuscany.assembly.Implementation;
import org.apache.tuscany.assembly.Property;
import org.apache.tuscany.assembly.SCABinding;
import org.apache.tuscany.assembly.Service;
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
import org.apache.tuscany.spi.component.ComponentManager;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.component.Scopeable;
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
    private ComponentManager componentManager;

    private final Map<Class<? extends Implementation>, ComponentBuilder> componentBuilders = new HashMap<Class<? extends Implementation>, ComponentBuilder>();
    private final Map<Class<? extends Binding>, BindingBuilder<? extends Binding>> bindingBuilders = new HashMap<Class<? extends Binding>, BindingBuilder<? extends Binding>>();

    public BuilderRegistryImpl(ComponentManager componentManager, ScopeRegistry scopeRegistry) {
        this.scopeRegistry = scopeRegistry;
        this.componentManager = componentManager;
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
        Class implementationInterface = Implementation.class;
        for (Class<?> interfaze : JavaIntrospectionHelper.getAllInterfaces(implClass)) {
            if (interfaze != implementationInterface && implementationInterface.isAssignableFrom(interfaze)) {
                implementationInterface = interfaze;
            }
        }
        if (implementationInterface != Implementation.class) {
            return implementationInterface;
        }
        return (Class<T>)implClass;
    }

    private <T extends Binding> Class<T> getBindingType(Class<?> bindingClass) {
        for (Class<?> interfaze : JavaIntrospectionHelper.getAllInterfaces(bindingClass)) {
            if (interfaze != Binding.class && Binding.class.isAssignableFrom(interfaze)) {
                return (Class<T>)interfaze;
            }
        }
        return (Class<T>)bindingClass;
    }

    @SuppressWarnings("unchecked")
    public Component build(org.apache.tuscany.assembly.Component componentDef, DeploymentContext context)
        throws BuilderException {
        Class<? extends Implementation> implClass = getImplementationType(componentDef.getImplementation().getClass());
        // noinspection SuspiciousMethodCalls
        ComponentBuilder componentBuilder = componentBuilders.get(implClass);
        if (componentBuilder == null) {
            String name = implClass.getName();
            throw new NoRegisteredBuilderException("No builder registered for implementation", name);
        }
        Component component = componentBuilder.build(componentDef, context);
        assert component != null;
        
        //if there are builders that have not handled properties, then ensure
        //it is copied into the component atleast at this point
        if (componentDef.getProperties().size() != component.getProperties().size()) {
            Map<String, Property> compProperties = new Hashtable<String, Property>();
            for (Property aProperty : componentDef.getProperties()) {
                compProperties.put(aProperty.getName(), aProperty);
            }
            component.setProperties(compProperties);
        }
        
        componentManager.add(component, componentDef);

        Scope scope = Scope.STATELESS;
        Implementation implementation = componentDef.getImplementation();
        if(implementation instanceof Scopeable) {
            scope = ((Scopeable) implementation).getScope();
        }

        if (scope == Scope.SYSTEM || scope == Scope.COMPOSITE) {
            component.setScopeContainer(context.getCompositeScope());
        } else {
            // Check for conversational contract if conversational scope
            if (scope == Scope.CONVERSATION) {
                boolean hasConversationalContract = false;
                ComponentType componentType = componentDef.getImplementation();
                for (Service serviceDef : componentType.getServices()) {
                    if (serviceDef.getInterfaceContract().getInterface().isConversational()) {
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
        if (serviceDefinition.getPromotedService() == null) {
            return null;
        }
        URI uri = URI.create(context.getComponentId() + "#" + serviceDefinition.getName());
        // FIXME:
        URI targetUri = URI.create("#" + serviceDefinition.getPromotedService().getName());
        org.apache.tuscany.spi.component.Service service = new ServiceImpl(uri, serviceDefinition, targetUri);
        for (Binding definition : serviceDefinition.getBindings()) {
            Class<?> bindingClass = definition.getClass();
            // noinspection SuspiciousMethodCalls
            BindingBuilder bindingBuilder = bindingBuilders.get(getBindingType(bindingClass));
            if (bindingBuilder == null) {
                throw new NoRegisteredBuilderException("No builder registered for type", bindingClass.getName());
            }
            ServiceBinding binding = bindingBuilder.build(serviceDefinition, definition, context);
            service.addServiceBinding(binding);
        }
        componentManager.add(service, serviceDefinition);
        return service;
    }

    @SuppressWarnings("unchecked")
    public Reference build(CompositeReference referenceDefinition, DeploymentContext context) throws BuilderException {
        if (referenceDefinition.getPromotedReferences().isEmpty()) {
            return null;
        }
        URI uri = URI.create(context.getComponentId() + "#" + referenceDefinition.getName());

        Reference reference = new ReferenceImpl(uri, referenceDefinition);
        for (Binding bindingDefinition : referenceDefinition.getBindings()) {
            Class<?> bindingClass = bindingDefinition.getClass();
            // noinspection SuspiciousMethodCalls
            BindingBuilder bindingBuilder = bindingBuilders.get(getBindingType(bindingClass));
            ReferenceBinding binding = bindingBuilder.build(referenceDefinition, bindingDefinition, context);
            reference.addReferenceBinding(binding);

        }
        componentManager.add(reference, referenceDefinition);
        return reference;
    }

}
