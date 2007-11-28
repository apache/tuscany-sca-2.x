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

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.apache.tuscany.sca.runtime.RuntimeWireProcessor;

/**
 * @version $Rev$ $Date$
 */
public class TransactionRuntimeWireProcessor implements RuntimeWireProcessor {
    private TransactionManagerHelper helper;

    public TransactionRuntimeWireProcessor(TransactionManagerHelper helper) {
        super();
        this.helper = helper;
    }

    /**
     * @see org.apache.tuscany.sca.runtime.RuntimeWireProcessor#process(org.apache.tuscany.sca.runtime.RuntimeWire)
     */
    public void process(RuntimeWire wire) {
        boolean outbound = (wire.getSource().getContract() instanceof Reference);
        Component component = outbound ? wire.getSource().getComponent() : wire.getTarget().getComponent();
        Binding binding = outbound ? wire.getSource().getBinding() : wire.getTarget().getBinding();

        TransactionPolicy interactionPolicy = null;
        TransactionPolicy implementationPolicy = null;
        for (PolicySet ps : component.getPolicySets()) {
            // TODO: Test operations
            if (ps.getName().equals(TransactionPolicy.NAME)) {
                implementationPolicy = (TransactionPolicy)ps.getPolicies().get(0);
            }
        }
        if (binding instanceof PolicySetAttachPoint) {
            PolicySetAttachPoint pap = (PolicySetAttachPoint)binding;
            for (PolicySet ps : pap.getPolicySets()) {
                if (ps.getName().equals(TransactionPolicy.NAME)) {
                    interactionPolicy = (TransactionPolicy)ps.getPolicies().get(0);

                }
            }
        }
        for (InvocationChain chain : wire.getInvocationChains()) {
            Operation operation = chain.getSourceOperation();

            TransactionInterceptor interceptor =
                new TransactionInterceptor(helper, outbound, interactionPolicy, implementationPolicy);
            chain.addInterceptor(interceptor);
        }

    }

}
