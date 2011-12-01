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

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.policy.DefaultIntent;
import org.apache.tuscany.sca.policy.Intent;

/**
 * Represents a default policy intent and the
 * intents that must not be present for it to 
 * be active
 */
public class DefaultIntentImpl implements DefaultIntent {

    private Intent defaultIntent = null;
    private List<Intent> mutuallyExclusiveIntents = new ArrayList<Intent>();

    protected DefaultIntentImpl() {
    }

    @Override
    public Intent getIntent() {
        return defaultIntent;
    }
    
    @Override
    public void setIntent(Intent defaultIntent) {
        this.defaultIntent = defaultIntent;
    }
    
    @Override
    public List<Intent> getMutuallyExclusiveIntents() {
        return mutuallyExclusiveIntents;
    }
    
    public String toString() {
        return String.valueOf(defaultIntent.getName());
    }
}
