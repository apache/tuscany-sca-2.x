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

import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.component.AbstractSCAObject;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireInvocationHandler;
import org.apache.tuscany.spi.wire.WireService;

/**
 * The default implementation of an SCA service
 *
 * @version $Rev$ $Date$
 */
public class ServiceExtension extends AbstractSCAObject implements Service {
    protected Class<?> interfaze;
    protected InboundWire inboundWire;
    protected OutboundWire outboundWire;
    protected WireService wireService;
    protected ServiceContract<?> bindingServiceContract;

    public ServiceExtension(String name, Class<?> interfaze, CompositeComponent parent, WireService wireService)
        throws CoreRuntimeException {
        super(name, parent);
        this.interfaze = interfaze;
        this.wireService = wireService;
    }

    public Scope getScope() {
        return Scope.SYSTEM;
    }

    public InboundWire getInboundWire() {
        return inboundWire;
    }

    public void setInboundWire(InboundWire wire) {
        inboundWire = wire;
    }

    public OutboundWire getOutboundWire() {
        return outboundWire;
    }

    public void setOutboundWire(OutboundWire outboundWire) {
        this.outboundWire = outboundWire;
    }

    public WireInvocationHandler getHandler() {
        return wireService.createHandler(inboundWire);
    }

    public Class<?> getInterface() {
        return interfaze;
    }

    public ServiceContract<?> getBindingServiceContract() {
        return bindingServiceContract;
    }

    public void setBindingServiceContract(ServiceContract<?> serviceContract) {
        this.bindingServiceContract = serviceContract;
    }

    public TargetInvoker createCallbackTargetInvoker(ServiceContract contract, Operation operation)
        throws TargetInvokerCreationException {
        throw new UnsupportedOperationException();
    }

    public Object getServiceInstance() throws TargetResolutionException {
        return wireService.createProxy(inboundWire);
    }

}
