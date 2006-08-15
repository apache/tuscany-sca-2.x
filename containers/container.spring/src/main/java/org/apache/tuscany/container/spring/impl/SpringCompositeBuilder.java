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
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentBuilderExtension;
import org.apache.tuscany.spi.model.Binding;
import org.apache.tuscany.spi.model.BoundReferenceDefinition;
import org.apache.tuscany.spi.model.BoundServiceDefinition;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ReferenceTarget;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;

import org.springframework.context.ConfigurableApplicationContext;
import org.apache.tuscany.container.spring.model.SpringImplementation;
import org.apache.tuscany.container.spring.impl.SpringCompositeComponent;

/**
 * Creates a {@link org.apache.tuscany.container.spring.impl.SpringCompositeComponent} from an assembly model
 *
 * @version $$Rev$$ $$Date$$
 */
public class SpringCompositeBuilder extends ComponentBuilderExtension<SpringImplementation> {


    public Component build(CompositeComponent<?> parent,
                           ComponentDefinition<SpringImplementation> componentDefinition,
                           DeploymentContext deploymentContext) throws BuilderConfigException {
        String name = componentDefinition.getName();
        SpringImplementation implementation = componentDefinition.getImplementation();
        ConfigurableApplicationContext applicationContext = implementation.getComponentType().getApplicationContext();
        SpringCompositeComponent component = new SpringCompositeComponent(name, applicationContext, parent, null);
        CompositeComponentType<BoundServiceDefinition<? extends Binding>,
            BoundReferenceDefinition<? extends Binding>,
            ? extends Property> componentType = implementation.getComponentType();

        // We still need to set the target invoker as opposed to having the connector do it since the
        // Spring context is "opaque" to the wiring fabric. In other words, the Spring context does not expose
        // its beans as SCA components to the connector t wire the services to
        for (BoundServiceDefinition<? extends Binding> serviceDefinition : componentType.getServices().values()) {
            // call back into builder registry to handle building of services
            Service<?> service = (Service) builderRegistry.build(parent, serviceDefinition, deploymentContext);
            // wire serviceDefinition to bean invokers
            InboundWire<?> wire = service.getInboundWire();
            QualifiedName targetName = new QualifiedName(serviceDefinition.getTarget().getPath());
            for (InboundInvocationChain chain : wire.getInvocationChains().values()) {
                chain.setTargetInvoker(component.createTargetInvoker(targetName.getPartName(), chain.getMethod()));
            }
            component.register(service);
        }
        for (ReferenceTarget target : componentDefinition.getReferenceTargets().values()) {
            ReferenceDefinition referenceDefinition = componentType.getReferences().get(target.getReferenceName());
            if (referenceDefinition instanceof BoundReferenceDefinition) {
                // call back into builder registry to handle building of references
                component.register(builderRegistry.build(parent, (BoundReferenceDefinition<? extends Binding>)
                    referenceDefinition, deploymentContext));
            }
        }
        return component;
    }

    protected Class<SpringImplementation> getImplementationType() {
        return SpringImplementation.class;
    }
}
