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

package org.apache.tuscany.sca.binding.ws.axis2.policy.wspolicy;

import java.util.logging.Logger;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.tuscany.sca.binding.ws.axis2.provider.Axis2BaseBindingProvider;
import org.apache.tuscany.sca.host.http.SecurityContext;
import org.apache.tuscany.sca.policy.PolicySubject;
import org.apache.tuscany.sca.policy.security.http.ssl.HTTPSPolicy;
import org.apache.tuscany.sca.policy.wspolicy.WSPolicy;
import org.apache.tuscany.sca.provider.BasePolicyProvider;

/**
 * @version $Rev: 918583 $ $Date: 2010-03-03 17:16:15 +0000 (Wed, 03 Mar 2010) $
 */
public class WSPolicyProvider extends BasePolicyProvider<WSPolicy> {
    private final Logger logger = Logger.getLogger(WSPolicyProvider.class.getName());

    public WSPolicyProvider(PolicySubject subject) {
        super(WSPolicy.class, subject);
    }

    public void configureBinding(Object context) {
        ConfigurationContext configContext = ((Axis2BaseBindingProvider)context).getAxisConfigurationContext();
        
        for ( Object policy : findPolicies() ) {
            if ( policy instanceof WSPolicy ) {
                WSPolicy wsPolicy = (WSPolicy)policy;
                try {
                    configContext.getAxisConfiguration().applyPolicy(wsPolicy.getNeethiPolicy());
                    configContext.getAxisConfiguration().engageModule("rampart");
                    
                    // TUSCANY-2824
                    // hack to make service side pick up rampart policies
                    // "rampartPolicy" comes from RampartMessageData.KEY_RAMPART_POLICY
                    // but I'm avoiding adding an explicit dependency just yet. 
                    // There must be a proper way of getting rampart to recognize
                    // these policies
                    configContext.setProperty("rampartPolicy", wsPolicy.getNeethiPolicy());
                    
                } catch ( AxisFault e ) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
