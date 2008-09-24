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

import java.security.Principal;

import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.SecurityUtil;
import org.apache.tuscany.sca.policy.util.PolicyHandler;

/**
 * @version $Rev: 635619 $ $Date: 2008-03-10 23:24:29 +0530 (Mon, 10 Mar 2008) $
 */
public class CheckingsDeptAuthorizationPolicyHandler implements PolicyHandler {
    private PolicySet applicablePolicySet = null;

    public void afterInvoke(Object... context) {
    }

    public void beforeInvoke(Object... context) {
        for ( int count = 0 ; count < context.length ; ++count ) {
            if ( context[count] instanceof Message ) {
                Message msg = (Message)context[count];
                Object args[] = (Object[])msg.getBody();
                Principal principal = SecurityUtil.getPrincipal(msg);
                if (principal != null){
                    BigbankCheckingsAcl.authorize(principal,
                                                 (String)args[0]);
                }                
            }
        }
    }

    public void cleanUp(Object... arg0) {
    }

    public PolicySet getApplicablePolicySet() {
        return this.applicablePolicySet;
    }

    public void setApplicablePolicySet(PolicySet policySet) {
        this.applicablePolicySet = policySet;
    }

    public void setUp(Object... arg0) {

    }

}
