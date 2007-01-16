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

import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

import org.apache.tuscany.core.component.event.HttpRequestEnded;
import org.apache.tuscany.core.component.event.HttpRequestStart;
import org.apache.tuscany.core.component.event.HttpSessionEnd;
import org.apache.tuscany.core.component.event.HttpSessionStart;
import org.apache.tuscany.core.component.event.RequestEnd;
import org.apache.tuscany.core.component.event.RequestStart;
import org.apache.tuscany.core.launcher.CompositeContextImpl;
import org.apache.tuscany.core.runtime.AbstractRuntime;
import org.apache.tuscany.host.runtime.InitializationException;
import org.apache.tuscany.host.servlet.ServletRequestInjector;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.ComponentRegistrationException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.event.EventPublisher;
import org.osoa.sca.SCA;

/**
 * Bootstrapper for the Tuscany runtime in a web application host. This listener manages one runtime per servlet
 * context; the lifecycle of that runtime corresponds to the the lifecycle of the associated servlet context.
 * <p/>
 * The bootstrapper launches the runtime, booting system extensions and applications, according to the servlet
 * parameters defined in {@link Constants}. When the runtime is instantiated, it is placed in the servlet context with
 * the attribute {@link Constants#RUNTIME_PARAM}. The runtime implements {@link WebappRuntime} so that filters and
 * servlets loaded in the parent web app classloader may pass events and requests to it.
 * <p/>
 * By default, the top-most application composite component will be returned when "non-managed" web application code
 * such as JSPs call {@link org.osoa.sca.CurrentCompositeContext}. If a composite deeper in the hierarchy should be
 * returned instead, the <code>web.xml</code> must contain an entry for {@link Constants#CURRENT_COMPOSITE_PATH_PARAM}
 * whose value is a component path expression using '/' as a delimeter such as foo/bar/baz.
 *
 * @version $$Rev$$ $$Date$$
 */

public class WebappRuntimeImpl extends AbstractRuntime implements WebappRuntime {
    private ServletContext servletContext;


    private ServletRequestInjector requestInjector;
    private CompositeContextImpl context;
    private CompositeComponent application;

    public ServletContext getServletContext() {
        return servletContext;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    protected void registerSystemComponents() throws InitializationException {
        super.registerSystemComponents();
        try {
            getSystemComponent().registerJavaObject(WebappRuntimeInfo.COMPONENT_NAME,
                                                    WebappRuntimeInfo.class,
                                                    (WebappRuntimeInfo) getRuntimeInfo());
        } catch (ComponentRegistrationException e) {
            throw new InitializationException(e);
        }
    }

    public void initialize() throws InitializationException {
        super.initialize();

        try {
            SCAObject host = getTuscanySystem().getSystemChild("servletHost");
            if (!(host instanceof AtomicComponent)) {
                throw new InitializationException("Servlet host must be an atomic component");
            }
            requestInjector = (ServletRequestInjector) ((AtomicComponent) host).getTargetInstance();

            if (getApplicationScdl() == null) {
                throw new TuscanyInitException("Could not find application SCDL");
            }
            getRuntime().getRootComponent().start();
            application = deployApplicationScdl(getDeployer(),
                getRuntime().getRootComponent(),
                getApplicationName(),
                getApplicationScdl(),
                getHostClassLoader());
            application.start();
            CompositeComponent current = application;
            String path = servletContext.getInitParameter(Constants.CURRENT_COMPOSITE_PATH_PARAM);
            if (path != null) {
                StringTokenizer tokenizer = new StringTokenizer(path, "/");
                while (tokenizer.hasMoreTokens()) {
                    SCAObject o = current.getChild(tokenizer.nextToken());
                    if (!(o instanceof CompositeComponent)) {
                        throw new ServletLauncherInitException("Invalid context path", path);
                    }
                    current = (CompositeComponent) o;
                }
            }
            context = new CompositeContextImpl(current, getWireService());
        } catch (Exception e) {
            throw new ServletLauncherInitException(e);
        }
    }

    public void destroy() {
        if (application != null) {
            application.stop();
            application = null;
        }
        super.destroy();
    }

    public SCA getContext() {
        return context;
    }

    public ServletRequestInjector getRequestInjector() {
        return requestInjector;
    }

    public void sessionCreated(HttpSessionEvent event) {
        HttpSessionStart startSession = new HttpSessionStart(this, event.getSession().getId());
        application.publish(startSession);
        ((EventPublisher) requestInjector).publish(startSession);
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSessionEnd endSession = new HttpSessionEnd(this, event.getSession().getId());
        application.publish(endSession);
        ((EventPublisher) requestInjector).publish(endSession);
    }

    public void httpRequestStarted(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object sessionId = session == null ? new LazyHTTPSessionId(request) : session.getId();
        HttpRequestStart httpRequestStart = new HttpRequestStart(this, sessionId);
        application.publish(httpRequestStart);
        ((EventPublisher) requestInjector).publish(httpRequestStart);
    }

    public void httpRequestEnded(Object sessionid) {
        HttpRequestEnded httpRequestEnded = new HttpRequestEnded(this, sessionid);
        application.publish(httpRequestEnded);
        ((EventPublisher) requestInjector).publish(httpRequestEnded);
    }


    public void startRequest() {
        application.publish(new RequestStart(this));
    }

    public void stopRequest() {
        application.publish(new RequestEnd(this));
    }
}
