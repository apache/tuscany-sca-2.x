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

import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicyContext;
import org.apache.tuscany.sca.policy.PolicySet;

/**
 *
 * @version $Rev$ $Date$
 */
public class PolicyContextImpl implements PolicyContext {
    private Map<Object, List<Intent>> intents = new Hashtable<Object , List<Intent>>();
    private Map<Object, List<PolicySet>> policySets = new Hashtable<Object, List<PolicySet>>();
    
    public void addIntent(Object key, Intent intent) {
        if ( intents.get(key) == null ) {
            intents.put(key, new ArrayList<Intent>());
        }
        intents.get(key).add(intent);
    }

    public void addIntents(Object key, List<Intent> intents) {
        if ( this.intents.get(key) == null ) {
            this.intents.put(key, new ArrayList<Intent>());
        }
        this.intents.get(key).addAll(intents);
    }

    public void addPolicySet(Object key, PolicySet policySet) {
        if ( policySets.get(key) == null ) {
            policySets.put(key, new ArrayList<PolicySet>());
        }
        policySets.get(key).add(policySet);
    }

    public void addPolicySets(Object key, List<PolicySet> policySets) {
        if ( this.policySets.get(key) == null ) {
            this.policySets.put(key, new ArrayList<PolicySet>());
        }
        this.policySets.get(key).addAll(policySets);
    }

    public List<Intent> getIntents(Object key) {
        if ( intents.get(key) == null ) {
            intents.put(key, new ArrayList<Intent>());
        }
        return intents.get(key);
    }

    public List<PolicySet> getPolicySets(Object key) {
        if ( policySets.get(key) == null ) {
            policySets.put(key, new ArrayList<PolicySet>());
        }
        return policySets.get(key);
    }

    public void clearIntents(Object key) {
        if ( intents.get(key) != null ) {
            intents.clear();
        }
    }

    public void clearPolicySets(Object key) {
        if ( policySets.get(key) != null ) {
            policySets.clear();
        }
    }

}
