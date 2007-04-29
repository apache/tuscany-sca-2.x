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
package org.apache.tuscany.spi.extension;

import org.apache.tuscany.assembly.Implementation;
import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.builder.ComponentBuilder;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.wire.ProxyService;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;

/**
 * An extension point for component builders. When adding support for new component types, implementations may extend
 * this class as a convenience.
 *
 * @version $$Rev$$ $$Date$$
 */
@EagerInit
public abstract class ComponentBuilderExtension<I extends Implementation> implements ComponentBuilder {
    protected BuilderRegistry builderRegistry;
    protected ProxyService proxyService;
    protected WorkContext workContext;

    @Reference
    public void setBuilderRegistry(BuilderRegistry registry) {
        this.builderRegistry = registry;
    }

    @Reference
    public void setProxyService(ProxyService proxyService) {
        this.proxyService = proxyService;
    }

    @Reference
    public void setWorkContext(WorkContext workContext) {
        this.workContext = workContext;
    }

    @Init
    public void init() {
        builderRegistry.register(getImplementationType(), this);
    }

    protected abstract Class<I> getImplementationType();
}
