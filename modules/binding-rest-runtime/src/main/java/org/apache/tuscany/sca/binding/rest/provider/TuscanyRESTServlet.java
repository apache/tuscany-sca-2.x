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
import java.lang.annotation.Annotation;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyWriter;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.binding.rest.RESTBinding;
import org.apache.tuscany.sca.common.http.HTTPCacheContext;
import org.apache.tuscany.sca.common.http.HTTPContext;
import org.apache.tuscany.sca.common.http.HTTPHeader;
import org.apache.tuscany.sca.common.http.ThreadHTTPContext;
import org.apache.tuscany.sca.common.http.cors.CORSHeaderProcessor;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.extensibility.ClassLoaderContext;
import org.apache.wink.common.internal.registry.ProvidersRegistry;
import org.apache.wink.common.model.wadl.WADLGenerator;
import org.apache.wink.server.handlers.HandlersChain;
import org.apache.wink.server.handlers.MessageContext;
import org.apache.wink.server.handlers.ResponseHandler;
import org.apache.wink.server.internal.DeploymentConfiguration;
import org.apache.wink.server.internal.servlet.RestServlet;

/**
 *
 */
public class TuscanyRESTServlet extends RestServlet {
    private static final long serialVersionUID = 89997233133964915L;
    
    private static final Logger logger = Logger.getLogger(TuscanyRESTServlet.class.getName());
    
    private static final Annotation[] annotations = new Annotation[0];

    
    private ExtensionPointRegistry registry;
    private RESTBinding binding;
    private Class<?> resourceClass;

    public TuscanyRESTServlet(ExtensionPointRegistry registry, Binding binding, Class<?> resourceClass) {
        super();
        this.registry = registry;
        this.binding = (RESTBinding) binding;
        this.resourceClass = resourceClass;
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        if (binding.isCORS()) {
            /*
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Headers", "X-Requested-With");
            if (request.getMethod().equals("OPTIONS")) {
                response.setHeader("Access-Control-Allow-Methods", "DELETE, GET, OPTIONS, POST, PUT");
                response.setHeader("Access-Control-Max-Age", "1728000");
                return;
            }
            */
            
            CORSHeaderProcessor.processCORS(binding.getCORSConfiguration(), request, response);
        }

        //create context
        HTTPContext bindingContext = new HTTPContext();
        bindingContext.setHttpRequest(request);
        bindingContext.setHttpResponse(response);
        

        try {
            //store in thread local
            ThreadHTTPContext.setHTTPContext(bindingContext);
            
            // handle special ?wadl request
            String query = request.getQueryString();
            if(query != null && query.indexOf("wadl") >= 0) {
                handleWadlRequest(request, response);
            } else {
                super.service(request, response);
            }
        } finally {
            //remove
            ThreadHTTPContext.removeHTTPContext();
        }
    }


    public void init() throws ServletException {
        ClassLoader cl =
            ClassLoaderContext.setContextClassLoader(Thread.currentThread().getContextClassLoader(),
                                                     registry.getServiceDiscovery(),
                                                     "/META-INF/server/wink-providers",
                                                     "javax.ws.rs.ext.RuntimeDelegate");
        try {
            super.init();
        } finally {
            if (cl != null) {
                // return previous classLoader
                Thread.currentThread().setContextClassLoader(cl);
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public DeploymentConfiguration getDeploymentConfiguration() throws ClassNotFoundException, InstantiationException,
        IllegalAccessException, IOException {


        // setup proper classLoader to work on OSGi environment
        ClassLoader cl =
            ClassLoaderContext.setContextClassLoader(Thread.currentThread().getContextClassLoader(),
                                                     registry.getServiceDiscovery(),
                                                     "javax.ws.rs.ext.RuntimeDelegate",
                                                     "/META-INF/wink-alternate-shortcuts.properties",
                                                     "/META-INF/server/wink-providers");

        DeploymentConfiguration config = null;
        try {
            config = super.getDeploymentConfiguration();
        } finally {
            if (cl != null) {
                // return previous classLoader
                Thread.currentThread().setContextClassLoader(cl);
            }
        }

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
        providers.addProvider(new DataBindingJAXRSReader(registry), 0.2, true);
        providers.addProvider(new DataBindingJAXRSWriter(registry), 0.2, true);

        config.getResponseUserHandlers().add(new TuscanyResponseHandler());

        return config;
    }
    
    private void handleWadlRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            org.apache.wink.common.model.wadl.Application wadlDocument = null;
            WADLGenerator generator = new WADLGenerator();
            Set<Class<?>> classes = new HashSet<Class<?>>();
            classes.add(resourceClass);
            wadlDocument = generator.generate(binding.getURI(), classes);
            
            MessageBodyWriter<org.apache.wink.common.model.wadl.Application> writer = 
                this.getDeploymentConfiguration().getProvidersRegistry().
                     getMessageBodyWriter(org.apache.wink.common.model.wadl.Application.class, 
                                          org.apache.wink.common.model.wadl.Application.class,
                                          annotations, 
                                          MediaType.APPLICATION_XML_TYPE,
                                          null);
            
            writer.writeTo(wadlDocument, 
                           org.apache.wink.common.model.wadl.Application.class, 
                           org.apache.wink.common.model.wadl.Application.class, 
                           annotations,
                           MediaType.APPLICATION_XML_TYPE,
                           null, response.getOutputStream());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    /**
     * TuscanyResponseHandler
     *
     * Required to support declarative HTTP Headers
     */
    class TuscanyResponseHandler implements ResponseHandler {
        public void handleResponse(MessageContext context, HandlersChain chain) throws Throwable {

            // assert response is not committed
            final HttpServletResponse httpResponse = context.getAttribute(HttpServletResponse.class);
            if (httpResponse.isCommitted()) {
                logger.log(Level.FINE, "The response is already committed. Nothing to do.");
                return;
            }

            //process declarative headers
            for(HTTPHeader header : binding.getHttpHeaders()) {
                //treat special headers that need to be calculated
                if(header.getName().equalsIgnoreCase("Expires")) {
                    GregorianCalendar calendar = new GregorianCalendar();
                    calendar.setTime(new Date());

                    calendar.add(Calendar.HOUR, Integer.parseInt(header.getValue()));

                    httpResponse.setHeader("Expires", HTTPCacheContext.RFC822DateFormat.format( calendar.getTime() ));
                } else {
                    //default behaviour to pass the header value to HTTP response
                    httpResponse.setHeader(header.getName(), header.getValue());
                }
            }

            chain.doChain(context);
        }

        public void init(Properties props) {

        }
    }
}
