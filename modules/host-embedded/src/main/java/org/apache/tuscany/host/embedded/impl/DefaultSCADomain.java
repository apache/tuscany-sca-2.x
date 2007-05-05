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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.Component;
import org.apache.tuscany.assembly.Composite;
import org.apache.tuscany.contribution.Contribution;
import org.apache.tuscany.contribution.service.ContributionException;
import org.apache.tuscany.contribution.service.ContributionService;
import org.apache.tuscany.contribution.service.util.FileHelper;
import org.apache.tuscany.core.runtime.ActivationException;
import org.apache.tuscany.core.runtime.CompositeActivator;
import org.apache.tuscany.core.runtime.DefaultCompositeActivator;
import org.apache.tuscany.host.embedded.SCADomain;
import org.apache.tuscany.interfacedef.IncompatibleInterfaceContractException;
import org.apache.tuscany.spi.Scope;
import org.apache.tuscany.spi.component.GroupInitializationException;
import org.apache.tuscany.spi.component.WorkContextTunnel;
import org.osoa.sca.CallableReference;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.Constants;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.ServiceRuntimeException;

public class DefaultSCADomain extends SCADomain {
    
    private String domainURI;
    private String location;
    private String[] composites;
    private Composite domainComposite;
    private Contribution contribution;
    private Map<String, ComponentContext> components = new HashMap<String, ComponentContext>();
    
    private ReallySmallRuntime runtime;
    
    public DefaultSCADomain(String domainURI, String contributionLocation, String... composites) {
        this.domainURI = domainURI;
        this.location = contributionLocation;
        this.composites = composites;
        
        ClassLoader runtimeClassLoader = getClass().getClassLoader();
        runtime = new ReallySmallRuntime(runtimeClassLoader);
        
        try {
            runtime.start();
            
        } catch (ActivationException e) {
            throw new ServiceRuntimeException(e);
        }
        
        // Contribute the given contribution to an in-memory repository
        ClassLoader applicationClassLoader = Thread.currentThread().getContextClassLoader(); 
        ContributionService contributionService = runtime.getContributionService();
        URL contributionURL;
        try {
            contributionURL = getContributionLocation(location, this.composites, applicationClassLoader);
        } catch (MalformedURLException e) {
            throw new ServiceRuntimeException(e);
        }

        URI contributionURI = URI.create("sca://default/");
        try {
            contributionService.contribute(contributionURI, contributionURL, false);
        } catch (ContributionException e) {
            throw new ServiceRuntimeException(e);
        } catch (IOException e) {
            throw new ServiceRuntimeException(e);
        }
        contribution = contributionService.getContribution(contributionURI);
        
        // Create an in-memory domain level composite
        AssemblyFactory assemblyFactory = runtime.getAssemblyFactory();
        domainComposite = assemblyFactory.createComposite();
        domainComposite.setName(new QName(Constants.SCA_NS, "domain"));
        domainComposite.setURI(domainURI);
        
        // Add the deployable composites to the SCA domain by "include"
        for (Composite composite : contribution.getDeployables()) {
            domainComposite.getIncludes().add(composite);
        }

        // Activate and start the SCA domain composite
        CompositeActivator compositeActivator = runtime.getCompositeActivator();
        try {
            ((DefaultCompositeActivator)compositeActivator).activate(domainComposite);
            compositeActivator.start(domainComposite);
        } catch (IncompatibleInterfaceContractException e) {
            throw new ServiceRuntimeException(e);
        } catch (ActivationException e) {
            throw new ServiceRuntimeException(e);
        }

        //FIXME remove this
        runtime.startDomainWorkContext(domainComposite);

        // Index the top level components
        for (Component component: domainComposite.getComponents()) {
            components.put(component.getName(), (ComponentContext)component);
        }
    }
        
    @Override
    public void close() {
        
        // Remove the contribution from the in-memory repository
        ContributionService contributionService = runtime.getContributionService();
        try {
            contributionService.remove(URI.create(location));
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

    private URL getContributionLocation(String contributionPath, String[] composites, ClassLoader classLoader) throws MalformedURLException {
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
        return getServiceReference(businessInterface, serviceName).getService();
    }

    @Override
    public <B> ServiceReference<B> getServiceReference(Class<B> businessInterface, String serviceName) {
        int i = serviceName.indexOf('/'); 
        if (i != -1) {
            String componentName = serviceName.substring(0, i);
            serviceName = serviceName.substring(i+1);
            
            ComponentContext componentContext = components.get(componentName);
            ServiceReference<B> serviceReference = componentContext.createSelfReference(businessInterface, serviceName);
            return serviceReference;
            
        } else {
            ComponentContext componentContext = components.get(serviceName);
            ServiceReference<B> serviceReference = componentContext.createSelfReference(businessInterface);
            return serviceReference;
        }
    }

    @Override
    public String getURI() {
        return domainURI;
    }

}
