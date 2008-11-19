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

package org.apache.tuscany.sca.assembly.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.assembly.ConfiguredOperation;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.OperationsConfigurator;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentAttachPointType;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;

/**
 * Base implementation class of Implementation model interface
 *
 * @version $Rev$ $Date$
 */
public abstract class ImplementationImpl extends ComponentTypeImpl implements Implementation, PolicySetAttachPoint,
    OperationsConfigurator {

    private List<PolicySet> applicablePolicySets = new ArrayList<PolicySet>();
    private List<ConfiguredOperation> configuredOperations = new ArrayList<ConfiguredOperation>();
    private IntentAttachPointType type;
    private List<PolicySet> policySets = new ArrayList<PolicySet>();
    private List<Intent> requiredIntents = new ArrayList<Intent>();

    protected ImplementationImpl() {
        super();
    }

    public List<PolicySet> getApplicablePolicySets() {
        return applicablePolicySets;
    }

    public List<ConfiguredOperation> getConfiguredOperations() {
        return configuredOperations;
    }

    public List<PolicySet> getPolicySets() {
        return policySets;
    }

    public List<Intent> getRequiredIntents() {
        return requiredIntents;
    }

    public IntentAttachPointType getType() {
        return type;
    }

    public void setType(IntentAttachPointType type) {
        this.type = type;
    }

}
