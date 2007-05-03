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

import static org.apache.tuscany.host.embedded.impl.SimpleRuntimeInfo.DEFAULT_COMPOSITE;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.stream.XMLInputFactory;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.Base;
import org.apache.tuscany.assembly.Composite;
import org.apache.tuscany.assembly.impl.DefaultAssemblyFactory;
import org.apache.tuscany.assembly.util.CompositeUtil;
import org.apache.tuscany.assembly.xml.ComponentTypeDocumentProcessor;
import org.apache.tuscany.assembly.xml.ComponentTypeProcessor;
import org.apache.tuscany.assembly.xml.CompositeDocumentProcessor;
import org.apache.tuscany.assembly.xml.CompositeProcessor;
import org.apache.tuscany.assembly.xml.ConstrainingTypeDocumentProcessor;
import org.apache.tuscany.assembly.xml.ConstrainingTypeProcessor;
import org.apache.tuscany.contribution.Contribution;
import org.apache.tuscany.contribution.DeployedArtifact;
import org.apache.tuscany.contribution.impl.DefaultContributionFactory;
import org.apache.tuscany.contribution.processor.DefaultStAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.contribution.processor.DefaultURLArtifactProcessorExtensionPoint;
import org.apache.tuscany.contribution.processor.PackageProcessorExtensionPoint;
import org.apache.tuscany.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.contribution.processor.impl.DefaultPackageProcessorExtensionPoint;
import org.apache.tuscany.contribution.processor.impl.FolderContributionProcessor;
import org.apache.tuscany.contribution.processor.impl.JarContributionProcessor;
import org.apache.tuscany.contribution.resolver.DefaultArtifactResolver;
import org.apache.tuscany.contribution.service.ContributionRepository;
import org.apache.tuscany.contribution.service.ContributionService;
import org.apache.tuscany.contribution.service.impl.ContributionRepositoryImpl;
import org.apache.tuscany.contribution.service.impl.ContributionServiceImpl;
import org.apache.tuscany.contribution.service.impl.PackageTypeDescriberImpl;
import org.apache.tuscany.contribution.service.util.FileHelper;
import org.apache.tuscany.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.core.ExtensionPointRegistry;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.monitor.NullMonitorFactory;
import org.apache.tuscany.core.runtime.AbstractRuntime;
import org.apache.tuscany.host.runtime.InitializationException;
import org.apache.tuscany.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.interfacedef.impl.DefaultInterfaceContractMapper;
import org.apache.tuscany.policy.PolicyFactory;
import org.apache.tuscany.policy.impl.DefaultPolicyFactory;
import org.apache.tuscany.spi.Scope;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.component.WorkContextTunnel;
import org.osoa.sca.CompositeContext;
import org.osoa.sca.CurrentCompositeContext;

/**
 * @version $Rev$ $Date$
 */
public class SimpleRuntimeImpl extends AbstractRuntime<SimpleRuntimeInfo> implements SimpleRuntime {
    private ScopeContainer<URI> container;
    
    private SimpleRuntimeInfo simpleRuntimeInfo;

    public SimpleRuntimeImpl(SimpleRuntimeInfo runtimeInfo) {
        super(SimpleRuntimeInfo.class, runtimeInfo, new NullMonitorFactory(), runtimeInfo.getClassLoader(), new DefaultAssemblyFactory());
        setApplicationSCDL(runtimeInfo.getApplicationSCDL());
        this.simpleRuntimeInfo = runtimeInfo;
    }

    public void initialize() throws InitializationException {
    }

    private static URL getContributionLocation(URL applicationSCDL, String compositePath) {
        URL root = null;
        // "jar:file://....../something.jar!/a/b/c/app.composite"
        try {
            String scdlUrl = applicationSCDL.toExternalForm();
            String protocol = applicationSCDL.getProtocol();
            if ("file".equals(protocol)) {
                // directory contribution
                if (scdlUrl.endsWith(compositePath)) {
                    String location = scdlUrl.substring(0, scdlUrl.lastIndexOf(compositePath));
                    // workaround from evil url/uri form maven
                    root = FileHelper.toFile(new URL(location)).toURI().toURL();
                }

            } else if ("jar".equals(protocol)) {
                // jar contribution
                String location = scdlUrl.substring(4, scdlUrl.lastIndexOf("!/"));
                // workaround from evil url/uri form maven
                root = FileHelper.toFile(new URL(location)).toURI().toURL();
            }
        } catch (MalformedURLException mfe) {
            throw new IllegalArgumentException(mfe);
        }

        return root;
    }

    @SuppressWarnings("unchecked")
    public Component start() throws Exception {
        ExtensionPointRegistry extensionRegistry = new DefaultExtensionPointRegistry();

        // Add artifact processor extension points
        DefaultStAXArtifactProcessorExtensionPoint staxProcessors = new DefaultStAXArtifactProcessorExtensionPoint();
        extensionRegistry.addExtensionPoint(StAXArtifactProcessorExtensionPoint.class, staxProcessors);
        DefaultURLArtifactProcessorExtensionPoint documentProcessors = new DefaultURLArtifactProcessorExtensionPoint();
        extensionRegistry.addExtensionPoint(URLArtifactProcessorExtensionPoint.class, documentProcessors);
        
        // Create default factories
        AssemblyFactory assemblyFactory = new DefaultAssemblyFactory();
        PolicyFactory policyFactory = new DefaultPolicyFactory();
        InterfaceContractMapper mapper = new DefaultInterfaceContractMapper();

        // Register base artifact processors
        staxProcessors.addArtifactProcessor(new CompositeProcessor(assemblyFactory, policyFactory, mapper, staxProcessors));
        staxProcessors.addArtifactProcessor(new ComponentTypeProcessor(assemblyFactory, policyFactory, staxProcessors));
        staxProcessors.addArtifactProcessor(new ConstrainingTypeProcessor(assemblyFactory, policyFactory, staxProcessors));

        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        documentProcessors.addArtifactProcessor(new CompositeDocumentProcessor(staxProcessors, inputFactory));
        documentProcessors.addArtifactProcessor(new ComponentTypeDocumentProcessor(staxProcessors, inputFactory));
        documentProcessors.addArtifactProcessor(new ConstrainingTypeDocumentProcessor(staxProcessors, inputFactory));

        // Create package processor extension point
        PackageTypeDescriberImpl describer = new PackageTypeDescriberImpl();
        PackageProcessorExtensionPoint packageProcessors = new DefaultPackageProcessorExtensionPoint(describer);
        extensionRegistry.addExtensionPoint(PackageProcessorExtensionPoint.class, packageProcessors);
        
        // Register base package processors
        new JarContributionProcessor(packageProcessors);
        new FolderContributionProcessor(packageProcessors);

        // Create a work context
        //WorkContext workContext = new SimpleWorkContext();
        WorkContext workContext = new WorkContextImpl();
        extensionRegistry.addExtensionPoint(WorkContext.class, workContext);
        workContext.setIdentifier(Scope.COMPOSITE, DEFAULT_COMPOSITE);
        WorkContextTunnel.setThreadWorkContext(workContext);

        // Create contribution service
        ContributionRepository repository = new ContributionRepositoryImpl("target");
        DefaultArtifactResolver artifactResolver = new DefaultArtifactResolver(simpleRuntimeInfo.getClassLoader());
        ContributionService contributionService = new ContributionServiceImpl(repository, packageProcessors,
                                                                              documentProcessors, artifactResolver, assemblyFactory, new DefaultContributionFactory());
        initialize(extensionRegistry);

        // Create a scope registry
        ScopeRegistry scopeRegistry = getScopeRegistry();
        container = scopeRegistry.getScopeContainer(Scope.COMPOSITE);

        // Contribute and activate the SCA contribution
        URI uri = URI.create("sca://default/");
        URL root = getContributionLocation(getApplicationSCDL(), runtimeInfo.getCompositePath());
        contributionService.contribute(uri, root, false);
        Contribution contribution = contributionService.getContribution(uri);

        // FIXME: Need to getDeployables() as list of Composites
        String artifactURI = uri + runtimeInfo.getCompositePath();
        DeployedArtifact artifact = null;
        for (DeployedArtifact a: contribution.getArtifacts()) {
            //FIXME this loop is temporary, as this whole class is going to be
            // replaced anyway by MiniRuntimeImpl
            if (artifactURI.equals(a.getURI())) {
                artifact = a;
                break;
            }
        }

        // Start all components
        Composite composite = (Composite)artifact.getModel();
        
        wire(composite);
        
        Collection<Component> components = getDeployer().deploy(composite);
        for (Component component : components) {
            component.start();
        }
        container.startContext(DEFAULT_COMPOSITE, DEFAULT_COMPOSITE);
        getWorkContext().setIdentifier(Scope.COMPOSITE, DEFAULT_COMPOSITE);
        tuscanySystem = getComponentManager().getComponent(URI.create("/" + composite.getName().getLocalPart()));

        // FIXME: Temporary here to help the bring up of samples and integration tests
        // that still use the 0.95 API
        CompositeContext context = new SimpleCompositeContextImpl(this, composite);
        CurrentCompositeContext.setContext(context);

        return tuscanySystem;
    }

    public <T> T getExtensionPoint(Class<T> type) throws TargetResolutionException {
        return extensionRegistry.getExtensionPoint(type);
    }

    @Override
    public void destroy() {
        container.stopContext(DEFAULT_COMPOSITE);
        getWorkContext().setIdentifier(Scope.COMPOSITE, null);
        super.destroy();
    }

    /**
     * FIXME This is temporary until we move over to use the new CompositeActivator.
     * @param composite
     */
    private void wire(Composite composite) {
        // Process the composite configuration
        CompositeUtil compositeUtil = new CompositeUtil(new DefaultAssemblyFactory(), new DefaultInterfaceContractMapper());

        List<Base> problems = new ArrayList<Base>() {
            private static final long serialVersionUID = 4819831446590718923L;

            
            @Override
            public boolean add(Base o) {
                //TODO Use a monitor to report configuration problems
                
                // Uncommenting the following two lines can be useful to detect
                // and troubleshoot SCA assembly XML composite configuration
                // problems.
                
//                System.err.println("Composite configuration problem:");
//                new PrintUtil(System.err).print(o);
                return super.add(o);
            }
        };
        

        // Configure and wire the composite
        compositeUtil.oldConfigureAndWire(composite, problems);

        // Uncommenting the following three lines can be useful to detect
        // and troubleshoot SCA assembly XML composite configuration
        // problems.
//        if (!problems.isEmpty()) {
//            throw new ContributionWireException("Problems in the composite...");
//        }
        
    }
}
