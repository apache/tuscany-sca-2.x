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
package org.apache.tuscany.binding.ejb;

import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.BindingBuilderExtension;
import org.apache.tuscany.spi.model.BoundReferenceDefinition;
import org.apache.tuscany.spi.model.BoundServiceDefinition;

public class EJBBindingBuilder extends BindingBuilderExtension<EJBBindingDefinition> {
    public ServiceBinding build(CompositeComponent parent,
                                BoundServiceDefinition serviceDefinition,
                                EJBBindingDefinition bindingDefinition,
                                DeploymentContext deploymentcontext) throws BuilderException {
        EJBServiceBinding ejbService = new EJBServiceBinding(serviceDefinition.getName(), parent);
        return ejbService;
    }

    public ReferenceBinding build(CompositeComponent parent,
                                  BoundReferenceDefinition boundReferenceDefinition,
                                  EJBBindingDefinition bindingDefinition,
                                  DeploymentContext deploymentcontext) throws BuilderException {
        return new EJBReferenceBinding(boundReferenceDefinition.getName(), parent, bindingDefinition);
    }

    public Class<EJBBindingDefinition> getBindingType() {
        return EJBBindingDefinition.class;
    }
}
