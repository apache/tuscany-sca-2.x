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
package org.apache.tuscany.runtime.webapp;

import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.tuscany.api.TuscanyRuntimeException;
import static org.apache.tuscany.runtime.webapp.Constants.RUNTIME_ATTRIBUTE;
import static org.apache.tuscany.runtime.webapp.Constants.ONLINE_PARAM;

/**
 * Launches a Tuscany runtime in a web application, loading information from servlet context parameters. This listener
 * manages one runtime per servlet context; the lifecycle of that runtime corresponds to the the lifecycle of the
 * associated servlet context.
 * <p/>
 * The runtime is launched in a child classloader of the web application, thereby providing isolation between
 * application and system artifacts. Application code only has access to the SCA API and may not reference Tuscany
 * system artifacts directly.
 * <p/>
 * The <code>web.xml</code> of a web application embedding Tuscany must have entries for this listener and {@link
 * TuscanySessionListener}. The latter notifies the runtime of session creation and expiration events through a
 * "bridging" contract, {@link WebappRuntime}. The <code>web.xml</code> may also optionally be configured with
 * entries for {@link TuscanyFilter} and {@link TuscanyServlet}. The former must be mapped to all urls that execute
 * "unmanaged" code which accesses the Tuscany runtime though the SCA API, for example, JSPs and Servlets. The latter
 * forwards service requests into the runtime, by default requests sent to URLs relative to the context path beginning
 * with <code>/services</code>.
 *
 * @version $Rev$ $Date$
 */
public class TuscanyContextListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent event) {
        ServletContext servletContext = event.getServletContext();
        WebappUtil utils = getUtils(servletContext);
        try {
            ClassLoader webappClassLoader = Thread.currentThread().getContextClassLoader();
            ClassLoader bootClassLoader = utils.getBootClassLoader(webappClassLoader);
            WebappRuntime runtime = utils.getRuntime(bootClassLoader);
            boolean online = Boolean.valueOf(utils.getInitParameter(ONLINE_PARAM, "true"));
            WebappRuntimeInfo info = new WebappRuntimeInfoImpl(servletContext,
                                                               servletContext.getResource("/WEB-INF/tuscany/"),
                                                               online);
            URL systemScdl = utils.getSystemScdl(bootClassLoader);
            URL applicationScdl = utils.getApplicationScdl(webappClassLoader);
            String name = utils.getApplicationName();

            runtime.setServletContext(servletContext);
            runtime.setMonitorFactory(runtime.createDefaultMonitorFactory());
            runtime.setRuntimeInfo(info);
            runtime.setHostClassLoader(webappClassLoader);
            runtime.setSystemScdl(systemScdl);
            runtime.setApplicationName(name);
            runtime.setApplicationScdl(applicationScdl);
            runtime.initialize();

            servletContext.setAttribute(RUNTIME_ATTRIBUTE, runtime);
        } catch (TuscanyRuntimeException e) {
            servletContext.log(e.getMessage(), e);
            e.printStackTrace();
            throw e;
        } catch (MalformedURLException e) {
            servletContext.log(e.getMessage(), e);
            e.printStackTrace();
            throw new TuscanyInitException(e);
        } catch (Throwable e) {
            servletContext.log(e.getMessage(), e);
            e.printStackTrace();
            throw new TuscanyInitException(e);
        }
    }

    protected WebappUtil getUtils(ServletContext servletContext) {
        return new WebappUtilImpl(servletContext);
    }

    public void contextDestroyed(ServletContextEvent event) {
        ServletContext servletContext = event.getServletContext();
        WebappRuntime runtime = (WebappRuntime) servletContext.getAttribute(RUNTIME_ATTRIBUTE);
        if (runtime == null) {
            return;
        }
        servletContext.removeAttribute(RUNTIME_ATTRIBUTE);
        runtime.destroy();
    }

}
