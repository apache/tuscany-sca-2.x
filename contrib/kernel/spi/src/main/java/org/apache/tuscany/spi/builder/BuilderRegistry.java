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
package org.apache.tuscany.spi.builder;

import org.apache.tuscany.spi.model.BindingDefinition;
import org.apache.tuscany.spi.model.Implementation;

/**
 * Maintains a registry of builders in the runtime, dispatching to the appropriate one as an assembly model is processed
 * into runtime artifacts
 *
 * @version $Rev$ $Date$
 */
public interface BuilderRegistry extends Builder {

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
     * Register a binding builder for a binding type
     *
     * @param implClass the binding type
     * @param builder   the buinder to be registered
     */
    <B extends BindingDefinition> void register(Class<B> implClass, BindingBuilder<B> builder);

}
