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
package org.apache.tuscany.binding.rmi;
 
import java.rmi.Remote;
 
import org.apache.tuscany.spi.builder.InvalidServiceInterfaceException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.BindingBuilderExtension;
import org.apache.tuscany.spi.model.BoundReferenceDefinition;
import org.apache.tuscany.spi.model.BoundServiceDefinition;

/**
 * Builds a Service or Reference for an RMI binding.
 *
 * @version $Rev$ $Date$
 */
public class RMIBindingBuilder extends BindingBuilderExtension<RMIBinding> {
    protected Class<RMIBinding> getBindingType() {
        return RMIBinding.class;
    }

    public SCAObject build(CompositeComponent parent,
                           BoundServiceDefinition<RMIBinding> boundServiceDefinition,
                           DeploymentContext deploymentContext) {
        String name = boundServiceDefinition.getName();
        Class<Remote> service = getServiceInterface(boundServiceDefinition);
        String uri = boundServiceDefinition.getBinding().getURI();
        
        RMIService rmiService = new RMIService<Remote>(name, parent, wireService, uri, service);
        return rmiService;
    }

    @SuppressWarnings({"unchecked"})
    protected Class<Remote> getServiceInterface(BoundServiceDefinition<RMIBinding> boundServiceDefinition) {
        Class<?> intf = boundServiceDefinition.getServiceContract().getInterfaceClass();
        if (!Remote.class.isAssignableFrom(intf)) {
            throw new InvalidServiceInterfaceException("RMI requires interface extend Remote", intf);
        }
        return (Class<Remote>) intf;
    }

    @SuppressWarnings({"unchecked"})
    public RMIReference build(CompositeComponent parent,
                              BoundReferenceDefinition<RMIBinding> boundReferenceDefinition,
                              DeploymentContext deploymentContext) {
        String name = boundReferenceDefinition.getName();
        String uri = boundReferenceDefinition.getBinding().getURI();
        Class<?> interfaze = boundReferenceDefinition.getServiceContract().getInterfaceClass();
        RMIReference rmiReference =  new RMIReference(name, 
                parent, 
                wireService, 
                uri, 
                boundReferenceDefinition.getServiceContract().getInterfaceClass());


return rmiReference;

    }
}
