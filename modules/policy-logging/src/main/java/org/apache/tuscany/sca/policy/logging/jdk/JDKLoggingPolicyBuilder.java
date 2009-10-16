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

package org.apache.tuscany.sca.policy.logging.jdk;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

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
 * 
 */
public class JDKLoggingPolicyBuilder implements PolicyBuilder<JDKLoggingPolicy> {

    public void build(Endpoint endpoint, BuilderContext context) {
        List<JDKLoggingPolicy> polices = getPolicies(endpoint);
        System.out.println(endpoint + ": " + polices);
    }

    public void build(EndpointReference endpointReference, BuilderContext context) {
        List<JDKLoggingPolicy> polices = getPolicies(endpointReference);
        System.out.println(endpointReference + ": " + polices);
    }

    public void build(Component component, Implementation implementation, BuilderContext context) {
        List<JDKLoggingPolicy> polices = getPolicies(implementation);
        System.out.println(implementation + ": " + polices);
    }

    public QName getPolicyType() {
        return JDKLoggingPolicy.JDK_LOGGING_POLICY_QNAME;
    }

    private List<JDKLoggingPolicy> getPolicies(PolicySubject subject) {
        List<JDKLoggingPolicy> polices = new ArrayList<JDKLoggingPolicy>();
        for (PolicySet ps : subject.getPolicySets()) {
            for (PolicyExpression exp : ps.getPolicies()) {
                if (getPolicyType().equals(exp.getName())) {
                    polices.add((JDKLoggingPolicy)exp.getPolicy());
                }
            }
        }
        return polices;
    }

}
