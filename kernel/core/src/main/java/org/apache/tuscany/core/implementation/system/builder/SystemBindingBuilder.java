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
package org.apache.tuscany.core.implementation.system.builder;

import java.net.URI;

import org.apache.tuscany.spi.builder.BindingBuilder;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.builder.MissingWireTargetException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.BindingBuilderExtension;
import org.apache.tuscany.spi.model.BoundReferenceDefinition;
import org.apache.tuscany.spi.model.BoundServiceDefinition;
import org.apache.tuscany.spi.model.ServiceContract;

import org.apache.tuscany.core.implementation.system.component.SystemReferenceBinding;
import org.apache.tuscany.core.implementation.system.component.SystemServiceBinding;
import org.apache.tuscany.core.implementation.system.model.SystemBindingDefinition;

/**
 * Creates serviceBindings and references confgured with the system binding
 *
 * @version $$Rev$$ $$Date$$
 */
public class SystemBindingBuilder extends BindingBuilderExtension<SystemBindingDefinition>
    implements BindingBuilder<SystemBindingDefinition> {

    public ServiceBinding build(CompositeComponent parent,
                                BoundServiceDefinition definition,
                                SystemBindingDefinition bindingDefinition,
                                DeploymentContext deploymentContext) throws BuilderException {

        URI uri = definition.getTarget();
        if (uri == null) {
            throw new MissingWireTargetException("Target URI not specified", definition.getName());
        }
        ServiceContract<?> contract = definition.getServiceContract();
        return new SystemServiceBinding(definition.getName(), parent, contract);
    }

    public ReferenceBinding build(CompositeComponent parent,
                                  BoundReferenceDefinition definition,
                                  SystemBindingDefinition bindingDefinition,
                                  DeploymentContext deploymentContext) {
        String name = definition.getName();
        return new SystemReferenceBinding(name, parent);
    }

    @Override
    protected Class<SystemBindingDefinition> getBindingType() {
        return SystemBindingDefinition.class;
    }
}
