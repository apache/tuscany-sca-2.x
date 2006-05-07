/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.model.assembly.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.model.assembly.AssemblyContext;
import org.apache.tuscany.model.assembly.AssemblyVisitor;
import org.apache.tuscany.model.assembly.ComponentType;
import org.apache.tuscany.model.assembly.Port;
import org.apache.tuscany.model.assembly.Property;
import org.apache.tuscany.model.assembly.Reference;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.util.NotifyingList;

/**
 * An implementation of ComponentType.
 */
public class ComponentTypeImpl extends ExtensibleImpl implements ComponentType {
    
    /**
     * A list of ports synchronized with a map
     */
    private class PortList<E extends Port> extends NotifyingList<E>{
        
        Map<String, E> map;
        
        private PortList(Map<String, E> map) {
            this.map=map;
        }
        
        protected void added(E element) {
            map.put(element.getName(), element);
        }
        protected void removed(E element) {
            map.remove(element.getName());
        }
    }
    
    private Map<String, Reference> referencesMap=new HashMap<String, Reference>();
    private List<Reference> references=new PortList<Reference>(referencesMap);
    
    private Map<String, Service> servicesMap=new HashMap<String, Service>();
    private List<Service> services=new PortList<Service>(servicesMap);

    /**
     * A list of properties synchronized with a map
     */
    private class PropertyList<E extends Property> extends NotifyingList<E>{
        
        protected void added(E element) {
            propertiesMap.put(element.getName(), element);
        }
        protected void removed(E element) {
            propertiesMap.remove(element.getName());
        }
    }
    
    private Map<String, Property> propertiesMap=new HashMap<String, Property>();
    private List<Property> properties=new PropertyList<Property>();

    protected ComponentTypeImpl() {
    }

    public List<Reference> getReferences() {
        return references;
    }

    public Reference getReference(String name) {
        return referencesMap.get(name);
    }

    public List<Service> getServices() {
        return services;
    }

    public Service getService(String name) {
        return servicesMap.get(name);
    }

    public List<Property> getProperties() {
        return properties;
    }

    public Property getProperty(String name) {
        return propertiesMap.get(name);
    }

    public void initialize(AssemblyContext modelContext) {
        if (isInitialized())
            return;
        super.initialize(modelContext);

        // Initialize references, properties and services
        for (Reference reference : references) {
            reference.initialize(modelContext);
        }
        for (Property property : properties) {
            property.initialize(modelContext);
        }
        for (Service service : services) {
            service.initialize(modelContext);
        }
    }

    public void freeze() {
        if (isFrozen())
            return;
        super.freeze();
        
        // Freeze lists of services, references and properties
        services=freeze(services);
        references=freeze(references);
        properties=freeze(properties);
    }

    public boolean accept(AssemblyVisitor visitor) {
        if (!super.accept(visitor))
            return false;
        
        if (!accept(services, visitor))
            return false;
        if (!accept(references, visitor))
            return false;
        if (!accept(properties, visitor))
            return false;
    
        return true;
    }
    
}
