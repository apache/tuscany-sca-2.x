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

package org.apache.tuscany.sca.policy.wspolicy;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.neethi.Policy;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.builder.BuilderContext;
import org.apache.tuscany.sca.assembly.builder.PolicyBuilder;
import org.apache.tuscany.sca.policy.PolicyExpression;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySubject;
import org.apache.tuscany.sca.policy.wspolicy.xml.WSPolicyProcessor;

/**
 * Processing for WS-Policy objects
 * TBD
 */
public class WSPolicyBuilder implements PolicyBuilder<Policy> {

    public boolean build(Endpoint endpoint, BuilderContext context) {
        List<Policy> polices = getPolicies(endpoint);
        System.out.println(endpoint + ": " + polices);
        return true;
    }

    public boolean build(EndpointReference endpointReference, BuilderContext context) {
        List<Policy> polices = getPolicies(endpointReference);
        System.out.println(endpointReference + ": " + polices);
        return true;
    }

    public boolean build(Component component, Implementation implementation, BuilderContext context) {
        List<Policy> polices = getPolicies(implementation);
        System.out.println(implementation + ": " + polices);
        return true;
    }

    public QName getPolicyType() {
        return WSPolicy.WS_POLICY_QNAME;
    }

    private List<Policy> getPolicies(PolicySubject subject) {
        List<Policy> polices = new ArrayList<Policy>();
        for (PolicySet ps : subject.getPolicySets()) {
            for (PolicyExpression exp : ps.getPolicies()) {
                if (getPolicyType().equals(exp.getName())) {
                    polices.add((Policy)exp.getPolicy());
                }
            }
        }
        return polices;
    }

    public boolean build(EndpointReference endpointReference, Endpoint endpoint, BuilderContext context) {
        return true;
    }
    
    public PolicyExpression match(EndpointReference endpointReference, Endpoint endpoint, BuilderContext context) {
        // Get the ws-policy elements from the endpoint reference and endpoint and work out the intersection
        
        return null;
    }    

}
