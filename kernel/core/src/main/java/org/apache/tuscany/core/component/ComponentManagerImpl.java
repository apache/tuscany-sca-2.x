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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.ComponentRegistrationException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.DuplicateNameException;
import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.services.management.TuscanyManagementService;
import org.apache.tuscany.spi.util.UriHelper;

import org.apache.tuscany.core.component.event.ComponentStart;
import org.apache.tuscany.core.component.event.ComponentStop;
import org.apache.tuscany.core.implementation.composite.SystemSingletonAtomicComponent;
import org.apache.tuscany.core.resolver.AutowireResolver;

/**
 * Default implementation of the component manager
 *
 * @version $Rev$ $Date$
 */
public class ComponentManagerImpl implements ComponentManager {
    private TuscanyManagementService managementService;
    private AutowireResolver resolver;
    private Map<URI, Component> components;
    private Map<URI, List<URI>> parentToChildren;

    public ComponentManagerImpl() {
        components = new ConcurrentHashMap<URI, Component>();
        parentToChildren = new ConcurrentHashMap<URI, List<URI>>();
    }

    public ComponentManagerImpl(TuscanyManagementService managementService, AutowireResolver resolver) {
        this();
        this.managementService = managementService;
        this.resolver = resolver;
    }

    public void register(Component component) throws ComponentRegistrationException {
        URI uri = component.getUri();
        assert uri != null;
        if (components.containsKey(uri)) {
            throw new DuplicateNameException(uri.toString());
        }
        components.put(uri, component);
        URI parentUri = UriHelper.getParentNameAsUri(uri);
        List<URI> children = parentToChildren.get(parentUri);
        if (children == null) {
            children = new ArrayList<URI>();
            parentToChildren.put(parentUri, children);
        }
        // the parent may not be registered in this VM
        synchronized (children) {
            children.add(uri);
        }
        if (managementService != null && component instanceof AtomicComponent) {
            // FIXME shouldn't it take the canonical name and also not distinguish atomic components?
            managementService.registerComponent(component.getUri().toString(), component);
        }
        if (component instanceof CompositeComponent) {
            component.addListener(this);
        }
    }

    public <S, I extends S> void registerJavaObject(URI uri, Class<S> service, I instance)
        throws ComponentRegistrationException {
        SystemSingletonAtomicComponent<S, I> component =
            new SystemSingletonAtomicComponent<S, I>(uri, null, service, instance);
        register(component);
        if (resolver != null) {
            for (ServiceContract contract : component.getServiceContracts()) {
                resolver.addPrimordialUri(contract, uri);
            }
        }
    }

    public <S, I extends S> void registerJavaObject(URI uri, List<Class<?>> services, I instance)
        throws ComponentRegistrationException {
        SystemSingletonAtomicComponent<S, I> component =
            new SystemSingletonAtomicComponent<S, I>(uri, null, services, instance);
        register(component);
        if (resolver != null) {
            for (ServiceContract contract : component.getServiceContracts()) {
                resolver.addPrimordialUri(contract, uri);
            }
        }
    }

    public void unregister(Component component) throws ComponentRegistrationException {
        URI uri = component.getUri();
        components.remove(uri);
        parentToChildren.remove(uri);
        component.removeListener(this);
    }

    public Component getComponent(URI name) {
        return components.get(name);
    }

    public void onEvent(Event event) {
        // This could be faster but it is not an operation that is performed often
        if (event instanceof ComponentStart) {
            URI uri = ((ComponentStart) event).getComponentUri();
            List<URI> children = parentToChildren.get(uri);
            if (children != null) {
                synchronized (children) {
                    for (URI childUri : children) {
                        // performs a depth-first traversal as the children will recursively fire start events
                        Component child = components.get(childUri);
                        assert child != null;
                        child.start();
                    }
                }
            }
        } else if (event instanceof ComponentStop) {
            URI uri = ((ComponentStop) event).getComponentUri();
            List<URI> children = parentToChildren.get(uri);
            if (children != null) {
                synchronized (children) {
                    for (URI childUri : children) {
                        // performs a depth-first traversal as the children will recursively fire stop events
                        Component child = components.get(childUri);
                        assert child != null;
                        child.stop();
                    }
                }
            }
        }

    }
}
