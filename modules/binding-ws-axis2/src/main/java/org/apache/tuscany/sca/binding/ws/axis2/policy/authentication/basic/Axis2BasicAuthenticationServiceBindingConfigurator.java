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

import java.security.Principal;
import java.util.Map;

import org.apache.axiom.om.util.Base64;
import org.apache.axis2.AxisFault;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.Parameter;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.authentication.basic.BasicAuthenticationPolicy;
import org.apache.tuscany.sca.policy.util.PolicyHandler;

/**
 * Deal with basic authentication configuration at the service
 *
 * @version $Rev$ $Date$
 */
public class Axis2BasicAuthenticationServiceBindingConfigurator {
        
    public static void parseHTTPHeader(MessageContext messageContext, Message msg, BasicAuthenticationPolicy policy) {
          
        Map httpHeaderProperties = (Map)messageContext.getProperty(org.apache.axis2.context.MessageContext.TRANSPORT_HEADERS);
                         
        String basicAuthString = (String)httpHeaderProperties.get("Authorization");
        String decodedBasicAuthString = null;
        String username = null;
        String password = null;
        
        if (basicAuthString != null) {
            basicAuthString = basicAuthString.trim();
            
            if (basicAuthString.startsWith("Basic ")) {
                decodedBasicAuthString = new String(Base64.decode(basicAuthString.substring(6)));
            }
            
            int collonIndex = decodedBasicAuthString.indexOf(':');
            
            if (collonIndex == -1){
                username = decodedBasicAuthString;
            } else {
                username = decodedBasicAuthString.substring(0, collonIndex);
                password = decodedBasicAuthString.substring(collonIndex + 1);
            }
        }
        
        // set the security context. 
        msg.getQoSContext().put(BasicAuthenticationPolicy.BASIC_AUTHENTICATION_USERNAME,
                                username);
        msg.getQoSContext().put(BasicAuthenticationPolicy.BASIC_AUTHENTICATION_PASSWORD,
                                password);

        // Set the http headers
        // This is just an experiment, looking at the alternatives to extracting
        // username and password in the binding. With HTTP headers in the message it
        // could be deferred to the interceptor. Asymetric though when compared with the
        // reference support. 
        // how to defined the scheme for message headers?
        msg.getHeader().put("httpheaders", httpHeaderProperties); 
    }    
    
    
}
