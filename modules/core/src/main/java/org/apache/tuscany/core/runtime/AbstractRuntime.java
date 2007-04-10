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

import static org.apache.tuscany.spi.bootstrap.ComponentNames.TUSCANY_DEPLOYER;
import static org.apache.tuscany.spi.bootstrap.ComponentNames.TUSCANY_SYSTEM;
import static org.apache.tuscany.spi.bootstrap.ComponentNames.TUSCANY_SYSTEM_ROOT;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.ComponentService;
import org.apache.tuscany.assembly.impl.DefaultAssemblyFactory;
import org.apache.tuscany.core.bootstrap.Bootstrapper;
import org.apache.tuscany.core.bootstrap.DefaultBootstrapper;
import org.apache.tuscany.core.bootstrap.ExtensionPointRegistryImpl;
import org.apache.tuscany.core.component.ComponentManagerImpl;
import org.apache.tuscany.core.component.SimpleWorkContext;
import org.apache.tuscany.core.monitor.NullMonitorFactory;
import org.apache.tuscany.core.services.classloading.ClassLoaderRegistryImpl;
import org.apache.tuscany.core.util.IOHelper;
import org.apache.tuscany.host.MonitorFactory;
import org.apache.tuscany.host.RuntimeInfo;
import org.apache.tuscany.host.management.ManagementService;
import org.apache.tuscany.host.monitor.FormatterRegistry;
import org.apache.tuscany.host.runtime.InitializationException;
import org.apache.tuscany.host.runtime.TuscanyRuntime;
import org.apache.tuscany.interfacedef.java.JavaInterface;
import org.apache.tuscany.interfacedef.java.impl.DefaultJavaFactory;
import org.apache.tuscany.services.spi.contribution.ContributionService;
import org.apache.tuscany.spi.bootstrap.ModuleActivator;
import org.apache.tuscany.spi.bootstrap.ExtensionPointRegistry;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.ComponentManager;
import org.apache.tuscany.spi.component.RegistrationException;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.deployer.Deployer;
import org.apache.tuscany.spi.services.classloading.ClassLoaderRegistry;
import org.apache.tuscany.spi.services.management.TuscanyManagementService;
import org.osoa.sca.ComponentContext;

/**
 * @version $Rev$ $Date$
 */
public abstract class AbstractRuntime<I extends RuntimeInfo> implements TuscanyRuntime<I> {
    private static final URI MONITOR_URI = TUSCANY_SYSTEM_ROOT.resolve("MonitorFactory");

    private static final URI COMPONENT_MGR_URI = TUSCANY_SYSTEM_ROOT.resolve("ComponentManager");

    private static final URI SCOPE_REGISTRY_URI = TUSCANY_SYSTEM_ROOT.resolve("ScopeRegistry");

    private static final URI WORK_CONTEXT_URI = TUSCANY_SYSTEM.resolve("WorkContext");

    private static final URI RUNTIME_INFO_URI = TUSCANY_SYSTEM_ROOT.resolve("RuntimeInfo");

    private static final URI CLASSLOADER_REGISTRY_URI = TUSCANY_SYSTEM_ROOT.resolve("ClassLoaderRegistry");

    private static final URI HOST_CLASSLOADER_ID = URI.create("sca://./hostClassLoader");

    private static final URI BOOT_CLASSLOADER_ID = URI.create("sca://./bootClassLoader");

    protected final XMLInputFactory xmlFactory;
    protected URL systemScdl;
    protected String applicationName;
    protected URL applicationScdl;
    protected Class<I> runtimeInfoType;
    protected ManagementService<?> managementService;

    // primorial components automatically registered with the runtime
    /**
     * Information provided by the host about its runtime environment.
     */
    protected I runtimeInfo;

    /**
     * MonitorFactory provided by the host for directing events to its
     * management framework.
     */
    protected MonitorFactory monitorFactory;

    /**
     * The ComponentManager that manages all components in this runtime.
     */
    protected ComponentManager componentManager;
    protected ExtensionPointRegistry extensionRegistry;

    /**
     * Registry for ClassLoaders used by this runtime.
     */
    protected ClassLoaderRegistry classLoaderRegistry;

    protected Component systemComponent;
    protected Component tuscanySystem;

    protected ContributionService contributionService;

    protected ScopeRegistry scopeRegistry;
    protected Collection<ModuleActivator> activators;

    protected AbstractRuntime(Class<I> runtimeInfoType) {
        this(runtimeInfoType, new NullMonitorFactory());
    }

    protected AbstractRuntime(Class<I> runtimeInfoType, MonitorFactory monitorFactory) {
        this.runtimeInfoType = runtimeInfoType;
        this.monitorFactory = monitorFactory;
        xmlFactory = XMLInputFactory.newInstance("javax.xml.stream.XMLInputFactory", getClass().getClassLoader());
        classLoaderRegistry = new ClassLoaderRegistryImpl();
        classLoaderRegistry.register(BOOT_CLASSLOADER_ID, getClass().getClassLoader());
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

    public ClassLoader getHostClassLoader() {
        return classLoaderRegistry.getClassLoader(HOST_CLASSLOADER_ID);
    }

    public void setHostClassLoader(ClassLoader hostClassLoader) {
        classLoaderRegistry.register(HOST_CLASSLOADER_ID, hostClassLoader);
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

    @SuppressWarnings("unchecked")
    public void initialize(ExtensionPointRegistry extensionRegistry, ContributionService contributionService)
        throws InitializationException {
        this.contributionService = contributionService;
        this.extensionRegistry = extensionRegistry;

        Bootstrapper bootstrapper = createBootstrapper();

        Deployer deployer = bootstrapper.createDeployer(extensionRegistry);

        extensionRegistry.addExtensionPoint(ContributionService.class, contributionService);

        registerSystemComponent(TUSCANY_DEPLOYER, Deployer.class, deployer);
        registerSystemComponent(WORK_CONTEXT_URI, WorkContext.class, new SimpleWorkContext());

        this.scopeRegistry = bootstrapper.getScopeRegistry();

        activators = getInstances(getHostClassLoader(), ModuleActivator.class);
        for (ModuleActivator activator : activators) {
            Map<Class, Object> extensionPoints = activator.getExtensionPoints();
            if (extensionPoints != null) {
                for (Map.Entry<Class, Object> e : extensionPoints.entrySet()) {
                    extensionRegistry.addExtensionPoint(e.getKey(), e.getValue());
                }
            }
            activator.start(extensionRegistry);
        }

        registerBaselineSystemComponents();
    }

    public void destroy() {
        for (ModuleActivator activator : activators) {
            activator.stop(extensionRegistry);
        }
        
        if (tuscanySystem != null) {
            tuscanySystem.stop();
            tuscanySystem = null;
        }
        if (systemComponent != null) {
            systemComponent.stop();
            systemComponent = null;
        }
    }

    public ComponentContext getComponentContext(URI componentName) {
        Component component = componentManager.getComponent(URI.create(tuscanySystem.getUri() + "/" + componentName));
        if (component == null) {
            return null;
        }
        return component.getComponentContext();
    }

    protected Bootstrapper createBootstrapper() {
        TuscanyManagementService tms = (TuscanyManagementService)getManagementService();
        componentManager = new ComponentManagerImpl(tms);
        return new DefaultBootstrapper(getMonitorFactory(), xmlFactory, componentManager);
    }

    protected void registerBaselineSystemComponents() throws InitializationException {
        // register the RuntimeInfo provided by the host
        registerSystemComponent(RUNTIME_INFO_URI, runtimeInfoType, runtimeInfo);

        // register the MonitorFactory provided by the host
        List<Class<?>> monitorServices = new ArrayList<Class<?>>();
        monitorServices.add(MonitorFactory.class);
        monitorServices.add(FormatterRegistry.class);
        registerSystemComponent(MONITOR_URI, monitorServices, getMonitorFactory());

        // register the ClassLoaderRegistry
        registerSystemComponent(CLASSLOADER_REGISTRY_URI, ClassLoaderRegistry.class, classLoaderRegistry);

        // register the ComponentManager to that the fabric can wire to it
        registerSystemComponent(COMPONENT_MGR_URI, ComponentManager.class, componentManager);

        // register the ScopeRegistry
        registerSystemComponent(SCOPE_REGISTRY_URI, ScopeRegistry.class, scopeRegistry);
    }

    protected <S, I extends S> void registerSystemComponent(URI uri, Class<S> type, I component)
        throws InitializationException {
        try {
            ComponentService contract = createContract(type);
            componentManager.registerJavaObject(uri, contract, component);
            extensionRegistry.addExtensionPoint(type, component);
        } catch (RegistrationException e) {
            throw new InitializationException(e);
        }
    }

    private <S> ComponentService createContract(Class<S> type) {
        AssemblyFactory factory = new DefaultAssemblyFactory();
        ComponentService contract = factory.createComponentService();
        JavaInterface javaInterface = new DefaultJavaFactory().createJavaInterface();
        javaInterface.setJavaClass(type);
        contract.setInterface(javaInterface);
        return contract;
    }

    protected <I> void registerSystemComponent(URI uri, List<Class<?>> types, I component)
        throws InitializationException {
        try {
            List<ComponentService> contracts = new ArrayList<ComponentService>();
            for (Class<?> type : types) {
                contracts.add(createContract(type));
            }
            componentManager.registerJavaObject(uri, contracts, component);
        } catch (RegistrationException e) {
            throw new InitializationException(e);
        }
    }

    protected ComponentManager getComponentManager() {
        return componentManager;
    }

    protected ScopeRegistry getScopeRegistry() {
        return scopeRegistry;
    }

    protected WorkContext getWorkContext() {
        try {
            AtomicComponent component = (AtomicComponent)getComponentManager().getComponent(WORK_CONTEXT_URI);
            return (WorkContext)component.getTargetInstance();
        } catch (TargetResolutionException e) {
            throw new AssertionError(e);
        }
    }

    protected Deployer getDeployer() {
        try {
            AtomicComponent component = (AtomicComponent)getComponentManager().getComponent(TUSCANY_DEPLOYER);
            return (Deployer)component.getTargetInstance();
        } catch (TargetResolutionException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Read the service name from a configuration file
     * 
     * @param classLoader
     * @param name The name of the service class
     * @return A class name which extends/implements the service class
     * @throws IOException
     */
    private static Set<String> getServiceNames(ClassLoader classLoader, String name) throws IOException {
        Set<String> set = new HashSet<String>();
        Enumeration<URL> urls = classLoader.getResources("META-INF/services/" + name);
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            Set<String> service = getServiceNames(url);
            if (service != null) {
                set.addAll(service);

            }
        }
        return set;
    }

    private static Set<String> getServiceNames(URL url) throws IOException {
        Set<String> names = new HashSet<String>();
        InputStream is = IOHelper.getInputStream(url);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is));
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                line = line.trim();
                if (!line.startsWith("#") && !"".equals(line)) {
                    names.add(line.trim());
                }
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return names;
    }

    private static <T> Collection<T> getInstances(final ClassLoader classLoader, Class<T> serviceClass) {
        List<T> instances = new ArrayList<T>();
        try {
            Set<String> services = getServiceNames(classLoader, serviceClass.getName());
            for (String className : services) {
                Class cls = Class.forName(className, true, classLoader);
                instances.add(serviceClass.cast(cls.newInstance())); // NOPMD
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return instances;
    }

    /**
     * @return the contributionService
     */
    public ContributionService getContributionService() {
        return contributionService;
    }
}
