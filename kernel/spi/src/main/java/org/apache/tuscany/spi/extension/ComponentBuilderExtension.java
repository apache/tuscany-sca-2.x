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

import org.osoa.sca.annotations.Init;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.builder.ComponentBuilder;
import org.apache.tuscany.spi.builder.Connector;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.policy.PolicyBuilderRegistry;
import org.apache.tuscany.spi.services.work.WorkScheduler;
import org.apache.tuscany.spi.wire.WireService;

import org.apache.tuscany.api.annotation.Monitor;

/**
 * An extension point for component builders. When adding support for new component types, implementations may extend
 * this class as a convenience.
 *
 * @version $$Rev$$ $$Date$$
 */
public abstract class ComponentBuilderExtension<I extends Implementation<?>> implements ComponentBuilder<I> {
    protected BuilderRegistry builderRegistry;
    protected ScopeRegistry scopeRegistry;
    protected WireService wireService;
    protected WorkScheduler workScheduler;
    protected WorkContext workContext;
    protected PolicyBuilderRegistry policyBuilderRegistry;
    protected Connector connector;
    protected ExecutionMonitor monitor;

    @Autowire
    public void setBuilderRegistry(BuilderRegistry registry) {
        this.builderRegistry = registry;
    }

    @Autowire
    public void setScopeRegistry(ScopeRegistry scopeRegistry) {
        this.scopeRegistry = scopeRegistry;
    }

    @Autowire
    public void setWireService(WireService wireService) {
        this.wireService = wireService;
    }

    @Autowire
    public void setWorkScheduler(WorkScheduler workScheduler) {
        this.workScheduler = workScheduler;
    }

    @Autowire
    public void setWorkContext(WorkContext workContext) {
        this.workContext = workContext;
    }

    @Autowire
    public void setPolicyBuilderRegistry(PolicyBuilderRegistry registry) {
        policyBuilderRegistry = registry;
    }

    @Autowire
    public void setConnector(Connector connector) {
        this.connector = connector;
    }

    @Monitor
    public void setMonitor(ExecutionMonitor monitor) {
        this.monitor = monitor;
    }

    @Init(eager = true)
    public void init() {
        builderRegistry.register(getImplementationType(), this);
    }

    protected abstract Class<I> getImplementationType();
}
