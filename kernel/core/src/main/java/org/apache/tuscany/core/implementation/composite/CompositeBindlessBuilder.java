/**
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
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
package org.apache.tuscany.core.implementation.composite;

import org.osoa.sca.annotations.Init;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.builder.BindlessBuilder;
import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.model.BindlessServiceDefinition;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.wire.WireService;

/**
 * The default implementation f a {@link BindlessBuilder} for a composite
 */
public class CompositeBindlessBuilder implements BindlessBuilder {

    protected BuilderRegistry builderRegistry;
    protected WireService wireService;
    protected WorkContext workContext;

    @Autowire
    public void setBuilderRegistry(BuilderRegistry registry) {
        this.builderRegistry = registry;
    }

    @Autowire
    public void setWireService(WireService wireService) {
        this.wireService = wireService;
    }

    @Autowire
    public void setWorkContext(WorkContext workContext) {
        this.workContext = workContext;
    }

    @Init(eager = true)
    public void init() {
        builderRegistry.register(this);
    }

    public Service build(CompositeComponent parent,
                         BindlessServiceDefinition definition,
                         DeploymentContext deploymentContext) {
        return new CompositeService(definition.getName(),
            definition.getServiceContract().getInterfaceClass(),
            parent,
            wireService,
            workContext);
    }

    public Reference build(CompositeComponent parent,
                           ReferenceDefinition definition,
                           DeploymentContext deploymentContext) {
        return new CompositeReference(definition.getName(),
            parent,
            wireService,
            definition.getServiceContract(),
            workContext);
    }
}
