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

package org.apache.tuscany.sca.binding.atom.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.OptimizableBinding;
import org.apache.tuscany.sca.binding.atom.AtomBinding;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentAttachPointType;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;

/**
 * Implementation of the Atom Feed binding model.
 *
 * @version $Rev$ $Date$
 */
class AtomBindingImpl implements AtomBinding, PolicySetAttachPoint {

    private String name;
    private String uri;
    private String title;
    private String description;
    private List<Intent> requiredIntents = new ArrayList<Intent>();
    private List<PolicySet> policySets = new ArrayList<PolicySet>();
    private IntentAttachPointType intentAttachPointType;
    private List<PolicySet> applicablePolicySets = new ArrayList<PolicySet>();

    public String getName() {
        return name;
    }

    public String getURI() {
        return uri;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setURI(String uri) {
        this.uri = uri;
    }

    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription() {
        return description;
    }

    /**
     * {@inheritDoc}
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isUnresolved() {
        // The binding is always resolved
        return false;
    }

    public void setUnresolved(boolean unresolved) {
        // The binding is always resolved
    }

    //Policy related getters/setters
    
    public List<PolicySet> getPolicySets() {
        return policySets;
    }
    
    public List<Intent> getRequiredIntents() {
        return requiredIntents;
    }

    public IntentAttachPointType getType() {
        return intentAttachPointType;
    }
    
    public void setType(IntentAttachPointType intentAttachPointType) {
        this.intentAttachPointType = intentAttachPointType;
    }

    public void setPolicySets(List<PolicySet> policySets) {
        this.policySets = policySets; 
    }

    public void setRequiredIntents(List<Intent> intents) {
        this.requiredIntents = intents;
    }
    
    public List<PolicySet> getApplicablePolicySets() {
        return applicablePolicySets;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
