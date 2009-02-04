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

package bigbank.account.security;

import org.apache.tuscany.sca.assembly.ConfiguredOperation;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.OperationsConfigurator;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Phase;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.provider.PolicyProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;

/**
 * @version $Rev$ $Date$
 */
public class CheckingsDeptAuthImplementationPolicyProvider implements PolicyProvider {
    private RuntimeComponent component;
    private Implementation implementation;

    public CheckingsDeptAuthImplementationPolicyProvider(RuntimeComponent component, Implementation implementation) {
        super();
        this.component = component;
        this.implementation = implementation;
    }

    private String getContext() {
        return "component.implementation: " + component.getURI() + "(" + implementation.getClass().getName() + ")";
    }

    private PolicySet findPolicySet(Operation operation) {
    	for (PolicySet ps : component.getPolicySets()) {
            for (Object p : ps.getPolicies()) {
                if (CheckingsDeptAuthPolicy.class.isInstance(p)) {
                    return ps;
                }
            }
        }
        
        if ( component instanceof OperationsConfigurator ) {
        	for ( ConfiguredOperation confOp : ((OperationsConfigurator)component).getConfiguredOperations() ) {
        		if ( confOp.getName().equals(operation.getName())) {
        			for (PolicySet ps : confOp.getPolicySets()) {
        	            for (Object p : ps.getPolicies()) {
        	                if (CheckingsDeptAuthPolicy.class.isInstance(p)) {
        	                    return ps;
        	                }
        	            }
        	        }
        		}
        	}
        }
        
        return null;
    }

    /**
     * @see org.apache.tuscany.sca.provider.PolicyProvider#createInterceptor(org.apache.tuscany.sca.interfacedef.Operation)
     */
    public Interceptor createInterceptor(Operation operation) {
        PolicySet ps = findPolicySet(operation);
        return ps == null ? null : new CheckingsDeptAuthPolicyInterceptor(getContext(), operation, ps);
    }

    /**
     * @see org.apache.tuscany.sca.provider.PolicyProvider#getPhase()
     */
    public String getPhase() {
        return Phase.IMPLEMENTATION_POLICY;
    }

}
