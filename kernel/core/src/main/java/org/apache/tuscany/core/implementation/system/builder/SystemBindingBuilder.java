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
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.BindingBuilderExtension;
import org.apache.tuscany.spi.model.BoundReferenceDefinition;
import org.apache.tuscany.spi.model.BoundServiceDefinition;
import org.apache.tuscany.spi.model.ServiceContract;

import org.apache.tuscany.core.implementation.system.component.SystemReferenceImpl;
import org.apache.tuscany.core.implementation.system.component.SystemServiceImpl;
import org.apache.tuscany.core.implementation.system.model.SystemBindingDefinition;

/**
 * Creates services and references confgured with the system binding
 *
 * @version $$Rev$$ $$Date$$
 */
public class SystemBindingBuilder extends BindingBuilderExtension<SystemBindingDefinition>
    implements BindingBuilder<SystemBindingDefinition> {

    public Service build(CompositeComponent parent,
                         BoundServiceDefinition<SystemBindingDefinition> definition,
                         DeploymentContext deploymentContext) throws BuilderException {

        URI uri = definition.getTarget();
        if (uri == null) {
            throw new MissingWireTargetException("Target URI not specified", definition.getName());
        }
        ServiceContract<?> contract = definition.getServiceContract();
        return new SystemServiceImpl(definition.getName(), parent, contract);
    }

    public Reference build(CompositeComponent parent,
                           BoundReferenceDefinition<SystemBindingDefinition> definition,
                           DeploymentContext deploymentContext) {
        String name = definition.getName();
        return new SystemReferenceImpl(name, parent);
    }

    @Override
    protected Class<SystemBindingDefinition> getBindingType() {
        return SystemBindingDefinition.class;
    }
}
