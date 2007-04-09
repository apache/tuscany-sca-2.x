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

import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ServiceDefinition;

/**
 * Implementations build <code>SCAObject</code> types from model objects.
 *
 * @version $Rev$ $Date$
 */
public interface Builder {
    /**
     * Builds a <code>Component</code> from a <code>ComponentDefinition</code>
     *
     * @param definition the component definition as parsed from an SCA assembly
     * @param context    the current deployment context
     * @return the newly created component
     * @throws BuilderException
     */
    <I extends Implementation<?>> Component build(ComponentDefinition<I> definition, DeploymentContext context)
        throws BuilderException;

    /**
     * Builds a <code>Service</code> and its bindings from a <code>BoundServiceDefinition</code>
     *
     * @param definition the service definition as parsed from an SCA assembly
     * @param context    the current deployment context
     * @return the newly created service
     * @throws BuilderException
     */
    Service build(ServiceDefinition definition, DeploymentContext context) throws BuilderException;

    /**
     * Builds a <code>Reference</code> and its bindings from a <code>BoundReferenceDefinition</code>
     *
     * @param definition the reference definition as parsed from an SCA assembly
     * @param context    the current deployment context
     * @return the newly created reference
     * @throws BuilderException
     */
    Reference build(ReferenceDefinition definition, DeploymentContext context) throws BuilderException;

}
