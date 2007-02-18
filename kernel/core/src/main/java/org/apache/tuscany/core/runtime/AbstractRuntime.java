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
import java.util.Collection;
import javax.xml.stream.XMLInputFactory;

import org.osoa.sca.ComponentContext;

import org.apache.tuscany.spi.bootstrap.ComponentNames;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.builder.Connector;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.ComponentException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.RegistrationException;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.deployer.Deployer;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.resolver.ResolutionException;
import org.apache.tuscany.spi.services.management.TuscanyManagementService;

import org.apache.tuscany.core.bootstrap.Bootstrapper;
import org.apache.tuscany.core.bootstrap.DefaultBootstrapper;
import org.apache.tuscany.core.builder.ConnectorImpl;
import org.apache.tuscany.core.component.ComponentManager;
import org.apache.tuscany.core.component.ComponentManagerImpl;
import org.apache.tuscany.core.component.event.ComponentStart;
import org.apache.tuscany.core.implementation.system.model.SystemCompositeImplementation;
import org.apache.tuscany.core.monitor.NullMonitorFactory;
import org.apache.tuscany.core.resolver.AutowireResolver;
import org.apache.tuscany.core.resolver.DefaultAutowireResolver;
import org.apache.tuscany.host.MonitorFactory;
import org.apache.tuscany.host.RuntimeInfo;
import org.apache.tuscany.host.management.ManagementService;
import org.apache.tuscany.host.runtime.InitializationException;
import org.apache.tuscany.host.runtime.TuscanyRuntime;

/**
 * @version $Rev$ $Date$
 */
public abstract class AbstractRuntime<I extends RuntimeInfo> implements TuscanyRuntime<I> {
    private static final URI MONITOR_URI = ComponentNames.TUSCANY_SYSTEM_ROOT.resolve("MonitorFactory");

    private static final URI COMPONENT_MGR_URI = ComponentNames.TUSCANY_SYSTEM_ROOT.resolve("ComponentManager");

    private static final URI AUTOWIRE_RESOLVER_URI = ComponentNames.TUSCANY_SYSTEM_ROOT.resolve("AutowireResolver");

    private static final URI RUNTIME_INFO_URI = ComponentNames.TUSCANY_SYSTEM_ROOT.resolve("RuntimeInfo");

    private final XMLInputFactory xmlFactory;
    private URL systemScdl;
    private String applicationName;
    private URL applicationScdl;
    private ClassLoader hostClassLoader;
    private ClassLoader applicationClassLoader;
    private Class<I> runtimeInfoType;
    private I runtimeInfo;
    private MonitorFactory monitorFactory;
    private ManagementService<?> managementService;
    private ComponentManager componentManager;

    private CompositeComponent systemComponent;
    private CompositeComponent tuscanySystem;
    private AutowireResolver resolver;

    protected AbstractRuntime(Class<I> runtimeInfoType) {
        this(runtimeInfoType, new NullMonitorFactory());
    }

    protected AbstractRuntime(Class<I> runtimeInfoType, MonitorFactory monitorFactory) {
        this.runtimeInfoType = runtimeInfoType;
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

    public I getRuntimeInfo() {
        return runtimeInfo;
    }

    public void setRuntimeInfo(I runtimeInfo) {
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
        URI name = ComponentNames.TUSCANY_SYSTEM_ROOT.resolve("main");
        Bootstrapper bootstrapper = createBootstrapper();

        registerSystemComponents();

        // deploy the system scdl
        Collection<Component> components;
        try {
            components = deploySystemScdl(bootstrapper.createDeployer(),
                systemComponent,
                name,
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
        for (Component component : components) {
            component.start();
        }
        CompositeComponent composite = (CompositeComponent) componentManager.getComponent(name);
        composite.onEvent(new ComponentStart(this, name));
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
        return new DefaultBootstrapper(getMonitorFactory(), xmlFactory, componentManager, resolver, connector);
    }

    protected void registerSystemComponents() throws InitializationException {
        try {
            componentManager.registerJavaObject(RUNTIME_INFO_URI, runtimeInfoType, runtimeInfo);
            componentManager.registerJavaObject(MONITOR_URI, MonitorFactory.class, getMonitorFactory());
            // register the component manager with itself so it can be autowired
            componentManager.registerJavaObject(COMPONENT_MGR_URI, ComponentManager.class, componentManager);
            componentManager.registerJavaObject(AUTOWIRE_RESOLVER_URI, AutowireResolver.class, resolver);
        } catch (RegistrationException e) {
            throw new InitializationException(e);
        }
    }

    protected Collection<Component> deploySystemScdl(Deployer deployer,
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

        return deployer.deploy(parent, definition);
    }


    protected ComponentManager getComponentManager() {
        return componentManager;
    }


    protected Deployer getDeployer() {
        try {
            AtomicComponent component =
                (AtomicComponent) getComponentManager().getComponent(ComponentNames.TUSCANY_DEPLOYER);
            return (Deployer) component.getTargetInstance();
        } catch (TargetResolutionException e) {
            throw new AssertionError(e);
        }
    }
}
