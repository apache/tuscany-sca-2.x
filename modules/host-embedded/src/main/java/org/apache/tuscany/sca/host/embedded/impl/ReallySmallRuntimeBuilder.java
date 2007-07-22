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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.xml.ComponentTypeDocumentProcessor;
import org.apache.tuscany.sca.assembly.xml.ComponentTypeProcessor;
import org.apache.tuscany.sca.assembly.xml.CompositeDocumentProcessor;
import org.apache.tuscany.sca.assembly.xml.CompositeModelResolver;
import org.apache.tuscany.sca.assembly.xml.CompositeProcessor;
import org.apache.tuscany.sca.assembly.xml.ConstrainingTypeDocumentProcessor;
import org.apache.tuscany.sca.assembly.xml.ConstrainingTypeModelResolver;
import org.apache.tuscany.sca.assembly.xml.ConstrainingTypeProcessor;
import org.apache.tuscany.sca.assembly.xml.SCABindingProcessor;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.processor.ContributionPostProcessor;
import org.apache.tuscany.sca.contribution.processor.DefaultContributionPostProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.DefaultPackageProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.DefaultStAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.DefaultURLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ExtensibleContributionPostProcessor;
import org.apache.tuscany.sca.contribution.processor.ExtensiblePackageProcessor;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ExtensibleURLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.PackageProcessor;
import org.apache.tuscany.sca.contribution.processor.PackageProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.impl.FolderContributionProcessor;
import org.apache.tuscany.sca.contribution.processor.impl.JarContributionProcessor;
import org.apache.tuscany.sca.contribution.resolver.DefaultModelResolverExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ModelResolverExtensionPoint;
import org.apache.tuscany.sca.contribution.service.ContributionRepository;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.contribution.service.TypeDescriber;
import org.apache.tuscany.sca.contribution.service.impl.ContributionRepositoryImpl;
import org.apache.tuscany.sca.contribution.service.impl.ContributionServiceImpl;
import org.apache.tuscany.sca.contribution.service.impl.PackageTypeDescriberImpl;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.invocation.ExtensibleWireProcessor;
import org.apache.tuscany.sca.core.invocation.JDKProxyService;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.core.runtime.ActivationException;
import org.apache.tuscany.sca.core.runtime.CompositeActivator;
import org.apache.tuscany.sca.core.runtime.CompositeActivatorImpl;
import org.apache.tuscany.sca.core.runtime.RuntimeSCABindingProviderFactory;
import org.apache.tuscany.sca.core.scope.CompositeScopeContainerFactory;
import org.apache.tuscany.sca.core.scope.ConversationalScopeContainerFactory;
import org.apache.tuscany.sca.core.scope.RequestScopeContainerFactory;
import org.apache.tuscany.sca.core.scope.ScopeRegistryImpl;
import org.apache.tuscany.sca.core.scope.StatelessScopeContainerFactory;
import org.apache.tuscany.sca.core.work.Jsr237WorkScheduler;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.provider.DefaultProviderFactoryExtensionPoint;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;
import org.apache.tuscany.sca.runtime.DefaultWireProcessorExtensionPoint;
import org.apache.tuscany.sca.runtime.RuntimeWireProcessor;
import org.apache.tuscany.sca.runtime.RuntimeWireProcessorExtensionPoint;
import org.apache.tuscany.sca.scope.ScopeContainerFactory;
import org.apache.tuscany.sca.scope.ScopeRegistry;
import org.apache.tuscany.sca.work.WorkScheduler;

import commonj.work.WorkManager;

public class ReallySmallRuntimeBuilder {

    public static ProxyFactory createProxyFactory(ExtensionPointRegistry registry, InterfaceContractMapper mapper, MessageFactory messageFactory) {

        // Create a proxy factory
        ProxyFactory proxyFactory = new JDKProxyService(messageFactory, mapper);

        // FIXME remove this
        registry.addExtensionPoint(proxyFactory);
        registry.addExtensionPoint(mapper);

        return proxyFactory;
    }

    public static CompositeActivator createCompositeActivator(ExtensionPointRegistry registry,
                                                              AssemblyFactory assemblyFactory,
                                                              SCABindingFactory scaBindingFactory,
                                                              InterfaceContractMapper mapper,
                                                              ScopeRegistry scopeRegistry,
                                                              WorkManager workManager) {

        // Create a work scheduler
        WorkScheduler workScheduler = new Jsr237WorkScheduler(workManager);
        registry.addExtensionPoint(workScheduler);

        // Create a wire post processor extension point
        RuntimeWireProcessorExtensionPoint wireProcessors = new DefaultWireProcessorExtensionPoint();
        registry.addExtensionPoint(wireProcessors);
        RuntimeWireProcessor wireProcessor = new ExtensibleWireProcessor(wireProcessors);

        // Add the SCABindingProcessor extension
        PolicyFactory policyFactory = registry.getExtensionPoint(PolicyFactory.class);
        SCABindingProcessor scaBindingProcessor = new SCABindingProcessor(assemblyFactory,
                                                                          policyFactory,
                                                                          scaBindingFactory);
        StAXArtifactProcessorExtensionPoint processors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        processors.addArtifactProcessor(scaBindingProcessor);       

        // Create a provider factory extension point
        ProviderFactoryExtensionPoint providerFactories = new DefaultProviderFactoryExtensionPoint();
        registry.addExtensionPoint(providerFactories);
        providerFactories.addProviderFactory(new RuntimeSCABindingProviderFactory());

        // Create the composite activator
        CompositeActivator compositeActivator = new CompositeActivatorImpl(
                                                                           assemblyFactory, scaBindingFactory,
                                                                           mapper, scopeRegistry,
                                                                           workScheduler, wireProcessor,
                                                                           providerFactories);

        return compositeActivator;
    }

    /**
     * Create the contribution service used by this domain.
     * 
     * @throws ActivationException
     */
    public static ContributionService createContributionService(ClassLoader classLoader, 
                                                                ExtensionPointRegistry registry,
                                                                ContributionFactory contributionFactory,
                                                                AssemblyFactory assemblyFactory,
                                                                PolicyFactory policyFactory,
                                                                InterfaceContractMapper mapper)
        throws ActivationException {

        XMLInputFactory xmlFactory = XMLInputFactory.newInstance();

        // Create STAX artifact processor extension point
        DefaultStAXArtifactProcessorExtensionPoint staxProcessors = new DefaultStAXArtifactProcessorExtensionPoint();
        registry.addExtensionPoint(staxProcessors);

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
        URLArtifactProcessorExtensionPoint documentProcessors = new DefaultURLArtifactProcessorExtensionPoint();
        registry.addExtensionPoint(documentProcessors);

        // Create and register document processors for SCA assembly XML
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        documentProcessors.addArtifactProcessor(new CompositeDocumentProcessor(staxProcessor, inputFactory));
        documentProcessors.addArtifactProcessor(new ComponentTypeDocumentProcessor(staxProcessor, inputFactory));
        documentProcessors.addArtifactProcessor(new ConstrainingTypeDocumentProcessor(staxProcessor, inputFactory));

        // Create contribution package processor extension point
        TypeDescriber describer = new PackageTypeDescriberImpl();
        PackageProcessorExtensionPoint packageProcessors = new DefaultPackageProcessorExtensionPoint();
        registry.addExtensionPoint(packageProcessors);

        // Register base package processors
        packageProcessors.addPackageProcessor(new JarContributionProcessor());
        packageProcessors.addPackageProcessor(new FolderContributionProcessor());

        PackageProcessor packageProcessor = new ExtensiblePackageProcessor(packageProcessors, describer);

        //Create Contribution Model Resolver extension point
        ModelResolverExtensionPoint modelResolverExtensionPoint = new DefaultModelResolverExtensionPoint();
        registry.addExtensionPoint(modelResolverExtensionPoint);
        
        modelResolverExtensionPoint.addResolver(Composite.class, CompositeModelResolver.class);
        modelResolverExtensionPoint.addResolver(Composite.class, ConstrainingTypeModelResolver.class);
        
        //Create contribution postProcessor extension point
        DefaultContributionPostProcessorExtensionPoint contributionPostProcessors = new DefaultContributionPostProcessorExtensionPoint();
        ContributionPostProcessor postProcessor = new ExtensibleContributionPostProcessor(contributionPostProcessors);
        registry.addExtensionPoint(contributionPostProcessors);

        // Create a contribution repository
        ContributionRepository repository;
        try {
            repository = new ContributionRepositoryImpl("target");
        } catch (IOException e) {
            throw new ActivationException(e);
        }

        ExtensibleURLArtifactProcessor documentProcessor = new ExtensibleURLArtifactProcessor(documentProcessors);
        ContributionService contributionService = new ContributionServiceImpl(repository, packageProcessor,
                                                                              documentProcessor, postProcessor, 
                                                                              modelResolverExtensionPoint, assemblyFactory,
                                                                              contributionFactory, xmlFactory);
        return contributionService;
    }

    public static ScopeRegistry createScopeRegistry(ExtensionPointRegistry registry) {
        ScopeRegistry scopeRegistry = new ScopeRegistryImpl();
        ScopeContainerFactory[] factories = new ScopeContainerFactory[] {new CompositeScopeContainerFactory(),
                                                                         new StatelessScopeContainerFactory(),
                                                                         new RequestScopeContainerFactory(),
         new ConversationalScopeContainerFactory(null),
        // new HttpSessionScopeContainer(monitor)
        };
        for (ScopeContainerFactory f : factories) {
            scopeRegistry.register(f);
        }

        registry.addExtensionPoint(scopeRegistry);

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
    public static Set<String> getServiceClassNames(ClassLoader classLoader, String name) throws IOException {
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
        InputStream is = url.openStream();
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
            
            if (is != null){
                try {
                    is.close();
                } catch( IOException ioe) {
                    //ignore
                }
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

    /**
     * Parse a service declaration in the form class;attr=value,attr=value and
     * return a map of attributes
     * @param declaration
     * @return a map of attributes
     */
    public static Map<String, String> parseServiceDeclaration(String declaration) {
        Map<String, String> attributes = new HashMap<String, String>(); 
        StringTokenizer tokens = new StringTokenizer(declaration);
        String className = tokens.nextToken(";");
        if (className != null) {
            attributes.put("class", className);
        }
        for (;;) {
            if (!tokens.hasMoreTokens())
                break;
            String key = tokens.nextToken("=").substring(1);
            if (key == null)
                break;
            String value = tokens.nextToken(",").substring(1);
            if (value == null)
                break;
            attributes.put(key, value);
        }
        return attributes;
    }

}
