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
package org.apache.tuscany.implementation.java.context;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.assembly.Property;
import org.apache.tuscany.implementation.java.impl.JavaElement;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.Scope;
import org.apache.tuscany.spi.component.AbstractSCAObject;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.ComponentException;
import org.apache.tuscany.spi.component.InstanceWrapper;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.RegistrationException;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.Wire;
import org.osoa.sca.ComponentContext;

/**
 * Base class for Component implementations based on Java objects.
 *
 * @version $Rev$ $Date$
 * @param <T> the implementation class
 */
public abstract class PojoComponent<T> extends AbstractSCAObject implements AtomicComponent<T> {
    private final InstanceFactoryProvider<T> provider;
    private final ScopeContainer<?> scopeContainer;
    private final URI groupId;
    private final int initLevel;
    private final long maxIdleTime;
    private final long maxAge;
    private InstanceFactory<T> instanceFactory;

    public PojoComponent(URI componentId,
                         InstanceFactoryProvider<T> provider,
                         ScopeContainer<?> scopeContainer,
                         URI groupId,
                         int initLevel,
                         long maxIdleTime,
                         long maxAge) {
        super(componentId);
        this.provider = provider;
        this.scopeContainer = scopeContainer;
        this.groupId = groupId;
        this.initLevel = initLevel;
        this.maxIdleTime = maxIdleTime;
        this.maxAge = maxAge;
    }

    public boolean isEagerInit() {
        return initLevel > 0;
    }

    public int getInitLevel() {
        return initLevel;
    }

    public long getMaxIdleTime() {
        return maxIdleTime;
    }

    public long getMaxAge() {
        return maxAge;
    }

    public void attachWire(Wire wire) {
    }

    public void attachWires(List<Wire> wires) {
    }

    public void attachCallbackWire(Wire wire) {
    }

    public void start() {
        super.start();
        scopeContainer.register(this, groupId);
        instanceFactory = provider.createFactory();
    }

    public void stop() {
        instanceFactory = null;
        scopeContainer.unregister(this);
        super.stop();
    }

    public InstanceWrapper<T> createInstanceWrapper() {
        return instanceFactory.newInstance();
    }

    public ObjectFactory<T> createObjectFactory() {
        return new ComponentObjectFactory(this, scopeContainer);
    }

    public ComponentContext getComponentContext() {
        return null;
    }

    public List<Wire> getWires(String name) {
        return null;
    }

    public Map<String, Property> getDefaultPropertyValues() {
        return null;
    }

    public void setDefaultPropertyValues(Map<String, Property> defaultPropertyValues) {
    }

    public ScopeContainer getScopeContainer() {
        return scopeContainer;
    }

    public Class<T> getImplementationClass() {
        return provider.getImplementationClass();
    }

    public void setObjectFactory(JavaElement name, ObjectFactory<?> objectFactory) {
        provider.setObjectFactory(name, objectFactory);
    }

    public Class<?> getMemberType(JavaElement injectionSite) {
        return injectionSite.getType();
    }

    @Deprecated
    public Object createInstance() throws ObjectCreationException {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public void removeInstance() throws ComponentException {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public Object getTargetInstance() throws TargetResolutionException {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public Scope getScope() {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public void setScopeContainer(ScopeContainer scopeContainer) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public boolean isOptimizable() {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public void register(Service service) throws RegistrationException {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public void register(Reference reference) throws RegistrationException {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public Service getService(String name) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public Reference getReference(String name) {
        throw new UnsupportedOperationException();
    }

    public TargetInvoker createTargetInvoker(String targetName, Operation operation, boolean isCallback)
        throws TargetInvokerCreationException {
        throw new UnsupportedOperationException();
    }

    public void configureProperty(String propertyName) {
    }
}
