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
package org.apache.tuscany.core.component;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.assembly.ComponentService;
import org.apache.tuscany.core.component.scope.InstanceWrapperBase;
import org.apache.tuscany.core.injection.SingletonObjectFactory;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.Scope;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.InstanceWrapper;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.extension.AbstractComponentExtension;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.Wire;

/**
 * An {@link org.apache.tuscany.spi.component.AtomicComponent} used when
 * registering objects directly into a composite
 * 
 * @version $$Rev$$ $$Date: 2007-04-03 10:40:40 -0700 (Tue, 03 Apr
 *          2007) $$
 */
public class SingletonAtomicComponent<T> extends AbstractComponentExtension implements
    AtomicComponent<T> {
    private T instance;
    private List<ComponentService> contracts = new ArrayList<ComponentService>();

    public SingletonAtomicComponent(URI name, ComponentService contract, T instance) {
        super(name);
        this.instance = instance;
        this.contracts.add(contract);
    }

    public SingletonAtomicComponent(URI name, List<ComponentService> services, T instance) {
        super(name);
        this.instance = instance;
        for (ComponentService contract : services) {
            contracts.add(contract);
        }
    }

    public Scope getScope() {
        return Scope.COMPOSITE;
    }

    public boolean isEagerInit() {
        return false;
    }

    public int getInitLevel() {
        return 0;
    }

    public long getMaxIdleTime() {
        return -1;
    }

    public long getMaxAge() {
        return -1;
    }

    public T getTargetInstance() throws TargetResolutionException {
        return instance;
    }

    public Object createInstance() throws ObjectCreationException {
        throw new UnsupportedOperationException();
    }

    public void removeInstance() {
        throw new UnsupportedOperationException();
    }

    public InstanceWrapper<T> createInstanceWrapper() throws ObjectCreationException {
        return new InstanceWrapperBase<T>(instance);
    }

    public ObjectFactory<T> createObjectFactory() {
        return new SingletonObjectFactory<T>(instance);
    }

    public boolean isOptimizable() {
        return true;
    }

    public void attachWire(Wire wire) {
        throw new UnsupportedOperationException();
    }

    public void attachWires(List<Wire> wires) {
        throw new UnsupportedOperationException();
    }

    public List<Wire> getWires(String name) {
        throw new UnsupportedOperationException();
    }

    public void attachCallbackWire(Wire wire) {
        throw new UnsupportedOperationException();
    }
    
    public void configureProperty(String propertyName) {
        throw new UnsupportedOperationException();
    }

    public TargetInvoker createTargetInvoker(String targetName, Operation operation, boolean isCallback) {
        return null;
    }

    public List<ComponentService> getContracts() {
        return contracts;
    }

}
