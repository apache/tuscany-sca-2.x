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

package org.apache.tuscany.sca.policy.transaction;

import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.util.PolicyHandler;

/**
 * @version $Rev$ $Date$
 */
public class TransactionPolicyHandler implements PolicyHandler {
    protected PolicySet policySet;
    /**
     * @see org.apache.tuscany.sca.policy.util.PolicyHandler#afterInvoke(java.lang.Object[])
     */
    public void afterInvoke(Object... context) {
        System.out.println("afterInvoke");
    }

    /**
     * @see org.apache.tuscany.sca.policy.util.PolicyHandler#beforeInvoke(java.lang.Object[])
     */
    public void beforeInvoke(Object... context) {
        System.out.println("beforeInvoke");
    }

    /**
     * @see org.apache.tuscany.sca.policy.util.PolicyHandler#cleanUp(java.lang.Object[])
     */
    public void cleanUp(Object... context) {
    }

    /**
     * @see org.apache.tuscany.sca.policy.util.PolicyHandler#getApplicablePolicySet()
     */
    public PolicySet getApplicablePolicySet() {
        return policySet;
    }

    /**
     * @see org.apache.tuscany.sca.policy.util.PolicyHandler#setApplicablePolicySet(org.apache.tuscany.sca.policy.PolicySet)
     */
    public void setApplicablePolicySet(PolicySet context) {
        this.policySet = context;
    }

    /**
     * @see org.apache.tuscany.sca.policy.util.PolicyHandler#setUp(java.lang.Object[])
     */
    public void setUp(Object... context) {
    }

}
