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

package org.apache.tuscany.sca.definitions.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.tuscany.sca.definitions.SCADefinitions;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentAttachPointType;
import org.apache.tuscany.sca.policy.PolicySet;

/**
 * Provides a concrete implementation for SCADefinitions
 *
 * @version $Rev$ $Date$
 */
public class SCADefinitionsImpl implements SCADefinitions {
    private String targetNamespace = null;
    private List<Intent> policyIntents = new CopyOnWriteArrayList<Intent>();
    private List<PolicySet> policySets = new CopyOnWriteArrayList<PolicySet>();
    private List<IntentAttachPointType> bindingTypes = new CopyOnWriteArrayList<IntentAttachPointType>();
    private List<IntentAttachPointType> implementationTypes = new CopyOnWriteArrayList<IntentAttachPointType>();
    private List<Object> bindings = new CopyOnWriteArrayList<Object>();

   
    public List<IntentAttachPointType> getBindingTypes() {
        return bindingTypes;
    }

    public List<IntentAttachPointType> getImplementationTypes() {
        return implementationTypes;
    }

    public List<Intent> getPolicyIntents() {
        return policyIntents;
    }

    public List<PolicySet> getPolicySets() {
        return policySets;
    }

    public String getTargetNamespace() {
        return targetNamespace;
    }

    public void setTargetNamespace(String ns) {
       this.targetNamespace = ns;
    }

    public List<Object> getBindings() {
        return bindings;
    }
}
