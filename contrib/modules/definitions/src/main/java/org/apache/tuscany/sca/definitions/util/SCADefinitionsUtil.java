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

package org.apache.tuscany.sca.definitions.util;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.definitions.SCADefinitions;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentAttachPointType;
import org.apache.tuscany.sca.policy.PolicySet;

/**
 * Some utility functions to deal with SCADefinitions
 *
 * @version $Rev$ $Date$
 */
public class SCADefinitionsUtil {
    
    public static  void stripDuplicates(SCADefinitions scaDefns) {
        Map<QName, Intent> definedIntents = new HashMap<QName, Intent>();
        for (Intent intent : scaDefns.getPolicyIntents()) {
            definedIntents.put(intent.getName(), intent);
        }

        Map<QName, PolicySet> definedPolicySets = new HashMap<QName, PolicySet>();
        for (PolicySet policySet : scaDefns.getPolicySets()) {
            definedPolicySets.put(policySet.getName(), policySet);
        }
        
        Map<QName, IntentAttachPointType> definedBindingTypes = new HashMap<QName, IntentAttachPointType>();
        for (IntentAttachPointType bindingType : scaDefns.getBindingTypes()) {
            definedBindingTypes.put(bindingType.getName(), bindingType);
        }
        
        Map<QName, IntentAttachPointType> definedImplTypes = new HashMap<QName, IntentAttachPointType>();
        for (IntentAttachPointType implType : scaDefns.getImplementationTypes()) {
            definedImplTypes.put(implType.getName(), implType);
        }
        
        scaDefns.getPolicyIntents().clear();
        scaDefns.getPolicyIntents().addAll(definedIntents.values());
        scaDefns.getPolicySets().clear();
        scaDefns.getPolicySets().addAll(definedPolicySets.values());
        scaDefns.getBindingTypes().clear();
        scaDefns.getBindingTypes().addAll(definedBindingTypes.values());
        scaDefns.getImplementationTypes().clear();
        scaDefns.getImplementationTypes().addAll(definedImplTypes.values());
    }
    
    public static void aggregateSCADefinitions(SCADefinitions source, SCADefinitions target) {
        target.getPolicyIntents().addAll(source.getPolicyIntents());
        target.getPolicySets().addAll(source.getPolicySets());
        target.getBindingTypes().addAll(source.getBindingTypes());
        target.getImplementationTypes().addAll(source.getImplementationTypes());
        target.getBindings().addAll(source.getBindings());
    }
    
    public static boolean isSCADefnsFile(URI uri) {
        int index = uri.toString().lastIndexOf("/");

        index = (index != -1) ? index + 1 : 0;

        return uri.toString().substring(index).equals("definitions.xml");
    }

}
