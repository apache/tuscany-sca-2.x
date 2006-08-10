/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors, as applicable.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this 
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under 
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY 
 * KIND, either express or implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */
package org.apache.tuscany.binding.celtix;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.BindingBuilderExtension;
import org.apache.tuscany.spi.model.BoundReferenceDefinition;
import org.apache.tuscany.spi.model.BoundServiceDefinition;

/**
 * Builds a {@link Service} or {@link org.apache.tuscany.spi.component.Reference} configured 
 * with the Celtix binding
 *
 * @version $Rev$ $Date$
 */
public class CeltixBindingBuilder extends BindingBuilderExtension<WebServiceBinding> {

    private BusService busService;

    @Autowire
    public void setBusService(BusService service) {
        this.busService = service;
    }

    public SCAObject build(CompositeComponent parent,
                           BoundServiceDefinition<WebServiceBinding> boundServiceDefinition,
                           DeploymentContext deploymentContext) {
        //FIXME: CeltixService needs an instance of BusService. How to get BusService wired in 
        //and where BusService is created?
        WebServiceBinding wsBinding = boundServiceDefinition.getBinding();
        //FIXME get interface
        Class<?> interfaze = null;
        return new CeltixService(
            boundServiceDefinition.getName(),
            interfaze,
            parent,
            wireService,
            wsBinding,
            busService.getBus());
    }

    public SCAObject build(CompositeComponent parent,
                           BoundReferenceDefinition<WebServiceBinding> boundReferenceDefinition,
                           DeploymentContext deploymentContext) {
        WebServiceBinding wsBinding = boundReferenceDefinition.getBinding();
        //Definition definition = wsBinding.getWSDLDefinition();
        //Port port = wsBinding.getWSDLPort();
        //Service service = wsBinding.getWSDLService();
        return new CeltixReference(
            boundReferenceDefinition.getName(),
            boundReferenceDefinition.getServiceContract().getInterfaceClass(),
            parent,
            wireService,
            wsBinding,
            busService.getBus());
    }

    protected Class<WebServiceBinding> getBindingType() {
        return WebServiceBinding.class;
    }
}
