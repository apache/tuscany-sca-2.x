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
package org.apache.tuscany.core.implementation.system.wire;

import java.util.Collections;
import java.util.Map;

import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;

/**
 * Default implementation of an inbound system wire
 *
 * @version $$Rev$$ $$Date$$
 */
public class SystemInboundWireImpl implements SystemInboundWire {
    private String serviceName;
    private ServiceContract serviceContract;
    private Component component;
    private SystemOutboundWire wire;
    private SCAObject container;
    private Class<?> interfaze;

    /**
     * Constructs a new inbound wire
     *
     * @param serviceName the name of the service the inbound wire represents
     * @param interfaze   the service interface
     * @param target      the target context the wire is connected to
     */
    public SystemInboundWireImpl(String serviceName, Class<?> interfaze, Component target) {
        this.serviceName = serviceName;
        this.component = target;
        serviceContract = new JavaServiceContract(interfaze);
        this.interfaze = interfaze;
    }

    public SystemInboundWireImpl(String serviceName, Class<?> interfaze) {
        this(serviceName, interfaze, null);
    }

    public ServiceContract getServiceContract() {
        return serviceContract;
    }

    public void setServiceContract(ServiceContract serviceContract) {
        this.serviceContract = serviceContract;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        throw new UnsupportedOperationException();
    }

    public Object getTargetService() throws TargetResolutionException {
        if (wire != null) {
            return interfaze.cast(wire.getTargetService());
        }
        return interfaze.cast(component.getServiceInstance(serviceName));
    }

    public Class[] getImplementedInterfaces() {
        return new Class[0];
    }

    public Map<Operation<?>, InboundInvocationChain> getInvocationChains() {
        return Collections.emptyMap();
    }

    public void addInvocationChain(Operation<?> operation, InboundInvocationChain chain) {
        throw new UnsupportedOperationException();
    }

    public void addInvocationChains(Map chains) {
        throw new UnsupportedOperationException();
    }

    public Map<Operation<?>, OutboundInvocationChain> getSourceCallbackInvocationChains(Object targetAddr) {
        throw new UnsupportedOperationException();
    }

    public void addSourceCallbackInvocationChains(Object targetAddr,
                                                  Map<Operation<?>,
                                                      OutboundInvocationChain> chains) {
        throw new UnsupportedOperationException();
    }

    public void addSourceCallbackInvocationChain(Object targetAddr, Operation operation,
                                                 OutboundInvocationChain chain) {
        throw new UnsupportedOperationException();
    }

    public void addInterface(Class claz) {
        throw new UnsupportedOperationException();
    }

    public String getCallbackReferenceName() {
        return null;
    }

    public void setCallbackReferenceName(String callbackReferenceName) {
        throw new UnsupportedOperationException();
    }

    public boolean isOptimizable() {
        return true;  // system wires are always optimizable
    }

    public void setTargetWire(OutboundWire wire) {
        assert wire instanceof SystemOutboundWire : "wire must be a " + SystemOutboundWireImpl.class.getName();
        this.wire = (SystemOutboundWire) wire;
    }

    public SCAObject getContainer() {
        return container;
    }

    public void setContainer(SCAObject container) {
        this.container = container;
    }
}
