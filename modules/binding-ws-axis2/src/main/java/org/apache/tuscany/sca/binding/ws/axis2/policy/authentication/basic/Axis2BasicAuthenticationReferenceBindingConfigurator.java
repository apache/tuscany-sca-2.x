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
import java.util.ArrayList;
import java.util.List;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.apache.axis2.transport.http.HttpTransportProperties.Authenticator;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.policy.Policy;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.authentication.basic.BasicAuthenticationPolicy;
import org.osoa.sca.ServiceRuntimeException;

/**
 * Policy handler to handle PolicySet that contain Axis2ConfigParamPolicy instances
 *
 * @version $Rev$ $Date$
 */
public class Axis2BasicAuthenticationReferenceBindingConfigurator {
    
    
    public static void setOperationOptions(OperationClient operationClient, Message msg, BasicAuthenticationPolicy policy) {
        
        // get security context
        String securityPrincipal = (String)msg.getQoSContext().get(Message.QOS_CTX_SECURITY_PRINCIPAL);
        String username = null;
        String password = null;
        
        // could use the security principal to look up basic auth credentials
        if (  securityPrincipal != null ) {
            // look up usename and password based on security principal
        } else {
           // take the message username and password
            username = (String)msg.getQoSContext().get(BasicAuthenticationPolicy.BASIC_AUTHENTICATION_USERNAME);
            password = (String)msg.getQoSContext().get(BasicAuthenticationPolicy.BASIC_AUTHENTICATION_PASSWORD);
            
            if (username == null){
                username = policy.getUserName();
                password = policy.getPassword();
            }
        }
        
        if (username == null || password == null ){
            throw new ServiceRuntimeException("Basic authenication username or password is null");
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
    }

}
