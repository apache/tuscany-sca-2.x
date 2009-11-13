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

package org.apache.tuscany.sca.provider;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.PhasedInterceptor;
import org.apache.tuscany.sca.policy.PolicyExpression;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySubject;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;

/**
 * Abstract base class for policy providers
 */
public abstract class BasePolicyProvider<T> implements PolicyProvider {
    protected Class<T> policyType;
    protected PolicySubject subject;

    protected BasePolicyProvider(Class<T> policyType, PolicySubject subject) {
        this.policyType = policyType;
        this.subject = subject;
    }

    protected List<T> findPolicies() {
        List<T> policies = new ArrayList<T>();
        List<PolicySet> policySets = subject.getPolicySets();
        for (PolicySet ps : policySets) {
            for (Object p : ps.getPolicies()) {
                if (policyType.isInstance(p)) {
                    policies.add(policyType.cast(p));
                }
                if (p instanceof PolicyExpression) {
                    PolicyExpression exp = (PolicyExpression)p;
                    if (policyType.isInstance(exp.getPolicy())) {
                        policies.add(policyType.cast(exp.getPolicy()));
                    }
                }

            }
        }
        return policies;
    }

    protected List<PolicySet> findPolicySets() {
        List<PolicySet> policies = new ArrayList<PolicySet>();
        List<PolicySet> policySets = subject.getPolicySets();
        for (PolicySet ps : policySets) {
            for (Object p : ps.getPolicies()) {
                if (policyType.isInstance(p)) {
                    policies.add(ps);
                }
                if (p instanceof PolicyExpression) {
                    PolicyExpression exp = (PolicyExpression)p;
                    if (policyType.isInstance(exp.getPolicy())) {
                        policies.add(ps);
                    }
                }
            }
        }
        return policies;
    }

    protected PolicySet findPolicySet() {
        List<PolicySet> policySets = subject.getPolicySets();
        for (PolicySet ps : policySets) {
            for (Object p : ps.getPolicies()) {
                if (policyType.isInstance(p)) {
                    return ps;
                }
                if (p instanceof PolicyExpression) {
                    PolicyExpression exp = (PolicyExpression)p;
                    if (policyType.isInstance(exp.getPolicy())) {
                        return ps;
                    }
                }
            }
        }
        return null;
    }

    protected String getContext() {
        if (subject instanceof Endpoint) {
            Endpoint endpoint = (Endpoint)subject;
            return endpoint.getURI();
        } else if (subject instanceof EndpointReference) {
            EndpointReference endpointReference = (EndpointReference)subject;
            return endpointReference.getURI();
        } else if (subject instanceof Component) {
            Component component = (Component)subject;
            return component.getURI();
        }
        return null;
    }

    public void start() {
    }

    public void stop() {
    }

    public PhasedInterceptor createInterceptor(Operation operation) {
        return null;
    }

    protected InvocationChain getInvocationChain() {
        if (subject instanceof RuntimeEndpoint) {
            RuntimeEndpoint endpoint = (RuntimeEndpoint)subject;
            List<InvocationChain> chains = endpoint.getInvocationChains();
            for (InvocationChain chain : chains) {
                configure(chain, chain.getTargetOperation());
            }

        } else if (subject instanceof RuntimeEndpointReference) {
            RuntimeEndpointReference endpointReference = (RuntimeEndpointReference)subject;
            List<InvocationChain> chains = endpointReference.getInvocationChains();
            for (InvocationChain chain : chains) {
                configure(chain, chain.getSourceOperation());
            }
        } else if (subject instanceof RuntimeComponent) {
            RuntimeComponent component = (RuntimeComponent)subject;
            for (ComponentService s : component.getServices()) {
                RuntimeComponentService service = (RuntimeComponentService)s;
                for (Endpoint ep : service.getEndpoints()) {
                    List<InvocationChain> chains = ((RuntimeEndpoint)ep).getInvocationChains();
                    for (InvocationChain chain : chains) {
                        configure(chain, chain.getTargetOperation());
                    }
                }

            }
        }
        return null;
    }

    protected void configure(InvocationChain invocationChain, Operation operation) {
    }

}
