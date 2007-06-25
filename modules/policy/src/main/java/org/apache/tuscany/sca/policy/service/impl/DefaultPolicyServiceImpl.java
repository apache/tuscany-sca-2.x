/**
 *
 * Copyright 2006 The Apache Software Foundation
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
package org.apache.tuscany.sca.policy.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.service.DuplicateEntryException;
import org.apache.tuscany.sca.policy.service.EntryNotFoundException;
import org.apache.tuscany.sca.policy.service.PolicyService;

/**
 * Provides a Default implementation for the Policy Service
 */
public class DefaultPolicyServiceImpl implements PolicyService {
    private Map<QName, Intent> intentsRegistry = new HashMap<QName, Intent>();
    private Map<QName, PolicySet> policySetRegistry = new HashMap<QName, PolicySet>();
    
    public void addIntent(Intent anIntent) throws DuplicateEntryException {
        intentsRegistry.put(anIntent.getName(), anIntent);
    }
    public void addPolicySet(PolicySet aPolicySet) throws DuplicateEntryException {
        policySetRegistry.put(aPolicySet.getName(), aPolicySet);
        
    }
    public void removeIntent(QName intentName) throws EntryNotFoundException {
        intentsRegistry.remove(intentName);
        
    }
    public void removePolicySet(QName policySetName) throws EntryNotFoundException {
        policySetRegistry.remove(policySetName);
    }
}
