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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ConfiguredOperation;
import org.apache.tuscany.sca.assembly.OperationsConfigurator;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Phase;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;
import org.apache.tuscany.sca.policy.util.PolicyHandler;
import org.apache.tuscany.sca.policy.util.PolicyHandlerUtils;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.apache.tuscany.sca.runtime.RuntimeWireProcessor;

/**
 * Processor to inject policy handling interceptor whenever PolicySets are specified in a Java Implementation 
 *
 * @version $Rev$ $Date$
 */
public class JavaPolicyHandlingRuntimeWireProcessor implements RuntimeWireProcessor {
    private static final Logger logger = Logger.getLogger(JavaPolicyHandlingRuntimeWireProcessor.class.getName());

    public JavaPolicyHandlingRuntimeWireProcessor() {
        super();
    }

    public void process(RuntimeWire wire) {
        /*Contract contract = wire.getSource().getContract();
        if (!(contract instanceof RuntimeComponentReference)) {
            return;
        }*/

        RuntimeComponent component = wire.getTarget().getComponent();
        if (component != null && component.getImplementation() instanceof JavaImplementation) {
            JavaImplementation javaImpl = (JavaImplementation)component.getImplementation();
            if (javaImpl instanceof PolicySetAttachPoint) {
                PolicyHandler policyHandler = null;
                List<PolicyHandler> implPolicyHandlers = new ArrayList<PolicyHandler>();
                PolicySetAttachPoint policiedImpl = (PolicySetAttachPoint)javaImpl;

                try {
                    //for ( PolicySet policySet : policiedImpl.getPolicySets() ) {
                    for (PolicySet policySet : component.getPolicySets()) {
                        policyHandler =
                            PolicyHandlerUtils.findPolicyHandler(policySet, javaImpl.getPolicyHandlerClassNames());
                        if (policyHandler != null) {
                            policyHandler.setUp(javaImpl);
                            implPolicyHandlers.add(policyHandler);
                        } else {
                            //FIXME: to be removed after the PolicyHandler story has crystalized..
                            //maybe replace with exception then...
                            logger.warning("No PolicyHandler registered for PolicySet - " + policySet.getName());
                        }
                    }

                    List<PolicyHandler> applicablePolicyHandlers = null;
                    for (InvocationChain chain : wire.getInvocationChains()) {
                        applicablePolicyHandlers = new ArrayList<PolicyHandler>();
                        if (javaImpl instanceof OperationsConfigurator) {
                            String operationName = chain.getTargetOperation().getName();
                            OperationsConfigurator opConfigurator = (OperationsConfigurator)component;
                            for (ConfiguredOperation confOp : opConfigurator.getConfiguredOperations()) {
                                if (confOp.getName().equals(operationName)) {
                                    for (PolicySet policySet : confOp.getPolicySets()) {
                                        policyHandler =
                                            PolicyHandlerUtils.findPolicyHandler(policySet, javaImpl
                                                .getPolicyHandlerClassNames());
                                        if (policyHandler != null) {
                                            policyHandler.setUp(javaImpl);
                                            applicablePolicyHandlers.add(policyHandler);
                                        } else {
                                            logger.warning("No PolicyHandler registered for " + policySet);
                                        }
                                    }
                                    break;
                                }
                            }

                            //if no policies have been specified at the operation level then simply
                            //apply whatever is specified for the implementation level
                            if (applicablePolicyHandlers.isEmpty()) {
                                applicablePolicyHandlers = implPolicyHandlers;
                            }
                        }

                        if (!applicablePolicyHandlers.isEmpty()) {
                            String phase =
                                (wire.getSource().getContract() instanceof ComponentReference) ? Phase.REFERENCE_POLICY
                                    : Phase.SERVICE_POLICY;

                            chain.addInterceptor(Phase.IMPLEMENTATION_POLICY, new PolicyHandlingInterceptor(chain.getTargetOperation(),
                                                                                      applicablePolicyHandlers));
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
