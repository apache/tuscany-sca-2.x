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
package org.apache.tuscany.sca.assembly.builder.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.WireableBinding;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;

/**
 * This class encapsulates utility methods to deal with binding definitions
 */
class BindingUtil {
    private static boolean hasCompatiblePolicySets(Binding refBinding, Binding svcBinding) {
        if ( refBinding instanceof PolicySetAttachPoint && svcBinding instanceof PolicySetAttachPoint ) {
            //TODO : need to add more compatibility checks at the policy attachment levels
            for ( PolicySet svcPolicySet : ((PolicySetAttachPoint)svcBinding).getPolicySets() ) {
                for ( PolicySet refPolicySet : ((PolicySetAttachPoint)refBinding).getPolicySets() ) {
                    if ( !svcPolicySet.equals(refPolicySet) ) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    
    private static Binding matchBinding(Component component, ComponentService service, List<Binding> source, List<Binding> target) {
        List<Binding> matched = new ArrayList<Binding>();
        // Find the corresponding bindings from the service side
        for (Binding binding : source) {
            for (Binding serviceBinding : target) {
                if (binding.getClass() == serviceBinding.getClass() && 
                    hasCompatiblePolicySets(binding, serviceBinding)) {
                    
                    Binding cloned = binding;
                    if (binding instanceof WireableBinding) {
                        // TODO: We need to clone the reference binding
                        try {
                            cloned = (Binding)((WireableBinding)binding).clone();
                            WireableBinding endpoint = ((WireableBinding)cloned);
                            endpoint.setTargetComponent(component);
                            endpoint.setTargetComponentService(service);
                            endpoint.setTargetBinding(serviceBinding);
                            cloned.setURI(serviceBinding.getURI());
                        } catch (Exception e) {
                            // warning("The binding doesn't support clone: " + binding.getClass().getSimpleName(), binding);
                        }
                    } else {
                        if (binding.getURI() == null) {
                            binding.setURI(serviceBinding.getURI());
                        }
                    }
                    matched.add(cloned);
                    break;
                }
            }
        }
        if (matched.isEmpty()) {
            // No matching binding
            return null;
        } else {
            for (Binding binding : matched) {
                // If binding.sca is present, use it
                if (SCABinding.class.isInstance(binding)) {
                    return binding;
                }
            }
            // Use the first one
            return matched.get(0);
        }
    }

    /**
     * Choose a binding for the reference based on the bindings available on the
     * service
     * 
     * @param reference The component reference
     * @param service The component service
     * @return Resolved binding
     */
    static Binding resolveBindings(ComponentReference reference, Component component, ComponentService service) {
        List<Binding> source = reference.getBindings();
        List<Binding> target = service.getBindings();
    
        return matchBinding(component, service, source, target);
    
    }

    /**
     * @param reference
     * @param service
     * @return
     */
    static Binding resolveCallbackBindings(ComponentReference reference, Component component, ComponentService service) {
        List<Binding> source = reference.getCallback().getBindings();
        List<Binding> target = service.getCallback().getBindings();
    
        return matchBinding(component, service, source, target);
    }
}
