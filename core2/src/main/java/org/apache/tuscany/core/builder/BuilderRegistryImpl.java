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

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.core.util.JavaIntrospectionHelper;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.core.wire.MessageChannelImpl;
import org.apache.tuscany.model.Binding;
import org.apache.tuscany.model.BoundReference;
import org.apache.tuscany.model.BoundService;
import org.apache.tuscany.model.Component;
import org.apache.tuscany.model.ComponentType;
import org.apache.tuscany.model.Implementation;
import org.apache.tuscany.model.Reference;
import org.apache.tuscany.model.Scope;
import org.apache.tuscany.model.Service;
import org.apache.tuscany.spi.QualifiedName;
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
import org.apache.tuscany.spi.context.ReferenceContext;
import org.apache.tuscany.spi.context.ScopeContext;
import org.apache.tuscany.spi.context.ScopeRegistry;
import org.apache.tuscany.spi.context.ServiceContext;
import org.apache.tuscany.spi.wire.SourceInvocationChain;
import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.spi.wire.TargetInvocationChain;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.TargetWire;
import org.apache.tuscany.spi.wire.WireService;

/**
 * @version $Rev$ $Date$
 */
public class BuilderRegistryImpl implements BuilderRegistry {
    private final Map<Class<? extends Implementation<?>>, ComponentBuilder<? extends Implementation<?>>> componentBuilders = new HashMap<Class<? extends Implementation<?>>, ComponentBuilder<? extends Implementation<?>>>();
    private final Map<Class<? extends Binding>, BindingBuilder<? extends Binding>> bindingBuilders = new HashMap<Class<? extends Binding>, BindingBuilder<? extends Binding>>();
    private final Map<Class<? extends Context<?>>, WireBuilder<? extends Context<?>>> wireBuilders = new HashMap<Class<? extends Context<?>>, WireBuilder<? extends Context<?>>>();

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
        Class<I> implClass = JavaIntrospectionHelper.introspectGeneric(builder.getClass(), 0);
        if (implClass == null) {
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

    public <C extends Context<?>> void register(WireBuilder<C> builder) {
        Class<C> implClass = JavaIntrospectionHelper.introspectGeneric(builder.getClass(), 0);
        if (implClass == null) {
            throw new IllegalArgumentException("builder is not generified");
        }
        register(implClass, builder);
    }

    public <C extends Context<?>> void register(Class<C> implClass, WireBuilder<C> builder) {
        wireBuilders.put(implClass, builder);
    }

    public <T extends Class> void connect(Context<T> source, CompositeContext parent) {
        if (source instanceof ComponentContext) {
            ComponentContext<T> sourceContext = (ComponentContext<T>) source;
            for (SourceWire<?> sourceWire : sourceContext.getSourceWires()) {
                try {
                    connect(sourceWire, parent);
                } catch (BuilderConfigException e) {
                    e.addContextName(sourceContext.getName());
                    e.addContextName(parent.getName());
                    throw e;
                }
            }
        } else if (source instanceof ServiceContext) {
            ServiceContext<T> sourceContext = (ServiceContext<T>) source;
            SourceWire<T> sourceWire = sourceContext.getSourceWire();
            try {
                connect(sourceWire, parent);
            } catch (BuilderConfigException e) {
                e.addContextName(sourceContext.getName());
                e.addContextName(parent.getName());
                throw e;
            }
        } else {
            BuilderConfigException e = new BuilderConfigException("Invalid source context type");
            e.setIdentifier(source.getName());
            e.addContextName(parent.getName());
            throw e;
        }
    }

    private void connect(SourceWire sourceWire, CompositeContext parent) throws BuilderConfigException {
        QualifiedName targetName = sourceWire.getTargetName();
        Context<?> target = parent.getContext(targetName.getPartName());
        if (target == null) {
            BuilderConfigException e = new BuilderConfigException("Target not found for reference" + sourceWire.getReferenceName());
            e.setIdentifier(targetName.getQualifiedName());
            throw e;
        }
        TargetWire<?> targetWire;
        if (target instanceof ComponentContext) {
            targetWire = ((ComponentContext<?>) target).getTargetWires().get(targetName.getPortName());
            if (targetWire == null) {
                BuilderConfigException e = new BuilderConfigException("Target service not found for reference" + sourceWire.getReferenceName());
                e.setIdentifier(targetName.getPortName());
                throw e;
            }
            connect(sourceWire, targetWire, target);
        } else if (target instanceof ReferenceContext) {
            targetWire = ((ReferenceContext<?>) target).getTargetWire();
            assert(targetWire != null);
            connect(sourceWire, targetWire,target);
        } else {
            BuilderConfigException e = new BuilderConfigException("Invalid wire target type for reference " + sourceWire.getReferenceName());
            e.setIdentifier(targetName.getQualifiedName());
        }


    }

    private void connect(SourceWire<?> source, TargetWire<?> targetWire, Context<?> target) {
        // if null, the targetWire side has no interceptors or handlers
        Map<Method, TargetInvocationChain> targetInvocationConfigs = targetWire.getInvocationChains();
        for (SourceInvocationChain sourceInvocationConfig : source.getInvocationChains().values()) {
            // match wire chains
            TargetInvocationChain targetInvocationConfig = targetInvocationConfigs.get(sourceInvocationConfig.getMethod());
            if (targetInvocationConfig == null) {
                BuilderConfigException e = new BuilderConfigException("Incompatible source and targetWire interface types for reference");
                e.setIdentifier(source.getReferenceName());
                throw e;
            }
            // if handler is configured, add that
            if (targetInvocationConfig.getRequestHandlers() != null) {
                sourceInvocationConfig.setTargetRequestChannel(new MessageChannelImpl(targetInvocationConfig
                        .getRequestHandlers()));
                sourceInvocationConfig.setTargetResponseChannel(new MessageChannelImpl(targetInvocationConfig
                        .getResponseHandlers()));
            } else {
                // no handlers, just connect interceptors
                if (targetInvocationConfig.getHeadInterceptor() == null) {
                    BuilderConfigException e = new BuilderConfigException("No targetWire handler or interceptor for operation");
                    e.setIdentifier(targetInvocationConfig.getMethod().getName());
                    throw e;
                }
                if (!(sourceInvocationConfig.getTailInterceptor() instanceof InvokerInterceptor && targetInvocationConfig
                        .getHeadInterceptor() instanceof InvokerInterceptor)) {
                    // check that we do not have the case where the only interceptors are invokers since we just need one
                    sourceInvocationConfig.setTargetInterceptor(targetInvocationConfig.getHeadInterceptor());
                }
            }
        }

        for (SourceInvocationChain chain : source.getInvocationChains()
                .values()) {
            TargetInvoker invoker = target.createTargetInvoker(targetWire.getServiceName(), chain.getMethod());
            // TODO fix cacheable attrivute
            //invoker.setCacheable(cacheable);
            chain.setTargetInvoker(invoker);
        }

    }


    public void completeChain(Context<?> target) {

    }
}