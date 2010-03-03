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

package org.apache.tuscany.sca.binding.ws.axis2.policy.security.http.ssl;

import java.util.logging.Logger;

import org.apache.tuscany.sca.binding.ws.axis2.provider.Axis2BaseBindingProvider;
import org.apache.tuscany.sca.host.http.SecurityContext;
import org.apache.tuscany.sca.policy.PolicySubject;
import org.apache.tuscany.sca.policy.security.http.ssl.HTTPSPolicy;
import org.apache.tuscany.sca.provider.BasePolicyProvider;

/**
 * @version $Rev$ $Date$
 */
public class HTTPSPolicyProvider extends BasePolicyProvider<HTTPSPolicy> {
    private final Logger logger = Logger.getLogger(HTTPSPolicyProvider.class.getName());

    public HTTPSPolicyProvider(PolicySubject subject) {
        super(HTTPSPolicy.class, subject);
    }

    public void configureBinding(Object context) {
        SecurityContext securityContext = ((Axis2BaseBindingProvider)context).getHttpSecurityContext();
        
        for (Object policy : findPolicies()) {
            if (policy instanceof HTTPSPolicy) {
                HTTPSPolicy httpsPolicy = (HTTPSPolicy)policy;
                
                securityContext.setSSLEnabled(true);
                securityContext.setSSLProperties(httpsPolicy.toProperties());
                
                // TODO - what is the right way to set trust/key store on client side?
                
                logger.info("HTTPSPolicyProvider: Setting JVM trust store to " + httpsPolicy.getTrustStore());
                System.setProperty("javax.net.ssl.trustStore", httpsPolicy.getTrustStore());
                System.setProperty("javax.net.ssl.trustStorePassword", httpsPolicy.getTrustStorePassword());
                System.setProperty("javax.net.ssl.trustStoreType", httpsPolicy.getTrustStoreType());
                
                logger.info("HTTPSPolicyProvider: Setting JVM key store to " + httpsPolicy.getKeyStore());
                System.setProperty("javax.net.ssl.keyStore", httpsPolicy.getKeyStore());
                System.setProperty("javax.net.ssl.keyStorePassword", httpsPolicy.getKeyStorePassword());
                System.setProperty("javax.net.ssl.keyStoreType", httpsPolicy.getKeyStoreType());                

                return;
            }
        }        
    }
}
