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
import org.apache.tuscany.spi.component.TargetException;
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
public class ServiceExtension<T> extends AbstractSCAObject<T> implements Service<T> {

    protected Class<T> interfaze;
    protected InboundWire<T> inboundWire;
    protected OutboundWire<T> outboundWire;
    protected WireService wireService;

    public ServiceExtension(String name, Class<T> interfaze, CompositeComponent parent, WireService wireService)
        throws CoreRuntimeException {
        super(name, parent);
        this.interfaze = interfaze;
        this.wireService = wireService;
    }

    public Scope getScope() {
        return Scope.COMPOSITE;
    }

    public InboundWire<T> getInboundWire() {
        return inboundWire;
    }

    public void setInboundWire(InboundWire<T> wire) {
        inboundWire = wire;
    }

    public OutboundWire<T> getOutboundWire() {
        return outboundWire;
    }

    public void setOutboundWire(OutboundWire<T> outboundWire) {
        this.outboundWire = outboundWire;
    }

    public TargetInvoker createCallbackTargetInvoker(ServiceContract contract, Operation operation) {
        throw new UnsupportedOperationException();        
    }
    
    public T getServiceInstance() throws TargetException {
        return wireService.createProxy(inboundWire);
    }

    public WireInvocationHandler getHandler() {
        return wireService.createHandler(inboundWire);
    }

    public Class<T> getInterface() {
        return interfaze;
    }

}
