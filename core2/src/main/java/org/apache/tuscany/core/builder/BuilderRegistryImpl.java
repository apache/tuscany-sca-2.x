/**
 *
 * Copyright 2006 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.builder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.model.Binding;
import org.apache.tuscany.model.BoundReference;
import org.apache.tuscany.model.BoundService;
import org.apache.tuscany.model.Component;
import org.apache.tuscany.model.ComponentType;
import org.apache.tuscany.model.Implementation;
import org.apache.tuscany.model.Reference;
import org.apache.tuscany.model.Scope;
import org.apache.tuscany.model.Service;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.builder.BindingBuilder;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.builder.ComponentBuilder;
import org.apache.tuscany.spi.builder.WireBuilder;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.ComponentContext;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.Context;
import org.apache.tuscany.spi.context.ScopeContext;
import org.apache.tuscany.spi.context.ScopeRegistry;
import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.spi.wire.TargetWire;
import org.apache.tuscany.spi.wire.WireService;
import org.apache.tuscany.core.util.JavaIntrospectionHelper;

/**
 * @version $Rev$ $Date$
 */
public class BuilderRegistryImpl implements BuilderRegistry {
    private final Map<Class<? extends Implementation<?>>, ComponentBuilder<? extends Implementation<?>>> componentBuilders = new HashMap<Class<? extends Implementation<?>>, ComponentBuilder<? extends Implementation<?>>>();
    private final Map<Class<? extends Binding>, BindingBuilder<? extends Binding>> bindingBuilders = new HashMap<Class<? extends Binding>, BindingBuilder<? extends Binding>>();

    protected WireService wireService;
    protected ScopeRegistry scopeRegistry;

    public BuilderRegistryImpl() {
    }

    public BuilderRegistryImpl(WireService wireService, ScopeRegistry scopeRegistry) {
        this.wireService = wireService;
        this.scopeRegistry = scopeRegistry;
    }

    @Autowire
    public void setWireService(WireService wireService) {
        this.wireService = wireService;
    }

    @Autowire
    public void setScopeRegistry(ScopeRegistry scopeRegistry) {
        this.scopeRegistry = scopeRegistry;
    }

    public <I extends Implementation<?>> void register(ComponentBuilder<I> builder) {
       Class<I> implClass = JavaIntrospectionHelper.introspectGeneric(builder.getClass(),0);
       if (implClass == null){
          throw new IllegalArgumentException("builder is not generified");
       }
       register(implClass, builder);
    }

    public <I extends Implementation<?>> void register(Class<I> implClass, ComponentBuilder<I> builder) {
        componentBuilders.put(implClass, builder);
    }

    public <I extends Implementation<?>> Context build(CompositeContext parent, Component<I> component) {
        Class<I> implClass = (Class<I>) component.getImplementation().getClass();
        ComponentBuilder<I> componentBuilder = (ComponentBuilder<I>) componentBuilders.get(implClass);

        ComponentContext context = componentBuilder.build(parent, component);
        ComponentType componentType = component.getImplementation().getComponentType();
        assert(componentType != null): "Component type must be set";
        // create target wires
        for (Service service : componentType.getServices().values()) {
            TargetWire wire = wireService.createTargetWire(service);
            context.addTargetWire(wire);
        }
        // create source wires
        for (Reference reference : componentType.getReferences().values()) {
            SourceWire wire = wireService.createSourceWire(reference);
            context.addSourceWire(wire);
        }
        if (context instanceof AtomicContext) {
            AtomicContext ctx = (AtomicContext) context;
            Scope scope = ctx.getScope();
            if (scope == null) {
                scope = Scope.STATELESS;
            }
            ScopeContext scopeContext = scopeRegistry.getScopeContext(scope);
            if (scopeContext == null) {
                throw new BuilderConfigException("Scope context not registered for scope " + scope);
            }
            ctx.setScopeContext(scopeContext);
            scopeContext.register(ctx);
        }
        return context;
    }

    public <B extends Binding> void register(BindingBuilder<B> builder) {
        Type[] interfaces = builder.getClass().getGenericInterfaces();
        for (Type type : interfaces) {
            if (! (type instanceof ParameterizedType)) {
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

    public <B extends Binding> Context build(CompositeContext parent, BoundService<B> boundService) {
        Class<B> bindingClass = (Class<B>) boundService.getBinding().getClass();
        BindingBuilder<B> bindingBuilder = (BindingBuilder<B>) bindingBuilders.get(bindingClass);
        return bindingBuilder.build(parent, boundService);
    }

    public <B extends Binding> Context build(CompositeContext parent, BoundReference<B> boundReference) {
        Class<B> bindingClass = (Class<B>) boundReference.getBinding().getClass();
        BindingBuilder<B> bindingBuilder = (BindingBuilder<B>) bindingBuilders.get(bindingClass);
        return bindingBuilder.build(parent, boundReference);
    }

    public void register(WireBuilder builder) {
        throw new UnsupportedOperationException();
    }

    public void connect(SourceWire<?> source, TargetWire<?> target) {
        throw new UnsupportedOperationException();
    }

    public void completeChain(TargetWire<?> target) {
        throw new UnsupportedOperationException();
    }
}
