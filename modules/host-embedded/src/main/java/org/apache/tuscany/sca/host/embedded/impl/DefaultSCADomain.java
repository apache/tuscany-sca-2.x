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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.DeployedArtifact;
import org.apache.tuscany.sca.contribution.resolver.DefaultModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionException;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.contribution.service.util.FileHelper;
import org.apache.tuscany.sca.core.runtime.ActivationException;
import org.apache.tuscany.sca.core.runtime.CompositeActivator;
import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.osoa.sca.CallableReference;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.Constants;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.ServiceRuntimeException;

/**
 * A default SCA domain facade implementation.
 * 
 * @version $Rev$ $Date$
 */
public class DefaultSCADomain extends SCADomain {

    private String uri;
    private String location;
    private String[] composites;
    private Composite domainComposite;
    private Contribution contribution;
    private Map<String, Component> components = new HashMap<String, Component>();
    private ReallySmallRuntime runtime;

    /**
     * Constructs a new domain facade.
     * 
     * @param domainURI
     * @param contributionLocation
     * @param composites
     */
    public DefaultSCADomain(ClassLoader runtimeClassLoader,
                            ClassLoader applicationClassLoader,
                            String domainURI,
                            String contributionLocation,
                            String... composites) {
        this.uri = domainURI;
        this.location = contributionLocation;
        this.composites = composites;

        // Create and start the runtime
        runtime = new ReallySmallRuntime(runtimeClassLoader);
        try {
            runtime.start();

        } catch (ActivationException e) {
            throw new ServiceRuntimeException(e);
        }

        // Contribute the given contribution to an in-memory repository
        ContributionService contributionService = runtime.getContributionService();
        URL contributionURL;
        try {
            contributionURL = getContributionLocation(location, this.composites, applicationClassLoader);
        } catch (MalformedURLException e) {
            throw new ServiceRuntimeException(e);
        }

        try {
            DefaultModelResolver modelResolver = new DefaultModelResolver(applicationClassLoader);
            contribution = contributionService.contribute(location, contributionURL, modelResolver, false);
        } catch (ContributionException e) {
            throw new ServiceRuntimeException(e);
        } catch (IOException e) {
            throw new ServiceRuntimeException(e);
        }

        // Create an in-memory domain level composite
        AssemblyFactory assemblyFactory = runtime.getAssemblyFactory();
        domainComposite = assemblyFactory.createComposite();
        domainComposite.setName(new QName(Constants.SCA_NS, "domain"));
        domainComposite.setURI(domainURI);

        // Include all specified deployable composites in the SCA domain
        Map<String, Composite> compositeArtifacts = new HashMap<String, Composite>();
        for (DeployedArtifact artifact : contribution.getArtifacts()) {
            if (artifact.getModel() instanceof Composite) {
                compositeArtifacts.put(artifact.getURI(), (Composite)artifact.getModel());
            }
        }
        for (String compositePath : composites) {
            Composite composite = compositeArtifacts.get(compositePath);
            if (composite == null) {
                throw new ServiceRuntimeException("Composite not found: " + compositePath);
            }
            domainComposite.getIncludes().add(composite);
        }

        // Activate and start the SCA domain composite
        CompositeActivator compositeActivator = runtime.getCompositeActivator();
        try {
            compositeActivator.start(domainComposite);
        } catch (ActivationException e) {
            throw new ServiceRuntimeException(e);
        }

        // FIXME remove this
        runtime.startDomainWorkContext(domainComposite);

        // Index the top level components
        for (Component component : domainComposite.getComponents()) {
            components.put(component.getName(), component);
        }
    }

    @Override
    public void close() {
        
        super.close();

        // Remove the contribution from the in-memory repository
        ContributionService contributionService = runtime.getContributionService();
        try {
            contributionService.remove(location);
        } catch (ContributionException e) {
            throw new ServiceRuntimeException(e);
        }
        
        // Stop the SCA domain composite
        CompositeActivator compositeActivator = runtime.getCompositeActivator();
        try {
            compositeActivator.stop(domainComposite);
        } catch (ActivationException e) {
            throw new ServiceRuntimeException(e);

        }

        // Stop the runtime
        try {
            runtime.stop();
        } catch (ActivationException e) {
            throw new ServiceRuntimeException(e);
        }
    }

    /**
     * Determine the location of a contribution, given a contribution path and a
     * list of composites.
     * 
     * @param contributionPath
     * @param composites
     * @param classLoader
     * @return
     * @throws MalformedURLException
     */
    private URL getContributionLocation(String contributionPath, String[] composites, ClassLoader classLoader)
        throws MalformedURLException {
        URI contributionURI = URI.create(contributionPath);
        if (contributionURI.isAbsolute() || composites.length == 0) {
            return new URL(contributionPath);
        }

        String compositePath = composites[0];
        URL compositeURL = classLoader.getResource(compositePath);
        if (compositeURL == null) {
            throw new IllegalArgumentException("Composite not found: " + compositePath);
        }

        URL contributionURL = null;
        // "jar:file://....../something.jar!/a/b/c/app.composite"
        try {
            String scdlUrl = compositeURL.toExternalForm();
            String protocol = compositeURL.getProtocol();
            if ("file".equals(protocol)) {
                // directory contribution
                if (scdlUrl.endsWith(compositePath)) {
                    String location = scdlUrl.substring(0, scdlUrl.lastIndexOf(compositePath));
                    // workaround from evil url/uri form maven
                    contributionURL = FileHelper.toFile(new URL(location)).toURI().toURL();
                }

            } else if ("jar".equals(protocol)) {
                // jar contribution
                String location = scdlUrl.substring(4, scdlUrl.lastIndexOf("!/"));
                // workaround from evil url/uri form maven
                contributionURL = FileHelper.toFile(new URL(location)).toURI().toURL();
            }
        } catch (MalformedURLException mfe) {
            throw new IllegalArgumentException(mfe);
        }

        return contributionURL;
    }

    @Override
    public <B, R extends CallableReference<B>> R cast(B target) throws IllegalArgumentException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <B> B getService(Class<B> businessInterface, String serviceName) {
        ServiceReference<B> serviceReference = getServiceReference(businessInterface, serviceName);
        if (serviceReference == null) {
            throw new ServiceRuntimeException("Service not found: " + serviceName);
        }
        return serviceReference.getService();
    }

    @Override
    public <B> ServiceReference<B> getServiceReference(Class<B> businessInterface, String name) {

        // Extract the component name
        String componentName;
        String serviceName;
        int i = name.indexOf('/');
        if (i != -1) {
            componentName = name.substring(0, i);
            serviceName = name.substring(i + 1);

        } else {
            componentName = name;
            serviceName = null;
        }

        // Lookup the component in the domain
        Component component = components.get(componentName);
        if (component == null) {
            throw new ServiceRuntimeException("Component not found: " + componentName);
        }
        ComponentContext componentContext = null;

        // If the component is a composite, then we need to find the
        // non-composite
        // component that provides the requested service
        if (component.getImplementation() instanceof Composite) {
            ComponentService promotedService = null;
            for (ComponentService componentService : component.getServices()) {
                if (serviceName == null || serviceName.equals(componentService.getName())) {

                    CompositeService compositeService = (CompositeService)componentService.getService();
                    if (compositeService != null) {
                        promotedService = compositeService.getPromotedService();
                        SCABinding scaBinding = promotedService.getBinding(SCABinding.class);
                        if (scaBinding != null) {
                            Component promotedComponent = scaBinding.getComponent();
                            if (serviceName != null) {
                                serviceName = "$promoted$." + serviceName;
                            }
                            componentContext = (ComponentContext)promotedComponent;
                        }
                    }
                    break;
                }
            }
            if (componentContext == null) {
                throw new ServiceRuntimeException("Composite service not found: " + name);
            }
        } else {
            componentContext = (ComponentContext)component;
        }

        ServiceReference<B> serviceReference;
        if (serviceName != null) {
            serviceReference = componentContext.createSelfReference(businessInterface, serviceName);
        } else {
            serviceReference = componentContext.createSelfReference(businessInterface);
        }
        return serviceReference;

    }

    @Override
    public String getURI() {
        return uri;
    }

}
