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
package org.apache.tuscany.core.binding.local;

import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.BindingBuilderExtension;
import org.apache.tuscany.spi.model.BoundReferenceDefinition;
import org.apache.tuscany.spi.model.BoundServiceDefinition;

/**
 * Creates runtime artifacts for the local binding
 *
 * @version $Rev$ $Date$
 */
public class LocalBindingBuilder extends BindingBuilderExtension<LocalBindingDefinition> {

    protected Class<LocalBindingDefinition> getBindingType() {
        return LocalBindingDefinition.class;
    }

    public ServiceBinding build(CompositeComponent parent,
                                BoundServiceDefinition boundServiceDefinition,
                                LocalBindingDefinition bindingDefinition,
                                DeploymentContext deploymentContext)
        throws BuilderException {
        return new LocalServiceBinding(boundServiceDefinition.getName(), parent);
    }


    public ReferenceBinding build(CompositeComponent parent,
                                  BoundReferenceDefinition boundReferenceDefinition,
                                  LocalBindingDefinition bindingDefinition,
                                  DeploymentContext deploymentContext) throws BuilderException {
        return new LocalReferenceBinding(boundReferenceDefinition.getName(), parent);
    }
}
