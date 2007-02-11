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

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.w3c.dom.Document;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.builder.Connector;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.ComponentRegistrationException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.DuplicateNameException;
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
import org.apache.tuscany.spi.services.management.TuscanyManagementService;
import org.apache.tuscany.spi.util.UriHelper;
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

    // autowire mappings
    protected final Map<Class, InboundWire> autowireInternal = new ConcurrentHashMap<Class, InboundWire>();
    protected final Map<Class, InboundWire> autowireExternal = new ConcurrentHashMap<Class, InboundWire>();

    /**
     * Management service to use.
     */
    private TuscanyManagementService managementService;

    protected CompositeComponentExtension(URI name,
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
    public final void setManagementService(TuscanyManagementService managementService) {
        this.managementService = managementService;
    }

    public Scope getScope() {
        return Scope.SYSTEM;
    }

    public void onEvent(Event event) {
        publish(event);
    }

    public Document getPropertyValue(String name) {
        return propertyValues.get(name);
    }

    public SCAObject getChild(String name) {
        assert name != null;
        return children.get(name);
    }


    public List<Service> getServices() {
        return Collections.unmodifiableList(services);
    }

    public List<Reference> getReferences() {
        return Collections.unmodifiableList(references);
    }

    public void register(SCAObject child) throws ComponentRegistrationException {
        assert child instanceof Service || child instanceof Reference || child instanceof Component;
        String name;
        // TODO JFM should just use fragment when only refs and services are registered
        if (child.getUri().getFragment() != null) {
            name = child.getUri().getFragment();
        } else {
            name = UriHelper.getBaseName(child.getUri());
        }
        if (children.get(name) != null) {
            String uri = child.getUri().toString();
            throw new DuplicateNameException("A child is already registered with the name", uri);
        }
        children.put(name, child);
        if (child instanceof Service) {
            Service service = (Service) child;
            synchronized (services) {
                services.add(service);
            }
            registerAutowire(service);
        } else if (child instanceof Reference) {
            Reference reference = (Reference) child;
            synchronized (references) {
                references.add(reference);
            }
            registerAutowire(reference);
        } else if (child instanceof AtomicComponent) {
            AtomicComponent atomic = (AtomicComponent) child;
            registerAutowire(atomic);
            if (managementService != null) {
                managementService.registerComponent(atomic.getUri().toString(), atomic);
            }
        } else if (child instanceof CompositeComponent) {
            CompositeComponent component = (CompositeComponent) child;
            registerAutowire(component);
        }
    }

    public Map<String, List<OutboundWire>> getOutboundWires() {
        synchronized (references) {
            Map<String, List<OutboundWire>> map = new HashMap<String, List<OutboundWire>>();
            for (Reference reference : references) {
                List<OutboundWire> wires = new ArrayList<OutboundWire>();
                map.put(reference.getUri().getFragment(), wires);
                for (ReferenceBinding binding : reference.getReferenceBindings()) {
                    OutboundWire wire = binding.getOutboundWire();
                    if (Wire.LOCAL_BINDING.equals(wire.getBindingType())) {
                        wires.add(wire);
                    }
                }
            }
            return map;
        }
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

    public InboundWire getTargetWire(String targetName) {
        SCAObject object = null;
        if (targetName == null) {
            if (services.size() == 1) {
                object = services.get(0);
            } else if (references.size() == 1) {
                object = references.get(0);
            }
        } else {
            object = children.get(targetName);
        }
        if (object instanceof Service) {
            Service service = (Service) object;
            List<ServiceBinding> bindings = service.getServiceBindings();
            if (bindings.isEmpty()) {
                return null;
            }
            for (ServiceBinding binding : bindings) {
                InboundWire wire = binding.getInboundWire();
                if (Wire.LOCAL_BINDING.equals(wire.getBindingType())) {
                    return wire;
                }
            }
            // for now, pick the first one
            return bindings.get(0).getInboundWire();
        } else if (object instanceof Reference) {
            Reference reference = (Reference) object;
            List<ReferenceBinding> bindings = reference.getReferenceBindings();
            if (bindings.isEmpty()) {
                return null;
            }
            for (ReferenceBinding binding : bindings) {
                InboundWire wire = binding.getInboundWire();
                if (Wire.LOCAL_BINDING.equals(wire.getBindingType())) {
                    return wire;
                }
            }
            return bindings.get(0).getInboundWire();
        }
        return null;
    }

    public Collection<InboundWire> getInboundWires() {
        synchronized (services) {
            List<InboundWire> map = new ArrayList<InboundWire>();
            for (Service service : services) {
                for (ServiceBinding binding : service.getServiceBindings()) {
                    InboundWire wire = binding.getInboundWire();
                    if (Wire.LOCAL_BINDING.equals(wire.getBindingType())) {
                        map.add(wire);
                    }
                }
            }
            return map;
        }
    }

    public InboundWire resolveAutowire(Class<?> instanceInterface) throws TargetResolutionException {
        // FIXME JNB make this faster and thread safe
        for (Map.Entry<Class, InboundWire> service : autowireInternal.entrySet()) {
            if (instanceInterface.isAssignableFrom(service.getKey())) {
                return service.getValue();
            }
        }
        if (getParent() != null) {
            return getParent().resolveAutowire(instanceInterface);
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

    public void prepare() throws PrepareException {
        for (Service service : services) {
            service.prepare();
        }
        for (Reference reference : references) {
            reference.prepare();
        }
    }

    protected void registerAutowireExternal(Class<?> interfaze, Service service) throws InvalidAutowireInterface {
        if (interfaze == null) {
            // The ServiceContract is not from Java
            return;
        }
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

    protected void registerAutowireInternal(Class<?> interfaze, InboundWire wire) throws InvalidAutowireInterface {
        if (interfaze == null) {
            // The ServiceContract is not from Java
            return;
        }
        if (autowireInternal.containsKey(interfaze)) {
            return;
        }
        if (!interfaze.isAssignableFrom(wire.getServiceContract().getInterfaceClass())) {
            String iName = interfaze.getName();
            throw new InvalidAutowireInterface("Matching inbound wire not found for interface", iName);
        }
        autowireInternal.put(interfaze, wire);
    }

    protected void registerAutowireInternal(Class<?> interfaze, Reference reference) throws InvalidAutowireInterface {
        if (interfaze == null) {
            // The ServiceContract is not from Java
            return;
        }
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

    protected void registerAutowireInternal(Class<?> interfaze, AtomicComponent component)
        throws InvalidAutowireInterface {
        if (interfaze == null) {
            // The ServiceContract is not from Java
            return;
        }
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

    protected void registerAutowire(CompositeComponent component) throws InvalidAutowireInterface {
        // the composite is under the application hierarchy so only register its non-system services
        Collection<InboundWire> wires = component.getInboundWires();
        for (InboundWire wire : wires) {
            Class<?> clazz = wire.getServiceContract().getInterfaceClass();
            registerAutowireInternal(clazz, wire);
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
