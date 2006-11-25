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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.component.AbstractSCAObject;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.services.work.WorkScheduler;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.WireService;

/**
 * An extension point for atomic component type, which new implementation types may extend
 *
 * @version $$Rev$$ $$Date$$
 */
public abstract class AtomicComponentExtension extends AbstractSCAObject implements AtomicComponent {

    protected ScopeContainer scopeContainer;
    protected Scope scope;
    protected Map<String, InboundWire> serviceWires = new HashMap<String, InboundWire>();
    protected Map<String, List<OutboundWire>> referenceWires = new HashMap<String, List<OutboundWire>>();
    protected WireService wireService;
    protected WorkContext workContext;
    protected WorkScheduler workScheduler;
    protected ExecutionMonitor monitor;
    private final int initLevel;


    protected AtomicComponentExtension(String name,
                                       CompositeComponent parent,
                                       ScopeContainer scopeContainer,
                                       WireService wireService,
                                       WorkContext workContext,
                                       WorkScheduler workScheduler,
                                       ExecutionMonitor monitor,
                                       int initLevel) {
        super(name, parent);
        this.scopeContainer = scopeContainer;
        this.wireService = wireService;
        this.workContext = workContext;
        this.workScheduler = workScheduler;
        this.monitor = monitor;
        this.initLevel = initLevel;
    }

    public Scope getScope() {
        return scope;
    }

    public int getInitLevel() {
        return initLevel;
    }

    public boolean isEagerInit() {
        return initLevel > 0;
    }

    public void start() throws CoreRuntimeException {
        super.start();
        scopeContainer.register(this);
    }

    public void init(Object instance) throws TargetException {

    }

    public void destroy(Object instance) throws TargetException {

    }

    public void addInboundWire(InboundWire wire) {
        serviceWires.put(wire.getServiceName(), wire);
        onServiceWire(wire);
    }

    public InboundWire getInboundWire(String serviceName) {
        if (serviceName == null) {
            if (serviceWires.size() < 1) {
                return null;
            }
            return serviceWires.values().iterator().next();
        } else {
            return serviceWires.get(serviceName);
        }
    }

    public Map<String, InboundWire> getInboundWires() {
        return Collections.unmodifiableMap(serviceWires);
    }

    public void addOutboundWire(OutboundWire wire) {
        List<OutboundWire> list = new ArrayList<OutboundWire>();
        list.add(wire);
        referenceWires.put(wire.getReferenceName(), list);
        onReferenceWire(wire);
    }

    public Map<String, List<OutboundWire>> getOutboundWires() {
        return Collections.unmodifiableMap(referenceWires);
    }

    public void addOutboundWires(Class<?> multiplicityClass, List<OutboundWire> wires) {
        assert wires != null && wires.size() > 0;
        referenceWires.put(wires.get(0).getReferenceName(), wires);
        onReferenceWires(multiplicityClass, wires);
    }

    public void destroyInstance() {
        scopeContainer.remove(this);
    }

    protected void onReferenceWire(OutboundWire wire) {
    }

    protected void onReferenceWires(Class<?> multiplicityClass, List<OutboundWire> wires) {
    }

    protected void onServiceWire(InboundWire wire) {
    }


}
