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
import java.net.URL;
import java.util.Collection;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSessionEvent;

import org.apache.tuscany.core.runtime.AbstractRuntime;
import org.apache.tuscany.host.runtime.InitializationException;
import org.apache.tuscany.host.servlet.ServletRequestInjector;
import org.apache.tuscany.runtime.webapp.implementation.webapp.WebappComponent;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.ComponentException;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.GroupInitializationException;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.CompositeImplementation;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.resolver.ResolutionException;

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

public class WebappRuntimeImpl extends AbstractRuntime<WebappRuntimeInfo> implements WebappRuntime {
    private ServletContext servletContext;

    private ServletRequestInjector requestInjector;

    public WebappRuntimeImpl() {
        super(WebappRuntimeInfo.class);
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
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

    @Deprecated
    public void deploy(URI compositeId, URL applicationScdl, URI componentId) throws InitializationException {
        CompositeImplementation impl = new CompositeImplementation();
        impl.setScdlLocation(applicationScdl);
        impl.setClassLoader(getHostClassLoader());

        ComponentDefinition<CompositeImplementation> definition =
            new ComponentDefinition<CompositeImplementation>(compositeId, impl);
        Collection<Component> components;
        try {
            components = getDeployer().deploy(null, definition);
        } catch (LoaderException e) {
            throw new InitializationException(e);
        } catch (BuilderException e) {
            throw new InitializationException(e);
        } catch (ComponentException e) {
            throw new InitializationException(e);
        } catch (ResolutionException e) {
            throw new InitializationException(e);
        }
        for (Component component : components) {
            component.start();
        }

        try {
            ScopeRegistry scopeRegistry = getScopeRegistry();
            ScopeContainer<URI, URI> container = scopeRegistry.getScopeContainer(Scope.COMPOSITE);
            container.startContext(compositeId, compositeId);
            getWorkContext().setIdentifier(Scope.COMPOSITE, compositeId);
        } catch (GroupInitializationException e) {
            throw new InitializationException(e);
        }

        componentId = URI.create(compositeId.toString()+'/').resolve(componentId);
        Component component = getComponentManager().getComponent(componentId);
        if (component == null) {
            throw new TuscanyInitException("No component found with id " + componentId, componentId.toString());
        }
        if (component instanceof WebappComponent) {
            WebappComponent webapp = (WebappComponent) component;
            webapp.bind(getServletContext());
        }
    }

    public ServletRequestInjector getRequestInjector() {
        return requestInjector;
    }

    public void sessionCreated(HttpSessionEvent event) {
/*
        HttpSessionStart startSession = new HttpSessionStart(this, event.getSession().getId());
        application.publish(startSession);
        ((EventPublisher) requestInjector).publish(startSession);
*/
    }

    public void sessionDestroyed(HttpSessionEvent event) {
/*
        HttpSessionEnd endSession = new HttpSessionEnd(this, event.getSession().getId());
        application.publish(endSession);
        ((EventPublisher) requestInjector).publish(endSession);
*/
    }

    public void httpRequestStarted(HttpServletRequest request) {
/*
        HttpSession session = request.getSession(false);
        Object sessionId = session == null ? new LazyHTTPSessionId(request) : session.getId();
        HttpRequestStart httpRequestStart = new HttpRequestStart(this, sessionId);
        application.publish(httpRequestStart);
        ((EventPublisher) requestInjector).publish(httpRequestStart);
*/
    }

    public void httpRequestEnded(Object sessionid) {
/*
        HttpRequestEnded httpRequestEnded = new HttpRequestEnded(this, sessionid);
        application.publish(httpRequestEnded);
        ((EventPublisher) requestInjector).publish(httpRequestEnded);
*/
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
