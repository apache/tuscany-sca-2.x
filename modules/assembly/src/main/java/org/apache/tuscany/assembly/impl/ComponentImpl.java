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

import org.apache.tuscany.assembly.Component;
import org.apache.tuscany.assembly.ComponentProperty;
import org.apache.tuscany.assembly.ComponentReference;
import org.apache.tuscany.assembly.ComponentService;
import org.apache.tuscany.assembly.ConstrainingType;
import org.apache.tuscany.assembly.Implementation;
import org.apache.tuscany.assembly.util.Visitable;
import org.apache.tuscany.assembly.util.Visitor;
import org.apache.tuscany.policy.Intent;
import org.apache.tuscany.policy.PolicySet;

/**
 * Represents a component.
 * 
 * @version $Rev$ $Date$
 */
public class ComponentImpl implements Component, Visitable {
    private List<Object> extensions = new ArrayList<Object>();
    private boolean unresolved;
    private ConstrainingType constrainingType;
    private Implementation implementation;
    private String name;
    private List<ComponentProperty> properties = new ArrayList<ComponentProperty>();
    private List<ComponentReference> references = new ArrayList<ComponentReference>();
    private List<ComponentService> services = new ArrayList<ComponentService>();
    private List<Intent> requiredIntents = new ArrayList<Intent>();
    private List<PolicySet> policySets = new ArrayList<PolicySet>();
    private boolean autowire;
    
    /**
     * Constructs a new component.
     */
    public ComponentImpl() {
    }
    
    public ComponentImpl(Component other) {
        unresolved = other.isUnresolved();
        extensions.addAll(other.getExtensions());

        constrainingType = other.getConstrainingType();
        implementation = other.getImplementation();
        name = other.getName();
        for (ComponentProperty property: other.getProperties()) {
            properties.add(new ComponentPropertyImpl(property));
        }
        for (ComponentReference reference: other.getReferences()) {
            references.add(new ComponentReferenceImpl(reference));
        }
        for (ComponentService service: other.getServices()) {
            services.add(new ComponentServiceImpl(service));
        }
        requiredIntents.addAll(other.getRequiredIntents());
        policySets.addAll(other.getPolicySets());
        autowire = other.isAutowire();
    }

    public Component instanciate() {
        ComponentImpl instance = new ComponentImpl();
        instance.instanciate(this);
        return instance;
    }
    
    private void instanciate(Component other) {
        unresolved = other.isUnresolved();
        extensions.addAll(other.getExtensions());

        constrainingType = other.getConstrainingType();
        implementation = other.getImplementation();
        name = other.getName();
        properties = other.getProperties();
        for (ComponentReference reference: other.getReferences()) {
            references.add(new ComponentReferenceImpl(reference));
        }
        requiredIntents = other.getRequiredIntents();
        policySets = other.getPolicySets();;
        autowire = other.isAutowire();
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

    public ConstrainingType getConstrainingType() {
        return constrainingType;
    }

    public Implementation getImplementation() {
        return implementation;
    }

    public String getName() {
        return name;
    }

    public List<ComponentProperty> getProperties() {
        return properties;
    }

    public List<ComponentReference> getReferences() {
        return references;
    }

    public List<ComponentService> getServices() {
        return services;
    }

    public void setConstrainingType(ConstrainingType constrainingType) {
        this.constrainingType = constrainingType;
    }

    public void setImplementation(Implementation implementation) {
        this.implementation = implementation;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Intent> getRequiredIntents() {
        return requiredIntents;
    }

    public List<PolicySet> getPolicySets() {
        return policySets;
    }

    public boolean isAutowire() {
        return autowire;
    }

    public void setAutowire(boolean autowire) {
        this.autowire = autowire;
    }

    public boolean accept(Visitor visitor) {
        if (!visitor.visit(this)) {
            return false;
        }
        for (ComponentProperty property : properties) {
            if (!visitor.visit(property)) {
                return false;
            }
        }
        for (ComponentReference reference : references) {
            if (!visitor.visit(reference)) {
                return false;
            }
        }
        for (ComponentService service : services) {
            if (!visitor.visit(service)) {
                return false;
            }
        }
        return true;
    }
}
