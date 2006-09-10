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
 * The default implementation of an SCA reference
 *
 * @version $Rev$ $Date$
 */
public abstract class ReferenceExtension extends AbstractSCAObject implements Reference {

    protected InboundWire inboundWire;
    protected OutboundWire outboundWire;
    protected Class<?> referenceInterface;
    protected WireService wireService;

    protected ReferenceExtension(String name,
                                 Class<?> referenceInterface,
                                 CompositeComponent parent,
                                 WireService wireService) {
        super(name, parent);
        this.referenceInterface = referenceInterface;
        this.wireService = wireService;
    }

    public Scope getScope() {
        return Scope.COMPOSITE;
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

    public Class<?> getInterface() {
        return referenceInterface;
    }

    public Object getServiceInstance() throws TargetException {
        return wireService.createProxy(inboundWire);
    }

    public WireInvocationHandler getHandler() throws TargetException {
        return wireService.createHandler(inboundWire);
    }

    public TargetInvoker createCallbackTargetInvoker(ServiceContract contract, Operation operation) {
        throw new UnsupportedOperationException();
    }

    public TargetInvoker createAsyncTargetInvoker(OutboundWire wire, Operation operation) {
        throw new UnsupportedOperationException();
    }

}
