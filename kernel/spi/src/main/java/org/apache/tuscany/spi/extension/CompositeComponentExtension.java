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
package org.apache.tuscany.spi.extension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.w3c.dom.Document;

import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.builder.Connector;
import org.apache.tuscany.spi.builder.WiringException;
import org.apache.tuscany.spi.component.AbstractSCAObject;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.ComponentRegistrationException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.DuplicateNameException;
import org.apache.tuscany.spi.component.IllegalTargetException;
import org.apache.tuscany.spi.component.PrepareException;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.component.TargetNotFoundException;
import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;

/**
 * An extension point for composite components, which new types may extend
 *
 * @version $$Rev$$ $$Date$$
 */
public abstract class CompositeComponentExtension extends AbstractSCAObject implements CompositeComponent {
    protected final Map<String, SCAObject> children = new ConcurrentHashMap<String, SCAObject>();
    protected final List<Service> services = new ArrayList<Service>();
    protected final List<Reference> references = new ArrayList<Reference>();

    protected final Map<String, Document> propertyValues;
    protected final Connector connector;

    protected final Map<String, SCAObject> systemChildren = new ConcurrentHashMap<String, SCAObject>();
    protected final List<Service> systemServices = new ArrayList<Service>();
    protected final List<Reference> systemReferences = new ArrayList<Reference>();

    // autowire mappings
    protected final Map<Class, SCAObject> autowireInternal = new ConcurrentHashMap<Class, SCAObject>();
    protected final Map<Class, Service> autowireExternal = new ConcurrentHashMap<Class, Service>();
    protected final Map<Class, SCAObject> systemAutowireInternal = new ConcurrentHashMap<Class, SCAObject>();
    protected final Map<Class, Service> systemAutowireExternal = new ConcurrentHashMap<Class, Service>();

    protected CompositeComponentExtension(String name,
                                          CompositeComponent parent,
                                          Connector connector,
                                          Map<String, Document> propertyValues) {
        super(name, parent);
        this.propertyValues = propertyValues;
        this.connector = connector;
    }

    public Scope getScope() {
        return Scope.COMPOSITE;
    }

    public void onEvent(Event event) {
        publish(event);
    }

    public <S, I extends S> void registerJavaObject(String name, Class<S> service, I instance)
        throws ComponentRegistrationException {
        throw new UnsupportedOperationException();
    }

    public <S, I extends S> void registerJavaObject(String name, List<Class<?>> services, I instance)
        throws ComponentRegistrationException {
        throw new UnsupportedOperationException();
    }

    public Document getPropertyValue(String name) {
        return propertyValues.get(name);
    }

    public SCAObject getChild(String name) {
        assert name != null : "Name was null";
        return children.get(name);
    }

    public SCAObject getSystemChild(String name) {
        assert name != null : "Name was null";
        return systemChildren.get(name);
    }

    public List<SCAObject> getSystemChildren() {
        return Collections.unmodifiableList(new ArrayList<SCAObject>(systemChildren.values()));
    }

    public List<Service> getSystemServices() {
        return Collections.unmodifiableList(systemServices);
    }

    public List<Reference> getSystemReferences() {
        return Collections.unmodifiableList(systemReferences);
    }

    public List<SCAObject> getChildren() {
        return Collections.unmodifiableList(new ArrayList<SCAObject>(children.values()));
    }

    public List<Service> getServices() {
        return Collections.unmodifiableList(services);
    }

    public List<Reference> getReferences() {
        return Collections.unmodifiableList(references);
    }

    public void register(SCAObject child) throws ComponentRegistrationException {
        if (child.isSystem()) {
            if (systemChildren.get(child.getName()) != null) {
                throw new DuplicateNameException("A system child is already registered with the name", child.getName());
            }
            systemChildren.put(child.getName(), child);
        } else {
            if (children.get(child.getName()) != null) {
                throw new DuplicateNameException("A child is already registered with the name", child.getName());
            }
            children.put(child.getName(), child);
        }
        if (child instanceof Service) {
            Service service = (Service) child;
            synchronized (services) {
                if (service.isSystem()) {
                    systemServices.add(service);
                } else {
                    services.add(service);
                }
            }
            registerAutowire(service);
        } else if (child instanceof Reference) {
            Reference reference = (Reference) child;
            synchronized (references) {
                if (reference.isSystem()) {
                    systemReferences.add(reference);
                } else {
                    references.add(reference);
                }
            }
            registerAutowire(reference);
        } else if (child instanceof AtomicComponent) {
            AtomicComponent atomic = (AtomicComponent) child;
            registerAutowire(atomic);
        } else if (child instanceof CompositeComponent) {
            CompositeComponent component = (CompositeComponent) child;
            if (lifecycleState == RUNNING && component.getLifecycleState() == UNINITIALIZED) {
                component.start();
            }
            registerAutowire(component);
            addListener(component);
        }
    }


    public void addOutboundWire(OutboundWire wire) {

    }

    public void addOutboundWires(Class<?> multiplicityClass, List<OutboundWire> wires) {

    }

    public Map<String, List<OutboundWire>> getOutboundWires() {
        return null;
    }

    public void addInboundWire(InboundWire wire) {
        //TODO implement
    }

    public InboundWire getInboundWire(String serviceName) {
        SCAObject object = children.get(serviceName);
        if (!(object instanceof Service)) {
            return null;
        }
        return ((Service) object).getInboundWire();
    }

    public Map<String, InboundWire> getInboundWires() {
        synchronized (services) {
            Map<String, InboundWire> map = new HashMap<String, InboundWire>();
            for (Service service : services) {
                map.put(service.getName(), service.getInboundWire());
            }
            return map;
        }
    }

    public Service getService(String name) {
        SCAObject ctx = children.get(name);
        if (ctx instanceof Service) {
            return (Service) ctx;
        }
        return null;
    }

    public Object getServiceInstance() throws TargetException {
        Service service = services.get(0);
        if (service == null) {
            throw new TargetNotFoundException("Component has no services");
        }
        return service.getServiceInstance();
    }

    public Service getSystemService(String name) {
        SCAObject ctx = systemChildren.get(name);
        if (ctx instanceof Service) {
            return (Service) ctx;
        }
        return null;
    }

    public <T> T locateService(Class<T> serviceInterface, String name) throws TargetException {
        SCAObject target = children.get(name);
        if (target == null) {
            TargetNotFoundException e = new TargetNotFoundException(name);
            e.addContextName(getName());
            throw e;
        }
        return serviceInterface.cast(target.getServiceInstance());
    }

    public <T> T locateSystemService(Class<T> serviceInterface, String name) throws TargetException {
        SCAObject object = systemChildren.get(name);
        if (object == null) {
            TargetNotFoundException e = new TargetNotFoundException(name);
            e.addContextName(getName());
            throw e;
        }
        return serviceInterface.cast(object.getServiceInstance());
    }

    public Object getServiceInstance(String name) throws TargetException {
        SCAObject context = children.get(name);
        if (context == null) {
            TargetNotFoundException e = new TargetNotFoundException(name);
            e.addContextName(getName());
            throw e;
        } else if (context instanceof Service) {
            return context.getServiceInstance();
        } else {
            throw new IllegalTargetException("Target must be a service", name);
        }
    }

    public Object getSystemServiceInstance(String name) throws TargetException {
        SCAObject target = systemChildren.get(name);
        if (target == null) {
            TargetNotFoundException e = new TargetNotFoundException(name);
            e.addContextName(getName());
            throw e;
        } else if (target instanceof Service) {
            return target.getServiceInstance();
        } else {
            throw new IllegalTargetException("Target must be a service", name);
        }
    }

    public List<Class<?>> getServiceInterfaces() {
        List<Class<?>> serviceInterfaces = new ArrayList<Class<?>>(services.size());
        synchronized (services) {
            for (Service service : services) {
                serviceInterfaces.add(service.getInterface());
            }
        }
        return serviceInterfaces;
    }

    public <T> T resolveInstance(Class<T> instanceInterface) throws TargetException {
        if (CompositeComponent.class.equals(instanceInterface)) {
            return instanceInterface.cast(this);
        }
        SCAObject context = autowireInternal.get(instanceInterface);
        if (context != null) {
            try {
                if (context instanceof AtomicComponent || context instanceof Reference || context instanceof Service) {
                    return instanceInterface.cast(context.getServiceInstance());
                } else {
                    String interfaceName = instanceInterface.getName();
                    throw new IllegalTargetException("Autowire target must be a system type", interfaceName);
                }
            } catch (CoreRuntimeException e) {
                e.addContextName(getName());
                throw e;
            }
        } else {
            if (CompositeComponent.class.isAssignableFrom(instanceInterface)) {
                return instanceInterface.cast(this);
            }
            // resolve to parent
            if (getParent() == null) {
                return null;
            }
            return getParent().resolveInstance(instanceInterface);
        }
    }

    public <T> T resolveSystemInstance(Class<T> instanceInterface) throws TargetException {
        if (CompositeComponent.class.equals(instanceInterface)) {
            return instanceInterface.cast(this);
        }
        SCAObject context = systemAutowireInternal.get(instanceInterface);
        if (context != null) {
            try {
                if (context instanceof AtomicComponent || context instanceof Reference || context instanceof Service) {
                    return instanceInterface.cast(context.getServiceInstance());
                } else {
                    String interfaceName = instanceInterface.getName();
                    throw new IllegalTargetException("Autowire target must be a system type", interfaceName);
                }
            } catch (CoreRuntimeException e) {
                e.addContextName(getName());
                throw e;
            }
        } else {
            // resolve to parent
            if (getParent() != null) {
                return getParent().resolveSystemInstance(instanceInterface);
            } else {
                return null;
            }
        }
    }

    public <T> T resolveExternalInstance(Class<T> instanceInterface) throws TargetException {
        Service service = autowireExternal.get(instanceInterface);
        if (service != null) {
            try {
                return instanceInterface.cast(service.getServiceInstance());
            } catch (CoreRuntimeException e) {
                e.addContextName(getName());
                throw e;
            }
        } else {
            return null;
        }
    }

    public <T> T resolveSystemExternalInstance(Class<T> instanceInterface) throws TargetException {
        Service service = systemAutowireExternal.get(instanceInterface);
        if (service != null) {
            try {
                return instanceInterface.cast(service.getServiceInstance());
            } catch (CoreRuntimeException e) {
                e.addContextName(getName());
                throw e;
            }
        } else {
            return null;
        }
    }

    public void prepare() throws PrepareException {
        // Connect services and references first so that their wires are linked first
        List<SCAObject> childList = new ArrayList<SCAObject>();
        for (SCAObject child : systemChildren.values()) {
            if (child instanceof Component) {
                childList.add(child);
            } else {
                childList.add(0, child);
            }
        }
        // connect system artifacts
        for (SCAObject child : childList) {
            // connect all children
            // TODO for composite wires, should delegate down
            try {
                connector.connect(child);
                child.prepare();
            } catch (PrepareException e) {
                e.addContextName(getName());
            } catch (WiringException e) {
                throw new PrepareException("Error preparing composite", getName(), e);
            }
        }

        // connect application artifacts
        childList.clear();
        for (SCAObject child : children.values()) {
            if (child instanceof Component) {
                childList.add(child);
            } else {
                childList.add(0, child);
            }
        }
        for (SCAObject child : childList) {
            // connect all children
            // TODO for composite wires, should delegate down
            try {
                connector.connect(child);
                child.prepare();
            } catch (PrepareException e) {
                e.addContextName(getName());
            } catch (WiringException e) {
                throw new PrepareException("Error preparing composite", getName(), e);
            }
        }
    }

    protected void registerAutowireExternal(Class<?> interfaze, Service service) {
        if (interfaze == null) {
            // The ServiceContract is not from Java
            return;
        }
        if (service.isSystem()) {
            if (systemAutowireExternal.containsKey(interfaze)) {
                return;
            }
            systemAutowireExternal.put(interfaze, service);
        } else {
            if (autowireExternal.containsKey(interfaze)) {
                return;
            }
            autowireExternal.put(interfaze, service);
        }
    }

    protected void registerAutowireInternal(Class<?> interfaze, SCAObject object) {
        if (interfaze == null) {
            // The ServiceContract is not from Java
            return;
        }
        if (object.isSystem()) {
            if (systemAutowireInternal.containsKey(interfaze)) {
                return;
            }
            systemAutowireInternal.put(interfaze, object);
        } else {
            if (autowireInternal.containsKey(interfaze)) {
                return;
            }
            autowireInternal.put(interfaze, object);
        }
    }

    protected void registerAutowire(CompositeComponent component) {
        List<Service> services = component.getServices();
        for (Service service : services) {
            registerAutowireInternal(service.getInterface(), service);
        }
    }

    protected void registerAutowire(AtomicComponent component) {
        List<Class<?>> services = component.getServiceInterfaces();
        for (Class<?> service : services) {
            registerAutowireInternal(service, component);
        }
    }

    protected void registerAutowire(Reference reference) {
        registerAutowireInternal(reference.getInterface(), reference);
    }

    protected void registerAutowire(Service service) {
        registerAutowireExternal(service.getInterface(), service);
    }


}
