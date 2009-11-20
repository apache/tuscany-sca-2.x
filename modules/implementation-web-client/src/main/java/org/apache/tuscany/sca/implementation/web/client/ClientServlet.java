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

package org.apache.tuscany.sca.implementation.web.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
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
 * Handles requests for SCA services and references that use <binding.dwr>
 */
public class ClientServlet extends DwrServlet {
    private static final long serialVersionUID = 1L;

    private transient Map<String, ServiceHolder> services;
//    private transient List<String> referenceNames;
    private transient boolean initialized;
    private transient Map<String, String> initParams;

    public static final String SCRIPT_PATH = "/org.oasisopen.sca.componentContext.js";

    public ClientServlet() {
        this.services = new HashMap<String, ServiceHolder>();
//        this.referenceNames = new ArrayList<String>();

        this.initParams = new HashMap<String, String>();
        // maybe use <binding.dwr> attributes to define the init params
        initParams.put("activeReverseAjaxEnabled", "true");
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        super.service(req, res);
    }

    /**
     * Initialize the Servlet
     * There is a single instance of this Servlet which is registered
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
     * Add in the handler to process the HTTP get for /sca/scaDomain.js
     * 
     * This wrappers the DWR Engine handler which returns the DWR engine.js script,
     * this wrappers that handler so as to add Tuscany specific header and footer code
     * to the DWR engine.js to define the Tuscany SCADomain control functions and
     * functions for each SCA service and reference that use <binding.dwr>.
     */
    private void addScriptHandler() {

        UrlProcessor urlProcessor = (UrlProcessor)getContainer().getBean(UrlProcessor.class.getName());

        final EngineHandler engineHandler =
            (EngineHandler)getContainer().getBean(PathConstants.URL_PREFIX + "/engine.js");

        final Handler scaDomainScriptHandler = new Handler() {
            public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
                PrintWriter out = response.getWriter();
                out.println("/** Apache Tuscany componentContext.js Header */");

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
            public Collection<?> getBeanNames() {
                return Arrays.asList(new String[] {PathConstants.URL_PREFIX + SCRIPT_PATH});
            }
        });
    }

    /**
     * Adds the JavaScript defining SCADomain, its control functions,
     * and functions for all the available SCA services and references.
     */
    private void tuscanyFooter(HttpServletRequest request, PrintWriter out) {
        out.println("if (SCA == undefined) var SCA = new Object();");
        out.println("if (SCA.componentContext == undefined) {");
        out.println("   SCA.componentContext = new Object();");
        out.println("   SCA.componentContext.serviceNames = [];");
        out.println("   SCA.componentContext.serviceProxys = [];");
        out.println("   SCA.componentContext.getService = function(serviceName){");
        out.println("      var i = SCA.componentContext.serviceNames.indexOf(serviceName);");
        out.println("      return SCA.componentContext.serviceProxys[i];");
        out.println("   };");
        out.println("   if (componentContext == undefined) var componentContext = SCA.componentContext;");
        out.println("}");

//        out.println("/** Apache Tuscany scaDomain.js Footer */");
//        out.println();
//        out.println("function scaDomain() { }");
//        out.println();
//        out.println("// SCA services");

        // Use the DWR remoter to generate the JavaScipt function for each SCA service        
        Remoter remoter = (Remoter)getContainer().getBean(Remoter.class.getName());


        for (String serviceName : services.keySet()) {
            String path = request.getServletPath() + "/" + services.get(serviceName).c.getName();
            String serviceScript = remoter.generateInterfaceScript(serviceName, path);
            out.println(serviceScript);
            out.println("SCA.componentContext.serviceNames.push('" + serviceName + "');");
            out.println("SCA.componentContext.serviceProxys.push(" + serviceName + ");");
//            ServiceHolder s = services.get(serviceName);
//            final Class<?> iface = ((JavaInterface)s.cr.getInterfaceContract().getInterface()).getJavaClass();
//            for (Method m : iface.getMethods()) {
//                out.println("if (" + serviceName + " == null) var " + serviceName + " = {};");
//                out.println("SCA.componentContext.serviceNames.push('" + serviceName + "');");
////                out.println("SCA.componentContext.serviceProxys.push(new JSONRpcClient('" + serviceName + "').Service);");
//                out.println(serviceName + "." + m.getName() + " = function(p0, callback) {");
//                out.println("    dwr.engine._execute(" + serviceName + "._path, '" + serviceName + "', '" + m.getName() + "', p0, callback);");
//                out.println("  }");
//            }
        }
        
//        if (referenceNames.size() > 0) {
//
//            out.println("// SCA reverse ajax control functions");
//            out.println();
//            out.println("scaDomain.open = function() { dwr.engine.setActiveReverseAjax(true); };");
//            out.println("scaDomain.close = function() { dwr.engine.setActiveReverseAjax(false); };");
//
//            out.println();
//            out.println("// SCA references");
//            out.println();
//           
//            // the JavaScript function for SCA references has an 
//            // empty impl as it uses DWR severside "push" 
//            for (String referenceName : referenceNames) {
//                out.println("function " + referenceName + "() { }");
//            }
//        }

        out.println();
        out.println("/** End of Apache Tuscany componentContext.js */");
        out.println();
    }

//    /**
//     * Add an SCA reference to be added to the DWR runtime 
//     */
//    public void addReference(String name) {
//        referenceNames.add(name);
//    }

    /**
     * Add an SCA service to be added to the DWR runtime
     */
    public void addService(ComponentReference cr, RuntimeComponent c) {
        ServiceHolder holder = new ServiceHolder();
        holder.name = cr.getName();
        holder.cr = cr;
        holder.c = c;
        services.put(cr.getName(), holder);
    }

    /**
     * Defines each SCA service proxy instance to DWR 
     */
    private void initServices() {
        CreatorManager creatorManager = (CreatorManager)getContainer().getBean(CreatorManager.class.getName());

        for (final ServiceHolder service : services.values()) {

            final Class<?> iface = ((JavaInterface)service.cr.getInterfaceContract().getInterface()).getJavaClass();
            final Object instance = service.c.getComponentContext().getServiceReference(iface, service.cr.getName()).getService();
            
            creatorManager.addCreator(service.cr.getName(), new AbstractCreator() {
                public Class<?> getType() {
                    return iface;
                }

                public Object getInstance() throws InstantiationException {
                    return instance;
                }
            });
        }
    }

    // utility class to aid passing around services
    private class ServiceHolder {
        String name;
        ComponentReference cr;
        RuntimeComponent c;
    }

    /**
     * Patch the ServletConfig to enable setting init params for DWR
     * and so DWR can't see the Tuscany servlet's init params.
     */
    private ServletConfig patchConfig(final ServletConfig servletConfig) {
        ServletConfig patchedContext = new ServletConfig() {
            public String getInitParameter(String name) {
                return initParams.get(name);
            }
            public Enumeration<?> getInitParameterNames() {
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

}
