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
package org.apache.tuscany.sca.binding.ws.axis2.policy.authentication.basic;


import java.util.Map;

import javax.security.auth.Subject;
import javax.xml.namespace.QName;

import org.apache.axiom.util.base64.Base64Utils;
import org.apache.axis2.context.MessageContext;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.Phase;
import org.apache.tuscany.sca.invocation.PhasedInterceptor;
import org.apache.tuscany.sca.policy.PolicyExpression;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.authentication.basic.BasicAuthenticationPolicy;
import org.apache.tuscany.sca.policy.authentication.basic.BasicAuthenticationPrincipal;
import org.apache.tuscany.sca.policy.security.SecurityUtil;


/**
 * Policy handler to handle PolicySet related to Logging with the QName
 * {http://tuscany.apache.org/xmlns/sca/1.1/impl/java}LoggingPolicy
 *
 * @version $Rev$ $Date$
 */
public class BasicAuthenticationServicePolicyInterceptor implements PhasedInterceptor {
    private static final String SCA10_TUSCANY_NS = "http://tuscany.apache.org/xmlns/sca/1.1";
    public static final QName policySetQName = new QName(SCA10_TUSCANY_NS, "wsBasicAuthentication");

    private Invoker next;
    private PolicySet policySet = null;
    private String context;
    private BasicAuthenticationPolicy policy;

    public BasicAuthenticationServicePolicyInterceptor(String context, PolicySet policySet) {
        super();
        this.policySet = policySet;
        this.context = context;
        init();
    }

    private void init() {
        // TODO - how to get the appropriate expression out of the
        //        policy set. Need WS Policy help here
        if (policySet != null) {
            for (PolicyExpression policyExpression : policySet.getPolicies()){
                if (policyExpression.getPolicy() instanceof BasicAuthenticationPolicy){
                    policy = (BasicAuthenticationPolicy)policyExpression.getPolicy();
                    break;
                }
            }
        }
    }

    public Message invoke(Message msg) {
        
        MessageContext messageContext = msg.getBindingContext();
        Map httpHeaderProperties = (Map)messageContext.getProperty(org.apache.axis2.context.MessageContext.TRANSPORT_HEADERS);
        
        String basicAuthString = (String)httpHeaderProperties.get("Authorization");
        String decodedBasicAuthString = null;
        String username = null;
        String password = null;
        
        if (basicAuthString != null) {
            basicAuthString = basicAuthString.trim();
            
            if (basicAuthString.startsWith("Basic ")) {
                decodedBasicAuthString = new String(Base64Utils.decode(basicAuthString.substring(6)));
            }
            
            int collonIndex = decodedBasicAuthString.indexOf(':');
            
            if (collonIndex == -1){
                username = decodedBasicAuthString;
            } else {
                username = decodedBasicAuthString.substring(0, collonIndex);
                password = decodedBasicAuthString.substring(collonIndex + 1);
            }
        }
        
        // get the security context
        Subject subject = SecurityUtil.getSubject(msg);
        BasicAuthenticationPrincipal principal =  new BasicAuthenticationPrincipal(username,
                                                                                   password);
        subject.getPrincipals().add(principal);
    
        return getNext().invoke(msg);
    }

    public Invoker getNext() {
        return next;
    }

    public void setNext(Invoker next) {
        this.next = next;
    }

    public String getPhase() {
        return Phase.SERVICE_BINDING_POLICY;
    }
    
}
