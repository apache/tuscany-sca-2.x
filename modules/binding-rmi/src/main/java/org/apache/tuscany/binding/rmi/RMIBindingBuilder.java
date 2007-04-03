/**
 *
 * Copyright 2006 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.binding.rmi;

import java.rmi.Remote;

import org.osoa.sca.annotations.Constructor;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.BindingBuilderExtension;
import org.apache.tuscany.spi.model.BoundReferenceDefinition;
import org.apache.tuscany.spi.model.BoundServiceDefinition;

import org.apache.tuscany.host.rmi.RMIHost;

/**
 * Builds a Service or Reference for an RMI binding.
 *
 * @version $Rev$ $Date$
 */

public class RMIBindingBuilder extends BindingBuilderExtension<RMIBindingDefinition> {

    private RMIHost rmiHost;

    @Constructor({"rmiHost"})
    public RMIBindingBuilder(@Autowire RMIHost rHost) {
        this.rmiHost = rHost;
    }

    protected Class<RMIBindingDefinition> getBindingType() {
        return RMIBindingDefinition.class;
    }

    @SuppressWarnings({"unchecked"})
    public ServiceBinding build(CompositeComponent parent,
                                BoundServiceDefinition boundServiceDefinition,
                                RMIBindingDefinition bindingDefinition,
                                DeploymentContext deploymentContext) {

        Class intf = boundServiceDefinition.getServiceContract().getInterfaceClass();

        return new RMIServiceBinding<Remote>(boundServiceDefinition.getName(),
            parent,
            wireService,
            rmiHost,
            bindingDefinition.getHost(),
            bindingDefinition.getPort(),
            bindingDefinition.getServiceName(),
            intf);
    }

    public ReferenceBinding build(CompositeComponent parent,
                                  BoundReferenceDefinition boundReferenceDefinition,
                                  RMIBindingDefinition bindingDefinition,
                                  DeploymentContext deploymentContext) {
        String name = boundReferenceDefinition.getName();
        String host = bindingDefinition.getHost();
        String port = bindingDefinition.getPort();
        String svcName = bindingDefinition.getServiceName();
        return new RMIReferenceBinding(name, parent, rmiHost, host, port, svcName);
    }

}
