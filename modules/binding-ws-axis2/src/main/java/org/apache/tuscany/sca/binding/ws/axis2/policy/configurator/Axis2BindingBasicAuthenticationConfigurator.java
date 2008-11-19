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

package org.apache.tuscany.sca.binding.ws.axis2.policy.configurator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.security.auth.Subject;

import org.apache.axiom.om.util.Base64;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.apache.axis2.transport.http.HttpTransportProperties.Authenticator;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.policy.SecurityUtil;
import org.apache.tuscany.sca.policy.authentication.basic.BasicAuthenticationPolicy;
import org.apache.tuscany.sca.policy.authentication.basic.BasicAuthenticationPrincipal;
import org.osoa.sca.ServiceRuntimeException;

/**
 * Policy handler to handle PolicySet that contain Axis2ConfigParamPolicy instances
 *
 * @version $Rev$ $Date$
 */
public class Axis2BindingBasicAuthenticationConfigurator {
    
    
    public static void setOperationOptions(OperationClient operationClient, Message msg, BasicAuthenticationPolicy policy) {
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
        }
        
        if (username == null || password == null ){
            throw new ServiceRuntimeException("Basic authentication username or password is null");
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
        
        // get the security context
        Subject subject = SecurityUtil.getSubject(msg);
        BasicAuthenticationPrincipal principal =  new BasicAuthenticationPrincipal(username,
                                                                                   password);
        subject.getPrincipals().add(principal);
    }    
}
