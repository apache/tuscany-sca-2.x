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
package org.apache.tuscany.core.implementation.system.component;

import javax.xml.namespace.QName;

import org.apache.tuscany.spi.component.AbstractSCAObject;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.Wire;

/**
 * Default implementation of a reference configured with the system binding
 *
 * @version $Rev$ $Date$
 */
public class SystemReferenceBinding extends AbstractSCAObject implements ReferenceBinding {
    protected Reference reference;
    protected InboundWire inboundWire;
    protected OutboundWire outboundWire;

    public SystemReferenceBinding(String name, CompositeComponent parent) {
        super(name, parent);
    }

    public Scope getScope() {
        return Scope.SYSTEM;
    }

    public void setInboundWire(InboundWire wire) {
        this.inboundWire = wire;
    }

    public QName getBindingType() {
        return Wire.LOCAL_BINDING;
    }

    public void setReference(Reference reference) {
        this.reference = reference;
    }

    public InboundWire getInboundWire() {
        return inboundWire;
    }

    public OutboundWire getOutboundWire() {
        return outboundWire;
    }

    public void setOutboundWire(OutboundWire wire) {
        this.outboundWire = wire;
    }

    public TargetInvoker createTargetInvoker(ServiceContract contract, Operation operation) {
        throw new UnsupportedOperationException();
    }

    public TargetInvoker createAsyncTargetInvoker(OutboundWire wire, Operation operation) {
        throw new UnsupportedOperationException();
    }

    public ServiceContract<?> getBindingServiceContract() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSystem() {
        return reference != null && reference.isSystem();
    }

}
