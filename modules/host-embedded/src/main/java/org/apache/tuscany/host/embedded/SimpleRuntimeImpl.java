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
package org.apache.tuscany.host.embedded;

import static org.apache.tuscany.host.embedded.SimpleRuntimeInfo.DEFAULT_COMPOSITE;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collection;

import org.apache.tuscany.assembly.Composite;
import org.apache.tuscany.assembly.xml.ComponentTypeDocumentProcessor;
import org.apache.tuscany.assembly.xml.ComponentTypeProcessor;
import org.apache.tuscany.assembly.xml.CompositeDocumentProcessor;
import org.apache.tuscany.assembly.xml.CompositeProcessor;
import org.apache.tuscany.assembly.xml.ConstrainingTypeDocumentProcessor;
import org.apache.tuscany.assembly.xml.ConstrainingTypeProcessor;
import org.apache.tuscany.contribution.Contribution;
import org.apache.tuscany.contribution.DeployedArtifact;
import org.apache.tuscany.contribution.processor.ContributionPackageProcessorRegistry;
import org.apache.tuscany.contribution.processor.DefaultStAXArtifactProcessorRegistry;
import org.apache.tuscany.contribution.processor.DefaultURLArtifactProcessorRegistry;
import org.apache.tuscany.contribution.processor.StAXArtifactProcessorRegistry;
import org.apache.tuscany.contribution.processor.URLArtifactProcessorRegistry;
import org.apache.tuscany.contribution.processor.impl.ContributionPackageProcessorRegistryImpl;
import org.apache.tuscany.contribution.processor.impl.FolderContributionProcessor;
import org.apache.tuscany.contribution.processor.impl.JarContributionProcessor;
import org.apache.tuscany.contribution.resolver.DefaultArtifactResolver;
import org.apache.tuscany.contribution.service.ContributionRepository;
import org.apache.tuscany.contribution.service.ContributionService;
import org.apache.tuscany.contribution.service.impl.ContributionRepositoryImpl;
import org.apache.tuscany.contribution.service.impl.ContributionServiceImpl;
import org.apache.tuscany.contribution.service.impl.PackageTypeDescriberImpl;
import org.apache.tuscany.contribution.service.util.FileHelper;
import org.apache.tuscany.core.bootstrap.ExtensionPointRegistryImpl;
import org.apache.tuscany.core.component.SimpleWorkContext;
import org.apache.tuscany.core.runtime.AbstractRuntime;
import org.apache.tuscany.host.runtime.InitializationException;
import org.apache.tuscany.interfacedef.java.xml.JavaInterfaceProcessor;
import org.apache.tuscany.interfacedef.wsdl.xml.WSDLDocumentProcessor;
import org.apache.tuscany.interfacedef.wsdl.xml.WSDLInterfaceProcessor;
import org.apache.tuscany.spi.Scope;
import org.apache.tuscany.spi.bootstrap.ExtensionPointRegistry;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.WorkContextTunnel;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.component.WorkContext;
import org.osoa.sca.CompositeContext;
import org.osoa.sca.CurrentCompositeContext;

/**
 * @version $Rev$ $Date$
 */
public class SimpleRuntimeImpl extends AbstractRuntime<SimpleRuntimeInfo> implements SimpleRuntime {
    private ScopeContainer<URI> container;

    public SimpleRuntimeImpl(SimpleRuntimeInfo runtimeInfo) {
        super(SimpleRuntimeInfo.class);
        ClassLoader hostClassLoader = runtimeInfo.getClassLoader();
        setHostClassLoader(hostClassLoader);
        setApplicationScdl(runtimeInfo.getApplicationSCDL());
        setSystemScdl(runtimeInfo.getSystemSCDL());
        setRuntimeInfo(runtimeInfo);
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
        ExtensionPointRegistry extensionRegistry = new ExtensionPointRegistryImpl();
        ContributionRepository repository = new ContributionRepositoryImpl("target");

        DefaultStAXArtifactProcessorRegistry staxExtensionPoint = new DefaultStAXArtifactProcessorRegistry();
        staxExtensionPoint.addArtifactProcessor(new CompositeProcessor(staxExtensionPoint));
        staxExtensionPoint.addArtifactProcessor(new ComponentTypeProcessor(staxExtensionPoint));
        staxExtensionPoint.addArtifactProcessor(new ConstrainingTypeProcessor(staxExtensionPoint));
        staxExtensionPoint.addArtifactProcessor(new JavaInterfaceProcessor());
        staxExtensionPoint.addArtifactProcessor(new WSDLInterfaceProcessor());
        extensionRegistry.addExtensionPoint(StAXArtifactProcessorRegistry.class, staxExtensionPoint);

        DefaultURLArtifactProcessorRegistry documentExtensionPoint = new DefaultURLArtifactProcessorRegistry();
        documentExtensionPoint.addArtifactProcessor(new CompositeDocumentProcessor(staxExtensionPoint));
        documentExtensionPoint.addArtifactProcessor(new ComponentTypeDocumentProcessor(staxExtensionPoint));
        documentExtensionPoint.addArtifactProcessor(new ConstrainingTypeDocumentProcessor(staxExtensionPoint));
        documentExtensionPoint.addArtifactProcessor(new WSDLDocumentProcessor());
        extensionRegistry.addExtensionPoint(URLArtifactProcessorRegistry.class, documentExtensionPoint);

        PackageTypeDescriberImpl describer = new PackageTypeDescriberImpl();
        ContributionPackageProcessorRegistry pkgRegistry = new ContributionPackageProcessorRegistryImpl(describer);
        new JarContributionProcessor(pkgRegistry);
        new FolderContributionProcessor(pkgRegistry);

        WorkContext workContext = new SimpleWorkContext();
        workContext.setIdentifier(Scope.COMPOSITE, DEFAULT_COMPOSITE);
        WorkContextTunnel.setThreadWorkContext(workContext);

        DefaultArtifactResolver artifactResolver = new DefaultArtifactResolver();

        ContributionService contributionService = new ContributionServiceImpl(repository, pkgRegistry,
                                                                              documentExtensionPoint, artifactResolver);

        extensionRegistry.addExtensionPoint(ContributionService.class, contributionService);
        initialize(extensionRegistry, contributionService);

        ScopeRegistry scopeRegistry = getScopeRegistry();
        container = scopeRegistry.getScopeContainer(Scope.COMPOSITE);

        URI uri = URI.create("sca://default/");
        URL root = getContributionLocation(getApplicationScdl(), runtimeInfo.getCompositePath());
        contributionService.contribute(uri, root, false);
        Contribution contribution = contributionService.getContribution(uri);

        // FIXME: Need to getDeployables() as list of Composites
        DeployedArtifact artifact = contribution.getArtifact(URI.create(uri + runtimeInfo.getCompositePath()));
        Composite composite = (Composite)artifact.getModelObject();

        Collection<Component> components = getDeployer().deploy(composite);
        for (Component component : components) {
            component.start();
        }
        container.startContext(DEFAULT_COMPOSITE, DEFAULT_COMPOSITE);
        getWorkContext().setIdentifier(Scope.COMPOSITE, DEFAULT_COMPOSITE);
        tuscanySystem = getComponentManager().getComponent(URI.create("/" + composite.getName().getLocalPart()));

        // Temporary here to help the bring up of samples and integration tests
        // that still
        // use the 0.95 API
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

}
