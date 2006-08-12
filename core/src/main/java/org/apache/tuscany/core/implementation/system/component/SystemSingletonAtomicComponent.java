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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.component.AbstractSCAObject;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * An {@link org.apache.tuscany.spi.component.AtomicComponent} used when registering objects directly into a composite
 *
 * @version $$Rev$$ $$Date$$
 */
public class SystemSingletonAtomicComponent<S, T extends S> extends AbstractSCAObject<S>
    implements SystemAtomicComponent<S> {

    private T instance;
    private List<Class<?>> serviceInterfaces;

    public SystemSingletonAtomicComponent(String name, CompositeComponent<?> parent, Class<S> interfaze, T instance) {
        super(name, parent);
        this.instance = instance;
        serviceInterfaces = new ArrayList<Class<?>>(1);
        serviceInterfaces.add(interfaze);
    }

    public List<Class<?>> getServiceInterfaces() {
        return serviceInterfaces;
    }

    public Scope getScope() {
        return Scope.MODULE;
    }

    public boolean isEagerInit() {
        return false;
    }

    public int getInitLevel() {
        return 0;
    }

    public T getTargetInstance() throws TargetException {
        return instance;
    }

    public Object getServiceInstance(String name) throws TargetException {
        return getTargetInstance();
    }

    public S getServiceInstance() throws TargetException {
        return getTargetInstance();
    }

    public void init(Object instance) throws TargetException {

    }

    public void destroy(Object instance) throws TargetException {

    }

    public Object createInstance() throws ObjectCreationException {
        throw new UnsupportedOperationException();
    }

    public void addInboundWire(InboundWire wire) {
        throw new UnsupportedOperationException();
    }

    public Map<String, InboundWire> getInboundWires() {
        return Collections.emptyMap();
    }

    public InboundWire getInboundWire(String serviceName) {
        return null;
    }

    public void addOutboundWire(OutboundWire wire) {
        throw new UnsupportedOperationException();
    }

    public void addOutboundWires(Class<?> multiplicityClass, List<OutboundWire> wires) {
        throw new UnsupportedOperationException();
    }

    public Map<String, List<OutboundWire>> getOutboundWires() {
        return Collections.emptyMap();
    }


    public TargetInvoker createTargetInvoker(String serviceName, Method operation) {
        return null;
    }

    public TargetInvoker createAsyncTargetInvoker(String serviceName, Method operation, OutboundWire wire) {
        return null;
    }
}
