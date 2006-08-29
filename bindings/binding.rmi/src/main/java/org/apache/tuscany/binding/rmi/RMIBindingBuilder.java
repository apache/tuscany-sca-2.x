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

import org.apache.tuscany.host.rmi.RMIHost;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.BindingBuilderExtension;
import org.apache.tuscany.spi.model.BoundReferenceDefinition;
import org.apache.tuscany.spi.model.BoundServiceDefinition;
import org.osoa.sca.annotations.Constructor;

/**
 * Builds a Service or Reference for an RMI binding.
 *
 * @version $Rev$ $Date$
 */

public class RMIBindingBuilder extends BindingBuilderExtension<RMIBinding> {

    private RMIHost rmiHost;

    @Constructor({"rmiHost"})
    public RMIBindingBuilder(@Autowire RMIHost rHost) {
        this.rmiHost = rHost;
    }

    protected Class<RMIBinding> getBindingType() {
        return RMIBinding.class;
    }

    @SuppressWarnings({"unchecked"})
    public SCAObject build(CompositeComponent parent,
                           BoundServiceDefinition<RMIBinding> boundServiceDefinition,
                           DeploymentContext deploymentContext) {

        Class intf = boundServiceDefinition.getServiceContract().getInterfaceClass();

        return new RMIService<Remote>(boundServiceDefinition.getName(), parent, wireService, rmiHost,
            boundServiceDefinition.getBinding().getHost(), boundServiceDefinition.getBinding().getPort(),
            boundServiceDefinition.getBinding().getServiceName(), intf);
    }

    @SuppressWarnings({"unchecked"})
    public RMIReference build(CompositeComponent parent,
                              BoundReferenceDefinition<RMIBinding> boundReferenceDefinition,
                              DeploymentContext deploymentContext) {
        String name = boundReferenceDefinition.getName();
        String host = boundReferenceDefinition.getBinding().getHost();
        String port = boundReferenceDefinition.getBinding().getPort();
        String svcName = boundReferenceDefinition.getBinding().getServiceName();
        // Class<?> interfaze = boundReferenceDefinition.getServiceContract().getInterfaceClass();

        return new RMIReference(name, parent, wireService, rmiHost, host, port, svcName,
            boundReferenceDefinition.getServiceContract().getInterfaceClass());

    }

}
