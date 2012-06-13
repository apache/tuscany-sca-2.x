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

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.provider.PolicyProvider;
import org.apache.tuscany.sca.provider.PolicyProviderFactory;
import org.apache.tuscany.sca.runtime.RuntimeComponent;

import ${package}.${policyName}.${policyName}Policy;

/**
 * @version ${symbol_dollar}Rev${symbol_dollar} ${symbol_dollar}Date${symbol_dollar}
 */
public class ${policyName}PolicyProviderFactory implements PolicyProviderFactory<${policyName}Policy> {
    private ExtensionPointRegistry registry;
    
    public ${policyName}PolicyProviderFactory(ExtensionPointRegistry registry) {
        super();
        this.registry = registry;
    }

    /**
     * @see ${groupId}.provider.PolicyProviderFactory${symbol_pound}createImplementationPolicyProvider(${groupId}.runtime.RuntimeComponent, ${groupId}.assembly.Implementation)
     */
    @Override
    public PolicyProvider createImplementationPolicyProvider(RuntimeComponent component) {
        return new ${policyName}ImplementationPolicyProvider(component);
    }

    /**
     * @see ${groupId}.provider.PolicyProviderFactory${symbol_pound}createReferencePolicyProvider(${groupId}.runtime.RuntimeComponent, ${groupId}.runtime.RuntimeComponentReference, ${groupId}.assembly.Binding)
     */
    @Override
    public PolicyProvider createReferencePolicyProvider(EndpointReference endpointReference) {
        return new ${policyName}ReferencePolicyProvider(endpointReference);
    }

    /**
     * @see ${groupId}.provider.PolicyProviderFactory${symbol_pound}createServicePolicyProvider(${groupId}.runtime.RuntimeComponent, ${groupId}.runtime.RuntimeComponentService, ${groupId}.assembly.Binding)
     */
    @Override
    public PolicyProvider createServicePolicyProvider(Endpoint endpoint) {
        return new ${policyName}ServicePolicyProvider(endpoint);
    }

    /**
     * @see ${groupId}.provider.ProviderFactory${symbol_pound}getModelType()
     */
    @Override
    public Class<${policyName}Policy> getModelType() {
        return ${policyName}Policy.class;
    }

}
