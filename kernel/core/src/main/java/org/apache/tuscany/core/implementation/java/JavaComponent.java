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
package org.apache.tuscany.core.implementation.java;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.core.component.InstanceFactory;
import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.RegistrationException;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.event.EventFilter;
import org.apache.tuscany.spi.event.RuntimeEventListener;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.PropertyValue;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.Wire;
import org.osoa.sca.ComponentContext;

/**
 * 
 * @version $Revision$ $Date$
 *
 */
public class JavaComponent implements Component {
    
    // Instance factory class
    private Class<InstanceFactory<?>> instanceFactoryClass;
    
    // Scope container
    private ScopeContainer scopeContainer;

    /**
     * Injects the instance factory class.
     * @param instanceFactoryClass Instance factory class.
     */
    public void setInstanceFactoryClass(Class<InstanceFactory<?>> instanceFactoryClass) {
        this.instanceFactoryClass = instanceFactoryClass;
    }

    /**
     * Injects the scope container.
     * @param scopeContainer Scope container.
     */
    public void setScopeContainer(ScopeContainer scopeContainer) {
        throw new UnsupportedOperationException();
    }
    
    

    public void attachCallbackWire(Wire wire) {
        throw new UnsupportedOperationException();
    }

    public void attachWire(Wire wire) {
        throw new UnsupportedOperationException();
    }

    public void attachWires(List<Wire> wires) {
        throw new UnsupportedOperationException();
    }

    public ComponentContext getComponentContext() {
        throw new UnsupportedOperationException();
    }

    public Map<String, PropertyValue<?>> getDefaultPropertyValues() {
        throw new UnsupportedOperationException();
    }

    public Reference getReference(String name) {
        throw new UnsupportedOperationException();
    }

    public Scope getScope() {
        throw new UnsupportedOperationException();
    }

    public Service getService(String name) {
        throw new UnsupportedOperationException();
    }

    public List<Wire> getWires(String name) {
        throw new UnsupportedOperationException();
    }

    public boolean isOptimizable() {
        throw new UnsupportedOperationException();
    }

    public void register(Service service) throws RegistrationException {
        throw new UnsupportedOperationException();
    }

    public void register(Reference reference) throws RegistrationException {
        throw new UnsupportedOperationException();
    }

    public void setDefaultPropertyValues(Map<String, PropertyValue<?>> defaultPropertyValues) {
        throw new UnsupportedOperationException();
    }

    public TargetInvoker createTargetInvoker(String targetName, Operation operation)
        throws TargetInvokerCreationException {
        throw new UnsupportedOperationException();
    }

    public URI getUri() {
        throw new UnsupportedOperationException();
    }

    public void addListener(RuntimeEventListener listener) {
        throw new UnsupportedOperationException();
    }

    public void addListener(EventFilter filter, RuntimeEventListener listener) {
        throw new UnsupportedOperationException();
    }

    public void publish(Event object) {
        throw new UnsupportedOperationException();
    }

    public void removeListener(RuntimeEventListener listener) {
        throw new UnsupportedOperationException();
    }

    public int getLifecycleState() {
        throw new UnsupportedOperationException();
    }

    public void start() throws CoreRuntimeException {
        throw new UnsupportedOperationException();
    }

    public void stop() throws CoreRuntimeException {
        throw new UnsupportedOperationException();
    }

}
