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
package org.apache.tuscany.binding.celtix;

import java.util.Map;
import java.util.WeakHashMap;

import org.apache.tuscany.idl.wsdl.WSDLDefinitionRegistry;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.BindingBuilderExtension;
import org.apache.tuscany.spi.model.BoundReferenceDefinition;
import org.apache.tuscany.spi.model.BoundServiceDefinition;

import org.objectweb.celtix.Bus;

/**
 * Builds a {@link Service} or {@link org.apache.tuscany.spi.component.Reference} configured
 * with the Celtix binding
 *
 * @version $Rev$ $Date$
 */
public class CeltixBindingBuilder extends BindingBuilderExtension<WebServiceBinding> {

    private Bus bus;

    public SCAObject build(CompositeComponent parent,
                           BoundServiceDefinition<WebServiceBinding> boundServiceDefinition,
                           DeploymentContext deploymentContext) {
        WebServiceBinding wsBinding = boundServiceDefinition.getBinding();
        if (bus == null) {
            bus = getBus(wsBinding.getWSDLDefinitionRegistry());
        }
        return new CeltixService(
            boundServiceDefinition.getName(),
            boundServiceDefinition.getServiceContract().getInterfaceClass(),
            parent,
            wireService,
            wsBinding,
            bus);
    }

    public SCAObject build(CompositeComponent parent,
                           BoundReferenceDefinition<WebServiceBinding> boundReferenceDefinition,
                           DeploymentContext deploymentContext) {
        WebServiceBinding wsBinding = boundReferenceDefinition.getBinding();
        if (bus == null) {
            bus = getBus(wsBinding.getWSDLDefinitionRegistry());
        }
        return new CeltixReference(
            boundReferenceDefinition.getName(),
            boundReferenceDefinition.getServiceContract().getInterfaceClass(),
            parent,
            wireService,
            wsBinding,
            bus);
    }

    protected Class<WebServiceBinding> getBindingType() {
        return WebServiceBinding.class;
    }

    private Bus getBus(WSDLDefinitionRegistry wsdlDefinitionRegistry) {
        Bus celtixBus = null;
        try {
            Map<String, Object> properties = new WeakHashMap<String, Object>();
            properties.put("celtix.WSDLManager", new TuscanyWSDLManager(wsdlDefinitionRegistry));
            celtixBus = Bus.init(new String[0], properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return celtixBus;
    }

}
