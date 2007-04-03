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

package org.apache.tuscany.services.spi.contribution.extension;

import org.apache.tuscany.services.spi.contribution.ArtifactResolver;
import org.apache.tuscany.services.spi.contribution.ArtifactResolverRegistry;
import org.osoa.sca.annotations.Constructor;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

/**
 * The base class for ContributionProcessor implementations
 * 
 * @version $Rev$ $Date$
 */
@EagerInit
@Service(ArtifactResolver.class)
public abstract class ArtifactResolverExtension implements ArtifactResolver {
    /**
     * The ArtifactResolverRegistry that this resolver should register with; usually set by injection.
     */
    protected final ArtifactResolverRegistry registry;

    /**
     * @param registry the registry to set
     */
    @Constructor
    public ArtifactResolverExtension(@Reference ArtifactResolverRegistry registry) {
        this.registry = registry;
    }
    
    /**
     * Initialize the resolver. It registers itself to the registry by model type it supports. 
     */ 
    @Init
    public void start() {
        this.registry.registerResolver(this.getType(), this);
    }

    /**
     * Destroy the resolver. It unregisters itself from the registry. 
     */
    @Destroy
    public void stop() {
        registry.unregisterResolver(this.getType());
    }

    /**
     * Returns the model type that this resolver can handle.
     * 
     * @return the model type that this resolver can handle
     */
    public abstract Class<?> getType();

}
