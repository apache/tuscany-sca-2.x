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

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionEvent;
import javax.xml.stream.XMLInputFactory;

import org.osoa.sca.SCA;

import org.apache.tuscany.spi.bootstrap.ComponentNames;
import org.apache.tuscany.spi.bootstrap.RuntimeComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.deployer.Deployer;
import org.apache.tuscany.spi.event.EventPublisher;

import org.apache.tuscany.core.bootstrap.Bootstrapper;
import org.apache.tuscany.core.bootstrap.DefaultBootstrapper;
import org.apache.tuscany.core.component.event.HttpRequestEnded;
import org.apache.tuscany.core.component.event.HttpRequestStart;
import org.apache.tuscany.core.component.event.HttpSessionEnd;
import org.apache.tuscany.core.component.event.HttpSessionStart;
import org.apache.tuscany.core.component.event.RequestEnd;
import org.apache.tuscany.core.component.event.RequestStart;
import org.apache.tuscany.core.launcher.CompositeContextImpl;
import org.apache.tuscany.core.monitor.MonitorFactoryUtil;
import org.apache.tuscany.core.runtime.AbstractRuntime;
import org.apache.tuscany.host.MonitorFactory;
import org.apache.tuscany.host.RuntimeInfo;
import org.apache.tuscany.host.servlet.ServletRequestInjector;

/**
 * Bootstrapper for the Tuscany runtime in a web application host. This listener manages one runtime per servlet
 * context; the lifecycle of that runtime corresponds to the the lifecycle of the associated servlet context.
 * <p/>
 * The bootstrapper launches the runtime, booting system extensions and applications, according to the servlet
 * parameters defined in {@link Constants}. When the runtime is instantiated, it is placed in the servlet context with
 * the attribute {@link Constants.RUNTIME_ATTRIBUTE}. The runtime implements {@link WebappRuntime} so that filters and
 * servlets loaded in the parent web app classloader may pass events and requests to it.
 * <p/>
 * By default, the top-most application composite component will be returned when "non-managed" web application code
 * such as JSPs call {@link org.osoa.sca.CurrentCompositeContext}. If a composite deeper in the hierarchy should be
 * returned instead, the <code>web.xml</code> must contain an entry for {@link Constants.CURRENT_COMPOSITE_PATH_PARAM}
 * whose value is a component path expression using '/' as a delimeter such as foo/bar/baz.
 *
 * @version $$Rev$$ $$Date$$
 */

public class WebappRuntimeImpl extends AbstractRuntime implements WebappRuntime {
    private ServletContext servletContext;


    private ServletRequestInjector requestInjector;
    private CompositeContextImpl context;
    private RuntimeComponent runtime;
    private CompositeComponent systemComponent;
    private CompositeComponent tuscanySystem;
    private CompositeComponent application;

    public ServletContext getServletContext() {
        return servletContext;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public void initialize() {
        ClassLoader bootClassLoader = getClass().getClassLoader();

        // Read optional system monitor factory classname
        MonitorFactory mf = getMonitorFactory();


        XMLInputFactory xmlFactory = XMLInputFactory.newInstance("javax.xml.stream.XMLInputFactory", bootClassLoader);

        Bootstrapper bootstrapper = new DefaultBootstrapper(mf, xmlFactory);
        runtime = bootstrapper.createRuntime();
        runtime.start();
        systemComponent = runtime.getSystemComponent();

        // register the runtime info provided by the host
        // FIXME andyp@bea.com -- autowire appears to need an exact type match,
        // hence the need to register this twice
        systemComponent.registerJavaObject(RuntimeInfo.COMPONENT_NAME,
            RuntimeInfo.class,
            (WebappRuntimeInfo) getRuntimeInfo());
        systemComponent.registerJavaObject(WebappRuntimeInfo.COMPONENT_NAME,
            WebappRuntimeInfo.class,
            (WebappRuntimeInfo) getRuntimeInfo());

        // register the monitor factory provided by the host
        systemComponent.registerJavaObject("MonitorFactory", MonitorFactory.class, mf);

        systemComponent.start();

        if (getSystemScdl() == null) {
            throw new TuscanyInitException("Could not find system SCDL");
        }

        try {
            // deploy the system scdl
            Deployer deployer = bootstrapper.createDeployer();
            tuscanySystem = deploySystemScdl(deployer,
                systemComponent,
                ComponentNames.TUSCANY_SYSTEM,
                getSystemScdl(),
                bootClassLoader);
            tuscanySystem.start();

            requestInjector = (ServletRequestInjector) tuscanySystem.getSystemChild("servletHost").getServiceInstance();

            // switch to the system deployer
            deployer = (Deployer) tuscanySystem.getSystemChild("deployer").getServiceInstance();

            if (getApplicationScdl() == null) {
                throw new TuscanyInitException("Could not find application SCDL");
            }
            runtime.getRootComponent().start();
            application = deployApplicationScdl(deployer,
                runtime.getRootComponent(),
                getApplicationName(),
                getApplicationScdl(),
                getHostClassLoader());
            application.start();
            CompositeComponent current = application;
            String path = servletContext.getInitParameter(Constants.CURRENT_COMPOSITE_PATH_PARAM);
            if (path != null) {
                StringTokenizer tokenizer = new StringTokenizer(path, "/");
                while(tokenizer.hasMoreTokens()) {
                    SCAObject o = current.getChild(tokenizer.nextToken());
                    if (!(o instanceof CompositeComponent)){
                        ServletLauncherInitException e = new ServletLauncherInitException("Invalid context path");
                        e.setIdentifier(path);
                        throw e;
                    }
                    current = (CompositeComponent)o;
                }
            }
            context = new CompositeContextImpl(current);
        } catch (Exception e) {
            throw new ServletLauncherInitException(e);
        }
    }

    public void destroy() {
        context = null;
        if (application != null) {
            application.stop();
            application = null;
        }
        if (tuscanySystem != null) {
            tuscanySystem.stop();
            tuscanySystem = null;
        }
        if (systemComponent != null) {
            systemComponent.stop();
            systemComponent = null;
        }
        if (runtime != null) {
            runtime.stop();
            runtime = null;
        }
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

    public void httpRequestStarted(Object sessionid) {
        HttpRequestStart httpRequestStart = new HttpRequestStart(this, sessionid);
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

    /**
     * Returns a monitor factory for the funtime
     *
     * @param loggingLevel
     */
    private MonitorFactory getMonitorFactory(String loggingLevel) {
        String factoryName = "org.apache.tuscany.core.monitor.NullMonitorFactory";
        Map<String, Object> props = null;
        if (loggingLevel != null) {
            factoryName = "org.apache.tuscany.core.monitor.JavaLoggingMonitorFactory";
            props = new HashMap<String, Object>();
            Level level = Level.SEVERE;
            try {
                level = Level.parse(loggingLevel);
            } catch (IllegalArgumentException e) {
                // ignore bad loggingLevel
            }
            props.put("bundleName", "SystemMessages");
            props.put("defaultLevel", level);
        }

        return MonitorFactoryUtil.createMonitorFactory(factoryName, props);
    }
}
