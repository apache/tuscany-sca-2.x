#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
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

package ${package}.${policyName}.provider;

import java.util.List;

import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Phase;
import org.apache.tuscany.sca.invocation.PhasedInterceptor;
import org.apache.tuscany.sca.provider.BasePolicyProvider;

import ${package}.util.PolicyHelper;
import ${package}.${policyName}.${policyName}Policy;

/**
 * @version ${symbol_dollar}Rev${symbol_dollar} ${symbol_dollar}Date${symbol_dollar}
 */
public class ${policyName}ServicePolicyProvider extends BasePolicyProvider<${policyName}Policy> {
    private Endpoint endpoint;
    
    public ${policyName}ServicePolicyProvider(Endpoint endpoint) {
        super(${policyName}Policy.class, endpoint);
        this.endpoint = endpoint;
    }

    @Override
    public PhasedInterceptor createInterceptor(Operation operation) {
        List<PolicySet> policySets = PolicyHelper.findPolicySets(endpoint, ${policyName}Policy.POLICY_QNAME);
        
        return policySets.isEmpty() ? null : new ${policyName}PolicyInterceptor(getContext(), operation, Phase.SERVICE_POLICY);
    }

}
