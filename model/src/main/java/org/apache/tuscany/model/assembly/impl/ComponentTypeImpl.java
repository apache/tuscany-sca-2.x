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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelVisitor;
import org.apache.tuscany.model.assembly.ComponentType;
import org.apache.tuscany.model.assembly.Property;
import org.apache.tuscany.model.assembly.Reference;
import org.apache.tuscany.model.assembly.Service;

/**
 * An implementation of ComponentType.
 */
public class ComponentTypeImpl extends ExtensibleImpl implements ComponentType {
    
    private List<Reference> references=new ArrayList<Reference>();
    private Map<String, Reference> referencesMap;
    private List<Service> services=new ArrayList<Service>();
    private Map<String, Service> servicesMap;
    private List<Property> properties=new ArrayList<Property>();
    private Map<String, Property> propertiesMap;

    /**
     * Constructor
     */
    protected ComponentTypeImpl() {
    }

    /**
     * @see org.apache.tuscany.model.assembly.ComponentType#getReferences()
     */
    public List<Reference> getReferences() {
        return references;
    }

    /**
     * @see org.apache.tuscany.model.assembly.ComponentType#getReference(java.lang.String)
     */
    public Reference getReference(String name) {
        checkInitialized();
        return referencesMap.get(name);
    }

    /**
     * @see org.apache.tuscany.model.assembly.ComponentType#getServices()
     */
    public List<Service> getServices() {
        return services;
    }

    /**
     * @see org.apache.tuscany.model.assembly.ComponentType#getService(java.lang.String)
     */
    public Service getService(String name) {
        checkInitialized();
        return servicesMap.get(name);
    }

    /**
     * @see org.apache.tuscany.model.assembly.ComponentType#getProperties()
     */
    public List<Property> getProperties() {
        return properties;
    }

    /**
     * @see org.apache.tuscany.model.assembly.ComponentType#getProperty(java.lang.String)
     */
    public Property getProperty(String name) {
        checkInitialized();
        return propertiesMap.get(name);
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#initialize(org.apache.tuscany.model.assembly.AssemblyModelContext)
     */
    public void initialize(AssemblyModelContext modelContext) {
        if (isInitialized())
            return;
        super.initialize(modelContext);

        // Populate maps of references, properties and services
        referencesMap = new HashMap<String, Reference>();
        for (Reference reference : references) {
            referencesMap.put(reference.getName(), reference);
            reference.initialize(modelContext);
        }
        propertiesMap = new HashMap<String, Property>();
        for (Property property : properties) {
            propertiesMap.put(property.getName(), property);
            property.initialize(modelContext);
        }
        servicesMap = new HashMap<String, Service>();
        for (Service service : services) {
            servicesMap.put(service.getName(), service);
            service.initialize(modelContext);
        }
    }

    public void freeze() {
        if (isFrozen())
            return;
        super.freeze();
        
        // Freeze lists of services, references and properties
        services=Collections.unmodifiableList(services);
        freeze(services);
        references=Collections.unmodifiableList(references);
        freeze(references);
        properties=Collections.unmodifiableList(properties);
        freeze(properties);
    }

    /**
     * @see org.apache.tuscany.model.assembly.impl.AssemblyModelObjectImpl#accept(org.apache.tuscany.model.assembly.AssemblyModelVisitor)
     */
    public boolean accept(AssemblyModelVisitor visitor) {
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
