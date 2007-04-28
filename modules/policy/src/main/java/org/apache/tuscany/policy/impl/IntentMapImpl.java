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
package org.apache.tuscany.policy.impl;

import java.util.List;

import org.apache.tuscany.policy.Intent;
import org.apache.tuscany.policy.IntentMap;

/**
 * Represents an intent map.
 * 
 * @version $Rev$ $Date$
 */
public class IntentMapImpl implements IntentMap {

    private boolean unresolved;
    private IntentMap defaultQualifiedIntentMap;
    private List<Object> policies;
    private Intent providedIntent;
    private List<IntentMap> qualifiedIntentMaps;
    
    protected IntentMapImpl() {
    }

    public IntentMap getDefaultQualifiedIntentMap() {
        return defaultQualifiedIntentMap;
    }

    public List<Object> getPolicies() {
        return policies;
    }

    public Intent getProvidedIntent() {
        return providedIntent;
    }

    public List<IntentMap> getQualifiedIntentMaps() {
        return qualifiedIntentMaps;
    }

    public void setDefaultQualifiedIntentMap(IntentMap defaultQualifiedIntentMap) {
        this.defaultQualifiedIntentMap = defaultQualifiedIntentMap;
    }

    public void setProvidedIntent(Intent providedIntent) {
        this.providedIntent = providedIntent;
    }

    public boolean isUnresolved() {
        return unresolved;
    }

    public void setUnresolved(boolean unresolved) {
        this.unresolved = unresolved;
    }
}
