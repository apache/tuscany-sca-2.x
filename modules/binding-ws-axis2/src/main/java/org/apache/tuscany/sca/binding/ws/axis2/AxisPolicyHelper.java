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

package org.apache.tuscany.sca.binding.ws.axis2;

import java.util.List;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentAttachPoint;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;

/**
 * @version $Rev$ $Date$
 */
public class AxisPolicyHelper {

    public static final String XMLNS_SCA_1_0 = "http://www.osoa.org/xmlns/sca/1.0";
    public static final QName AUTHENTICATION_INTENT = new QName(XMLNS_SCA_1_0, "authentication");
    public static final QName CONFIDENTIALITY_INTENT = new QName(XMLNS_SCA_1_0, "confidentiality");
    public static final QName INTEGRITY_INTENT = new QName(XMLNS_SCA_1_0, "integrity");
    public static final QName MTOM_INTENT = new QName(XMLNS_SCA_1_0, "MTOM");
    public static final QName SOAP12_INTENT = new QName(XMLNS_SCA_1_0, "SOAP12");

    public static PolicySet getPolicySet(Binding wsBinding, QName intentName) {
        PolicySet returnPolicySet = null;

        if (wsBinding instanceof PolicySetAttachPoint) {
            PolicySetAttachPoint policiedBinding = (PolicySetAttachPoint)wsBinding;
            for (PolicySet policySet : policiedBinding.getPolicySets()) {
                for (Intent intent : policySet.getProvidedIntents()) {
                    if (intent.getName().equals(intentName)) {
                        returnPolicySet = policySet;
                        break;
                    }
                }
            }
        }

        return returnPolicySet;
    }

    public static boolean isIntentRequired(Binding wsBinding, QName intent) {
        if (wsBinding instanceof IntentAttachPoint) {
            List<Intent> intents = ((IntentAttachPoint)wsBinding).getRequiredIntents();
            for (Intent i : intents) {
                if (intent.equals(i.getName())) {
                    return true;
                }
            }
        }
        return getPolicySet(wsBinding, intent) != null;
    }

    public static boolean isRampartRequired(Binding wsBinding) {
        return isIntentRequired(wsBinding, AUTHENTICATION_INTENT) || isIntentRequired(wsBinding, INTEGRITY_INTENT)
            || isIntentRequired(wsBinding, CONFIDENTIALITY_INTENT);
    }

}
