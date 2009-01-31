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
package echo.provider.policy;

import org.apache.tuscany.sca.policy.Policy;
import org.apache.tuscany.sca.policy.PolicySet;

/**
 * Sample policy handler 
 */
public class EncryptionPolicyHandler implements PolicyHandler {

    public void applyPolicy(Object msg, PolicySet policySet) throws Exception {
        for ( Object aPolicy : policySet.getPolicies() ) {
            if ( aPolicy instanceof EchoBindingEncryptionPolicy ) {
                encrypt(msg, (EchoBindingEncryptionPolicy)aPolicy); 
            }
        }
    }
    
    private void encrypt(Object msg, EchoBindingEncryptionPolicy policy) throws Exception {
        if ( !policy.isUnresolved() && msg instanceof Object[] ) {
            EncryptionStrategy strategy = policy.getStrategyClass().newInstance();
            Object[] msgArgs = (Object[])msg;
            for ( int count = 0 ; count < msgArgs.length ; ++count ) {
                msgArgs[count] = strategy.encryptMessage(msgArgs[count]);
            }
        }

    }

}
