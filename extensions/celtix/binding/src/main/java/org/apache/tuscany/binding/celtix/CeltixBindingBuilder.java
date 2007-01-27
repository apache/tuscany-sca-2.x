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

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.BindingBuilderExtension;
import org.apache.tuscany.spi.model.BoundReferenceDefinition;
import org.apache.tuscany.spi.model.BoundServiceDefinition;

import commonj.sdo.helper.TypeHelper;
import org.apache.tuscany.idl.wsdl.WSDLDefinitionRegistry;
import org.objectweb.celtix.Bus;

/**
 * Builds a {@link org.apache.tuscany.spi.component.ServiceBinding} or {@link org.apache.tuscany.spi.component.ReferenceBinding}
 * configured with the Celtix binding
 *
 * @version $Rev$ $Date$
 */
public class CeltixBindingBuilder extends BindingBuilderExtension<WebServiceBindingDefinition> {

    private Bus bus;

    public ServiceBinding build(CompositeComponent parent,
                                BoundServiceDefinition boundServiceDefinition,
                                WebServiceBindingDefinition bindingDefinition,
                                DeploymentContext deploymentContext) {
        TypeHelper typeHelper = (TypeHelper) deploymentContext.getExtension(TypeHelper.class.getName());
        if (typeHelper == null) {
            typeHelper = TypeHelper.INSTANCE;
        }
        if (bus == null) {
            bus = getBus(bindingDefinition.getWSDLDefinitionRegistry());
        }
        return new CeltixServiceBinding(
            boundServiceDefinition.getName(),
            parent,
            bindingDefinition,
            bus,
            typeHelper);
    }

    public ReferenceBinding build(CompositeComponent parent,
                                  BoundReferenceDefinition boundReferenceDefinition,
                                  WebServiceBindingDefinition wsBinding,
                                  DeploymentContext deploymentContext) {
        TypeHelper typeHelper = (TypeHelper) deploymentContext.getExtension(TypeHelper.class.getName());
        if (typeHelper == null) {
            typeHelper = TypeHelper.INSTANCE;
        }
        if (bus == null) {
            bus = getBus(wsBinding.getWSDLDefinitionRegistry());
        }
        return new CeltixReferenceBinding(
            boundReferenceDefinition.getName(),
            parent,
            wsBinding,
            bus,
            typeHelper);
    }

    protected Class<WebServiceBindingDefinition> getBindingType() {
        return WebServiceBindingDefinition.class;
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
