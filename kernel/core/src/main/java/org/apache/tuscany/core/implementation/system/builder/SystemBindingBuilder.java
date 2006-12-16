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

import java.net.URI;

import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.builder.BindingBuilder;
import org.apache.tuscany.spi.builder.MissingWireTargetException;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.component.SystemAtomicComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.BindingBuilderExtension;
import org.apache.tuscany.spi.model.BoundReferenceDefinition;
import org.apache.tuscany.spi.model.BoundServiceDefinition;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InboundWire;

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
import org.apache.tuscany.core.builder.InvalidTargetTypeException;

/**
 * Creates {@link SystemService}s and {@link org.apache.tuscany.core.implementation.system.component.SystemReference}s
 * by evaluating an assembly definition
 *
 * @version $$Rev$$ $$Date$$
 */
public class SystemBindingBuilder extends BindingBuilderExtension<SystemBinding>
    implements BindingBuilder<SystemBinding> {

    public SystemService build(CompositeComponent parent,
                               BoundServiceDefinition<SystemBinding> definition,
                               DeploymentContext deploymentContext) throws BuilderException {

        URI uri = definition.getTarget();
        if (uri == null) {
            throw new MissingWireTargetException("Target URI not specified", definition.getName());
        }
        QualifiedName targetName = new QualifiedName(uri.getPath());
        String targetComponentName = targetName.getPartName();
        SCAObject target = parent.getSystemChild(targetComponentName);
        if (target == null) {
            throw new MissingWireTargetException(targetName.toString());
        } else if (!(target instanceof SystemAtomicComponent)) {
            throw new InvalidTargetTypeException("Target must be a system component",
                definition.getName(),
                null,
                targetName.getPartName(),
                null);
        }
        SystemAtomicComponent atomicComponent = (SystemAtomicComponent) target;
        Class<?> interfaze = definition.getServiceContract().getInterfaceClass();
        String name = definition.getName();
        InboundWire inboundWire = new SystemInboundWireImpl(name, interfaze, atomicComponent);
        SystemOutboundWire outboundWire = new SystemOutboundWireImpl(name, targetName, interfaze);
        ServiceContract<?> contract = definition.getServiceContract();
        SystemService service = new SystemServiceImpl(definition.getName(), parent, contract);
        service.setInboundWire(inboundWire);
        service.setOutboundWire(outboundWire);
        return service;
    }

    public SystemReference build(CompositeComponent parent,
                                 BoundReferenceDefinition<SystemBinding> definition,
                                 DeploymentContext deploymentContext) {
        CompositeComponent autowireComponent = parent.getParent();
        Class<?> interfaze = definition.getServiceContract().getInterfaceClass();
        String name = definition.getName();
        SystemReferenceImpl reference = new SystemReferenceImpl(name, interfaze, parent);
        SystemInboundWire inboundWire = new SystemInboundWireImpl(name, interfaze);
        String refName = definition.getName();
        boolean required = definition.isRequired();
        SystemOutboundWire outboundWire = new SystemOutboundAutowire(refName, interfaze, autowireComponent, required);
        reference.setInboundWire(inboundWire);
        reference.setOutboundWire(outboundWire);
        return reference;
    }

    @Override
    protected Class<SystemBinding> getBindingType() {
        return SystemBinding.class;
    }
}
