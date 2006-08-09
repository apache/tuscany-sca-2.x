/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors, as applicable.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.binding.axis2;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.BindingBuilderExtension;
import org.apache.tuscany.spi.model.BoundReferenceDefinition;
import org.apache.tuscany.spi.model.BoundServiceDefinition;
import org.apache.tuscany.spi.model.ServiceContract;

/**
 * Builds a {@link org.osoa.sca.annotations.Service} or {@link org.apache.tuscany.spi.component.Reference} configured
 * with the Axis2 binding
 *
 * @version $Rev$ $Date$
 */
public class Axis2BindingBuilder extends BindingBuilderExtension<WebServiceBinding> {
    public SCAObject build(CompositeComponent parent,
                           BoundServiceDefinition<WebServiceBinding> serviceDefinition,
                           DeploymentContext deploymentContext) {
        WebServiceBinding wsBinding = serviceDefinition.getBinding();
        // FIXME need to get interface for the service
        Class<?> interfaze = null;
        //FIXME: Axis2Service needs an instance of ServletHost as parameter. How to get it?
        return new Axis2Service(
            serviceDefinition.getName(),
            interfaze,
            parent,
            wireService,
            wsBinding,
            null);
    }

    public SCAObject build(CompositeComponent parent,
                           BoundReferenceDefinition<WebServiceBinding> boundReferenceDefinition,
                           DeploymentContext deploymentContext) {
        WebServiceBinding wsBinding = boundReferenceDefinition.getBinding();
       
        return new Axis2Reference(
            boundReferenceDefinition.getName(),
            parent,
            wireService,
            wsBinding, 
            boundReferenceDefinition.getServiceContract());
    }

    protected Class<WebServiceBinding> getBindingType() {
        return WebServiceBinding.class;
    }
}
