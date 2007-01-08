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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.w3c.dom.Document;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.builder.Connector;
import org.apache.tuscany.spi.builder.WiringException;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.ComponentRegistrationException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.DuplicateNameException;
import org.apache.tuscany.spi.component.IllegalTargetException;
import org.apache.tuscany.spi.component.InvalidAutowireInterface;
import org.apache.tuscany.spi.component.PrepareException;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.services.management.ManagementService;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.Wire;

/**
 * An extension point for composite components, which new types may extend
 *
 * @version $$Rev$$ $$Date$$
 */
public abstract class CompositeComponentExtension extends AbstractComponentExtension implements CompositeComponent {

    protected final Map<String, SCAObject> children = new ConcurrentHashMap<String, SCAObject>();
    protected final List<Service> services = new ArrayList<Service>();
    protected final List<Reference> references = new ArrayList<Reference>();

    protected final Map<String, Document> propertyValues;
    protected final Connector connector;

    protected final Map<String, SCAObject> systemChildren = new ConcurrentHashMap<String, SCAObject>();
    protected final List<Service> systemServices = new ArrayList<Service>();
    protected final List<Reference> systemReferenceBindings = new ArrayList<Reference>();

    // autowire mappings
    protected final Map<Class, InboundWire> autowireInternal = new ConcurrentHashMap<Class, InboundWire>();
    protected final Map<Class, InboundWire> autowireExternal = new ConcurrentHashMap<Class, InboundWire>();
    protected final Map<Class, InboundWire> systemAutowireInternal = new ConcurrentHashMap<Class, InboundWire>();
    protected final Map<Class, InboundWire> systemAutowireExternal = new ConcurrentHashMap<Class, InboundWire>();

    /**
     * Management service to use.
     */
    private ManagementService managementService;

    protected CompositeComponentExtension(String name,
                                          CompositeComponent parent,
                                          Connector connector,
                                          Map<String, Document> propertyValues) {
        super(name, parent);
        this.propertyValues = propertyValues;
        this.connector = connector;
    }

    /**
     * Autowires the management service.
     *
     * @param managementService Management service used for registering components.
     */
    @Autowire
    public final void setManagementService(ManagementService managementService) {
        this.managementService = managementService;
    }

    public Scope getScope() {
        return Scope.SYSTEM;
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
        assert name != null;
        return children.get(name);
    }

    public SCAObject getSystemChild(String name) {
        assert name != null;
        return systemChildren.get(name);
    }

    public void register(SCAObject child) throws ComponentRegistrationException {
        assert child instanceof Service || child instanceof Reference || child instanceof Component;
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
                    systemReferenceBindings.add(reference);
                } else {
                    references.add(reference);
                }
            }
            registerAutowire(reference);
        } else if (child instanceof AtomicComponent) {
            AtomicComponent atomic = (AtomicComponent) child;
            registerAutowire(atomic);
            if (managementService != null) {
                managementService.registerComponent(atomic.getName(), atomic);
            }
        } else if (child instanceof CompositeComponent) {
            CompositeComponent component = (CompositeComponent) child;
            if (lifecycleState == RUNNING && component.getLifecycleState() == UNINITIALIZED) {
                component.start();
            }
            registerAutowire(component);
            addListener(component);
        }
    }

    public Map<String, List<OutboundWire>> getOutboundWires() {
        return null;
    }

    public InboundWire getInboundWire(String serviceName) {
        Service service;
        if (serviceName == null) {
            if (services.size() != 1) {
                return null;
            }
            service = services.get(0);
        } else {
            SCAObject object = children.get(serviceName);
            if (!(object instanceof Service)) {
                return null;
            }
            service = (Service) object;
        }
        for (ServiceBinding binding : service.getServiceBindings()) {
            InboundWire wire = binding.getInboundWire();
            if (Wire.LOCAL_BINDING.equals(wire.getBindingType())) {
                return wire;
            }
        }
        return null;
    }

    public InboundWire getInboundSystemWire(String serviceName) {
        Service service;
        if (serviceName == null) {
            if (systemServices.size() != 1) {
                return null;
            }
            service = systemServices.get(0);
        } else {
            SCAObject object = systemChildren.get(serviceName);
            if (!(object instanceof Service)) {
                return null;
            }
            service = (Service) object;
        }
        for (ServiceBinding binding : service.getServiceBindings()) {
            InboundWire wire = binding.getInboundWire();
            if (Wire.LOCAL_BINDING.equals(wire.getBindingType())) {
                return wire;
            }
        }
        return null;
    }

    public Collection<InboundWire> getInboundWires() {
        synchronized (services) {
            List<InboundWire> map = new ArrayList<InboundWire>();
            for (Service service : services) {
                for (ServiceBinding binding : service.getServiceBindings()) {
                    map.add(binding.getInboundWire());
                }
            }
            return map;
        }
    }

    public InboundWire resolveAutowire(Class<?> instanceInterface) throws TargetResolutionException {
        // FIXME JNB make this faster and thread safe
        for (Map.Entry<Class, InboundWire> service : autowireInternal.entrySet()) {
            if (instanceInterface.isAssignableFrom(service.getKey())) {
                InboundWire wire = service.getValue();
                SCAObject parent = wire.getContainer();

                if (parent instanceof AtomicComponent
                    || parent instanceof ReferenceBinding
                    || parent instanceof ServiceBinding) {
                    return wire;
                } else {
                    throw new IllegalTargetException("Autowire target must be a system type", parent.getName());
                }
            }
        }
        if (getParent() != null) {
            return getParent().resolveAutowire(instanceInterface);
        }
        return null;
    }

    public InboundWire resolveSystemAutowire(Class<?> instanceInterface) throws TargetResolutionException {
        // FIXME JNB make this faster and thread safe
        for (Map.Entry<Class, InboundWire> service : systemAutowireInternal.entrySet()) {
            if (instanceInterface.isAssignableFrom(service.getKey())) {
                return service.getValue();
            }
        }
        if (getParent() != null) {
            return getParent().resolveSystemAutowire(instanceInterface);
        }
        return null;
    }

    public InboundWire resolveExternalAutowire(Class<?> instanceInterface) throws TargetResolutionException {
        // FIXME JNB make this faster and thread safe
        for (Map.Entry<Class, InboundWire> service : autowireExternal.entrySet()) {
            if (instanceInterface.isAssignableFrom(service.getKey())) {
                return service.getValue();
            }
        }
        if (getParent() != null) {
            return getParent().resolveAutowire(instanceInterface);
        }
        return null;
    }

    public InboundWire resolveSystemExternalAutowire(Class<?> instanceInterface) throws TargetResolutionException {
        // FIXME JNB make this faster and thread safe
        for (Map.Entry<Class, InboundWire> service : systemAutowireExternal.entrySet()) {
            if (instanceInterface.isAssignableFrom(service.getKey())) {
                return service.getValue();
            }
        }
        if (getParent() != null) {
            return getParent().resolveAutowire(instanceInterface);
        }
        return null;
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
                // TODO JFM fixme test
                if (!(child instanceof CompositeComponent)) {
                    connector.connect(child);
                }
                child.prepare();
            } catch (PrepareException e) {
                e.addContextName(getName());
                throw e;
            } catch (WiringException e) {
                throw new PrepareException("Error preparing composite", getName(), e);
            }
        }
    }

    protected void registerAutowireExternal(Class<?> interfaze, Service service) throws InvalidAutowireInterface {
        if (interfaze == null) {
            // The ServiceContract is not from Java
            return;
        }
        if (service.isSystem()) {
            if (systemAutowireExternal.containsKey(interfaze)) {
                return;
            }
            // TODO autowire should allow multiple interfaces
            List<ServiceBinding> bindings = service.getServiceBindings();
            if (bindings.size() == 0) {
                return;
            }
            // pick the first binding until autowire allows multiple interfaces
            InboundWire wire = bindings.get(0).getInboundWire();
            if (!interfaze.isAssignableFrom(wire.getServiceContract().getInterfaceClass())) {
                throw new InvalidAutowireInterface("Matching inbound wire not found for interface",
                    interfaze.getName());
            }
            systemAutowireExternal.put(interfaze, wire);
        } else {
            if (autowireExternal.containsKey(interfaze)) {
                return;
            }
            // TODO autowire should allow multiple interfaces
            List<ServiceBinding> bindings = service.getServiceBindings();
            if (bindings.size() == 0) {
                return;
            }
            // pick the first binding until autowire allows multiple interfaces
            InboundWire wire = bindings.get(0).getInboundWire();
            if (!interfaze.isAssignableFrom(wire.getServiceContract().getInterfaceClass())) {
                String iName = interfaze.getName();
                throw new InvalidAutowireInterface("Matching inbound wire not found for interface", iName);
            }
            autowireExternal.put(interfaze, wire);
        }
    }

    protected void registerAutowireInternal(Class<?> interfaze, InboundWire wire, boolean isSystem)
        throws InvalidAutowireInterface {
        if (interfaze == null) {
            // The ServiceContract is not from Java
            return;
        }
        if (isSystem()) {
            if (systemAutowireInternal.containsKey(interfaze)) {
                return;
            }
            systemAutowireInternal.put(interfaze, wire);
        } else {
            if (autowireInternal.containsKey(interfaze)) {
                return;
            }
            if (!interfaze.isAssignableFrom(wire.getServiceContract().getInterfaceClass())) {
                String iName = interfaze.getName();
                throw new InvalidAutowireInterface("Matching inbound wire not found for interface", iName);
            }
            autowireInternal.put(interfaze, wire);
        }
    }

    protected void registerAutowireInternal(Class<?> interfaze, Reference reference) throws InvalidAutowireInterface {
        if (interfaze == null) {
            // The ServiceContract is not from Java
            return;
        }
        if (reference.isSystem()) {
            if (systemAutowireInternal.containsKey(interfaze)) {
                return;
            }
            List<ReferenceBinding> bindings = reference.getReferenceBindings();
            if (bindings.size() == 0) {
                return;
            }
            // pick the first binding until autowire allows multiple interfaces
            InboundWire wire = bindings.get(0).getInboundWire();
            if (!interfaze.isAssignableFrom(wire.getServiceContract().getInterfaceClass())) {
                throw new InvalidAutowireInterface("Matching inbound wire not found for interface",
                    interfaze.getName());
            }
            systemAutowireInternal.put(interfaze, wire);
        } else {
            if (autowireInternal.containsKey(interfaze)) {
                return;
            }
            List<ReferenceBinding> bindings = reference.getReferenceBindings();
            if (bindings.size() == 0) {
                return;
            }
            // pick the first binding until autowire allows multiple interfaces
            InboundWire wire = bindings.get(0).getInboundWire();
            if (!interfaze.isAssignableFrom(wire.getServiceContract().getInterfaceClass())) {
                String iName = interfaze.getName();
                throw new InvalidAutowireInterface("Matching inbound wire not found for interface", iName);
            }
            autowireInternal.put(interfaze, wire);
        }
    }

    protected void registerAutowireInternal(Class<?> interfaze, AtomicComponent component)
        throws InvalidAutowireInterface {
        if (interfaze == null) {
            // The ServiceContract is not from Java
            return;
        }
        if (component.isSystem()) {
            if (systemAutowireInternal.containsKey(interfaze) || component.getInboundWires().size() == 0) {
                return;
            }
            for (InboundWire wire : component.getInboundWires()) {
                Class<?> clazz = wire.getServiceContract().getInterfaceClass();
                if (clazz.isAssignableFrom(interfaze)) {
                    systemAutowireInternal.put(interfaze, wire);
                    return;
                }
            }
            throw new InvalidAutowireInterface("Matching inbound wire not found for interface", interfaze.getName());
        } else {
            if (autowireInternal.containsKey(interfaze) || component.getInboundWires().size() == 0) {
                return;
            }
            for (InboundWire wire : component.getInboundWires()) {
                if (interfaze.isAssignableFrom(wire.getServiceContract().getInterfaceClass())) {
                    autowireInternal.put(interfaze, wire);
                    return;
                }
            }
            throw new InvalidAutowireInterface("Matching inbound wire not found for interface", interfaze.getName());
        }
    }

    protected void registerAutowire(CompositeComponent component) throws InvalidAutowireInterface {
        Collection<InboundWire> wires = component.getInboundWires();
        for (InboundWire wire : wires) {
            Class<?> clazz = wire.getServiceContract().getInterfaceClass();
            registerAutowireInternal(clazz, wire, false);
        }
        wires = component.getInboundWires();
        for (InboundWire wire : wires) {
            Class<?> clazz = wire.getServiceContract().getInterfaceClass();
            registerAutowireInternal(clazz, wire, true);
        }
    }

    protected void registerAutowire(AtomicComponent component) throws InvalidAutowireInterface {
        for (InboundWire wire : component.getInboundWires()) {
            registerAutowireInternal(wire.getServiceContract().getInterfaceClass(), component);
        }
    }

    protected void registerAutowire(Reference reference) throws InvalidAutowireInterface {
        // TODO autowire should allow multiple interfaces
        List<ReferenceBinding> bindings = reference.getReferenceBindings();
        if (bindings.size() == 0) {
            return;
        }
        // pick the first binding until autowire allows multiple interfaces
        InboundWire wire = bindings.get(0).getInboundWire();
        Class<?> clazz = wire.getServiceContract().getInterfaceClass();
        registerAutowireInternal(clazz, reference);


    }

    protected void registerAutowire(Service service) throws InvalidAutowireInterface {
        // TODO autowire should allow multiple interfaces
        List<ServiceBinding> bindings = service.getServiceBindings();
        if (bindings.size() == 0) {
            return;
        }
        // pick the first binding until autowire allows multiple interfaces
        InboundWire wire = bindings.get(0).getInboundWire();
        Class<?> clazz = wire.getServiceContract().getInterfaceClass();
        registerAutowireExternal(clazz, service);
    }
}
