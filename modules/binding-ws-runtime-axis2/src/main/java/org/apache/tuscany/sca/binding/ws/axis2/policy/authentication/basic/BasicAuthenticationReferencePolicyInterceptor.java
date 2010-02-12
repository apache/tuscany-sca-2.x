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

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.Subject;
import javax.xml.namespace.QName;

import org.apache.axis2.client.OperationClient;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.apache.axis2.transport.http.HttpTransportProperties.Authenticator;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.Phase;
import org.apache.tuscany.sca.invocation.PhasedInterceptor;
import org.apache.tuscany.sca.policy.PolicyExpression;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.authentication.basic.BasicAuthenticationPolicy;
import org.apache.tuscany.sca.policy.authentication.basic.BasicAuthenticationPrincipal;
import org.apache.tuscany.sca.policy.security.SecurityUtil;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 *
 * @version $Rev$ $Date$
 */
public class BasicAuthenticationReferencePolicyInterceptor implements PhasedInterceptor {
    private static final String SCA10_TUSCANY_NS = "http://tuscany.apache.org/xmlns/sca/1.1";
    public static final QName policySetQName = new QName(SCA10_TUSCANY_NS, "wsBasicAuthentication");

    private Invoker next;
    private PolicySet policySet = null;
    private String context;
    private BasicAuthenticationPolicy policy;

    public BasicAuthenticationReferencePolicyInterceptor(String context, PolicySet policySet) {
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
        
        OperationClient operationClient = msg.getBindingContext();
        
        String username = null;
        String password = null;
        
        // get the security context
        Subject subject = SecurityUtil.getSubject(msg);
        BasicAuthenticationPrincipal principal = SecurityUtil.getPrincipal(subject, 
                                                                           BasicAuthenticationPrincipal.class);

        // could use the security principal to look up basic auth credentials
        if (  principal != null ) {
            username = ((BasicAuthenticationPrincipal)principal).getName();
            password = ((BasicAuthenticationPrincipal)principal).getPassword();
        } else if (policy != null ){
            username = policy.getUserName();
            password = policy.getPassword();
            
            principal = new BasicAuthenticationPrincipal(username,
                                                         password);
            subject.getPrincipals().add(principal);
        }        
        
        if (username == null || password == null ){
            throw new ServiceRuntimeException("Basic authentication username and/or password is null");
        }
        
        HttpTransportProperties.Authenticator authenticator = new HttpTransportProperties.Authenticator();
        List<String> auth = new ArrayList<String>();
        auth.add(Authenticator.BASIC);
        authenticator.setAuthSchemes(auth);
        authenticator.setPreemptiveAuthentication(true);
        authenticator.setUsername(username);
        authenticator.setPassword(password);
    
        operationClient.getOptions().setProperty(HTTPConstants.AUTHENTICATE,
                                                 authenticator);
        
        return getNext().invoke(msg);
    }

    public Invoker getNext() {
        return next;
    }

    public void setNext(Invoker next) {
        this.next = next;
    }
    
    public String getPhase() {
        return Phase.REFERENCE_BINDING_POLICY;
    }    
}
