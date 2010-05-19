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

package org.apache.tuscany.sca.binding.rest.provider;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.wink.common.internal.registry.ProvidersRegistry;
import org.apache.wink.server.internal.DeploymentConfiguration;
import org.apache.wink.server.internal.RequestProcessor;
import org.apache.wink.server.internal.handlers.ServerMessageContext;
import org.apache.wink.server.internal.servlet.RestServlet;

/**
 * 
 */
public class TuscanyRESTServlet extends RestServlet {
    private static final long serialVersionUID = 89997233133964915L;
    private ExtensionPointRegistry registry;

    public TuscanyRESTServlet(ExtensionPointRegistry registry) {
        super();
        this.registry = registry;
    }

    @Override
    public DeploymentConfiguration getDeploymentConfiguration() throws ClassNotFoundException, InstantiationException,
        IllegalAccessException, IOException {
        DeploymentConfiguration config = super.getDeploymentConfiguration();
        // [rfeng] FIXME: This is a hack to fool Apache wink to not remove the servlet path
        config.setFilterConfig(new FilterConfig() {
            
            public ServletContext getServletContext() {
                return getServletContext();
            }
            
            public Enumeration getInitParameterNames() {
                return getInitParameterNames();
            }
            
            public String getInitParameter(String arg0) {
                return getInitParameter(arg0);
            }
            
            public String getFilterName() {
                return getServletName();
            }
        });
        ProvidersRegistry providers = config.getProvidersRegistry();
        providers.addProvider(new DataBindingJAXRSReader(registry), 0.001, true);
        providers.addProvider(new DataBindingJAXRSWriter(registry), 0.001, true);
        return config;
    }

    @Override
    public RequestProcessor getRequestProcessor() {
        return super.getRequestProcessor();
    }

    public ServerMessageContext createMessageContext(HttpServletRequest request, HttpServletResponse response) {
        ServerMessageContext messageContext;
        try {
            messageContext = new ServerMessageContext(request, response, getDeploymentConfiguration());
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        return messageContext;
    }
    

}
