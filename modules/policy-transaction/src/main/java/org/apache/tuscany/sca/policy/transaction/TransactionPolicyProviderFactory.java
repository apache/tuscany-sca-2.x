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

package org.apache.tuscany.sca.policy.transaction;

import javax.transaction.TransactionManager;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;
import org.apache.tuscany.sca.provider.PolicyProvider;
import org.apache.tuscany.sca.provider.PolicyProviderFactory;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * @version $Rev$ $Date$
 */
public class TransactionPolicyProviderFactory implements PolicyProviderFactory<TransactionPolicy> {
    private TransactionManagerHelper helper;

    public TransactionPolicyProviderFactory(ExtensionPointRegistry registry) {
        super();
        UtilityExtensionPoint utilities = registry.getExtensionPoint(UtilityExtensionPoint.class);
        TransactionManager tm = utilities.getUtility(TransactionManager.class);
        this.helper = new TransactionManagerHelper(tm);
    }
    
    /**
     * @see org.apache.tuscany.sca.provider.PolicyProviderFactory#createImplementationPolicyProvider(org.apache.tuscany.sca.runtime.RuntimeComponent, org.apache.tuscany.sca.assembly.Implementation)
     */
    public PolicyProvider createImplementationPolicyProvider(RuntimeComponent component, Implementation implementation) {
        if (component instanceof PolicySetAttachPoint) {
            return new TransactionImplementationPolicyProvider(helper, (PolicySetAttachPoint)component);
        } else {
            return null;
        }
    }

    /**
     * @see org.apache.tuscany.sca.provider.PolicyProviderFactory#createReferencePolicyProvider(org.apache.tuscany.sca.runtime.RuntimeComponent, org.apache.tuscany.sca.runtime.RuntimeComponentReference, org.apache.tuscany.sca.assembly.Binding)
     */
    public PolicyProvider createReferencePolicyProvider(RuntimeComponent component,
                                                        RuntimeComponentReference reference,
                                                        Binding binding) {
        if (binding instanceof PolicySetAttachPoint) {
            return new TransactionReferencePolicyProvider(helper, (PolicySetAttachPoint)binding);
        } else {
            return null;
        }
    }

    /**
     * @see org.apache.tuscany.sca.provider.PolicyProviderFactory#createServicePolicyProvider(org.apache.tuscany.sca.runtime.RuntimeComponent, org.apache.tuscany.sca.runtime.RuntimeComponentService, org.apache.tuscany.sca.assembly.Binding)
     */
    public PolicyProvider createServicePolicyProvider(RuntimeComponent component,
                                                      RuntimeComponentService service,
                                                      Binding binding) {
        if (binding instanceof PolicySetAttachPoint) {
            return new TransactionServicePolicyProvider(helper, (PolicySetAttachPoint)binding);
        } else {
            return null;
        }
    }

    /**
     * @see org.apache.tuscany.sca.provider.ProviderFactory#getModelType()
     */
    public Class<TransactionPolicy> getModelType() {
        return TransactionPolicy.class;
    }

}
