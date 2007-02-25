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
package org.apache.tuscany.spi.builder;

import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.model.BindingDefinition;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ServiceDefinition;

/**
 * Responsible for processing a service or reference in an assembly configured with a particular binding. The builder
 * will create and return corresponding {@link org.apache.tuscany.spi.component.ServiceBinding} or {@link
 * org.apache.tuscany.spi.component.ReferenceBinding}
 *
 * @version $Rev$ $Date$
 */
public interface BindingBuilder<B extends BindingDefinition> {

    /**
     * Creates a service binding
     *
     * @param serviceDefinition the service the binding is configured for
     * @param bindingDefinition the binding definition
     * @param context           the current deployment context
     * @return a service binding
     * @throws BuilderException
     */
    ServiceBinding build(ServiceDefinition serviceDefinition, B bindingDefinition, DeploymentContext context)
        throws BuilderException;

    /**
     * Creates a reference binding
     *
     * @param referenceDefinition the reference the binding is configured for
     * @param bindingDefinition   the binding definition
     * @param context             the current deployment context
     * @return a reference binding
     * @throws BuilderException
     */
    ReferenceBinding build(ReferenceDefinition referenceDefinition, B bindingDefinition, DeploymentContext context)
        throws BuilderException;
}
