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

import java.util.HashSet;

import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.policy.BindingType;
import org.apache.tuscany.sca.policy.ImplementationType;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicySet;

/**
 * Some utility functions to deal with SCADefinitions
 *
 * @version $Rev$ $Date$
 */
public class DefinitionsUtil {
    
    /**
     * Add the source set of definitions into the target set of definitions checking that 
     * definitions artifacts are unique in the process
     * 
     * @param source the input definitions collection
     * @param target the definition collection into which source will aggregated
     */
    public static void aggregate(Definitions source, Definitions target, Monitor monitor) {
        
        HashSet<Intent> intents = new HashSet<Intent>(target.getIntents());
        for(Intent intent : source.getIntents()){           
            if (intents.contains(intent)){
                Monitor.error(monitor, 
                              target, 
                              "definitions-validation-messages", 
                              "DuplicateIntent", 
                              intent.getName().toString());
            } else {          
                target.getIntents().add(intent);
            }
        }
        
        HashSet<PolicySet> policySets = new HashSet<PolicySet>(target.getPolicySets());
        for(PolicySet policySet : source.getPolicySets()){           
            if (policySets.contains(policySet)){
                Monitor.error(monitor, 
                              target, 
                              "definitions-validation-messages", 
                              "DuplicatePolicySet", 
                              policySet.getName().toString());
            } else {          
                target.getPolicySets().add(policySet);
            }
        }        

        HashSet<BindingType> bindingTypes = new HashSet<BindingType>(target.getBindingTypes());
        for(BindingType bindingType : source.getBindingTypes()){           
            if (bindingTypes.contains(bindingType)){
                Monitor.error(monitor, 
                              target, 
                              "definitions-validation-messages", 
                              "DuplicateBindingType", 
                              bindingType.getType().toString());
            } else {          
                target.getBindingTypes().add(bindingType);
            }
        }   

        HashSet<ImplementationType> implementationTypes = new HashSet<ImplementationType>(target.getImplementationTypes());
        for(ImplementationType implementationType : source.getImplementationTypes()){           
            if (implementationTypes.contains(implementationType)){
                Monitor.error(monitor, 
                              target, 
                              "definitions-validation-messages", 
                              "DuplicateImplementationType", 
                              implementationType.getType().toString());
            } else {          
                target.getImplementationTypes().add(implementationType);
            }
        }        
      
        target.getBindings().addAll(source.getBindings());
    }
    
}
