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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.ComponentRegistrationException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.DuplicateNameException;
import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.services.management.TuscanyManagementService;

import org.apache.tuscany.core.component.event.ComponentStart;
import org.apache.tuscany.core.component.event.ComponentStop;

/**
 * Default implementation of the component manager
 *
 * @version $Rev$ $Date$
 */
public class ComponentManagerImpl implements ComponentManager {
    private TuscanyManagementService managementService;
    private Map<URI, Component> components;

    public ComponentManagerImpl() {
        components = new ConcurrentHashMap<URI, Component>();
    }

    public ComponentManagerImpl(TuscanyManagementService managementService) {
        this();
        this.managementService = managementService;
    }

    public void register(Component component) throws ComponentRegistrationException {
        URI uri = component.getUri();
        if (components.containsKey(uri)) {
            throw new DuplicateNameException(uri.toString());
        }
        components.put(uri, component);

        if (managementService != null && component instanceof AtomicComponent) {
            // FIXME shouldn't it take the canonical name and also not distinguish atomic components?
            managementService.registerComponent(component.getName(), component);
        }
        if (component instanceof CompositeComponent) {
            component.addListener(this);
        }
    }

    public void unregister(Component component) throws ComponentRegistrationException {
        components.remove(component.getUri());
        component.removeListener(this);
    }

    public Component getComponent(URI name) {
        return components.get(name);
    }

    public void onEvent(Event event) {
        // This could be faster but it is not an operation that is performed often
        if (event instanceof ComponentStart) {
            URI uri = ((ComponentStart) event).getComponentUri();
            for (Map.Entry<URI, Component> entry : components.entrySet()) {
                if (entry.getKey().toString().startsWith(uri.toString())) {
//xcv enable                    entry.getValue().start();
                }
            }
        } else if (event instanceof ComponentStop) {
            URI uri = ((ComponentStop) event).getComponentUri();
            for (Map.Entry<URI, Component> entry : components.entrySet()) {
                if (entry.getKey().toString().startsWith(uri.toString())) {
// xcv enable                    entry.getValue().stop();
                }
            }
        }

    }
}
