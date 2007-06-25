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
package org.apache.tuscany.sca.policy.service;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicySet;

/**
 * This is the interface for the Policy Infrastructure.  It deals with the maintenance of intents and 
 * policy sets defined for a SCA Domain, validating the endpoints of  a wire for 
 *
 */
public interface PolicyService {
    void addIntent(Intent anIntent) throws DuplicateEntryException;
    void addPolicySet(PolicySet aPolicySet)  throws DuplicateEntryException;
    
    void removeIntent(QName intentName) throws EntryNotFoundException;
    void removePolicySet(QName policySetName) throws EntryNotFoundException;
    
}
