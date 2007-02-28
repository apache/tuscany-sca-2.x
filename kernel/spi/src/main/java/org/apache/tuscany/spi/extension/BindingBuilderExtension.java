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

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;

import org.apache.tuscany.spi.builder.BindingBuilder;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.model.BindingDefinition;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ServiceDefinition;

/**
 * An extension point for binding builders. When adding support for new serviceBindings, implementations may extend this
 * class as a convenience.
 *
 * @version $$Rev$$ $$Date$$
 */
@EagerInit
public abstract class BindingBuilderExtension<B extends BindingDefinition> implements BindingBuilder<B> {
    protected BuilderRegistry builderRegistry;

    @Reference
    public void setBuilderRegistry(BuilderRegistry registry) {
        this.builderRegistry = registry;
    }

    @Init
    public void init() {
        builderRegistry.register(getBindingType(), this);
    }

    public ServiceBinding build(ServiceDefinition serviceDefinition, B bindingDefinition, DeploymentContext context)
        throws BuilderException {
        return null;
    }

    public ReferenceBinding build(ReferenceDefinition boundReferenceDefinition,
                                  B bindingDefinition,
                                  DeploymentContext context) throws BuilderException {
        return null;
    }

    protected abstract Class<B> getBindingType();
}
