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

import org.apache.tuscany.assembly.ComponentService;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.ComponentManager;
import org.apache.tuscany.spi.component.DuplicateNameException;
import org.apache.tuscany.spi.component.RegistrationException;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.services.management.TuscanyManagementService;

/**
 * Default implementation of the component manager
 * 
 * @version $Rev$ $Date$
 */
public class ComponentManagerImpl implements ComponentManager {
    private TuscanyManagementService managementService;
    private Map<URI, Component> components;
    
    private List<SCAObject> scaObjects = new ArrayList<SCAObject>();
    private List<Object> modelObjects = new ArrayList<Object>();
    

    public ComponentManagerImpl() {
        components = new ConcurrentHashMap<URI, Component>();
    }

    public ComponentManagerImpl(TuscanyManagementService managementService) {
        this();
        this.managementService = managementService;
    }

    public synchronized void register(Component component) throws RegistrationException {
        URI uri = component.getUri();
        assert uri != null;
        assert !uri.toString().endsWith("/");
        if (components.containsKey(uri)) {
            throw new DuplicateNameException(uri.toString());
        }
        components.put(uri, component);

        if (managementService != null && component instanceof AtomicComponent) {
            // FIXME shouldn't it take the canonical name and also not
            // distinguish atomic components?
            managementService.registerComponent(component.getUri().toString(), component);
        }
    }

    public <I> void registerJavaObject(URI uri, ComponentService service, I instance) throws RegistrationException {
        SingletonAtomicComponent<I> component = new SingletonAtomicComponent<I>(uri, service, instance);
        register(component);
    }

    public <I> void registerJavaObject(URI uri, List<ComponentService> services, I instance) throws RegistrationException {
        SingletonAtomicComponent<I> component = new SingletonAtomicComponent<I>(uri, services, instance);
        register(component);
    }

    public synchronized void unregister(Component component) throws RegistrationException {
        URI uri = component.getUri();
        components.remove(uri);
    }

    public Component getComponent(URI name) {
        return components.get(name);
    }

    public void onEvent(Event event) {
        throw new UnsupportedOperationException();
    }

    public void add(SCAObject object, Object model) {
        scaObjects.add(object);
        modelObjects.add(model);
    }

    public <T> T getModelObject(Class<T> modelType, SCAObject object) {
        for(int i=0; i<scaObjects.size(); i++) {
            if(scaObjects.get(i) == object) {
                return modelType.cast(modelObjects.get(i));
            }
        }
        return null;
    }

    public List<Object> getModelObjects() {
        return modelObjects;
    }

    public <T extends SCAObject> T getSCAObject(Class<T> objectType, Object model) {
        for(int i=0; i<modelObjects.size(); i++) {
            if(modelObjects.get(i) == model) {
                return objectType.cast(scaObjects.get(i));
            }
        }
        return null;
    }

    public List<SCAObject> getSCAObjects() {
        return scaObjects;
    }
}
