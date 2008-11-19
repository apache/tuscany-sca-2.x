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

package org.apache.tuscany.sca.policy.util;

import java.util.List;

import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicySet;

/**
 * Utility methods to deal with PolicyHandlers
 *
 * @version $Rev$ $Date$
 */
public class PolicyHandlerUtils {
    public static PolicyHandler findPolicyHandler(PolicySet policySet, List<PolicyHandlerTuple> policyHandlerClassNames)
        throws IllegalAccessException, ClassNotFoundException, InstantiationException {

        PolicyHandler handler = null;

        for (PolicyHandlerTuple handlerTuple : policyHandlerClassNames) {
            //System.out.println(handlerTuple);
            for (Intent intent : policySet.getProvidedIntents()) {
                if (intent.getName().equals(handlerTuple.getProvidedIntentName())) {
                    for (Object policy : policySet.getPolicies()) {
                        if (policy.getClass().getName().equals(handlerTuple.getPolicyModelClassName())) {
                            if (handlerTuple.getAppliesTo() != null) {
                                if (handlerTuple.getAppliesTo().equals(policySet.getAppliesTo())) {
                                    handler = (PolicyHandler)handlerTuple.getDeclaration().loadClass().newInstance();
                                    handler.setApplicablePolicySet(policySet);
                                    return handler;
                                }
                            } else {
                                handler = (PolicyHandler)handlerTuple.getDeclaration().loadClass().newInstance();
                                handler.setApplicablePolicySet(policySet);
                                return handler;
                            }
                        }
                    }
                }
            }
        }

        return handler;
    }  

}
