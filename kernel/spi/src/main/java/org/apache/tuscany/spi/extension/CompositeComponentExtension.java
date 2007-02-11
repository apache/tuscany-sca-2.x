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

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.DuplicateNameException;
import org.apache.tuscany.spi.component.PrepareException;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.RegistrationException;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.Wire;

/**
 * An extension point for composite components, which new types may extend
 *
 * @version $$Rev$$ $$Date$$
 */
public abstract class CompositeComponentExtension extends AbstractComponentExtension implements CompositeComponent {
    protected final List<Service> services = new ArrayList<Service>();
    protected final List<Reference> references = new ArrayList<Reference>();
    protected final Map<String, SCAObject> children = new ConcurrentHashMap<String, SCAObject>();
    protected final Map<String, Document> propertyValues;

    protected CompositeComponentExtension(URI name, CompositeComponent parent, Map<String, Document> propertyValues) {
        super(name, parent);
        this.propertyValues = propertyValues;
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

    public List<Service> getServices() {
        return Collections.unmodifiableList(services);
    }

    public List<Reference> getReferences() {
        return Collections.unmodifiableList(references);
    }

    public void register(Service service) throws RegistrationException {
        String name = service.getUri().getFragment();
        assert name != null;
        if (children.get(name) != null) {
            String uri = service.getUri().toString();
            throw new DuplicateNameException("A service or reference is already registered with the name", uri);
        }
        children.put(name, service);
        synchronized (services) {
            services.add(service);
        }
    }

    public void register(Reference reference) throws RegistrationException {
        String name = reference.getUri().getFragment();
        assert name != null;
        if (children.get(name) != null) {
            String uri = reference.getUri().toString();
            throw new DuplicateNameException("A service or reference is already registered with the name", uri);
        }
        children.put(name, reference);
        synchronized (services) {
            references.add(reference);
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

    public void prepare() throws PrepareException {
        for (Service service : services) {
            service.prepare();
        }
        for (Reference reference : references) {
            reference.prepare();
        }
    }

}
