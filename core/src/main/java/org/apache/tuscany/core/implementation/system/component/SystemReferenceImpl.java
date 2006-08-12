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

import java.lang.reflect.Method;

import org.apache.tuscany.spi.component.AbstractSCAObject;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireInvocationHandler;

import org.apache.tuscany.core.implementation.system.wire.SystemInboundWire;
import org.apache.tuscany.core.implementation.system.wire.SystemOutboundWire;

/**
 * Default implementation of a reference configured with the
 * {@link org.apache.tuscany.core.implementation.system.model.SystemBinding}
 *
 * @version $Rev$ $Date$
 */
public class SystemReferenceImpl<T> extends AbstractSCAObject<T> implements SystemReference<T> {

    protected SystemInboundWire<T> inboundWire;
    protected SystemOutboundWire<T> outboundWire;
    protected Class<T> referenceInterface;


    public SystemReferenceImpl(String name, Class<T> referenceInterface, CompositeComponent parent) {
        super(name, parent);
        assert referenceInterface != null : "Reference interface was null";
        this.referenceInterface = referenceInterface;
    }

    public Scope getScope() {
        return Scope.COMPOSITE;
    }

    public void setInboundWire(InboundWire<T> wire) {
        assert wire instanceof SystemInboundWire : "Wire must be a " + SystemInboundWire.class.getName();
        this.inboundWire = (SystemInboundWire<T>) wire;
    }

    public InboundWire<T> getInboundWire() {
        return inboundWire;
    }

    public OutboundWire<T> getOutboundWire() {
        return outboundWire;
    }

    public void setOutboundWire(OutboundWire<T> wire) {
        assert wire instanceof SystemOutboundWire : "Wire must be a " + SystemOutboundWire.class.getName();
        this.outboundWire = (SystemOutboundWire<T>) wire;
    }

    public Class<T> getInterface() {
        return referenceInterface;
    }

    public void setInterface(Class<T> referenceInterface) {
        this.referenceInterface = referenceInterface;
    }

    public T getServiceInstance() throws TargetException {
        return referenceInterface.cast(inboundWire.getTargetService());
    }

    public WireInvocationHandler getHandler() throws TargetException {
        throw new UnsupportedOperationException();
    }

    public TargetInvoker createTargetInvoker(Method operation) {
        throw new UnsupportedOperationException();
    }

    public TargetInvoker createAsyncTargetInvoker(Method operation, OutboundWire wire) {
        throw new UnsupportedOperationException();
    }

}
