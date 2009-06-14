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

package org.apache.tuscany.sca.binding.http.provider;

import java.io.IOException;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;
import org.apache.tuscany.sca.policy.authentication.basic.BasicAuthenticationPolicy;

/**
 * Servlet responsible for dispatching HTTP service requests to the
 * target component implementation.
 *
 * @version $Rev$ $Date$
 */
public class HTTPServiceListenerServlet implements Servlet {
    private static final QName AUTEHTICATION_INTENT = new QName("http://www.osoa.org/xmlns/sca/1.0","authentication");
    
    transient private Binding binding;
    transient private ServletConfig config;
    transient private MessageFactory messageFactory;
    transient private Invoker serviceInvoker;
    
    transient private boolean requiresAuthentication = false;
    transient private BasicAuthenticationPolicy basicAuthenticationPolicy = null;

    /**
     * Constructs a new HTTPServiceListenerServlet.
     */
    public HTTPServiceListenerServlet(Binding binding, Invoker serviceInvoker, MessageFactory messageFactory) {
        this.binding = binding;
        this.serviceInvoker = serviceInvoker;
        this.messageFactory = messageFactory;
        
        // find out which policies are active
        if (binding instanceof PolicySetAttachPoint) {
            List<Intent> intents = ((PolicySetAttachPoint)binding).getRequiredIntents();
            for(Intent intent : intents) {
                if(intent.getName().equals(AUTEHTICATION_INTENT)) {
                    requiresAuthentication = true;
                }
            }


            List<PolicySet> policySets = ((PolicySetAttachPoint)binding).getApplicablePolicySets();
            for (PolicySet ps : policySets) {
                for (Object p : ps.getPolicies()) {
                    if (BasicAuthenticationPolicy.class.isInstance(p)) {
                        basicAuthenticationPolicy = (BasicAuthenticationPolicy)p;
                    } else {
                        // etc. check for other types of policy being present
                    }
                }
            }
        }        
    }

    public ServletConfig getServletConfig() {
        return config;
    }

    public String getServletInfo() {
        return "";
    }

    public void init(ServletConfig config) throws ServletException {
        this.config = config;
    }

    public void destroy() {
        
    }

    public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        
        if(requiresAuthentication) {
            if(! hasAuthenticationHeader((HttpServletRequest)request, (HttpServletResponse)response)) {
                ((HttpServletResponse)response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }
        }
        
        // Dispatch the service interaction to the service invoker
        Message requestMessage = messageFactory.createMessage();
        requestMessage.setBody(new Object[]{request, response});
        Message responseMessage = serviceInvoker.invoke(requestMessage);
        if (responseMessage.isFault()) {            
            // Turn a fault into an exception
            //throw new ServletException((Throwable)responseMessage.getBody());
            Throwable e = (Throwable)responseMessage.getBody();
            ((HttpServletResponse)response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString());
        }
    }

    
    private boolean hasAuthenticationHeader(HttpServletRequest request, ServletResponse response) {
        boolean result = false;
        if(request.getHeader("Authorization") != null) {
            result = true;
        }
        
        return result;
    }
}
