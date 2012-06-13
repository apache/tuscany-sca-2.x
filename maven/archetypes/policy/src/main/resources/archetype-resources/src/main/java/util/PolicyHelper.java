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
package ${package}.util;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicyExpression;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySubject;

public class PolicyHelper {

    /**
     * Find a list of policy sets that provide the given intent
     * @param subject
     * @param providedIntent
     * @return
     */
    public static List<PolicySet> findPolicySets(PolicySubject subject, QName providedIntent) {
        List<PolicySet> policies = new ArrayList<PolicySet>();
        List<PolicySet> policySets = subject.getPolicySets();
        for (PolicySet ps : policySets) {
            for (Intent i : ps.getProvidedIntents()) {
                if (i.getName().equals(providedIntent)) {
                    policies.add(ps);
                }
            }
        }
        return policies;
    }

    /**
     * Find a list of policies of the given type
     * @param <T>
     * @param subject
     * @param policyType
     * @return
     */
    public static <T> List<T> findPolicies(PolicySubject subject, QName providedIntent, Class<T> policyType) {
        List<T> policies = new ArrayList<T>();
        List<PolicySet> policySets = findPolicySets(subject, providedIntent);
        for (PolicySet ps : policySets) {
            for (PolicyExpression exp : ps.getPolicies()) {
                if (policyType.isInstance(exp.getPolicy())) {
                    policies.add(policyType.cast(exp.getPolicy()));
                }
            }
        }
        return policies;
    }
}
