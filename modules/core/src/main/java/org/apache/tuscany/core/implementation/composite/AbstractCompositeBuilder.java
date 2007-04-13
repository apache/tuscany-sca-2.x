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
package org.apache.tuscany.core.implementation.composite;

import org.apache.tuscany.assembly.Composite;
import org.apache.tuscany.assembly.CompositeReference;
import org.apache.tuscany.assembly.CompositeService;
import org.apache.tuscany.assembly.Implementation;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.builder.BuilderInstantiationException;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.RegistrationException;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentBuilderExtension;

/**
 * Abstract builder for composites
 * 
 * @version $Rev$ $Date$
 */
public abstract class AbstractCompositeBuilder<T extends Implementation> extends ComponentBuilderExtension<T> {

    public Component build(Component component, Composite componentType, DeploymentContext deploymentContext)
        throws BuilderException {
        for (org.apache.tuscany.assembly.Component definition : componentType.getComponents()) {
            builderRegistry.build(definition, deploymentContext);
        }
        for (org.apache.tuscany.assembly.Service definition : componentType.getServices()) {
            try {
                Service service = builderRegistry.build((CompositeService)definition, deploymentContext);
                if (service != null) {
                    component.register(service);
                }
            } catch (RegistrationException e) {
                throw new BuilderInstantiationException("Error registering service", e);
            }
        }
        for (org.apache.tuscany.assembly.Reference definition : componentType.getReferences()) {
            try {
                Reference reference = builderRegistry.build((CompositeReference)definition, deploymentContext);
                if (reference != null) {
                    component.register(reference);
                }
            } catch (RegistrationException e) {
                throw new BuilderInstantiationException("Error registering reference", e);
            }
        }
        return component;
    }

}
