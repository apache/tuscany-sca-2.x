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
package org.apache.tuscany.spi.resolver;

import org.apache.tuscany.spi.model.ModelObject;

/**
 * Registry for resolvers that handle resolution of resources referenced by assembly model elements
 * <p/>
 * Resolvers are contributed by system extensions
 *
 * @version $Rev$ $Date$
 */
public interface ResolverRegistry {
    /**
     * Register a resolver for a model type.
     *
     * @param modelClass the type model element the resolver handles
     * @param resolver   the resolver to be registered
     */
    <T extends ModelObject> void register(Class<T> modelClass, Resolver<T> resolver);

    /**
     * Unregister a resolver for a model type.
     *
     * @param modelClass the model type whose builder should be unregistered
     */
    <T extends ModelObject> void unregister(Class<T> modelClass);

    /**
     * Initiates the resolution process
     *
     * @param object the top-level element to resolve
     */
    <T extends ModelObject> void resolve(T object) throws ResolutionException;
}
