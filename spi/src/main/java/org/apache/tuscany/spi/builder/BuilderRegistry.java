/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
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
package org.apache.tuscany.spi.builder;

import org.apache.tuscany.spi.model.Binding;
import org.apache.tuscany.spi.model.Implementation;

/**
 * Maintains a registry of builders in the runtime, dispatching to the appropriate one as an assembly model is processed
 * into runtime artifacts
 *
 * @version $Rev$ $Date$
 */
public interface BuilderRegistry extends Builder {
    /**
     * Register a builder based on an implementation type specified in an annotation. The implementation type is
     * obtained by reflecting the generic definition.
     *
     * @param builder the builder to register
     */
    <I extends Implementation<?>> void register(ComponentBuilder<I> builder);

    /**
     * Register a builder for an implementation type.
     *
     * @param implClass the type of implementation that this builder can handle
     * @param builder   the builder to be registered
     */
    <I extends Implementation<?>> void register(Class<I> implClass, ComponentBuilder<I> builder);

    /**
     * Unregister a builder for an implementation type.
     *
     * @param implClass the implementation whose builder should be unregistered
     */
    <I extends Implementation<?>> void unregister(Class<I> implClass);

    /**
     * TODO: JavaDoc this once we know if we will be building contexts for bindings
     */
    <B extends Binding> void register(BindingBuilder<B> builder);

    /**
     * TODO: JavaDoc this once we know if we will be building contexts for bindings
     */
    <B extends Binding> void register(Class<B> implClass, BindingBuilder<B> builder);

}
