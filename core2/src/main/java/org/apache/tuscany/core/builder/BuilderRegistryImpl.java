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
import org.apache.tuscany.model.Implementation;
import org.apache.tuscany.spi.builder.BindingBuilder;
import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.builder.ComponentBuilder;
import org.apache.tuscany.spi.builder.WireBuilder;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.Context;
import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.spi.wire.TargetWire;

/**
 * @version $Rev$ $Date$
 */
public class BuilderRegistryImpl implements BuilderRegistry {
    private final Map<Class<? extends Implementation<?>>, ComponentBuilder<? extends Implementation<?>>> componentBuilders = new HashMap<Class<? extends Implementation<?>>, ComponentBuilder<? extends Implementation<?>>>();
    private final Map<Class<? extends Binding>, BindingBuilder<? extends Binding>> bindingBuilders = new HashMap<Class<? extends Binding>, BindingBuilder<? extends Binding>>();

    public <I extends Implementation<?>> void register(ComponentBuilder<I> builder) {
        Type[] interfaces = builder.getClass().getGenericInterfaces();
        for (Type type : interfaces) {
            if (! (type instanceof ParameterizedType)) {
                continue;
            }
            ParameterizedType interfaceType = (ParameterizedType) type;
            if (!ComponentBuilder.class.equals(interfaceType.getRawType())) {
                continue;
            }
            Class<I> implClass = (Class<I>) interfaceType.getActualTypeArguments()[0];
            register(implClass, builder);
            return;
        }
        throw new IllegalArgumentException("builder is not generified");
    }

    public <I extends Implementation<?>> void register(Class<I> implClass, ComponentBuilder<I> builder) {
        componentBuilders.put(implClass, builder);
    }

    public <I extends Implementation<?>> Context build(CompositeContext parent, Component<I> component) {
        Class<I> implClass = (Class<I>) component.getImplementation().getClass();
        ComponentBuilder<I> componentBuilder = (ComponentBuilder<I>) componentBuilders.get(implClass);
        return componentBuilder.build(parent, component);
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
