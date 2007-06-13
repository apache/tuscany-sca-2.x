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

package org.apache.tuscany.sca.topology.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ConstrainingType;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.topology.ProcessorImplementation;

/**
 * Represents an SCA processor component implementation.
 *
 * @version $Rev$ $Date$
 */
public class ProcessorImplementationImpl implements ProcessorImplementation {
    
    private List<Component> components = new ArrayList<Component>();
    private List<Property> properties = new ArrayList<Property>();
    private List<Service> services = new ArrayList<Service>();
    private List<Reference> references = new ArrayList<Reference>();
    private ConstrainingType constrainingType;
    private List<PolicySet> policySets = new ArrayList<PolicySet>();
    private List<Intent> requiredIntents = new ArrayList<Intent>();
    private List<Object> extensions = new ArrayList<Object>();
    private boolean unresolved;
    private String uri;

    /**
     * Constructs a new processor implementation.
     */
    protected ProcessorImplementationImpl() {
    }
    
    public List<Component> getComponents() {
        return components;
    }

    public ConstrainingType getConstrainingType() {
        return constrainingType;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public List<Service> getServices() {
        return services;
    }
    
    public List<Reference> getReferences() {
        return references;
    }

    public String getURI() {
        return uri;
    }

    public void setConstrainingType(ConstrainingType constrainingType) {
        this.constrainingType = constrainingType;
    }

    public void setURI(String uri) {
        this.uri = uri;
    }

    public List<PolicySet> getPolicySets() {
        return policySets;
    }

    public List<Intent> getRequiredIntents() {
        return requiredIntents;
    }

    public List<Object> getExtensions() {
        return extensions;
    }

    public boolean isUnresolved() {
        return unresolved;
    }

    public void setUnresolved(boolean unresolved) {
        this.unresolved = unresolved;
    }

}
