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

package org.apache.tuscany.sca.binding.jms.policy.authentication.token;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.provider.PolicyProvider;
import org.apache.tuscany.sca.provider.PolicyProviderFactory;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * @version $Rev$ $Date$
 */
public class JMSTokenAuthenticationPolicyProviderFactory implements PolicyProviderFactory<JMSTokenAuthenticationPolicy> {
    private ExtensionPointRegistry registry;
    
    public JMSTokenAuthenticationPolicyProviderFactory(ExtensionPointRegistry registry) {
        super();
        this.registry = registry;
    }

    /**
     * @see org.apache.tuscany.sca.provider.PolicyProviderFactory#createImplementationPolicyProvider(org.apache.tuscany.sca.runtime.RuntimeComponent, org.apache.tuscany.sca.assembly.Implementation)
     */
    public PolicyProvider createImplementationPolicyProvider(RuntimeComponent component, Implementation implementation) {
        return null;//new TokenAuthenticationImplementationPolicyProvider(component, implementation);
    }

    /**
     * @see org.apache.tuscany.sca.provider.PolicyProviderFactory#createReferencePolicyProvider(org.apache.tuscany.sca.runtime.RuntimeComponent, org.apache.tuscany.sca.runtime.RuntimeComponentReference, org.apache.tuscany.sca.assembly.Binding)
     */
    public PolicyProvider createReferencePolicyProvider(RuntimeComponent component,
                                                        RuntimeComponentReference reference,
                                                        Binding binding) {
        return new JMSTokenAuthenticationReferencePolicyProvider(component, reference, binding);
    }

    /**
     * @see org.apache.tuscany.sca.provider.PolicyProviderFactory#createServicePolicyProvider(org.apache.tuscany.sca.runtime.RuntimeComponent, org.apache.tuscany.sca.runtime.RuntimeComponentService, org.apache.tuscany.sca.assembly.Binding)
     */
    public PolicyProvider createServicePolicyProvider(RuntimeComponent component,
                                                      RuntimeComponentService service,
                                                      Binding binding) {
        return new JMSTokenAuthenticationServicePolicyProvider(component, service, binding);
    }

    /**
     * @see org.apache.tuscany.sca.provider.ProviderFactory#getModelType()
     */
    public Class getModelType() {
        // TODO Auto-generated method stub
        return null;
    }

}
