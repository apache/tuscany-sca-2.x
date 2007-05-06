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

package org.apache.tuscany.host.embedded.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.xml.ComponentTypeDocumentProcessor;
import org.apache.tuscany.assembly.xml.ComponentTypeProcessor;
import org.apache.tuscany.assembly.xml.CompositeDocumentProcessor;
import org.apache.tuscany.assembly.xml.CompositeProcessor;
import org.apache.tuscany.assembly.xml.ConstrainingTypeDocumentProcessor;
import org.apache.tuscany.assembly.xml.ConstrainingTypeProcessor;
import org.apache.tuscany.contribution.ContributionFactory;
import org.apache.tuscany.contribution.impl.DefaultContributionFactory;
import org.apache.tuscany.contribution.processor.DefaultStAXArtifactProcessor;
import org.apache.tuscany.contribution.processor.DefaultStAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.contribution.processor.DefaultURLArtifactProcessor;
import org.apache.tuscany.contribution.processor.DefaultURLArtifactProcessorExtensionPoint;
import org.apache.tuscany.contribution.processor.PackageProcessor;
import org.apache.tuscany.contribution.processor.PackageProcessorExtensionPoint;
import org.apache.tuscany.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.contribution.processor.impl.DefaultPackageProcessor;
import org.apache.tuscany.contribution.processor.impl.DefaultPackageProcessorExtensionPoint;
import org.apache.tuscany.contribution.processor.impl.FolderContributionProcessor;
import org.apache.tuscany.contribution.processor.impl.JarContributionProcessor;
import org.apache.tuscany.contribution.resolver.DefaultArtifactResolver;
import org.apache.tuscany.contribution.service.ContributionRepository;
import org.apache.tuscany.contribution.service.ContributionService;
import org.apache.tuscany.contribution.service.impl.ContributionRepositoryImpl;
import org.apache.tuscany.contribution.service.impl.ContributionServiceImpl;
import org.apache.tuscany.contribution.service.impl.DefaultPackageTypeDescriber;
import org.apache.tuscany.core.ExtensionPointRegistry;
import org.apache.tuscany.core.WireProcessorExtensionPoint;
import org.apache.tuscany.core.invocation.DefaultWireProcessorExtensionPoint;
import org.apache.tuscany.core.invocation.JDKProxyService;
import org.apache.tuscany.core.runtime.ActivationException;
import org.apache.tuscany.core.runtime.CompositeActivator;
import org.apache.tuscany.core.runtime.DefaultCompositeActivator;
import org.apache.tuscany.core.scope.AbstractScopeContainer;
import org.apache.tuscany.core.scope.CompositeScopeContainer;
import org.apache.tuscany.core.scope.RequestScopeContainer;
import org.apache.tuscany.core.scope.ScopeRegistryImpl;
import org.apache.tuscany.core.scope.StatelessScopeContainer;
import org.apache.tuscany.core.util.IOHelper;
import org.apache.tuscany.core.work.Jsr237WorkScheduler;
import org.apache.tuscany.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.invocation.ProxyFactory;
import org.apache.tuscany.policy.PolicyFactory;
import org.apache.tuscany.scope.ScopeRegistry;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.component.WorkContextImpl;
import org.apache.tuscany.spi.component.WorkContextTunnel;
import org.apache.tuscany.spi.services.work.WorkScheduler;

import commonj.work.WorkManager;

public class ReallySmallRuntimeBuilder {
    
    public static WorkContext createWorkContext(ExtensionPointRegistry registry) {
        
        // Create a work context
        WorkContext workContext = new WorkContextImpl();
        registry.addExtensionPoint(WorkContext.class, workContext);
        WorkContextTunnel.setThreadWorkContext(workContext);
        return workContext;
    }
    
    public static ProxyFactory createProxyFactory(ExtensionPointRegistry registry,
                                            WorkContext workContext, InterfaceContractMapper mapper) {

        // Create a proxy factory
        ProxyFactory proxyFactory = new JDKProxyService(workContext, mapper);

        //FIXME remove this
        registry.addExtensionPoint(ProxyFactory.class, proxyFactory);

        return proxyFactory;
    }
    
    public static CompositeActivator createCompositeActivator(ExtensionPointRegistry registry,
                                                        AssemblyFactory assemblyFactory,
                                                        InterfaceContractMapper mapper,
                                                        WorkContext workContext,
                                                        WorkManager workManager) {

        // Create a work scheduler
        WorkScheduler workScheduler = new Jsr237WorkScheduler(workManager);
        
        // Create a wire post processor extension point
        //FIXME do we still need this?
        WireProcessorExtensionPoint wireProcessorExtensionPoint = new DefaultWireProcessorExtensionPoint();
        registry.addExtensionPoint(WireProcessorExtensionPoint.class, wireProcessorExtensionPoint);
        
        // Create the composite activator
        CompositeActivator compositeActivator = new DefaultCompositeActivator(assemblyFactory, mapper, workContext,
                                                           workScheduler, wireProcessorExtensionPoint);

        return compositeActivator;
    }
    
    /**
     * Create the contribution service used by this domain.
     * 
     * @throws ActivationException
     */
    public static ContributionService createContributionService(ExtensionPointRegistry registry,
                                                          AssemblyFactory assemblyFactory,
                                                          PolicyFactory policyFactory,
                                                          InterfaceContractMapper mapper,
                                                          ClassLoader classLoader) throws ActivationException {
        
        XMLInputFactory xmlFactory = XMLInputFactory.newInstance();

        // Create STAX artifact processor extension point
        DefaultStAXArtifactProcessorExtensionPoint staxProcessors = new DefaultStAXArtifactProcessorExtensionPoint();
        registry.addExtensionPoint(StAXArtifactProcessorExtensionPoint.class, staxProcessors);

        // Create and register STAX processors for SCA assembly XML
        DefaultStAXArtifactProcessor staxProcessor = new DefaultStAXArtifactProcessor(staxProcessors, xmlFactory, XMLOutputFactory.newInstance());
        staxProcessors.addArtifactProcessor(new CompositeProcessor(assemblyFactory, policyFactory, mapper, staxProcessor));
        staxProcessors.addArtifactProcessor(new ComponentTypeProcessor(assemblyFactory, policyFactory, staxProcessor));
        staxProcessors.addArtifactProcessor(new ConstrainingTypeProcessor(assemblyFactory, policyFactory, staxProcessor));

        // Create URL artifact processor extension point
        //FIXME use the interface instead of the class
        DefaultURLArtifactProcessorExtensionPoint documentProcessors = new DefaultURLArtifactProcessorExtensionPoint();
        registry.addExtensionPoint(URLArtifactProcessorExtensionPoint.class, documentProcessors);
        
        // Create and register document processors for SCA assembly XML
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        documentProcessors.addArtifactProcessor(new CompositeDocumentProcessor(staxProcessor, inputFactory));
        documentProcessors.addArtifactProcessor(new ComponentTypeDocumentProcessor(staxProcessor, inputFactory));
        documentProcessors.addArtifactProcessor(new ConstrainingTypeDocumentProcessor(staxProcessor, inputFactory));

        // Create contribution package processor extension point
        DefaultPackageTypeDescriber describer = new DefaultPackageTypeDescriber();
        PackageProcessorExtensionPoint packageProcessors = new DefaultPackageProcessorExtensionPoint();
        PackageProcessor packageProcessor = new DefaultPackageProcessor(packageProcessors ,describer);
        registry.addExtensionPoint(PackageProcessorExtensionPoint.class, packageProcessors);

        // Register base package processors
        packageProcessors.addPackageProcessor(new JarContributionProcessor());
        packageProcessors.addPackageProcessor(new FolderContributionProcessor());

        // Create a contribution repository
        ContributionRepository repository;
        try {
            repository = new ContributionRepositoryImpl("target");
        } catch (IOException e) {
            throw new ActivationException(e);
        }

        //FIXME move artifact resolver to each contribution
        DefaultArtifactResolver artifactResolver = new DefaultArtifactResolver(classLoader);
        ContributionFactory contributionFactory = new DefaultContributionFactory();
        DefaultURLArtifactProcessor documentProcessor = new DefaultURLArtifactProcessor(documentProcessors);
        ContributionService contributionService = new ContributionServiceImpl(
                                                                              repository, packageProcessor,
                                                                              documentProcessor, artifactResolver,
                                                                              assemblyFactory,
                                                                              contributionFactory,
                                                                              xmlFactory);
        return contributionService;
    }

    
    public static ScopeRegistry createScopeRegistry(ExtensionPointRegistry registry) {
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

        registry.addExtensionPoint(ScopeRegistry.class, scopeRegistry);

        return scopeRegistry;
    }

    /**
     * Read the service name from a configuration file
     * 
     * @param classLoader
     * @param name The name of the service class
     * @return A class name which extends/implements the service class
     * @throws IOException
     */
    private static Set<String> getServiceClassNames(ClassLoader classLoader, String name) throws IOException {
        Set<String> set = new HashSet<String>();
        Enumeration<URL> urls = classLoader.getResources("META-INF/services/" + name);
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            Set<String> service = getServiceClassNames(url);
            if (service != null) {
                set.addAll(service);

            }
        }
        return set;
    }

    private static Set<String> getServiceClassNames(URL url) throws IOException {
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

    public static <T> List<T> getServices(final ClassLoader classLoader, Class<T> serviceClass) {
        List<T> instances = new ArrayList<T>();
        try {
            Set<String> services = getServiceClassNames(classLoader, serviceClass.getName());
            for (String className : services) {
                Class cls = Class.forName(className, true, classLoader);
                instances.add(serviceClass.cast(cls.newInstance()));
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return instances;
    }

}
