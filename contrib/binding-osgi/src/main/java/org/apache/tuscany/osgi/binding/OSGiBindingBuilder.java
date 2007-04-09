/*
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
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
package org.apache.tuscany.osgi.binding;

import org.osoa.sca.annotations.Constructor;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.BindingBuilderExtension;
import org.apache.tuscany.spi.model.BoundReferenceDefinition;
import org.apache.tuscany.spi.model.BoundServiceDefinition;

import org.apache.tuscany.osgi.OSGiHost;

/**
 * Builds a Service or Reference for an OSGi binding.
 *
 * @version $Rev$ $Date$
 */
public class OSGiBindingBuilder extends BindingBuilderExtension<OSGiBindingDefinition> {

    OSGiHost host;

    @Constructor
    public OSGiBindingBuilder(@Autowire OSGiHost host) {
        this.host = host;
    }

    protected Class<OSGiBindingDefinition> getBindingType() {
        return OSGiBindingDefinition.class;
    }

    public ServiceBinding build(CompositeComponent parent,
                                BoundServiceDefinition boundServiceDefinition,
                                OSGiBindingDefinition bindingDefinition,
                                DeploymentContext deploymentContext) {
        String name = boundServiceDefinition.getName();
        String osgiServiceName = bindingDefinition.getService();
        return new OSGiServiceBinding(name, parent, wireService, osgiServiceName, host);
    }

    public OSGiReferenceBinding build(CompositeComponent parent,
                                      BoundReferenceDefinition boundReferenceDefinition,
                                      OSGiBindingDefinition bindingDefinition, DeploymentContext deploymentContext) {
        String name = boundReferenceDefinition.getName();
        return new OSGiReferenceBinding(name, parent);
    }

    protected Class<? extends Object> getServiceInterface(BoundServiceDefinition boundServiceDefinition) {
        return boundServiceDefinition.getServiceContract().getInterfaceClass();
    }

}
