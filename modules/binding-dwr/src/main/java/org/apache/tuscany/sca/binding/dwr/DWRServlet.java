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

package org.apache.tuscany.sca.binding.dwr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.directwebremoting.Container;
import org.directwebremoting.create.AbstractCreator;
import org.directwebremoting.extend.CreatorManager;
import org.directwebremoting.extend.Handler;
import org.directwebremoting.extend.Remoter;
import org.directwebremoting.servlet.DwrServlet;
import org.directwebremoting.servlet.EngineHandler;
import org.directwebremoting.servlet.PathConstants;
import org.directwebremoting.servlet.UrlProcessor;

/**
 * Tuscany customized DWR Servlet to implement support for the DWR binding
 * 
 * Handles requests for SCA services and references that use <binding.dwr>,
 * and also the HTTP GET for the Tuscany DWR system script "scaDomain.js"  
 */
public class DWRServlet extends DwrServlet {
    private static final long serialVersionUID = 1L;

    transient protected Map<String, ServiceHolder> services;
    transient protected List<String> referenceNames;
    transient protected boolean initialized;
    transient protected Map<String, String> initParams;

    protected static final String SCADOMAIN_SCRIPT_PATH = "/scaDomain.js";
    public static final String AJAX_SERVLET_PATH = "/SCADomain";

    public DWRServlet() {
        this.services = new HashMap<String, ServiceHolder>();
        this.referenceNames = new ArrayList<String>();

        this.initParams = new HashMap<String, String>();
        // maybe use <binding.dwr> attributes to define the init params
        initParams.put("activeReverseAjaxEnabled", "true");
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        super.service(patchRequest((HttpServletRequest)req), res);
    }

    /**
     * Initialize the servlet
     * There is a single instance of this servlet which is registered
     * for multiple path mappings, but the init should only run once.
     */
    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        if (!initialized) {
            super.init(patchConfig(servletConfig));
            addScriptHandler();
            initServices();
            initialized = true;
        }
    }

    /**
     * Add in the handler to process the http get for /sca/scaDomain.js
     * 
     * This wrappers the DWR Engine handler which returns the DWR engine.js script,
     * this wrappers that handler so as to add Tuscany specific header and footer code
     * to the DWR engine.js to define the Tuscany SCADomain control functions and
     * functions for each SCA service and reference that use <binding.dwr>.
     */
    protected void addScriptHandler() {

        UrlProcessor urlProcessor = (UrlProcessor)getContainer().getBean(UrlProcessor.class.getName());

        final EngineHandler engineHandler =
            (EngineHandler)getContainer().getBean(PathConstants.URL_PREFIX + "/engine.js");

        final Handler scaDomainScriptHandler = new Handler() {
            public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
                PrintWriter out = response.getWriter();
                out.println("/** Apache Tuscany scaDomain.js Header */");

                engineHandler.handle(request, response);

                tuscanyFooter(request, out);
            }

        };

        // add the scaDomainScriptHandler to the urlProcessor
        // bit of a hack, there's probably cleaner way to get it registered
        urlProcessor.afterContainerSetup(new Container() {
            public Object getBean(String name) {
                return scaDomainScriptHandler;
            }
            public Collection getBeanNames() {
                return Arrays.asList(new String[] {PathConstants.URL_PREFIX + SCADOMAIN_SCRIPT_PATH});
            }
        });
    }

    /**
     * Adds the JavaScript defining SCADomain, its control functions,
     * and functions for all the available SCA services and references.
     */
    protected void tuscanyFooter(HttpServletRequest request, PrintWriter out) {
        out.println("/** Apache Tuscany scaDomain.js Footer */");
        out.println();
        out.println("function scaDomain() { }");
        out.println();

        // Alter the value of these variables in DWR engine.js to add in this servlet's path
        out.println("dwr.engine._ModePlainCall = '" + AJAX_SERVLET_PATH + "/call/plaincall/';");
        out.println("dwr.engine._ModeHtmlCall = '" + AJAX_SERVLET_PATH + "/call/htmlcall/';");
        out.println("dwr.engine._ModePlainPoll = '" + AJAX_SERVLET_PATH + "/call/plainpoll/';");
        out.println("dwr.engine._ModeHtmlPoll = '" + AJAX_SERVLET_PATH + "/call/htmlpoll/';");
        
        out.println();
        out.println("// SCA sevices");

        // Use the DWR remoter to generate the JavaScipt function for each SCA service        
        Remoter remoter = (Remoter)getContainer().getBean(Remoter.class.getName());

        String path = request.getContextPath() + request.getServletPath();

        for (String serviceName : services.keySet()) {
            String serviceScript = remoter.generateInterfaceScript(serviceName, path);
            out.println(serviceScript);
        }
        
        if (referenceNames.size() > 0) {

            out.println("// SCA reverse ajax control functions");
            out.println();
            out.println("scaDomain.open = function() { dwr.engine.setActiveReverseAjax(true); };");
            out.println("scaDomain.close = function() { dwr.engine.setActiveReverseAjax(false); };");

            out.println();
            out.println("// SCA references");
            out.println();
           
            // the JavaScript function for SCA references has an 
            // empty impl as it uses DWR severside "push" 
            for (String referenceName : referenceNames) {
                out.println("function " + referenceName + "() { }");
            }
        }

        out.println();
        out.println("/** End of Apache Tuscany scaDomain.js */");
        out.println();
    }

    /**
     * Add an SCA reference to be added to the DWR runtime 
     */
    public void addReference(String name) {
        referenceNames.add(name);
    }

    /**
     * Add an SCA service to be added to the DWR runtime
     */
    public void addService(String name, final Class type, final Object instance) {
        ServiceHolder holder = new ServiceHolder();
        holder.name = name;
        holder.type = type;
        holder.instance = instance;
        services.put(name, holder);
    }

    /**
     * Defines each SCA service proxy instance to DWR 
     */
    protected void initServices() {
        CreatorManager creatorManager = (CreatorManager)getContainer().getBean(CreatorManager.class.getName());

        for (final ServiceHolder holder : services.values()) {
            creatorManager.addCreator(holder.name, new AbstractCreator() {
                public Class getType() {
                    return holder.type;
                }

                public Object getInstance() throws InstantiationException {
                    return holder.instance;
                }
            });
        }
    }

    // utility class to aid passing around services
    class ServiceHolder {
        String name;
        Class type;
        Object instance;
    }

    /**
     * Patch the ServletConfig to enable setting init params for DWR
     * and so DWR can't see the Tuscany servlet's init params.
     */
    protected ServletConfig patchConfig(final ServletConfig servletConfig) {
        ServletConfig patchedContext = new ServletConfig() {
            public String getInitParameter(String name) {
                return initParams.get(name);
            }
            public Enumeration getInitParameterNames() {
                return Collections.enumeration(initParams.keySet());
            }
            public ServletContext getServletContext() {
                return servletConfig.getServletContext();
            }
            public String getServletName() {
                return servletConfig.getServletName();
            }
        };
        return patchedContext;
    }

    /**
     * This changes the value returned from getPathInfo so that it does not include
     * the path prefix of this Servlet. The DWR servlet expects its handlers to be
     * at the root, eg /call/plaincall/, so they aren't found if the value request 
     * getPathInfo returns includes this servlets path, eg /SCADomain/call/plaincall/
     */
    protected HttpServletRequest patchRequest(final HttpServletRequest req) {

        final String translatedPath = req.getPathInfo().substring(AJAX_SERVLET_PATH.length());

        HttpServletRequest patchedRequest = new HttpServletRequest() {
            public Object getAttribute(String arg0) {
                return req.getAttribute(arg0);
            }
            public Enumeration getAttributeNames() {
                return req.getAttributeNames();
            }
            public String getCharacterEncoding() {
                return req.getCharacterEncoding();
            }
            public int getContentLength() {
                return req.getContentLength();
            }
            public String getContentType() {
                return req.getContentType();
            }
            public ServletInputStream getInputStream() throws IOException {
                return req.getInputStream();
            }
            public String getLocalAddr() {
                return req.getLocalAddr();
            }
            public String getLocalName() {
                return req.getLocalName();
            }
            public int getLocalPort() {
                return req.getLocalPort();
            }
            public Locale getLocale() {
                return req.getLocale();
            }
            public Enumeration getLocales() {
                return req.getLocales();
            }
            public String getParameter(String arg0) {
                return req.getParameter(arg0);
            }
            public Map getParameterMap() {
                return req.getParameterMap();
            }
            public Enumeration getParameterNames() {
                return req.getParameterNames();
            }
            public String[] getParameterValues(String arg0) {
                return req.getParameterValues(arg0);
            }
            public String getProtocol() {
                return req.getProtocol();
            }
            public BufferedReader getReader() throws IOException {
                return req.getReader();
            }
            @SuppressWarnings("deprecation")
            public String getRealPath(String arg0) {
                return req.getRealPath(arg0);
            }
            public String getRemoteAddr() {
                return req.getRemoteAddr();
            }
            public String getRemoteHost() {
                return req.getRemoteHost();
            }
            public int getRemotePort() {
                return req.getRemotePort();
            }
            public RequestDispatcher getRequestDispatcher(String arg0) {
                return req.getRequestDispatcher(arg0);
            }
            public String getScheme() {
                return req.getScheme();
            }
            public String getServerName() {
                return req.getServerName();
            }
            public int getServerPort() {
                return req.getServerPort();
            }
            public boolean isSecure() {
                return req.isSecure();
            }
            public void removeAttribute(String arg0) {
                req.removeAttribute(arg0);
            }
            public void setAttribute(String arg0, Object arg1) {
                req.setAttribute(arg0, arg1);
            }
            public void setCharacterEncoding(String arg0) throws UnsupportedEncodingException {
                req.setCharacterEncoding(arg0);
            }
            public String getAuthType() {
                return req.getAuthType();
            }
            public String getContextPath() {
                return req.getContextPath();
            }
            public Cookie[] getCookies() {
                return req.getCookies();
            }
            public long getDateHeader(String arg0) {
                return req.getDateHeader(arg0);
            }
            public String getHeader(String arg0) {
                return req.getHeader(arg0);
            }
            public Enumeration getHeaderNames() {
                return req.getHeaderNames();
            }
            public Enumeration getHeaders(String arg0) {
                return req.getHeaders(arg0);
            }
            public int getIntHeader(String arg0) {
                return req.getIntHeader(arg0);
            }
            public String getMethod() {
                return req.getMethod();
            }
            public String getPathInfo() {

                // *** return the translated path

                return translatedPath;
            }
            public String getPathTranslated() {
                return req.getPathTranslated();
            }
            public String getQueryString() {
                return req.getQueryString();
            }
            public String getRemoteUser() {
                return req.getRemoteUser();
            }
            public String getRequestURI() {
                return req.getRequestURI();
            }
            public StringBuffer getRequestURL() {
                return req.getRequestURL();
            }
            public String getRequestedSessionId() {
                return req.getRequestedSessionId();
            }
            public String getServletPath() {
                return req.getServletPath();
            }
            public HttpSession getSession() {
                return req.getSession();
            }
            public HttpSession getSession(boolean arg0) {
                return req.getSession(arg0);
            }
            public Principal getUserPrincipal() {
                return req.getUserPrincipal();
            }
            public boolean isRequestedSessionIdFromCookie() {
                return req.isRequestedSessionIdFromCookie();
            }
            public boolean isRequestedSessionIdFromURL() {
                return req.isRequestedSessionIdFromURL();
            }
            @SuppressWarnings("deprecation")
            public boolean isRequestedSessionIdFromUrl() {
                return req.isRequestedSessionIdFromUrl();
            }
            public boolean isRequestedSessionIdValid() {
                return req.isRequestedSessionIdValid();
            }
            public boolean isUserInRole(String arg0) {
                return req.isUserInRole(arg0);
            }
            
        };
        return patchedRequest;
    }
}
