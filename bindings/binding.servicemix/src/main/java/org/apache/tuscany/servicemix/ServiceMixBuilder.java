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
 * specific language governing pejbissions and limitations
 * under the License.    
 */
package org.apache.tuscany.servicemix;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.BindingBuilderExtension;
import org.apache.tuscany.spi.model.BoundReferenceDefinition;
import org.apache.tuscany.spi.model.BoundServiceDefinition;

/**
 * Builds a Service or Reference for a JBI binding.
 */
public class ServiceMixBuilder extends BindingBuilderExtension<JBIBinding> {

    protected Class<JBIBinding> getBindingType() {
        return JBIBinding.class;
    }

    @SuppressWarnings( { "unchecked" })
    public SCAObject build(CompositeComponent parent, BoundServiceDefinition<JBIBinding> boundServiceDefinition, DeploymentContext deploymentContext) {

        String name = boundServiceDefinition.getName();

        ServiceMixService serviceMixService = new ServiceMixService(name, parent, wireService, null);

        return serviceMixService;
    }

    @SuppressWarnings( { "unchecked" })
    public ServiceMixReference build(CompositeComponent parent, BoundReferenceDefinition<JBIBinding> boundReferenceDefinition,
            DeploymentContext deploymentContext) {

        
        ServiceMixReference serviceMixReference = new ServiceMixReference(null, parent, wireService, null, null);
        
        return serviceMixReference;

    }
}
