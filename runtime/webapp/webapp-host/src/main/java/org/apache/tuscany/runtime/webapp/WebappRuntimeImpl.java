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

import java.net.URI;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

import org.osoa.sca.ComponentContext;

import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.RegistrationException;
import org.apache.tuscany.spi.event.EventPublisher;

import org.apache.tuscany.core.component.event.HttpRequestEnded;
import org.apache.tuscany.core.component.event.HttpRequestStart;
import org.apache.tuscany.core.component.event.HttpSessionEnd;
import org.apache.tuscany.core.component.event.HttpSessionStart;
import org.apache.tuscany.core.runtime.AbstractRuntime;
import org.apache.tuscany.host.runtime.InitializationException;
import org.apache.tuscany.host.servlet.ServletRequestInjector;
import static org.apache.tuscany.runtime.webapp.Constants.CONTEXT_ATTRIBUTE;

/**
 * Bootstrapper for the Tuscany runtime in a web application host. This listener manages one runtime per servlet
 * context; the lifecycle of that runtime corresponds to the the lifecycle of the associated servlet context.
 * <p/>
 * The bootstrapper launches the runtime, booting system extensions and applications, according to the servlet
 * parameters defined in {@link Constants}. When the runtime is instantiated, it is placed in the servlet context with
 * the attribute {@link Constants#RUNTIME_PARAM}. The runtime implements {@link WebappRuntime} so that filters and
 * servlets loaded in the parent web app classloader may pass events and requests to it.
 * <p/>
 *
 * @version $$Rev$$ $$Date$$
 */

public class WebappRuntimeImpl extends AbstractRuntime implements WebappRuntime {
    private ServletContext servletContext;


    private ServletRequestInjector requestInjector;
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
            getComponentManager().registerJavaObject(WebappRuntimeInfo.COMPONENT_NAME,
                WebappRuntimeInfo.class,
                (WebappRuntimeInfo) getRuntimeInfo());
        } catch (RegistrationException e) {
            throw new InitializationException(e);
        }
    }

/*
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
            servletContext.setAttribute(Constants.CONTEXT_ATTRIBUTE, context);
        } catch (Exception e) {
            throw new ServletLauncherInitException(e);
        }
    }
*/

    public void destroy() {
        if (application != null) {
            application.stop();
            application = null;
        }
        super.destroy();
    }

    public void bindComponent(URI componentId) {
        Component component = getComponentManager().getComponent(componentId);
        if (component == null) {
            throw new TuscanyInitException("No component found with id " + componentId, componentId.toString());
        }
        ComponentContext componentContext = component.getComponentContext();
        servletContext.setAttribute(CONTEXT_ATTRIBUTE, componentContext);
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
/*
        application.publish(httpRequestStart);
*/
        ((EventPublisher) requestInjector).publish(httpRequestStart);
    }

    public void httpRequestEnded(Object sessionid) {
        HttpRequestEnded httpRequestEnded = new HttpRequestEnded(this, sessionid);
/*
        application.publish(httpRequestEnded);
*/
        ((EventPublisher) requestInjector).publish(httpRequestEnded);
    }


    public void startRequest() {
/*
        application.publish(new RequestStart(this));
*/
    }

    public void stopRequest() {
/*
        application.publish(new RequestEnd(this));
*/
    }
}
