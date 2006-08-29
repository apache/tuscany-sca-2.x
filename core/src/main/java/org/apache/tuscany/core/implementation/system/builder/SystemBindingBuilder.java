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
package org.apache.tuscany.core.implementation.system.builder;

import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.builder.BindingBuilder;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.model.BoundReferenceDefinition;
import org.apache.tuscany.spi.model.BoundServiceDefinition;
import org.apache.tuscany.spi.wire.OutboundWire;

import org.apache.tuscany.core.component.AutowireComponent;
import org.apache.tuscany.core.implementation.system.component.SystemReference;
import org.apache.tuscany.core.implementation.system.component.SystemReferenceImpl;
import org.apache.tuscany.core.implementation.system.component.SystemService;
import org.apache.tuscany.core.implementation.system.component.SystemServiceImpl;
import org.apache.tuscany.core.implementation.system.model.SystemBinding;
import org.apache.tuscany.core.implementation.system.wire.SystemInboundWire;
import org.apache.tuscany.core.implementation.system.wire.SystemInboundWireImpl;
import org.apache.tuscany.core.implementation.system.wire.SystemOutboundAutowire;
import org.apache.tuscany.core.implementation.system.wire.SystemOutboundWire;
import org.apache.tuscany.core.implementation.system.wire.SystemOutboundWireImpl;

/**
 * Creates {@link SystemService}s and {@link SystemReference}s by evaluating an assembly definition
 *
 * @version $$Rev$$ $$Date$$
 */
public class SystemBindingBuilder implements BindingBuilder<SystemBinding> {

    public SystemService build(CompositeComponent parent,
                               BoundServiceDefinition<SystemBinding> boundServiceDefinition,
                               DeploymentContext deploymentContext) {
        Class interfaze = boundServiceDefinition.getServiceContract().getInterfaceClass();
        QualifiedName targetName = new QualifiedName(boundServiceDefinition.getTarget().getPath());
        Component target = (Component) parent.getChild(targetName.getPartName());
        if (target == null) {
            throw new BuilderConfigException("Target not found: [" + targetName + ']');
        }
        SystemInboundWire<Object> inboundWire =
            new SystemInboundWireImpl<Object>(boundServiceDefinition.getName(), interfaze, target);
        SystemOutboundWire<Object> outboundWire =
            new SystemOutboundWireImpl<Object>(boundServiceDefinition.getName(), targetName, interfaze);
        SystemService<Object> service = new SystemServiceImpl<Object>(boundServiceDefinition.getName(), parent);
        service.setInboundWire(inboundWire);
        service.setOutboundWire(outboundWire);
        return service;
    }

    public SystemReference build(CompositeComponent parent,
                                 BoundReferenceDefinition<SystemBinding> boundReferenceDefinition,
                                 DeploymentContext deploymentContext) {
        assert parent.getParent() instanceof AutowireComponent
            : "Grandparent not an instance of " + AutowireComponent.class.getName();
        AutowireComponent autowireComponent = (AutowireComponent) parent.getParent();
        Class<?> interfaze = boundReferenceDefinition.getServiceContract().getInterfaceClass();
        SystemReferenceImpl reference = new SystemReferenceImpl(boundReferenceDefinition.getName(), interfaze, parent);
        SystemInboundWire<?> inboundWire = new SystemInboundWireImpl(boundReferenceDefinition.getName(), interfaze);
        String refName = boundReferenceDefinition.getName();
        OutboundWire outboundWire = new SystemOutboundAutowire(refName, interfaze, autowireComponent);
        reference.setInboundWire(inboundWire);
        reference.setOutboundWire(outboundWire);
        return reference;
    }
}
