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

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentAttachPoint;
import org.apache.tuscany.sca.policy.IntentAttachPointType;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;

/**
 * @version $Rev$ $Date$
 */
public class PolicyValidationUtils {

    public static boolean isConstrained(QName constrained, IntentAttachPointType attachPointType) {
        return (attachPointType != null && attachPointType.getName().getNamespaceURI()
            .equals(constrained.getNamespaceURI()) && attachPointType.getName().getLocalPart()
            .startsWith(constrained.getLocalPart()));
    }

    public static void validateIntents(IntentAttachPoint attachPoint,
                                       IntentAttachPointType attachPointType)
        throws PolicyValidationException {
        boolean found = false;
        if (attachPointType != null) {
            // validate intents specified against the parent (binding /
            // implementation)
            found = false;
            for (Intent intent : attachPoint.getRequiredIntents()) {
                if (!intent.isUnresolved()) {
                    for (QName constrained : intent.getConstrains()) {
                        if (isConstrained(constrained, attachPointType)) {
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        throw new PolicyValidationException("Policy Intent '" + intent.getName()
                            + "' does not constrain extension type  "
                            + attachPointType.getName());
                    }
                } else {
                    throw new PolicyValidationException("Policy Intent '" + intent.getName()
                        + "' is not defined in this domain  ");
                }
            }
        }
    }

    public static void validatePolicySets(PolicySetAttachPoint policySetAttachPoint)
        throws PolicyValidationException {
        validatePolicySets(policySetAttachPoint, 
                           policySetAttachPoint.getType(),
                           policySetAttachPoint.getApplicablePolicySets());
    }
    
    public static void validatePolicySets(PolicySetAttachPoint policySetAttachPoint,
                                          IntentAttachPointType attachPointType)
        throws PolicyValidationException {
        validatePolicySets(policySetAttachPoint,
                           attachPointType,
                           policySetAttachPoint.getApplicablePolicySets());
    }

    public static void validatePolicySets(PolicySetAttachPoint policySetAttachPoint,
                                          IntentAttachPointType attachPointType,
                                          List<PolicySet> applicablePolicySets)
        throws PolicyValidationException {
        // Since the applicablePolicySets in a policySetAttachPoint will already
        // have the list of policysets that might ever be applicable to this attachPoint,
        // just check if the defined policysets feature in the list of applicable
        // policysets
        for (PolicySet definedPolicySet : policySetAttachPoint.getPolicySets()) {
            if (!definedPolicySet.isUnresolved()) {
                if (!applicablePolicySets.contains(definedPolicySet)) {
                    throw new PolicyValidationException("Policy Set '" + definedPolicySet
                        .getName()
                        + "' does not apply to extension type  "
                        + attachPointType.getName());
                }
            } else {
                throw new PolicyValidationException("Policy Set '" + definedPolicySet.getName()
                    + "' is not defined in this domain  ");

            }
        }
    }
}
