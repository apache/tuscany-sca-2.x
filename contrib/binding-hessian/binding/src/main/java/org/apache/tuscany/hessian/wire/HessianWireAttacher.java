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
package org.apache.tuscany.hessian.wire;

import java.net.URI;

import org.apache.tuscany.hessian.DestinationCreationException;
import org.apache.tuscany.hessian.InvalidDestinationException;
import org.apache.tuscany.hessian.component.HessianBindingComponent;
import org.apache.tuscany.hessian.model.physical.HessianWireSourceDefinition;
import org.apache.tuscany.hessian.model.physical.HessianWireTargetDefinition;
import org.apache.tuscany.spi.builder.physical.WireAttacher;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.wire.Wire;
import org.osoa.sca.annotations.Reference;

/**
 * @version $Rev$ $Date$
 */
public class HessianWireAttacher<C extends Component> implements
    WireAttacher<C, HessianWireSourceDefinition, HessianWireTargetDefinition> {

    private HessianBindingComponent bindingComponent;

    public HessianWireAttacher(@Reference(name="bindingComponent") HessianBindingComponent bindingComponent) {
        this.bindingComponent = bindingComponent;
    }

    public void attach(C source, Component target, Wire wire, HessianWireSourceDefinition definition)
        throws DestinationCreationException {
        URI endpointUri = definition.getEndpointUri();
        // FIXME classloader
        bindingComponent.createEndpoint(endpointUri, wire, null); 

    }

    public void attach(C component, Wire wire, HessianWireTargetDefinition definition)
        throws InvalidDestinationException {
        URI endpointUri = definition.getEndpointUri();
        bindingComponent.bindToEndpoint(endpointUri, wire);
    }
}
