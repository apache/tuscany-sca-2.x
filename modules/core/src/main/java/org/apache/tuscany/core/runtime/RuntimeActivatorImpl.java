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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.Composite;
import org.apache.tuscany.contribution.Contribution;
import org.apache.tuscany.core.ExtensionPointRegistry;
import org.apache.tuscany.core.ModuleActivator;
import org.apache.tuscany.core.builder.WirePostProcessorRegistryImpl;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.invocation.JDKProxyService;
import org.apache.tuscany.core.scope.AbstractScopeContainer;
import org.apache.tuscany.core.scope.CompositeScopeContainer;
import org.apache.tuscany.core.scope.RequestScopeContainer;
import org.apache.tuscany.core.scope.ScopeRegistryImpl;
import org.apache.tuscany.core.scope.StatelessScopeContainer;
import org.apache.tuscany.core.util.IOHelper;
import org.apache.tuscany.core.work.Jsr237WorkScheduler;
import org.apache.tuscany.core.work.ThreadPoolWorkManager;
import org.apache.tuscany.host.RuntimeInfo;
import org.apache.tuscany.interfacedef.IncompatibleInterfaceContractException;
import org.apache.tuscany.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.interfacedef.impl.DefaultInterfaceContractMapper;
import org.apache.tuscany.invocation.ProxyFactory;
import org.apache.tuscany.policy.PolicyFactory;
import org.apache.tuscany.policy.impl.DefaultPolicyFactory;
import org.apache.tuscany.scope.ScopeRegistry;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.component.WorkContextTunnel;
import org.apache.tuscany.spi.services.work.WorkScheduler;
import org.apache.tuscany.spi.wire.WirePostProcessorRegistry;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.Constants;

/**
 * @version $Rev$ $Date$
 */
public abstract class RuntimeActivatorImpl<I extends RuntimeInfo> implements RuntimeActivator<I> {

    protected final XMLInputFactory xmlFactory;
    protected Class<I> runtimeInfoType;
    protected ClassLoader hostClassLoader;

    /**
     * Information provided by the host about its runtime environment.
     */
    protected I runtimeInfo;

    protected DefaultCompositeActivator compositeActivator;
    protected ScopeRegistry scopeRegistry;
    protected Collection<ModuleActivator> activators;

    protected ThreadPoolWorkManager workManager;

    protected ExtensionPointRegistry extensionPointRegistry;
    protected Composite domain;
    protected AssemblyFactory assemblyFactory;
    protected PolicyFactory policyFactory;
    protected InterfaceContractMapper interfaceContractMapper;

    protected RuntimeActivatorImpl(Class<I> runtimeInfoType,
                                   I runtimeInfo,
                                   ClassLoader hostClassLoader,
                                   ExtensionPointRegistry extensionPointRegistry) {
        this.runtimeInfoType = runtimeInfoType;
        this.runtimeInfo = runtimeInfo;
        this.hostClassLoader = hostClassLoader;
        xmlFactory = XMLInputFactory.newInstance("javax.xml.stream.XMLInputFactory", getClass().getClassLoader());
        this.extensionPointRegistry = extensionPointRegistry;
    }

    public void init() throws ActivationException {
        // Create default factories
        assemblyFactory = new RuntimeAssemblyFactory();
        policyFactory = new DefaultPolicyFactory();
        interfaceContractMapper = new DefaultInterfaceContractMapper();
        scopeRegistry = createScopeRegistry();

        extensionPointRegistry.addExtensionPoint(AssemblyFactory.class, assemblyFactory);
        extensionPointRegistry.addExtensionPoint(PolicyFactory.class, policyFactory);
        extensionPointRegistry.addExtensionPoint(InterfaceContractMapper.class, interfaceContractMapper);
        extensionPointRegistry.addExtensionPoint(ScopeRegistry.class, scopeRegistry);

        // Create a work context
        // WorkContext workContext = new SimpleWorkContext();
        WorkContext workContext = new WorkContextImpl();
        extensionPointRegistry.addExtensionPoint(WorkContext.class, workContext);

        WorkContextTunnel.setThreadWorkContext(workContext);
        
        extensionPointRegistry.addExtensionPoint(ProxyFactory.class, new JDKProxyService(workContext, interfaceContractMapper));

        workManager = new ThreadPoolWorkManager(10);
        WorkScheduler workScheduler = new Jsr237WorkScheduler(workManager);

        WirePostProcessorRegistry wirePostProcessorRegistry = new WirePostProcessorRegistryImpl();
        extensionPointRegistry.addExtensionPoint(WirePostProcessorRegistry.class, wirePostProcessorRegistry);

        // Create the composite activator
        compositeActivator = new DefaultCompositeActivator(assemblyFactory, interfaceContractMapper, workContext,
                                                           workScheduler, wirePostProcessorRegistry);

        // Create the default SCA domain
        domain = assemblyFactory.createComposite();
        domain.setName(new QName(Constants.SCA_NS, "sca.domain"));
        domain.setURI("sca://local/");
    }
    
    public <B> B locateService(Class<B> businessInterface, String componentName, String serviceName) {
        return getComponentContext(componentName).createSelfReference(businessInterface, serviceName).getService();
    }

    public <B> B locateService(Class<B> businessInterface, String componentName) {
        return getComponentContext(componentName).createSelfReference(businessInterface).getService();
    }
    
    public void start() throws ActivationException {
        activators = getInstances(hostClassLoader, ModuleActivator.class);
        for (ModuleActivator activator : activators) {
            Map<Class, Object> extensionPoints = activator.getExtensionPoints();
            if (extensionPoints != null) {
                for (Map.Entry<Class, Object> e : extensionPoints.entrySet()) {
                    extensionPointRegistry.addExtensionPoint(e.getKey(), e.getValue());
                }
            }
        }

        // Start all the extension modules
        for (ModuleActivator activator : activators) {
            activator.start(extensionPointRegistry);
        }

    }

    public void stop() throws ActivationException {
        compositeActivator.stop(domain);

        workManager.destroy();

        for (ModuleActivator activator : activators) {
            activator.stop(extensionPointRegistry);
        }
    }

    /**
     * Create a basic ScopeRegistry containing the ScopeContainers that are
     * available to components in the system definition. The implementation
     * returned only support COMPOSITE scope.
     * 
     * @return a new ScopeRegistry
     */
    private ScopeRegistry createScopeRegistry() {
        ScopeRegistry scopeRegistry = new ScopeRegistryImpl();
        AbstractScopeContainer[] containers = new AbstractScopeContainer[] {new CompositeScopeContainer(),
                                                                            new StatelessScopeContainer(),
                                                                            new RequestScopeContainer(),
        // new ConversationalScopeContainer(monitor),
        // new HttpSessionScopeContainer(monitor)
        };
        for (AbstractScopeContainer c : containers) {
            c.start();
            scopeRegistry.register(c);
        }

        return scopeRegistry;
    }

    protected ScopeRegistry getScopeRegistry() {
        return scopeRegistry;
    }

    protected WorkContext getWorkContext() {
        return extensionPointRegistry.getExtensionPoint(WorkContext.class);
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

    public ComponentContext getComponentContext(String componentName) {
        for (org.apache.tuscany.assembly.Component component : domain.getComponents()) {
            if (component.getName().equals(componentName)) {
                return (ComponentContext)component;
            }
        }
        return null;
    }

    public void start(Contribution contribution) throws ActivationException {
        // Add the deployable composites to the SCA domain by "include"
        for (Composite composite : contribution.getDeployables()) {
            domain.getIncludes().add(composite);
        }

        // Activate the SCA domain composite
        try {
            compositeActivator.activate(domain);
        } catch (IncompatibleInterfaceContractException e) {
            throw new ActivationException(e);

        }
    }

    public void stop(Contribution contribution) throws ActivationException {
    }

}
