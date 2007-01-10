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
package org.apache.tuscany.spi.extension;

import org.apache.tuscany.spi.component.AbstractSCAObject;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;

/**
 * The default implementation of an SCA reference
 *
 * @version $Rev$ $Date$
 */
public abstract class ReferenceBindingExtension extends AbstractSCAObject implements ReferenceBinding {
    protected Reference reference;
    protected InboundWire inboundWire;
    protected OutboundWire outboundWire;
    protected ServiceContract<?> bindingServiceContract;

    protected ReferenceBindingExtension(String name, CompositeComponent parent) {
        super(name, parent);
    }

    public Scope getScope() {
        return Scope.SYSTEM;
    }

    public void setReference(Reference reference) {
        this.reference = reference;
    }

    public void setInboundWire(InboundWire wire) {
        this.inboundWire = wire;
    }

    public InboundWire getInboundWire() {
        return inboundWire;
    }

    public OutboundWire getOutboundWire() {
        return outboundWire;
    }

    public void setOutboundWire(OutboundWire outboundWire) {
        this.outboundWire = outboundWire;
    }

    public ServiceContract<?> getBindingServiceContract() {
        return bindingServiceContract;
    }

    @Override
    public boolean isSystem() {
        return reference != null && reference.isSystem();
    }

}
