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

import java.util.List;

import org.apache.tuscany.sca.policy.IntentMap;
import org.apache.tuscany.sca.policy.IntentMapQualifier;

/**
 * @author administrator
 *
 */
public class IntentMapQualifierImpl implements IntentMapQualifier {

    private IntentMap qualifiedIntentMap = null;
    
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    public List<Object> getPolicies() {
        return policies;
    }

    public IntentMap getQualifiedIntentMap() {
        return qualifiedIntentMap;
    }

    
    /* (non-Javadoc)
     * @see org.apache.tuscany.sca.policy.IntentMapQualifier#setName(java.lang.String)
     */
    public void setName(String name) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.apache.tuscany.sca.policy.IntentMapQualifier#setQualifiedIntentMap(org.apache.tuscany.sca.policy.IntentMap)
     */
    public void setQualifiedIntentMap(IntentMap intentMap) {
        // TODO Auto-generated method stub

    }

    public boolean isUnresolved() {
        return unresolved;
    }

    public void setUnresolved(boolean unresolved) {
        this.unresolved = unresolved;
    }

}
