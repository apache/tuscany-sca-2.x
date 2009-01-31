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

import java.util.List;

import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Phase;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;
import org.apache.tuscany.sca.provider.PolicyProvider;

/**
 * @version $Rev$ $Date$
 */
public class TransactionReferencePolicyProvider implements PolicyProvider {
    private TransactionManagerHelper helper;
    private List<PolicySet> policySets;

    public TransactionReferencePolicyProvider(TransactionManagerHelper helper, PolicySetAttachPoint attachPoint) {
        super();
        this.helper = helper;
        this.policySets = attachPoint.getApplicablePolicySets();
    }

    public Interceptor createInterceptor(Operation operation) {
        for (PolicySet policySet : policySets) {
            for (Object p : policySet.getPolicies()) {
                if (p instanceof TransactionPolicy) {
                    TransactionInterceptor interceptor =
                        new TransactionInterceptor(helper, true, (TransactionPolicy)p, null);
                    return interceptor;
                }
            }
        }
        return null;
    }

    public String getPhase() {
        return Phase.REFERENCE_POLICY;
    }

}
