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
import org.apache.tuscany.assembly.SCABinding;
import org.apache.tuscany.policy.Intent;
import org.apache.tuscany.policy.PolicySet;

/**
 * Represents an SCA binding.
 * 
 * @version $Rev$ $Date$
 */
public class SCABindingImpl implements SCABinding {
    private String name;
    private String uri;
    private List<PolicySet> policySets = new ArrayList<PolicySet>();
    private List<Intent> requiredIntents = new ArrayList<Intent>();
    private List<Object> extensions = new ArrayList<Object>();
    
    private Component component;
    
    /**
     * Constructs a new SCA binding.
     */
    protected SCABindingImpl() {
    }
    
    public Component getComponent() {
        return component;
    }
    
    public void setComponent(Component component) {
        this.component = component;
    }

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
        return false;
    }
    
    public void setUnresolved(boolean unresolved) {
    }
}
