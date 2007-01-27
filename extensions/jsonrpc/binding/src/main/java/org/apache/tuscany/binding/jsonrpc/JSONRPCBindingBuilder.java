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
package org.apache.tuscany.binding.jsonrpc;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.BindingBuilderExtension;
import org.apache.tuscany.spi.host.ServletHost;
import org.apache.tuscany.spi.model.BoundServiceDefinition;
import org.apache.tuscany.spi.wire.WireService;

/**
 * Builds a Service for JSON-RPC binding.
 *
 * @version $Rev$ $Date$
 */
public class JSONRPCBindingBuilder extends BindingBuilderExtension<JSONRPCBindingDefinition> {

    private ServletHost servletHost;
    private WireService wireService;

    @Autowire()
    public void setServletHost(ServletHost servletHost) {
        this.servletHost = servletHost;
    }

    @Autowire
    public void setWireService(WireService wireService) {
        this.wireService = wireService;
    }


    public ServletHost getServletHost() {
        return servletHost;
    }

    protected Class<JSONRPCBindingDefinition> getBindingType() {
        return JSONRPCBindingDefinition.class;
    }

    @SuppressWarnings("unchecked")
    public ServiceBinding build(CompositeComponent parent,
                                BoundServiceDefinition serviceDefinition,
                                JSONRPCBindingDefinition bindingDefinition, DeploymentContext deploymentContext) {
        return new JSONRPCServiceBinding(serviceDefinition.getName(), parent, this.wireService, servletHost);
    }

}