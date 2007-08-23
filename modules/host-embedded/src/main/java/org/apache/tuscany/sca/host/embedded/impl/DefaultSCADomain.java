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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.DeployedArtifact;
import org.apache.tuscany.sca.contribution.service.ContributionException;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.contribution.service.util.FileHelper;
import org.apache.tuscany.sca.core.assembly.ActivationException;
import org.apache.tuscany.sca.core.assembly.CompositeActivator;
import org.apache.tuscany.sca.core.assembly.RuntimeComponentImpl;
import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.apache.tuscany.sca.host.embedded.management.ComponentListener;
import org.apache.tuscany.sca.host.embedded.management.ComponentManager;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
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
    private String[] composites;
    private Composite domainComposite;
    private Contribution contribution;
    private Map<String, Component> components = new HashMap<String, Component>();
    private ReallySmallRuntime runtime;
    private ComponentManager componentManager;

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
            contributionURL = getContributionLocation(applicationClassLoader, contributionLocation, this.composites);
            if (contributionURL != null) {
                // Make sure the URL is correctly encoded (for example, escape the space characters) 
                contributionURL = contributionURL.toURI().toURL();
            }
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }

        try {
            String contributionURI = FileHelper.getName(contributionURL.getPath());
            if(contributionURI == null || contributionURI.length() == 0) {
                contributionURI = contributionURL.toString();
            }
            contribution = contributionService.contribute(contributionURI, contributionURL, false);
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

        //when the deployable composites were specified when initializing the runtime
        if (composites != null && composites.length > 0 && composites[0].length() > 0) {
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
        } else {
            // in this case, a sca-contribution.xml should have been specified
            for(Composite composite : contribution.getDeployables()) {
                domainComposite.getIncludes().add(composite);
            }
            
        }
        
        // Build the SCA composites
        CompositeBuilder compositeBuilder = runtime.getCompositeBuilder();
        
        for (Composite composite: domainComposite.getIncludes()) {
            try {
                compositeBuilder.build(composite);
            } catch (CompositeBuilderException e) {
                throw new ServiceRuntimeException(e);
            }
        }

        // Activate and start composites
        CompositeActivator compositeActivator = runtime.getCompositeActivator();
        compositeActivator.setDomainComposite(domainComposite);
        for (Composite composite: domainComposite.getIncludes()) {
            try {
                compositeActivator.activate(composite);
            } catch (ActivationException e) {
                throw new ServiceRuntimeException(e);
            }
        }
        for (Composite composite: domainComposite.getIncludes()) {
            try {
                for (Component component : composite.getComponents()) {
                    compositeActivator.start(component);
                }
            } catch (ActivationException e) {
                throw new ServiceRuntimeException(e);
            }
        }

        // Index the top level components
        for (Composite composite: domainComposite.getIncludes()) {
            for (Component component : composite.getComponents()) {
                components.put(component.getName(), component);
            }
        }
        
        this.componentManager = new DefaultSCADomainComponentManager(this);
    }

    @Override
    public void close() {
        
        super.close();

        // Stop and deactivate composites
        CompositeActivator compositeActivator = runtime.getCompositeActivator();
        for (Composite composite: domainComposite.getIncludes()) {
            try {
                compositeActivator.deactivate(composite);
            } catch (ActivationException e) {
                throw new ServiceRuntimeException(e);
            }
        }
        for (Composite composite: domainComposite.getIncludes()) {
            try {
                for (Component component : composite.getComponents()) {
                    compositeActivator.stop(component);
                }
            } catch (ActivationException e) {
                throw new ServiceRuntimeException(e);
            }
        }

        // Remove the contribution from the in-memory repository
        ContributionService contributionService = runtime.getContributionService();
        try {
            contributionService.remove(contribution.getURI());
        } catch (ContributionException e) {
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
    private URL getContributionLocation(ClassLoader classLoader, String contributionPath, String[] composites)
        throws MalformedURLException {
        if (contributionPath != null && contributionPath.length() > 0) {
            URI contributionURI = URI.create(contributionPath);
            if (contributionURI.isAbsolute() || composites.length == 0) {
                return new URL(contributionPath);
            }
        }

        String contributionArtifactPath = null;
        URL contributionArtifactURL = null;
        if (composites != null && composites.length > 0 && composites[0].length() > 0) {

            // Here the SCADomain was started with a reference to a composite file
            contributionArtifactPath = composites[0];
            contributionArtifactURL = classLoader.getResource(contributionArtifactPath);
            if (contributionArtifactURL == null) {
                throw new IllegalArgumentException("Composite not found: " + contributionArtifactPath);
            }
        } else {
            
            // Here the SCADomain was started without any reference to a composite file
            // We are going to look for an sca-contribution.xml or sca-contribution-generated.xml
            
            // Look for META-INF/sca-contribution.xml
            contributionArtifactPath = Contribution.SCA_CONTRIBUTION_META;
            contributionArtifactURL = classLoader.getResource(contributionArtifactPath);
            
            // Look for META-INF/sca-contribution-generated.xml
            if( contributionArtifactURL == null ) {
                contributionArtifactPath = Contribution.SCA_CONTRIBUTION_GENERATED_META;
                contributionArtifactURL = classLoader.getResource(contributionArtifactPath);
            }
            
                // Look for META-INF/sca-deployables directory
                if (contributionArtifactURL == null) {
                    contributionArtifactPath = Contribution.SCA_CONTRIBUTION_DEPLOYABLES;
                    contributionArtifactURL = classLoader.getResource(contributionArtifactPath);
                }
        }
        
        if (contributionArtifactURL == null) {
            throw new IllegalArgumentException("Can't determine contribution deployables. Either specify a composite file, or use an sca-contribution.xml file to specify the deployables.");
        }

        URL contributionURL = null;
        // "jar:file://....../something.jar!/a/b/c/app.composite"
        try {
            String url = contributionArtifactURL.toExternalForm();
            String protocol = contributionArtifactURL.getProtocol();
            if ("file".equals(protocol)) {
                // directory contribution
                if (url.endsWith(contributionArtifactPath)) {
                    String location = url.substring(0, url.lastIndexOf(contributionArtifactPath));
                    // workaround from evil url/uri form maven
                    contributionURL = FileHelper.toFile(new URL(location)).toURI().toURL();
                }

            } else if ("jar".equals(protocol)) {
                // jar contribution
                String location = url.substring(4, url.lastIndexOf("!/"));
                // workaround for evil url/uri from maven
                contributionURL = FileHelper.toFile(new URL(location)).toURI().toURL();
            }
        } catch (MalformedURLException mfe) {
            throw new IllegalArgumentException(mfe);
        }

        return contributionURL;
    }

    @Override
    public <B, R extends CallableReference<B>> R cast(B target) throws IllegalArgumentException {
        return (R) runtime.getProxyFactory().cast(target);
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
        // non-composite component that provides the requested service
        if (component.getImplementation() instanceof Composite) {
            for (ComponentService componentService : component.getServices()) {
                if (serviceName == null || serviceName.equals(componentService.getName())) {
                    CompositeService compositeService = (CompositeService)componentService.getService();
                    if (compositeService != null) {
                        if (serviceName != null) {
                            serviceName = "$promoted$." + serviceName;
                        }
                        componentContext = ((RuntimeComponent)compositeService.getPromotedComponent()).getComponentContext();
                    }
                    break;
                }
            }
            if (componentContext == null) {
                throw new ServiceRuntimeException("Composite service not found: " + name);
            }
        } else {
            componentContext = ((RuntimeComponent)component).getComponentContext();
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

    @Override
    public ComponentManager getComponentManager() {
        return componentManager;
    }

    public Set<String> getComponentNames() {
        Set<String> componentNames = new HashSet<String>();
        for (DeployedArtifact artifact : contribution.getArtifacts()) {
            if (artifact.getModel() instanceof Composite) {
                for (Component component : ((Composite)artifact.getModel()).getComponents()) {
                    componentNames.add(component.getName());
                }
            }
        }
        return componentNames;
    }
    
    
    public Component getComponent(String componentName) {
        for (DeployedArtifact artifact : contribution.getArtifacts()) {
            if (artifact.getModel() instanceof Composite) {
                for (Component component : ((Composite)artifact.getModel()).getComponents()) {
                    if (component.getName().equals(componentName)) {
                        return component;
                    }
                }
            }
        }
        return null;
    }
    
    public void startComponent(String componentName) throws ActivationException {
        Component component = getComponent(componentName);
        if (component == null) {
            throw new IllegalArgumentException("no component: " + componentName);
        }
        CompositeActivator compositeActivator = runtime.getCompositeActivator();
        compositeActivator.start(component);
    }

    public void stopComponent(String componentName) throws ActivationException {
        Component component = getComponent(componentName);
        if (component == null) {
            throw new IllegalArgumentException("no component: " + componentName);
        }
        CompositeActivator compositeActivator = runtime.getCompositeActivator();
        compositeActivator.stop(component);
    }
}

class DefaultSCADomainComponentManager implements ComponentManager {

    protected DefaultSCADomain scaDomain;
    protected List<ComponentListener> listeners = new CopyOnWriteArrayList<ComponentListener>();

    public DefaultSCADomainComponentManager(DefaultSCADomain scaDomain) {
        this.scaDomain = scaDomain;
    }

    public void addComponentListener(ComponentListener listener) {
        this.listeners.add(listener);
    }

    public void removeComponentListener(ComponentListener listener) {
        this.listeners.remove(listener);
    }

    public Set<String> getComponentNames() {
        return scaDomain.getComponentNames();
    }

    public Component getComponent(String componentName) {
        return scaDomain.getComponent(componentName);
    }

    public void startComponent(String componentName) throws ActivationException {
        scaDomain.startComponent(componentName);
    }

    public void stopComponent(String componentName) throws ActivationException {
        scaDomain.stopComponent(componentName);
    }

    public void notifyComponentStarted(String componentName) {
        for (ComponentListener listener : listeners) {
            try {
                listener.componentStarted(componentName);
            } catch (Exception e) {
                e.printStackTrace(); // TODO: log
            }
        }
    }

    public void notifyComponentStopped(String componentName) {
        for (ComponentListener listener : listeners) {
            try {
                listener.componentStopped(componentName);
            } catch (Exception e) {
                e.printStackTrace(); // TODO: log
            }
        }
    }

    public boolean isComponentStarted(String componentName) {
        RuntimeComponentImpl runtimeComponent = (RuntimeComponentImpl)getComponent(componentName);
        return runtimeComponent.isStarted();
    }

}
