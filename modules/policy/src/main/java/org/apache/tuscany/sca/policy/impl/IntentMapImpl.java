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
import java.util.List;

import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentMap;
import org.apache.tuscany.sca.policy.Qualifier;

/**
 * Represents an intent map.
 * 
 * @version $Rev$ $Date$
 */
public class IntentMapImpl implements IntentMap {

    private boolean unresolved;
    private Intent providedIntent;
    private List<Qualifier> qualifiers = new ArrayList<Qualifier>();
    
    protected IntentMapImpl() {
    }

    public boolean isUnresolved() {
        return unresolved;
    }

    public void setUnresolved(boolean unresolved) {
        this.unresolved = unresolved;
    }

    public Intent getProvidedIntent() {
        return providedIntent;
    }

    public void setProvidedIntent(Intent providedIntent) {
        this.providedIntent = providedIntent;
    }

    public List<Qualifier> getQualifiers() {
        return qualifiers;
    }
}
