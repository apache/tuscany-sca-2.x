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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.extension.AtomicComponentExtension;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;

import groovy.lang.GroovyObject;

/**
 * The Groovy atomic component implementation. Groovy implementations may be "scripts" or classes.
 *
 * @version $Rev$ $Date$
 */
public class GroovyAtomicComponent extends AtomicComponentExtension {
    private final Class<? extends GroovyObject> groovyClass;
    //FIXME properties should move up to AtomicComponentExtension
    private final Map<String, ObjectFactory> properties;

    public GroovyAtomicComponent(GroovyConfiguration configuration) {
        super(configuration.getName(),
            configuration.getParent(),
            configuration.getWireService(),
            configuration.getWorkContext(),
            null,
            configuration.getMonitor(),
            configuration.getInitLevel());

        this.groovyClass = configuration.getGroovyClass();
        this.properties = new HashMap<String, ObjectFactory>();
        assert groovyClass != null;
    }

    public TargetInvoker createTargetInvoker(String targetName, Operation operation, InboundWire callbackWire) {
        return new GroovyInvoker(operation.getName(), this, callbackWire, workContext, monitor);
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
            for (OutboundWire wire : referenceWires) {
                Class<?> clazz = wire.getServiceContract().getInterfaceClass();
                instance.setProperty(wire.getReferenceName(), wireService.createProxy(clazz, wire));
            }
        }
        return instance;
    }

    public GroovyObject getTargetInstance() throws TargetResolutionException {
        return (GroovyObject) scopeContainer.getInstance(this);
    }

    public void addPropertyFactory(String name, ObjectFactory<?> factory) {
        properties.put(name, factory);
    }
}
