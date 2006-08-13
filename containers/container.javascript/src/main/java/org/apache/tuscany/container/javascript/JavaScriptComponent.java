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
package org.apache.tuscany.container.javascript;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.container.javascript.rhino.RhinoScript;
import org.apache.tuscany.container.javascript.rhino.RhinoScriptInstance;
import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.extension.AtomicComponentExtension;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireService;

/**
 * The JavaScript component implementation.
 */
public class JavaScriptComponent<T> extends AtomicComponentExtension<T> {

    private final List<Class<?>> services;

    private final Map<String, Object> properties;

    private RhinoScript rhinoScript;

    public JavaScriptComponent(String name, RhinoScript rhinoScript, List<Class<?>> services, Map<String, Object> properties,
            CompositeComponent parent, ScopeContainer scopeContainer, WireService wireService, WorkContext workContext) {
        super(name, parent, scopeContainer, wireService, workContext, null, 0);

        this.rhinoScript = rhinoScript;
        this.services = services;
        this.properties = properties;
        this.scope = scopeContainer.getScope();
    }

    public Object createInstance() throws ObjectCreationException {

        Map<String, Object> context = new HashMap<String, Object>(getProperties());

        for (List<OutboundWire> referenceWires : getOutboundWires().values()) {
            for (OutboundWire<?> wire : referenceWires) {
                context.put(wire.getReferenceName(), wireService.createProxy(wire));
            }
        }

        Object instance = rhinoScript.createRhinoScriptInstance(context);

        return instance;
    }

    public TargetInvoker createTargetInvoker(String serviceName, Method method) {
        return new JavaScriptInvoker(method.getName(), this);
    }

    // TODO: move all the following up to AtomicComponentExtension?
    
    public List<Class<?>> getServiceInterfaces() {
        return services;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public RhinoScriptInstance getTargetInstance() throws TargetException {
        return scopeContainer.getInstance(this);
    }

    public T getServiceInstance() throws TargetException {
        return getServiceInstance(null);
    }

    @SuppressWarnings("unchecked")
    public T getServiceInstance(String service) throws TargetException {
        InboundWire<?> wire = getInboundWire(service);
        if (wire == null) {
            TargetException e = new TargetException("ServiceDefinition not found"); // TODO better error message
            e.setIdentifier(service);
            throw e;
        }
        return (T) wireService.createProxy(wire);
    }

}
