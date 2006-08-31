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
package org.apache.tuscany.container.groovy;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.extension.AtomicComponentExtension;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;

import groovy.lang.GroovyObject;

/**
 * The Groovy atomic component implementation. Groovy implementations may be "scripts" or classes.
 */
public class GroovyAtomicComponent<T> extends AtomicComponentExtension<T> {
    private final Class<? extends GroovyObject> groovyClass;
    private final List<Class<?>> services;
    //FIXME properties should move up to AtomicComponentExtension
    private final Map<String, ObjectFactory> properties;
    private AsyncMonitor monitor;

    public GroovyAtomicComponent(GroovyConfiguration configuration, AsyncMonitor monitor) {
        super(configuration.getName(),
            configuration.getParent(),
            configuration.getScopeContainer(),
            configuration.getWireService(),
            configuration.getWorkContext(),
            null, configuration.getInitLevel());

        this.groovyClass = configuration.getGroovyClass();
        this.services = Collections.unmodifiableList(configuration.getServices());
        this.properties = new HashMap<String, ObjectFactory>();
        assert groovyClass != null;
        this.monitor = monitor;
    }

    public List<Class<?>> getServiceInterfaces() {
        return services;
    }

    public TargetInvoker createTargetInvoker(String targetName, Operation operation) {
        return new GroovyInvoker(operation.getName(), this);
    }

    public TargetInvoker createAsyncTargetInvoker(OutboundWire wire, Operation operation) {
        return new AsyncGroovyInvoker(operation.getName(), wire, this, workScheduler, monitor, workContext);
    }

    public Object createInstance() throws ObjectCreationException {
        GroovyObject instance;
        try {
            instance = groovyClass.newInstance();
        } catch (IllegalAccessException e) {
            throw new ObjectCreationException(e);
        } catch (InstantiationException e) {
            throw new ObjectCreationException(e);
        }

        // inject properties
        for (Map.Entry<String, ObjectFactory> property : properties.entrySet()) {
            instance.setProperty(property.getKey(), property.getValue().getInstance());
        }

        // inject references
        for (List<OutboundWire> referenceWires : getOutboundWires().values()) {
            for (OutboundWire<?> wire : referenceWires) {
                instance.setProperty(wire.getReferenceName(), wireService.createProxy(wire));
            }
        }
        return instance;
    }

    public GroovyObject getTargetInstance() throws TargetException {
        return (GroovyObject) scopeContainer.getInstance(this);
    }

    @SuppressWarnings("unchecked")
    public T getServiceInstance() throws TargetException {
        //TODO this should return a default service from a wire
        return (T) scopeContainer.getInstance(this);
    }

    public Object getServiceInstance(String service) throws TargetException {
        InboundWire<?> wire = getInboundWire(service);
        if (wire == null) {
            TargetException e = new TargetException("ServiceDefinition not found"); // TODO better error message
            e.setIdentifier(service);
            throw e;
        }
        return wireService.createProxy(wire);
    }

    public void addPropertyFactory(String name, ObjectFactory<?> factory) {
        properties.put(name, factory);
    }
}
