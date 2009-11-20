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

package org.apache.tuscany.sca.binding.ws.axis2.policy.header;

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.provider.PolicyProvider;
import org.apache.tuscany.sca.provider.PolicyProviderFactory;
import org.apache.tuscany.sca.runtime.RuntimeComponent;

/**
 * @version $Rev$ $Date$
 */
public class Axis2HeaderPolicyProviderFactory implements PolicyProviderFactory<Axis2HeaderPolicy> {
    private ExtensionPointRegistry registry;
    
    public Axis2HeaderPolicyProviderFactory(ExtensionPointRegistry registry) {
        super();
        this.registry = registry;
    }

    public PolicyProvider createImplementationPolicyProvider(RuntimeComponent component) {
        return null;//new TokenAuthenticationImplementationPolicyProvider(component, implementation);
    }

    public PolicyProvider createReferencePolicyProvider(EndpointReference endpointReference) {
        return new Axis2HeaderReferencePolicyProvider(endpointReference);
    }

    public PolicyProvider createServicePolicyProvider(Endpoint endpoint) {
        return new Axis2HeaderServicePolicyProvider(endpoint);
    }

    /**
     * @see org.apache.tuscany.sca.provider.ProviderFactory#getModelType()
     */
    public Class<Axis2HeaderPolicy> getModelType() {
        return Axis2HeaderPolicy.class;
    }

}
