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
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.BindingBuilderExtension;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ServiceDefinition;

/**
 * Creates runtime artifacts for the local binding
 *
 * @version $Rev$ $Date$
 */
public class LocalBindingBuilder extends BindingBuilderExtension<LocalBindingDefinition> {

    protected Class<LocalBindingDefinition> getBindingType() {
        return LocalBindingDefinition.class;
    }

    public ServiceBinding build(ServiceDefinition serviceDefinition,
                                LocalBindingDefinition bindingDefinition,
                                DeploymentContext context) throws BuilderException {
        return new LocalServiceBinding(serviceDefinition.getUri());
    }


    public ReferenceBinding build(ReferenceDefinition referenceDefinition,
                                  LocalBindingDefinition bindingDefinition,
                                  DeploymentContext context) throws BuilderException {
        return new LocalReferenceBinding(referenceDefinition.getUri(), bindingDefinition.getTargetUri());
    }
}
