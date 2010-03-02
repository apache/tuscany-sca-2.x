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

/**
 * Processing for WS-Policy objects
 * TBD
 */
public class WSPolicyBuilder implements PolicyBuilder<Policy> {

    public boolean build(Endpoint endpoint, BuilderContext context) {
        List<WSPolicy> polices = getPolicies(endpoint);
        System.out.println(endpoint + ": " + polices);
        return true;
    }

    public boolean build(EndpointReference endpointReference, BuilderContext context) {
        List<WSPolicy> polices = getPolicies(endpointReference);
        System.out.println(endpointReference + ": " + polices);
        return true;
    }

    public boolean build(Component component, Implementation implementation, BuilderContext context) {
        List<WSPolicy> polices = getPolicies(implementation);
        System.out.println(implementation + ": " + polices);
        return true;
    }

    public QName getPolicyType() {
        return WSPolicy.WS_POLICY_QNAME;
    }
    
    public List<QName> getSupportedBindings() {
        return null;
    }    

    public boolean build(EndpointReference endpointReference, Endpoint endpoint, BuilderContext context) {
        
        // TODO - neethi doesn't include code for matching ws policy 
        //        cxf have the class Intersector http://svn.apache.org/repos/asf/cxf/trunk/api/src/main/java/org/apache/cxf/ws/policy/Intersector.java
        //        but this does its work based on the cxf AssertionBuilders and extension
        //        registry mechanism. I don't want to commit to that at the moment. 
        //       
        //        At the moment we do the simplest top level QName based matching
               
        // match EPR policy sets 
        for (PolicySet eprPolicySet : endpointReference.getPolicySets()){
            for (PolicySet epPolicySet : endpoint.getPolicySets()){
                if (!build(eprPolicySet, epPolicySet)){
                    return false;
                }
            }
        }
        
        // match EP policy sets 
        for (PolicySet epPolicySet : endpoint.getPolicySets()){
            for (PolicySet eprPolicySet : endpointReference.getPolicySets()){
                if (!build(epPolicySet, eprPolicySet)){
                    return false;
                }
            }
        }        
        
        return true;
    }   
    
    private boolean build(PolicySet policySet1, PolicySet policySet2){
        
        // extract the ws policy expressions out of the policy sets
        List<PolicyExpression> policyExpressions1 = new ArrayList<PolicyExpression>();
        List<PolicyExpression> policyExpressions2 = new ArrayList<PolicyExpression>();
        
        for (PolicyExpression policyExpression : policySet1.getPolicies()){
            if (policyExpression.getName().equals(getPolicyType())){
                policyExpressions1.add(policyExpression);
            }
        }
        
        for (PolicyExpression policyExpression : policySet2.getPolicies()){
            if (policyExpression.getName().equals(getPolicyType())){
                policyExpressions2.add(policyExpression);
            }
        }
        
        // Match the first set of expressions against the second set
        for (PolicyExpression policyExpression1 : policyExpressions1){
            for (PolicyExpression policyExpression2 : policyExpressions2){
                if (!build((WSPolicy)policyExpression1.getPolicy(), 
                           (WSPolicy)policyExpression2.getPolicy())){
                    return false;
                }
            }
        }
        
        // TODO set the reference policy set to include an interception of the
        //      ws policy sets discovered here
        //      Do we really need to do this?
        //      The method is called in both directions (reference to service and
        //      service to reference) so would need to fix that
        
        return true;
    }
    
    private List<WSPolicy> getPolicies(PolicySubject subject) {
        List<WSPolicy> polices = new ArrayList<WSPolicy>();
        for (PolicySet ps : subject.getPolicySets()) {
            for (PolicyExpression exp : ps.getPolicies()) {
                if (getPolicyType().equals(exp.getName())) {
                    polices.add((WSPolicy)exp.getPolicy());
                }
            }
        }
        return polices;
    }    
    
    private boolean build(WSPolicy wsPolicy1, WSPolicy wsPolicy2){
        // TODO - cheating here as we assume a flat policy structure
        //        we've read all the policy assertions into Tuscany models
        //        in the reader (without taking account of alternatives)
        //        so we just compare those here
        //        The real implementation of this comparison depends on how
        //        we decide to represent the ws policy hierarchy
        
        for (Object policyAssertion1 : wsPolicy1.getPolicyAssertions()){
            boolean matched = false;
            for (Object policyAssertion2 : wsPolicy2.getPolicyAssertions()){
                if (policyAssertion1.getClass() == policyAssertion2.getClass()){
                    matched = true;
                    break;
                }
            }
            if(!matched){
                return false;
            }
        }
        
        return true;
    }

}
