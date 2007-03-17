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
package org.apache.tuscany.runtime.webapp.implementation.webapp;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletContext;

import org.osoa.sca.CallableReference;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.ServiceReference;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.component.InstanceWrapper;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.extension.AtomicComponentExtension;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.physical.PhysicalOperationDefinition;
import org.apache.tuscany.spi.wire.ProxyService;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.Wire;

import org.apache.tuscany.core.component.ComponentContextImpl;
import org.apache.tuscany.core.component.ComponentContextProvider;
import org.apache.tuscany.core.component.ServiceReferenceImpl;
import org.apache.tuscany.core.wire.WireObjectFactory;
import org.apache.tuscany.runtime.webapp.Constants;

/**
 * @version $Rev$ $Date$
 */
public class WebappComponent extends AtomicComponentExtension implements ComponentContextProvider {
    private final Map<String, ObjectFactory<?>> propertyFactories;
    private final Map<String, Class<?>> referenceTypes;
    private final Map<String, Wire> referenceFactories;
    private final ComponentContext context;

    public WebappComponent(URI name,
                           ProxyService proxyService,
                           WorkContext workContext,
                           URI groupId,
                           Map<String, ObjectFactory<?>> attributes,
                           Map<String, Class<?>> referenceTypes) {
        super(name, proxyService, workContext, groupId, 0, 0, 0);
        this.propertyFactories = attributes;
        this.referenceTypes = referenceTypes;
        referenceFactories = new ConcurrentHashMap<String, Wire>(referenceTypes.size());
        context = new ComponentContextImpl(this);
    }

    public List<Wire> getWires(String name) {
        throw new UnsupportedOperationException();
    }

    public void attachCallbackWire(Wire wire) {
        throw new UnsupportedOperationException();
    }

    public void attachWire(Wire wire) {
        String name = wire.getSourceUri().getFragment();
        referenceFactories.put(name, wire);
    }

    public void attachWires(List<Wire> wires) {
        throw new UnsupportedOperationException();
    }

    protected <B> ObjectFactory<B> createWireFactory(Class<B> interfaze, Wire wire) {
        return new WireObjectFactory<B>(interfaze, wire, proxyService);
    }

    public void bind(ServletContext servletContext) {
        servletContext.setAttribute(Constants.CONTEXT_ATTRIBUTE, getComponentContext());
        for (Map.Entry<String, ObjectFactory<?>> entry : propertyFactories.entrySet()) {
            servletContext.setAttribute(entry.getKey(), entry.getValue().getInstance());
        }
        for (Map.Entry<String, Wire> entry : referenceFactories.entrySet()) {
            String name = entry.getKey();
            Wire wire = entry.getValue();
            Class<?> type = referenceTypes.get(name);
            ObjectFactory<?> factory = createWireFactory(type, wire);
            servletContext.setAttribute(name, factory.getInstance());
        }
    }


    public TargetInvoker createTargetInvoker(String targetName, Operation operation)
        throws TargetInvokerCreationException {
        throw new UnsupportedOperationException();
    }

    public TargetInvoker createTargetInvoker(String targetName, PhysicalOperationDefinition operation)
        throws TargetInvokerCreationException {
        throw new UnsupportedOperationException();
    }

    public Object createInstance() throws ObjectCreationException {
        throw new UnsupportedOperationException();
    }

    public InstanceWrapper<?> createInstanceWrapper() throws ObjectCreationException {
        throw new UnsupportedOperationException();
    }

    public Object getTargetInstance() throws TargetResolutionException {
        throw new UnsupportedOperationException();
    }

    public Object getAssociatedTargetInstance() throws TargetResolutionException {
        throw new UnsupportedOperationException();
    }

    public ComponentContext getComponentContext() {
        return context;
    }

    public <B> B getProperty(Class<B> type, String propertyName) {
        ObjectFactory<?> factory = propertyFactories.get(propertyName);
        if (factory != null) {
            return type.cast(factory.getInstance());
        } else {
            return null;
        }

    }

    public <B> B getService(Class<B> type, String name) {
        Wire wire = referenceFactories.get(name);
        if (wire == null) {
            return null;
        }
        ObjectFactory<B> factory = createWireFactory(type, wire);
        return factory.getInstance();
    }

    public <B> ServiceReference<B> getServiceReference(Class<B> type, String name) {
        Wire wire = referenceFactories.get(name);
        if (wire == null) {
            return null;
        }
        ObjectFactory<B> factory = createWireFactory(type, wire);
        return new ServiceReferenceImpl<B>(type, factory);
    }

    public <B, R extends CallableReference<B>> R cast(B target) {
        return (R) proxyService.cast(target);
    }
}
