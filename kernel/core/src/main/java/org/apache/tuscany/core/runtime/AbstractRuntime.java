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
package org.apache.tuscany.core.runtime;

import java.net.URI;
import java.net.URL;
import javax.xml.stream.XMLInputFactory;

import org.osoa.sca.ComponentContext;

import org.apache.tuscany.spi.bootstrap.ComponentNames;
import org.apache.tuscany.spi.bootstrap.RuntimeComponent;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.builder.Connector;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.ComponentException;
import org.apache.tuscany.spi.component.RegistrationException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.Deployer;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.resolver.ResolutionException;
import org.apache.tuscany.spi.services.management.TuscanyManagementService;

import org.apache.tuscany.core.bootstrap.Bootstrapper;
import org.apache.tuscany.core.bootstrap.DefaultBootstrapper;
import org.apache.tuscany.core.component.ComponentManager;
import org.apache.tuscany.core.component.ComponentManagerImpl;
import org.apache.tuscany.core.implementation.system.model.SystemCompositeImplementation;
import org.apache.tuscany.core.monitor.NullMonitorFactory;
import org.apache.tuscany.core.resolver.AutowireResolver;
import org.apache.tuscany.core.resolver.DefaultAutowireResolver;
import org.apache.tuscany.core.builder.ConnectorImpl;
import org.apache.tuscany.host.MonitorFactory;
import org.apache.tuscany.host.RuntimeInfo;
import org.apache.tuscany.host.management.ManagementService;
import org.apache.tuscany.host.runtime.InitializationException;
import org.apache.tuscany.host.runtime.TuscanyRuntime;

/**
 * @version $Rev$ $Date$
 */
public abstract class AbstractRuntime implements TuscanyRuntime {
    private static final URI MONITOR_URI =
        URI.create(ComponentNames.TUSCANY_SYSTEM_ROOT.toString() + "/MonitorFactory");
    private static final URI COMPONENT_MGR_URI =
        URI.create(ComponentNames.TUSCANY_SYSTEM_ROOT.toString() + "/ComponentManager");

    private static final URI AUTOWIRE_RESOLVER_URI =
        URI.create(ComponentNames.TUSCANY_SYSTEM_ROOT.toString() + "/AutowireResolver");

    private final XMLInputFactory xmlFactory;
    private URL systemScdl;
    private String applicationName;
    private URL applicationScdl;
    private ClassLoader hostClassLoader;
    private ClassLoader applicationClassLoader;
    private RuntimeInfo runtimeInfo;
    private MonitorFactory monitorFactory;
    private ManagementService<?> managementService;
    private ComponentManager componentManager;

    private RuntimeComponent runtime;
    private CompositeComponent systemComponent;
    private CompositeComponent tuscanySystem;
    private AutowireResolver resolver;

    protected AbstractRuntime() {
        this(new NullMonitorFactory());
    }

    protected AbstractRuntime(MonitorFactory monitorFactory) {
        this.monitorFactory = monitorFactory;
        xmlFactory = XMLInputFactory.newInstance("javax.xml.stream.XMLInputFactory", getClass().getClassLoader());
    }

    public URL getSystemScdl() {
        return systemScdl;
    }

    public void setSystemScdl(URL systemScdl) {
        this.systemScdl = systemScdl;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public URL getApplicationScdl() {
        return applicationScdl;
    }

    public void setApplicationScdl(URL applicationScdl) {
        this.applicationScdl = applicationScdl;
    }

    public ClassLoader getApplicationClassLoader() {
        return applicationClassLoader;
    }

    public void setApplicationClassLoader(ClassLoader applicationClassLoader) {
        this.applicationClassLoader = applicationClassLoader;
    }

    public ClassLoader getHostClassLoader() {
        return hostClassLoader;
    }

    public void setHostClassLoader(ClassLoader hostClassLoader) {
        this.hostClassLoader = hostClassLoader;
    }

    public RuntimeInfo getRuntimeInfo() {
        return runtimeInfo;
    }

    public void setRuntimeInfo(RuntimeInfo runtimeInfo) {
        this.runtimeInfo = runtimeInfo;
    }

    public MonitorFactory getMonitorFactory() {
        return monitorFactory;
    }

    public void setMonitorFactory(MonitorFactory monitorFactory) {
        this.monitorFactory = monitorFactory;
    }

    public ManagementService<?> getManagementService() {
        return managementService;
    }

    public void setManagementService(ManagementService<?> managementService) {
        this.managementService = managementService;
    }

    public void initialize() throws InitializationException {
        Bootstrapper bootstrapper = createBootstrapper();
        runtime = bootstrapper.createRuntime();
        runtime.start();

        systemComponent = runtime.getSystemComponent();
        registerSystemComponents();
        try {
            componentManager.register(systemComponent);
            componentManager.register(runtime.getRootComponent());
        } catch (RegistrationException e) {
            throw new InitializationException(e);
        }
        systemComponent.start();

        // deploy the system scdl
        try {
            tuscanySystem = deploySystemScdl(bootstrapper.createDeployer(),
                systemComponent,
                ComponentNames.TUSCANY_SYSTEM,
                getSystemScdl(),
                getClass().getClassLoader());
        } catch (LoaderException e) {
            throw new InitializationException(e);
        } catch (BuilderException e) {
            throw new InitializationException(e);
        } catch (ComponentException e) {
            throw new InitializationException(e);
        } catch (ResolutionException e) {
            throw new InitializationException(e);
        }
        tuscanySystem.start();
    }

    public void destroy() {
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


    public ComponentContext getComponentContext(URI componentId) {
        Component component = componentManager.getComponent(componentId);
        if (component == null) {
            return null;
        }
        return component.getComponentContext();
    }

    protected Bootstrapper createBootstrapper() {
        TuscanyManagementService tms = (TuscanyManagementService) getManagementService();
        resolver = new DefaultAutowireResolver();
        componentManager = new ComponentManagerImpl(tms, resolver);
        Connector connector = new ConnectorImpl(componentManager);
        return new DefaultBootstrapper(getMonitorFactory(), xmlFactory, componentManager, resolver, connector, tms);
    }

    protected void registerSystemComponents() throws InitializationException {
        try {
            componentManager.registerJavaObject(RuntimeInfo.COMPONENT_URI, RuntimeInfo.class, runtimeInfo);
            componentManager.registerJavaObject(MONITOR_URI, MonitorFactory.class, getMonitorFactory());
            // register the component manager with itself so it can be autowired
            componentManager.registerJavaObject(COMPONENT_MGR_URI, ComponentManager.class, componentManager);
            componentManager.registerJavaObject(AUTOWIRE_RESOLVER_URI, AutowireResolver.class, resolver);
        } catch (RegistrationException e) {
            throw new InitializationException(e);
        }
    }

    protected CompositeComponent deploySystemScdl(Deployer deployer,
                                                  CompositeComponent parent,
                                                  URI name,
                                                  URL systemScdl,
                                                  ClassLoader systemClassLoader)
        throws LoaderException, BuilderException, ComponentException, ResolutionException {

        SystemCompositeImplementation impl = new SystemCompositeImplementation();
        impl.setScdlLocation(systemScdl);
        impl.setClassLoader(systemClassLoader);
        ComponentDefinition<SystemCompositeImplementation> definition =
            new ComponentDefinition<SystemCompositeImplementation>(name, impl);

        return (CompositeComponent) deployer.deploy(parent, definition);
    }


    protected ComponentManager getComponentManager() {
        return componentManager;
    }
}
