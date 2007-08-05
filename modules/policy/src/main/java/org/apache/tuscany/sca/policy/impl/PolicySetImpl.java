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
package org.apache.tuscany.sca.policy.impl;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.Policy;
import org.apache.tuscany.sca.policy.PolicySet;

/**
 * Represents a policy set.
 * 
 * @version $Rev$ $Date$
 */
public class PolicySetImpl implements PolicySet {

    private QName name;
    private List<Operation> operations = new ArrayList<Operation>();
    //private List<QName> appliesTo;
    private String appliesTo;
    private List<Intent> providedIntents = new ArrayList<Intent>();
    private List<PolicySet> referencedPolicySets = new ArrayList<PolicySet>();
    private List<Object> policies = new ArrayList<Object>();
    Map<Intent, List<Policy>>  mappedPolicies = new Hashtable<Intent, List<Policy>>();
    private boolean unresolved = true;
    
    protected PolicySetImpl() {
    }

    public QName getName() {
        return name;
    }

    public void setName(QName name) {
        this.name = name;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public String getAppliesTo() {
        return appliesTo;
    }

    public void setAppliesTo(String appliesTo) {
        this.appliesTo = appliesTo;
    }

    public List<Intent> getProvidedIntents() {
        return providedIntents;
    }

    public List<PolicySet> getReferencedPolicySets() {
        return referencedPolicySets;
    }

    public List<Object> getPolicies() {
        return policies;
    }

    public boolean isUnresolved() {
        return unresolved;
    }

    public void setUnresolved(boolean unresolved) {
        this.unresolved = unresolved;
    }

    public Map<Intent, List<Policy>> getMappedPolicies() {
        return mappedPolicies;
    }
    
    @Override
    public int hashCode() {
        return String.valueOf(getName()).hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof PolicySet) {
            if (getName() != null) {
                return getName().equals(((PolicySet)obj).getName());
            } else {
                return ((PolicySet)obj).getName() == null;
            }
        } else {
            return false;
        }
    }
    
    public String toString() {
        return getName().toString();
    }
}
