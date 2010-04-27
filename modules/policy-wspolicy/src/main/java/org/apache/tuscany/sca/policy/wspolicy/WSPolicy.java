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
import org.apache.tuscany.sca.policy.PolicyContainer;

/**
 * The WS-Policy model. Currently defers to the Neethi policy model under the covers. 
 */
public class WSPolicy implements PolicyContainer {

    public final static String WS_POLICY_NS = "http://schemas.xmlsoap.org/ws/2004/09/policy";
    public final static String WS_POLICY = "Policy";

    public final static QName WS_POLICY_QNAME = new QName(WS_POLICY_NS, WS_POLICY);

    private Policy neethiPolicy;
    private List<Object> policyAssertions = new ArrayList<Object>();

    public QName getSchemaName() {
        return WS_POLICY_QNAME;
    }
    
    public Policy getNeethiPolicy() {
        return neethiPolicy;
    }
    
    public void setNeethiPolicy(Policy neethiPolicy) {
        this.neethiPolicy = neethiPolicy;
    }
    
    public List<Object> getPolicyAssertions(){
        return policyAssertions;
    }

    @Override
    public String toString() {
        return "WSPolicy [" + neethiPolicy + "]";
    }
    
    public <T> Object getChildPolicy(Class<T> policyType) {
        for (Object alternative : policyAssertions){
            for (Object policy : (List<Object>)alternative){
                if (policyType.isInstance(policy)){
                    return policy;
                }
            }
        }
        return null;
    }
}
