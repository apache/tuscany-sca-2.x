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

package org.apache.tuscany.implementation.spi;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.assembly.ConstrainingType;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicySet;

/**
 * TODO: couldn't something like this class be provided by the runtime?
 */
public abstract class AbstractImplementation implements Implementation {

    private List<Service> services = new ArrayList<Service>();
    private List<Reference> references = new ArrayList<Reference>();
    private List<Property> properties = new ArrayList<Property>();
    private ConstrainingType constrainingType;
    private String uri;
    private boolean unresolved = true;
    private List<Intent> intents;
    private List<PolicySet> policySets;

    public AbstractImplementation() {
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

    public String getURI() {
        return uri;
    }

    public void setURI(String uri) {
        this.uri = uri;
    }

    public ConstrainingType getConstrainingType() {
        return constrainingType;
    }

    public void setConstrainingType(ConstrainingType constrainingType) {
        this.constrainingType = constrainingType;
    }

    public List<Intent> getRequiredIntents() {
        return intents;
    }

    public List<PolicySet> getPolicySets() {
        return policySets;
    }

    public List<Object> getExtensions() {
        // TODO what is this for?
        return null;
    }

    public boolean isUnresolved() {
        // TODO what is this for?
        return unresolved;
    }

    public void setUnresolved(boolean unresolved) {
        // TODO what is this for?
        this.unresolved = unresolved;
    }

}
