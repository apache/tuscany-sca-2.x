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
package org.apache.tuscany.container.script.helper;

import static org.apache.tuscany.spi.idl.java.JavaIDLUtils.findMethod;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.extension.AtomicComponentExtension;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.services.work.WorkScheduler;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireService;

/**
 * A component implementation for script languages.
 */
public class ScriptHelperComponent extends AtomicComponentExtension {

    private final List<Class<?>> services;

    private final Map<String, Object> properties;

    protected ScriptHelperInstanceFactory instanceFactory;

    public ScriptHelperComponent(String name, ScriptHelperInstanceFactory instanceFactory, Map<String, Object> properties, List<Class<?>> services, CompositeComponent parent, ScopeContainer scopeContainer,
            WireService wireService, WorkContext workContext, WorkScheduler workScheduler) {

        super(name, parent, scopeContainer, wireService, workContext, workScheduler, 0);

        this.instanceFactory = instanceFactory;
        this.services = services;
        this.scope = scopeContainer.getScope();
        this.properties = properties;
    }

    @SuppressWarnings("unchecked")
    public Object createInstance() throws ObjectCreationException {

        Map<String, Object> context = new HashMap<String, Object>(getProperties());

        for (List<OutboundWire> referenceWires : getOutboundWires().values()) {
            for (OutboundWire wire : referenceWires) {
                Object wireProxy = wireService.createProxy(wire);
                context.put(wire.getReferenceName(), wireProxy);
            }
        }

        return instanceFactory.createInstance(services, context);
    }

    public TargetInvoker createTargetInvoker(String targetName, Operation operation) {
        Method[] methods = operation.getServiceContract().getInterfaceClass().getMethods();
        Method method = findMethod(operation, methods);
        return new ScriptHelperInvoker(method.getName(), this);
    }

    public TargetInvoker createAsyncTargetInvoker(InboundWire wire, Operation operation) {
        return new AsyncInvoker(operation.getName(), wire, this, workScheduler, null, workContext);
    }

    // TODO: move all the following up to AtomicComponentExtension?

    public List<Class<?>> getServiceInterfaces() {
        return services;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public Object getTargetInstance() throws TargetException {
        return scopeContainer.getInstance(this);
    }

    public Object getServiceInstance() throws TargetException {
        return getServiceInstance(null);
    }

    @SuppressWarnings("unchecked")
    public Object getServiceInstance(String service) throws TargetException {
        InboundWire wire = getInboundWire(service);
        if (wire == null) {
            TargetException e = new TargetException("ServiceDefinition not found"); // TODO better error message
            e.setIdentifier(service);
            throw e;
        }
        return wireService.createProxy(wire);
    }

}
