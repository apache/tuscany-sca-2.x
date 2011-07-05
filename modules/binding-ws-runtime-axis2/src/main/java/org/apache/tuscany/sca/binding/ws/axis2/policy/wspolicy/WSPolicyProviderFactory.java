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

package org.apache.tuscany.sca.binding.ws.axis2.policy.wspolicy;

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.policy.wspolicy.WSPolicy;
import org.apache.tuscany.sca.provider.PolicyProvider;
import org.apache.tuscany.sca.provider.PolicyProviderFactory;
import org.apache.tuscany.sca.runtime.RuntimeComponent;

/**
 * @version $Rev: 916317 $ $Date: 2010-02-25 15:16:44 +0000 (Thu, 25 Feb 2010) $
 */
public class WSPolicyProviderFactory implements PolicyProviderFactory<WSPolicy> {
    private ExtensionPointRegistry registry;
    
    public WSPolicyProviderFactory(ExtensionPointRegistry registry) {
        super();
        this.registry = registry;
    }

    public PolicyProvider createImplementationPolicyProvider(RuntimeComponent component) {
        return null;
    }

    public PolicyProvider createReferencePolicyProvider(EndpointReference endpointReference) {
        return new WSPolicyProvider(endpointReference);
    }

    public PolicyProvider createServicePolicyProvider(Endpoint endpoint) {
        return new WSPolicyProvider(endpoint);
    }

    public Class<WSPolicy> getModelType() {
        return WSPolicy.class;
    }

}
