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
package org.apache.tuscany.sca.assembly.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentProperty;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.ConstrainingType;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentAttachPointType;
import org.apache.tuscany.sca.policy.PolicySet;

/**
 * Represents a component.
 * 
 * @version $Rev$ $Date$
 */
public class ComponentImpl extends ExtensibleImpl implements Component, Cloneable {
    private ConstrainingType constrainingType;
    private Implementation implementation;
    private String name;
    private String uri;
    private List<ComponentProperty> properties = new ArrayList<ComponentProperty>();
    private List<ComponentReference> references = new ArrayList<ComponentReference>();
    private List<ComponentService> services = new ArrayList<ComponentService>();
    private List<Intent> requiredIntents = new ArrayList<Intent>();
    private List<PolicySet> policySets = new ArrayList<PolicySet>();
    private Boolean autowire;
    private IntentAttachPointType type;

    /**
     * Constructs a new component.
     */
    protected ComponentImpl() {
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        ComponentImpl clone = (ComponentImpl)super.clone();

        clone.properties = new ArrayList<ComponentProperty>();
        for (ComponentProperty property : getProperties()) {
            clone.properties.add((ComponentProperty)property.clone());
        }
        clone.references = new ArrayList<ComponentReference>();
        for (ComponentReference reference : getReferences()) {
            clone.references.add((ComponentReference)reference.clone());
        }
        clone.services = new ArrayList<ComponentService>();
        for (ComponentService service : getServices()) {
            clone.services.add((ComponentService)service.clone());
        }
        return clone;
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
        return (autowire == null) ? false : autowire.booleanValue();
    }

    public void setAutowire(Boolean autowire) {
        this.autowire = autowire;
    }
    
    public Boolean getAutowire() {
        return autowire;
    }

    public IntentAttachPointType getType() {
        return type;
    }

    public void setType(IntentAttachPointType type) {
        this.type = type;
    }

}
