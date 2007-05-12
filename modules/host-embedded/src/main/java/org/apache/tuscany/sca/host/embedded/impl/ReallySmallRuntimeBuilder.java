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

package org.apache.tuscany.sca.host.embedded.impl;

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

import org.apache.tuscany.contribution.ContributionFactory;
import org.apache.tuscany.contribution.impl.ContributionFactoryImpl;
import org.apache.tuscany.contribution.processor.DefaultPackageProcessorExtensionPoint;
import org.apache.tuscany.contribution.processor.DefaultStAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.contribution.processor.DefaultURLArtifactProcessorExtensionPoint;
import org.apache.tuscany.contribution.processor.ExtensiblePackageProcessor;
import org.apache.tuscany.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.contribution.processor.ExtensibleURLArtifactProcessor;
import org.apache.tuscany.contribution.processor.PackageProcessor;
import org.apache.tuscany.contribution.processor.PackageProcessorExtensionPoint;
import org.apache.tuscany.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.contribution.processor.impl.FolderContributionProcessor;
import org.apache.tuscany.contribution.processor.impl.JarContributionProcessor;
import org.apache.tuscany.contribution.service.ContributionRepository;
import org.apache.tuscany.contribution.service.ContributionService;
import org.apache.tuscany.contribution.service.impl.ContributionRepositoryImpl;
import org.apache.tuscany.contribution.service.impl.ContributionServiceImpl;
import org.apache.tuscany.contribution.service.impl.PackageTypeDescriberImpl;
import org.apache.tuscany.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.policy.PolicyFactory;
import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.xml.ComponentTypeDocumentProcessor;
import org.apache.tuscany.sca.assembly.xml.ComponentTypeProcessor;
import org.apache.tuscany.sca.assembly.xml.CompositeDocumentProcessor;
import org.apache.tuscany.sca.assembly.xml.CompositeProcessor;
import org.apache.tuscany.sca.assembly.xml.ConstrainingTypeDocumentProcessor;
import org.apache.tuscany.sca.assembly.xml.ConstrainingTypeProcessor;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.RuntimeWireProcessor;
import org.apache.tuscany.sca.core.RuntimeWireProcessorExtensionPoint;
import org.apache.tuscany.sca.core.invocation.DefaultWireProcessorExtensionPoint;
import org.apache.tuscany.sca.core.invocation.ExtensibleWireProcessor;
import org.apache.tuscany.sca.core.invocation.JDKProxyService;
import org.apache.tuscany.sca.core.invocation.MessageFactoryImpl;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.core.runtime.ActivationException;
import org.apache.tuscany.sca.core.runtime.CompositeActivator;
import org.apache.tuscany.sca.core.runtime.DefaultCompositeActivator;
import org.apache.tuscany.sca.core.runtime.RuntimeSCABindingProviderFactory;
import org.apache.tuscany.sca.core.scope.CompositeScopeContainerFactory;
import org.apache.tuscany.sca.core.scope.RequestScopeContainerFactory;
import org.apache.tuscany.sca.core.scope.ScopeRegistryImpl;
import org.apache.tuscany.sca.core.scope.StatelessScopeContainerFactory;
import org.apache.tuscany.sca.core.util.IOHelper;
import org.apache.tuscany.sca.core.work.Jsr237WorkScheduler;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.provider.DefaultProviderFactoryExtensionPoint;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;
import org.apache.tuscany.sca.scope.ScopeContainerFactory;
import org.apache.tuscany.sca.scope.ScopeRegistry;
import org.apache.tuscany.sca.spi.component.WorkContext;
import org.apache.tuscany.sca.spi.component.WorkContextImpl;
import org.apache.tuscany.sca.spi.component.WorkContextTunnel;
import org.apache.tuscany.sca.work.WorkScheduler;

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
                                                  WorkContext workContext,
                                                  InterfaceContractMapper mapper) {

        // Create a proxy factory
        MessageFactory messageFactory = new MessageFactoryImpl();
        ProxyFactory proxyFactory = new JDKProxyService(messageFactory, workContext, mapper);

        // FIXME remove this
        registry.addExtensionPoint(ProxyFactory.class, proxyFactory);
        registry.addExtensionPoint(InterfaceContractMapper.class, mapper);

        return proxyFactory;
    }

    public static CompositeActivator createCompositeActivator(ExtensionPointRegistry registry,
                                                              AssemblyFactory assemblyFactory,
                                                              InterfaceContractMapper mapper,
                                                              ScopeRegistry scopeRegistry,
                                                              WorkContext workContext,
                                                              WorkManager workManager) {

        // Create a work scheduler
        WorkScheduler workScheduler = new Jsr237WorkScheduler(workManager);
        registry.addExtensionPoint(WorkScheduler.class, workScheduler);

        // Create a wire post processor extension point
        RuntimeWireProcessorExtensionPoint wireProcessors = new DefaultWireProcessorExtensionPoint();
        registry.addExtensionPoint(RuntimeWireProcessorExtensionPoint.class, wireProcessors);
        RuntimeWireProcessor wireProcessor = new ExtensibleWireProcessor(wireProcessors);
        
        // Create a provider factory extension point
        ProviderFactoryExtensionPoint providerFactories = new DefaultProviderFactoryExtensionPoint();
        registry.addExtensionPoint(ProviderFactoryExtensionPoint.class, providerFactories);
        providerFactories.addProviderFactory(new RuntimeSCABindingProviderFactory());

        // Create the composite activator
        CompositeActivator compositeActivator = new DefaultCompositeActivator(assemblyFactory, mapper, scopeRegistry,
                                                                              workContext, workScheduler, wireProcessor, providerFactories);

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
                                                                InterfaceContractMapper mapper)
        throws ActivationException {

        XMLInputFactory xmlFactory = XMLInputFactory.newInstance();

        // Create STAX artifact processor extension point
        DefaultStAXArtifactProcessorExtensionPoint staxProcessors = new DefaultStAXArtifactProcessorExtensionPoint();
        registry.addExtensionPoint(StAXArtifactProcessorExtensionPoint.class, staxProcessors);

        // Create and register STAX processors for SCA assembly XML
        ExtensibleStAXArtifactProcessor staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, xmlFactory,
                                                                                            XMLOutputFactory
                                                                                                .newInstance());
        staxProcessors.addArtifactProcessor(new CompositeProcessor(assemblyFactory, policyFactory, mapper,
                                                                   staxProcessor));
        staxProcessors.addArtifactProcessor(new ComponentTypeProcessor(assemblyFactory, policyFactory, staxProcessor));
        staxProcessors
            .addArtifactProcessor(new ConstrainingTypeProcessor(assemblyFactory, policyFactory, staxProcessor));

        // Create URL artifact processor extension point
        // FIXME use the interface instead of the class
        DefaultURLArtifactProcessorExtensionPoint documentProcessors = new DefaultURLArtifactProcessorExtensionPoint();
        registry.addExtensionPoint(URLArtifactProcessorExtensionPoint.class, documentProcessors);

        // Create and register document processors for SCA assembly XML
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        documentProcessors.addArtifactProcessor(new CompositeDocumentProcessor(staxProcessor, inputFactory));
        documentProcessors.addArtifactProcessor(new ComponentTypeDocumentProcessor(staxProcessor, inputFactory));
        documentProcessors.addArtifactProcessor(new ConstrainingTypeDocumentProcessor(staxProcessor, inputFactory));

        // Create contribution package processor extension point
        PackageTypeDescriberImpl describer = new PackageTypeDescriberImpl();
        PackageProcessorExtensionPoint packageProcessors = new DefaultPackageProcessorExtensionPoint();
        PackageProcessor packageProcessor = new ExtensiblePackageProcessor(packageProcessors, describer);
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

        ContributionFactory contributionFactory = new ContributionFactoryImpl();
        ExtensibleURLArtifactProcessor documentProcessor = new ExtensibleURLArtifactProcessor(documentProcessors);
        ContributionService contributionService = new ContributionServiceImpl(repository, packageProcessor,
                                                                              documentProcessor, assemblyFactory,
                                                                              contributionFactory, xmlFactory);
        return contributionService;
    }

    public static ScopeRegistry createScopeRegistry(ExtensionPointRegistry registry) {
        ScopeRegistry scopeRegistry = new ScopeRegistryImpl();
        ScopeContainerFactory[] factories = new ScopeContainerFactory[] {new CompositeScopeContainerFactory(),
                                                                         new StatelessScopeContainerFactory(),
                                                                         new RequestScopeContainerFactory(),
        // new ConversationalScopeContainer(monitor),
        // new HttpSessionScopeContainer(monitor)
        };
        for (ScopeContainerFactory f : factories) {
            scopeRegistry.register(f);
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
