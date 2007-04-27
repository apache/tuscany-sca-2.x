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
package org.apache.tuscany.assembly.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.assembly.Base;
import org.apache.tuscany.assembly.ComponentType;
import org.apache.tuscany.assembly.ConstrainingType;
import org.apache.tuscany.assembly.Property;
import org.apache.tuscany.assembly.Reference;
import org.apache.tuscany.assembly.Service;
import org.apache.tuscany.assembly.util.Visitable;
import org.apache.tuscany.assembly.util.Visitor;
import org.apache.tuscany.policy.Intent;
import org.apache.tuscany.policy.PolicySet;

/**
 * Represents a component type.
 * 
 * @version $Rev$ $Date$
 */
public class ComponentTypeImpl implements ComponentType, Visitable {
    private List<Object> extensions = new ArrayList<Object>();
    private boolean unresolved;
    private String uri;
    private ConstrainingType constrainingType;
    private List<Property> properties = new ArrayList<Property>();
    private List<Reference> references = new ArrayList<Reference>();
    private List<Service> services = new ArrayList<Service>();
    private List<Intent> requiredIntents = new ArrayList<Intent>();
    private List<PolicySet> policySets = new ArrayList<PolicySet>();
    
    /**
     * Constructs a new component type.
     */
    public ComponentTypeImpl() {
    }
    
    /**
     * Copy constructor.
     * @param other
     */
    public ComponentTypeImpl(ComponentType other) {
        unresolved = other.isUnresolved();
        extensions.addAll(other.getExtensions());
        
        uri = other.getURI();
        constrainingType = other.getConstrainingType();
        for (Service service: other.getServices()) {
            services.add(new ServiceImpl(service));
        }
        for (Reference reference: other.getReferences()) {
            references.add(new ReferenceImpl(reference));
        }
        for (Property property: other.getProperties()) {
            properties.add(new PropertyImpl(property));
        }
        requiredIntents.addAll(other.getRequiredIntents());
        policySets.addAll(other.getPolicySets());
    }
    
    /**
     * Instanciate...
     * @param other
     */
    protected void instanciate(ComponentType other) {
        unresolved = other.isUnresolved();
        extensions = other.getExtensions();
        
        uri = other.getURI();
        constrainingType = other.getConstrainingType();
        services = other.getServices();
        references = other.getReferences();
        properties = other.getProperties();
        requiredIntents = other.getRequiredIntents();
        policySets = other.getPolicySets();
    }
    
    public String getURI() {
        return uri;
    }
    
    public void setURI(String uri) {
        this.uri = uri;
    }

    public ConstrainingType getConstrainingType() {
        return constrainingType;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public List<Reference> getReferences() {
        return references;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setConstrainingType(ConstrainingType constrainingType) {
        this.constrainingType = constrainingType;
    }

    public List<Intent> getRequiredIntents() {
        return requiredIntents;
    }

    public List<PolicySet> getPolicySets() {
        return policySets;
    }

    public List<Object> getExtensions() {
        return extensions;
    }

    public boolean isUnresolved() {
        return unresolved;
    }

    public void setUnresolved(boolean undefined) {
        this.unresolved = undefined;
    }

    public boolean accept(Visitor visitor) {
        if (!visitor.visit(this)) {
            return false;
        }
        for (Property property : properties) {
            if (!visitor.visit(property)) {
                return false;
            }
        }
        for (Reference reference : references) {
            if (!visitor.visit(reference)) {
                return false;
            }
        }
        for (Service service : services) {
            if (!visitor.visit(service)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return String.valueOf(getURI()).hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else {
            if (obj instanceof ComponentType) {
                if (getURI() != null) {
                    return getURI().equals(((ComponentType)obj).getURI());
                } else {
                    return ((ComponentType)obj).getURI() == null;
                }
            } else {
                return false;
            }
        }
    }
}
