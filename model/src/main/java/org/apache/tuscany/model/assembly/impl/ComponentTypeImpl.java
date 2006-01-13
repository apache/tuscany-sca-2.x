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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelVisitor;
import org.apache.tuscany.model.assembly.ComponentType;
import org.apache.tuscany.model.assembly.Property;
import org.apache.tuscany.model.assembly.Reference;
import org.apache.tuscany.model.assembly.Service;

/**
 * An implementation of the model object '<em><b>Component Type</b></em>'.
 */
public class ComponentTypeImpl extends org.apache.tuscany.model.assembly.sdo.impl.ComponentTypeImpl implements ComponentType {
    private Map<String, Reference> referencesMap;
    private Map<String, Service> servicesMap;
    private Map<String, Property> propertiesMap;

    /**
     * Constructor
     */
    public ComponentTypeImpl() {
    }

    /**
     * @see org.apache.tuscany.model.assembly.ComponentType#getReferences()
     */
    public List<Reference> getReferences() {
        return super.getReferences();
    }

    /**
     * @see org.apache.tuscany.model.assembly.ComponentType#getReference(java.lang.String)
     */
    public Reference getReference(String name) {
        return referencesMap.get(name);
    }

    /**
     * @see org.apache.tuscany.model.assembly.ComponentType#getServices()
     */
    public List<Service> getServices() {
        return super.getServices();
    }

    /**
     * @see org.apache.tuscany.model.assembly.ComponentType#getService(java.lang.String)
     */
    public Service getService(String name) {
        return servicesMap.get(name);
    }

    /**
     * @see org.apache.tuscany.model.assembly.ComponentType#getProperties()
     */
    public List<Property> getProperties() {
        return super.getProperties();
    }

    /**
     * @see org.apache.tuscany.model.assembly.ComponentType#getProperty(java.lang.String)
     */
    public Property getProperty(String name) {
        return propertiesMap.get(name);
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#initialize(org.apache.tuscany.model.assembly.AssemblyModelContext)
     */
    public void initialize(AssemblyModelContext modelContext) {
        // Populate maps of references, properties and services
        referencesMap = new HashMap<String, Reference>();
        for (Iterator<Reference> i = getReferences().iterator(); i.hasNext();) {
            Reference reference = i.next();
            referencesMap.put(reference.getName(), reference);
            reference.initialize(modelContext);
        }
        propertiesMap = new HashMap<String, Property>();
        for (Iterator<Property> i = getProperties().iterator(); i.hasNext();) {
            Property property = i.next();
            propertiesMap.put(property.getName(), property);
            property.initialize(modelContext);
        }
        servicesMap = new HashMap<String, Service>();
        for (Iterator<Service> i = getServices().iterator(); i.hasNext();) {
            Service service = i.next();
            servicesMap.put(service.getName(), service);
            service.initialize(modelContext);
        }
    }

    public void freeze() {
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#accept(org.apache.tuscany.model.assembly.AssemblyModelVisitor)
     */
    public boolean accept(AssemblyModelVisitor visitor) {
        return AssemblyModelVisitorHelperImpl.accept(this, visitor);
    }

} //ComponentTypeImpl
