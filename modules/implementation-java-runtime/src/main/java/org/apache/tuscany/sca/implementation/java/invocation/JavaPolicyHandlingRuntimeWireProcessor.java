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

package org.apache.tuscany.sca.implementation.java.invocation;

import org.apache.tuscany.sca.assembly.Contract;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.apache.tuscany.sca.runtime.RuntimeWireProcessor;

/**
 * Processor to inject policy handling interceptor wheneve PolicySets are specified in a Java Implementation 
 */
public class JavaPolicyHandlingRuntimeWireProcessor implements RuntimeWireProcessor {
    public JavaPolicyHandlingRuntimeWireProcessor() {
        super();
    }

    public void process(RuntimeWire wire) {
        Contract contract = wire.getSource().getContract();
        if (!(contract instanceof RuntimeComponentReference)) {
            return;
        }
        
        RuntimeComponent component = wire.getTarget().getComponent();
        if ( component != null && component.getImplementation() instanceof JavaImplementation ) {
            JavaImplementation javaImpl = (JavaImplementation)component.getImplementation();
            
            //if the implementation has policysets specified and if there are 
            //handlers for those policysets
            if ( !javaImpl.getPolicyHandlers().isEmpty() ) {
                //TODO: Right now we assume policy handlers are to be applied for all operations
                //... need to modify this if certain policies apply only to select operations 
                for (InvocationChain chain : wire.getInvocationChains() ) { 
                    chain.addInterceptor(0, new PolicyHandlingInterceptor(chain.getTargetOperation(),
                                                                       javaImpl.getPolicyHandlers()));
                }   
            }
        }
    }

}
