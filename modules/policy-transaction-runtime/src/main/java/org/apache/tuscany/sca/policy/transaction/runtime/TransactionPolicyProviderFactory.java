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

package org.apache.tuscany.sca.policy.transaction.runtime;

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.policy.transaction.TransactionPolicy;
import org.apache.tuscany.sca.provider.PolicyProvider;
import org.apache.tuscany.sca.provider.PolicyProviderFactory;
import org.apache.tuscany.sca.runtime.RuntimeComponent;


/**
 * @version $Rev$ $Date$
 */
public class TransactionPolicyProviderFactory implements PolicyProviderFactory<TransactionPolicy> {
    private TransactionManagerHelper helper;

    public TransactionPolicyProviderFactory(ExtensionPointRegistry registry) {
        super();
        UtilityExtensionPoint utilities = registry.getExtensionPoint(UtilityExtensionPoint.class);
        this.helper = utilities.getUtility(TransactionManagerHelper.class); 
    }

    public PolicyProvider createImplementationPolicyProvider(RuntimeComponent component) {
        return new TransactionImplementationPolicyProvider(helper, component);
    }

    public PolicyProvider createReferencePolicyProvider(EndpointReference endpointReference) {
        return new TransactionReferencePolicyProvider(helper, endpointReference);
    }
    
    public PolicyProvider createServicePolicyProvider(Endpoint endpoint) {
        return new TransactionServicePolicyProvider(helper, endpoint);
    }

    public Class<TransactionPolicy> getModelType() {
        return TransactionPolicy.class;
    }

}
