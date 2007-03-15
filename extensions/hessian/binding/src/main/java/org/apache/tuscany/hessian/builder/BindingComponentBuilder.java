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
package org.apache.tuscany.hessian.builder;

import java.net.URI;

import org.osoa.sca.annotations.Reference;

import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.builder.physical.PhysicalComponentBuilder;
import org.apache.tuscany.spi.builder.physical.WireAttacher;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.wire.Wire;

import org.apache.tuscany.hessian.DestinationCreationException;
import org.apache.tuscany.hessian.HessianService;
import org.apache.tuscany.hessian.InvalidDestinationException;
import org.apache.tuscany.hessian.component.BindingComponent;
import org.apache.tuscany.hessian.model.HessianBindingComponentDefinition;
import org.apache.tuscany.hessian.model.HessianWireSourceDefinition;
import org.apache.tuscany.hessian.model.HessianWireTargetDefinition;

/**
 * @version $Rev$ $Date$
 */
public class BindingComponentBuilder
    implements PhysicalComponentBuilder<HessianBindingComponentDefinition, BindingComponent>,
    WireAttacher<BindingComponent, HessianWireSourceDefinition, HessianWireTargetDefinition> {

    private HessianService hessianService;

    public BindingComponentBuilder(@Reference HessianService hessianService) {
        this.hessianService = hessianService;
    }

    public BindingComponent build(HessianBindingComponentDefinition definition) throws BuilderException {
        return new BindingComponent(definition.getComponentId(), hessianService);
    }

    public void attach(BindingComponent source, Component target, Wire wire, HessianWireSourceDefinition definition)
        throws DestinationCreationException {
        URI endpointUri = definition.getEndpointUri();
        hessianService.createDestination(endpointUri, wire, null); // FIXME classloader

    }

    public void attach(BindingComponent component, Wire wire, HessianWireTargetDefinition definition)
        throws InvalidDestinationException {
        URI endpointUri = definition.getEndpointUri();
        component.bindToEndpoint(endpointUri, wire);
    }
}
