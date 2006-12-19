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

import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.component.AbstractSCAObject;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireInvocationHandler;

import org.apache.tuscany.core.implementation.system.wire.SystemInboundWire;
import org.apache.tuscany.core.implementation.system.wire.SystemOutboundWire;

/**
 * Default implementation for services configured with the
 * {@link org.apache.tuscany.core.implementation.system.model.SystemBinding}
 *
 * @version $$Rev$$ $$Date$$
 */
public class SystemServiceImpl extends AbstractSCAObject implements SystemService {

    protected SystemInboundWire inboundWire;
    protected SystemOutboundWire outboundWire;
    protected ServiceContract<?> serviceContract;

    public SystemServiceImpl(String name, CompositeComponent parent, ServiceContract<?> serviceContract)
        throws CoreRuntimeException {
        super(name, parent);
        this.serviceContract = serviceContract;
    }

    public Scope getScope() {
        return Scope.SYSTEM;
    }

    public InboundWire getInboundWire() {
        return inboundWire;
    }

    public void setInboundWire(InboundWire wire) {
        assert wire instanceof SystemInboundWire : "wire must be a " + SystemInboundWire.class.getName();
        this.inboundWire = (SystemInboundWire) wire;
    }

    public OutboundWire getOutboundWire() {
        return outboundWire;
    }

    public void setOutboundWire(OutboundWire wire) {
        assert wire instanceof SystemOutboundWire : "wire must be a " + SystemOutboundWire.class.getName();
        this.outboundWire = (SystemOutboundWire) wire;
    }

    public Class<?> getInterface() {
        return inboundWire.getServiceContract().getInterfaceClass();
    }

    public WireInvocationHandler getHandler() {
        // system services do not proxy
        throw new UnsupportedOperationException();
    }

    public Object getServiceInstance() throws TargetResolutionException {
        return inboundWire.getTargetService();
    }


    public TargetInvoker createCallbackTargetInvoker(ServiceContract contract, Operation operation) {
        throw new UnsupportedOperationException();
    }

    public ServiceContract<?> getBindingServiceContract() {
        return serviceContract;
    }

    public void setBindingServiceContract(ServiceContract<?> serviceContract) {
        throw new UnsupportedOperationException();
    }

    public boolean isSystem() {
        return true;
    }
}
