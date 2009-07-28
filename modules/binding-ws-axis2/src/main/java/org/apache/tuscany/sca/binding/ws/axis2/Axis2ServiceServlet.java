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
package org.apache.tuscany.sca.binding.ws.axis2;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;
import java.util.Vector;

import javax.servlet.GenericServlet;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.TransportInDescription;
import org.apache.axis2.engine.ListenerManager;
import org.apache.axis2.transport.http.AxisServlet;
import org.apache.axis2.transport.http.ListingAgent;
import org.apache.axis2.transport.http.server.HttpUtils;

/**
 * This overrides the Servlet init of the AxisServlet so Tuscany can use
 * a single Axis2 ConfigurationContext instance shared between AxisServlet 
 * instances for each SCA service with a ws binding. 
 * TODO: need to review if thats really what we want to be doing
 *
 * @version $Rev$ $Date$
 */
public class Axis2ServiceServlet extends AxisServlet {

    protected TuscanyListingAgent agent;

    private static final long serialVersionUID = 1L;
    private static final ServletConfig DUMMY_CONFIG = createDummyServletConfig();
    
    private boolean initCalled = false;
    
//JIRA TUSCANY-1561 Port to Axis2 1.3    
    private ConfigurationContext tmpconfigContext;

    public void init(ConfigurationContext configContext) {
        this.tmpconfigContext = configContext;
        try {
            //super.init(DUMMY_CONFIG);
        	init(DUMMY_CONFIG);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
        agent = new TuscanyListingAgent(configContext);
    }

    /**
     * Override Axis2 Servlet method to avoid loop when init 
     * is called after servletConfig already initialized by
     * this classes init(ConfigurationContext) method.
     */
    @Override
    public void init() throws ServletException {
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        ServletContext servletContext = config.getServletContext();
        servletContext.setAttribute(CONFIGURATION_CONTEXT, tmpconfigContext);
       
        //super.init(config);
        
        // A copy of the init method from the base class because we need to replace the 
        // version of the ListenerManager that is used so that we can get it's 
        // shutdown hook removed properly. 
        
        // prevent this method from being called more than once per instance
        if (initCalled == false) {
	        initCalled = true;
	        // We can't call super.init() as it will just call the AxisServlet version
	        // which we are replacing here. So reflect on the base class and
	        // set the private config field in the base class. 
	        //super.init(config);
	        try {
	        	Field field = GenericServlet.class.getDeclaredField("config");
	        	field.setAccessible(true);
	        	field.set(this, config);
	        } catch (Exception ex){
	        	ex.printStackTrace();
	        }
	        
	        try {
	            this.servletConfig = config;
	            //ServletContext servletContext = servletConfig.getServletContext();
	            this.configContext =
	                    (ConfigurationContext) servletContext.getAttribute(CONFIGURATION_CONTEXT);
	            if(configContext == null){
	                configContext = initConfigContext(config);
	                config.getServletContext().setAttribute(CONFIGURATION_CONTEXT, configContext);
	            }
	            axisConfiguration = configContext.getAxisConfiguration();
	
	            ListenerManager listenerManager = new TuscanyListenerManager();
	            listenerManager.init(configContext);
	            TransportInDescription transportInDescription = new TransportInDescription(
	                    Constants.TRANSPORT_HTTP);
	            transportInDescription.setReceiver(this);
	            listenerManager.addListener(transportInDescription, true);
	            listenerManager.start();
	            ListenerManager.defaultConfigurationContext = configContext;
	            super.agent = new ListingAgent(configContext);
	
	            initParams();
	
	        } catch (Exception e) {
	            throw new ServletException(e);
	        }
        }
    }

    /**
     * We've setup the Servlet by passing in a ConfigurationContext on our init method 
     * override this method to just return that
     */
    @Override
    protected ConfigurationContext initConfigContext(ServletConfig config) throws ServletException {
        return this.tmpconfigContext;
    }
    
    @Override
    public ServletConfig getServletConfig() {
        return DUMMY_CONFIG;
    }
    
    @Override
    public String getServletName() {
        return "TuscanyAxis2Servlet";
    }

    /**
     * The AxisServlet gets NPE during init without a ServletConfig so this is a mocked up one to prevent that.
     */
    private static ServletConfig createDummyServletConfig() {
        ServletConfig sc = new ServletConfig() {

            public String getServletName() {
                return "TuscanyAxis2DummyServlet";
            }

            public ServletContext getServletContext() {
                return new ServletContext() {

                    public ServletContext getContext(String uripath) {
                        return null;
                    }

                    @SuppressWarnings("unused") // it's on the Servlet 2.5 API so we need it
                    public String getContextPath() {
                        return null;
                    }

                    public int getMajorVersion() {
                        return 0;
                    }

                    public int getMinorVersion() {
                        return 0;
                    }

                    public String getMimeType(String file) {
                        return null;
                    }

                    public Set<?> getResourcePaths(String path) {
                        return Collections.emptySet();
                    }

                    public URL getResource(String path) throws MalformedURLException {
                        if("/".equals(path)) {
                            // HACK: To avoid NPE
                            return new URL("/axis2");
                        }
                        return null;
                    }

                    public InputStream getResourceAsStream(String path) {
                        return null;
                    }

                    public RequestDispatcher getRequestDispatcher(String path) {
                        return null;
                    }

                    public RequestDispatcher getNamedDispatcher(String arg0) {
                        return null;
                    }

                    public Servlet getServlet(String arg0) throws ServletException {
                        return null;
                    }

                    public Enumeration getServlets() {
                        return null;
                    }

                    public Enumeration getServletNames() {
                        return null;
                    }

                    public void log(String arg0) {
                    }

                    public void log(Exception arg0, String arg1) {
                    }

                    public void log(String arg0, Throwable arg1) {
                    }

                    public String getRealPath(String arg0) {
                        return null;
                    }

                    public String getServerInfo() {
                        return null;
                    }

                    public String getInitParameter(String arg0) {
                        return null;
                    }

                    public Enumeration getInitParameterNames() {
                        return null;
                    }

                    public Object getAttribute(String arg0) {
                        return null;
                    }

                    public Enumeration getAttributeNames() {
                        return null;
                    }

                    public void setAttribute(String arg0, Object arg1) {
                    }

                    public void removeAttribute(String arg0) {
                    }

                    public String getServletContextName() {
                        return null;
                    }
                };
            }

            public String getInitParameter(String arg0) {
                return null;
            }

            public Enumeration getInitParameterNames() {
                return new Vector().elements();
            }
        };
        return sc;
    }

    @Override
    public void destroy() {
        try {
            super.destroy();
            servletConfig = null;
            if (tmpconfigContext.getListenerManager() != null){
            	tmpconfigContext.getListenerManager().destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    
    }

    /**
     * Override the AxisServlet doGet to use the TuscanyListingAgent for ?wsdl 
     */
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {

        initContextRoot(request);

        String query = request.getQueryString();
        if ((query != null) && (query.indexOf("wsdl2") >= 0 ||
                query.indexOf("wsdl") >= 0 || query.indexOf("xsd") >= 0 ||
                query.indexOf("policy") >= 0)) {
            agent.processListService(request, response);
        } else {
            super.doGet(request, response);
        }
    }
    
    /**

    /**
     * Override the AxisServlet method so as to not add "/services" into the URL
     * and to work with Tuscany service names. can go once moved to Axis2 1.3
     */
    @Override
    public EndpointReference[] getEPRsForService(String serviceName, String ip) throws AxisFault {
        //RUNNING_PORT
        String port = (String) configContext.getProperty(ListingAgent.RUNNING_PORT);
        if (port == null) {
            port = "8080";
        }
        if (ip == null) {
            try {
                ip = HttpUtils.getIpAddress();
                if (ip == null) {
                    ip = "localhost";
                }
            } catch (SocketException e) {
//TUSCANY-1561 Port to Axis2 1.3                 
//                throw new AxisFault.(e);
                throw AxisFault.makeFault(e);
            }
        }

        URI epURI = URI.create("http://" + ip + ":" + port + "/" + serviceName).normalize();

        return new EndpointReference[]{new EndpointReference(epURI.toString())};
    }
    
}
