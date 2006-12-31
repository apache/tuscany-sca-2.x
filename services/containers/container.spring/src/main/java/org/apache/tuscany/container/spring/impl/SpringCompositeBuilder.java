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
package org.apache.tuscany.container.spring.impl;

import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.builder.BuilderInstantiationException;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.ComponentRegistrationException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentBuilderExtension;
import org.apache.tuscany.spi.model.BoundReferenceDefinition;
import org.apache.tuscany.spi.model.BoundServiceDefinition;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;

import org.apache.tuscany.container.spring.model.SpringComponentType;
import org.apache.tuscany.container.spring.model.SpringImplementation;
import org.springframework.core.io.Resource;

/**
 * Creates a {@link org.apache.tuscany.container.spring.impl.SpringCompositeComponent} from an assembly model
 *
 * @version $$Rev$$ $$Date$$
 */
public class SpringCompositeBuilder extends ComponentBuilderExtension<SpringImplementation> {

    @SuppressWarnings("unchecked")
    public Component build(CompositeComponent parent,
                           ComponentDefinition<SpringImplementation> componentDefinition,
                           DeploymentContext deploymentContext) throws BuilderException {
        String name = componentDefinition.getName();
        SpringImplementation implementation = componentDefinition.getImplementation();
        Resource resource = implementation.getApplicationResource();
        SpringCompositeComponent component =
            new SpringCompositeComponent(name, resource, parent, wireService, connector, null);
        SpringComponentType<Property<?>> componentType = implementation.getComponentType();

        // We need to set the target invoker as opposed to having the connector do it since the
        // Spring context is "opaque" to the wiring fabric. In other words, the Spring context does not expose
        // its beans as SCA components to the connector to wire the serviceBindings to
        for (BoundServiceDefinition serviceDefinition : componentType.getServices().values()) {
            // call back into builder registry to handle building of serviceBindings
            Service service = builderRegistry.build(parent, serviceDefinition, deploymentContext);
            for (ServiceBinding binding : service.getServiceBindings()) {
                // wire service to bean invokers
                InboundWire wire = binding.getInboundWire();
                QualifiedName targetName = new QualifiedName(serviceDefinition.getTarget().getPath());
                for (InboundInvocationChain chain : wire.getInvocationChains().values()) {
                    // FIXME this should go to the connector and get policy and be invoked from
                    // SpringComposite.prepare()
                    chain.addInterceptor(new SpringInterceptor());
                    chain.setTargetInvoker(component.createTargetInvoker(targetName.getPartName(),
                        chain.getOperation(),
                        null));
                }
            }
            try {
                component.register(service);
            } catch (ComponentRegistrationException e) {
                throw new BuilderInstantiationException("Error registering service", e);
            }
        }
        for (BoundReferenceDefinition referenceDefinition : componentType.getReferences().values()) {
            // call back into builder registry to handle building of references
            Reference reference = builderRegistry.build(parent, referenceDefinition, deploymentContext);
            connector.connect(reference);
            try {
                component.register(reference);
            } catch (ComponentRegistrationException e) {
                throw new BuilderInstantiationException("Error registering reference", e);
            }
        }
        return component;
    }

    protected Class<SpringImplementation> getImplementationType() {
        return SpringImplementation.class;
    }
}
