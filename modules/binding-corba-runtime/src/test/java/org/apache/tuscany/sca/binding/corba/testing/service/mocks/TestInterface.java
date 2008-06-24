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

package org.apache.tuscany.sca.binding.corba.testing.service.mocks;

import java.util.List;

import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentAttachPointType;
import org.apache.tuscany.sca.policy.PolicySet;

/**
 * Mock TestInterface implementation. Only few methods needs to be implemented.
 */
public class TestInterface implements Interface {

    private List<Operation> operations;

    public TestInterface(List<Operation> opearations) {
        this.operations = opearations;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public boolean isConversational() {
        return false;
    }

    public boolean isDynamic() {
        return false;
    }

    public boolean isRemotable() {
        return false;
    }

    public void resetDataBinding(String dataBinding) {

    }

    public void setConversational(boolean conversational) {

    }

    public void setDefaultDataBinding(String dataBinding) {

    }

    public void setRemotable(boolean remotable) {

    }

    public List<PolicySet> getApplicablePolicySets() {
        return null;
    }

    public List<PolicySet> getPolicySets() {
        return null;
    }

    public List<Intent> getRequiredIntents() {
        return null;
    }

    public IntentAttachPointType getType() {
        return null;
    }

    public void setType(IntentAttachPointType type) {

    }

    public Object clone() {
        return null;
    }

}
