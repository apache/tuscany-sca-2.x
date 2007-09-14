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

package org.apache.tuscany.sca.node.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionException;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.core.assembly.ActivationException;
import org.apache.tuscany.sca.node.ContributionManager;
import org.apache.tuscany.sca.host.embedded.impl.ReallySmallRuntime;

public class ContributionManagerImpl implements ContributionManager {

    protected String domainURI;
    protected String nodeName;
    protected Composite nodeComposite;
    protected ReallySmallRuntime nodeRuntime;
    private ClassLoader classLoader;

    private ModelResolver modelResolver;
    
    private Map<URL, Contribution> contributions = new HashMap<URL, Contribution>(); 
    
    public ContributionManagerImpl(String domainURI, String nodeName, Composite nodeComposite, ReallySmallRuntime nodeRuntime, ClassLoader classLoader, ModelResolver modelResolver) {
        this.domainURI = domainURI;
        this.nodeName = nodeName;
        this.nodeComposite = nodeComposite;
        this.nodeRuntime = nodeRuntime;
        this.classLoader = classLoader;
        this.modelResolver = modelResolver;
    }
    
    public void addContribution(URL contributionLocation)
      throws ActivationException, ContributionException, IOException, CompositeBuilderException {        
    
        if (contributionLocation != null) {
	        // Get ready to add contributions to the domain
	        ContributionService contributionService = nodeRuntime.getContributionService();
	        
	        // Contribute the SCA application
	        Contribution contribution = contributionService.contribute(contributionLocation.toExternalForm(), 
	                                                                   contributionLocation, 
	                                                                   modelResolver, 
	                                                                   false);
	        
	        contributions.put(contributionLocation, contribution);
	        
	        // Add the composites to the top level domain
	        for(Composite composite: contribution.getDeployables()) {
	        	nodeComposite.getIncludes().add(composite);
	            nodeRuntime.getCompositeBuilder().build(composite); 
	        }
	        
	        // activate all of the composites just loaded
	        for(Composite composite: contribution.getDeployables()) {
	            nodeRuntime.getCompositeActivator().activate(composite);     
	        }
        } else {
        	throw new ActivationException("Contribution " + contributionLocation + " not found");
        }              
    }

    public void removeContribution(URL contributionLocation)
      throws ActivationException, ContributionException {
        
        Contribution contribution = contributions.get(contributionLocation);
    	
    	stopContribution(contributionLocation);
    	
        // Deactivate the composites
    	 for(Composite composite: contribution.getDeployables()) {
            nodeRuntime.getCompositeActivator().deactivate(composite);
        }

        // Remove the composites
    	 for(Composite composite: contribution.getDeployables()) {
            nodeComposite.getIncludes().remove(composite);
        }

        // Remove contribution
        nodeRuntime.getContributionService().remove(contributionLocation.toExternalForm());
    }
    
    public void startContribution(URL contributionLocation)
      throws ActivationException, ContributionException, IOException, CompositeBuilderException  {
        
        Contribution contribution = contributions.get(contributionLocation);
    	
    	if (contribution == null){
    		addContribution(contributionLocation);
    	}
    	
        contribution = contributions.get(contributionLocation);
    	
    	if (contribution != null) {
	        for(Composite composite: contribution.getDeployables()) {
	        	 nodeRuntime.getCompositeActivator().start(composite);
	        }
    	} else {
    		throw new ActivationException("Contribution " + contributionLocation + " not added");
    	}
    }
    
    public void stopContribution(URL contributionLocation)
      throws ActivationException {
        
        Contribution contribution = contributions.get(contributionLocation);
        
    	if (contribution != null) {
	        for(Composite composite: contribution.getDeployables()) {
	       	 nodeRuntime.getCompositeActivator().stop(composite);
	       }
    	} else {
    		throw new ActivationException("Contribution " + contributionLocation + " not added");
    	}
    }

    public void addContributionJAR(URL contributionJar) throws CompositeBuilderException, ActivationException {

        ContributionService contributionService = nodeRuntime.getContributionService();

        Contribution contribution = contributeJAR(contributionJar, contributionService);

        // Add the composites to the top level domain
        for (Composite composite : contribution.getDeployables()) {
            nodeComposite.getIncludes().add(composite);
            nodeRuntime.getCompositeBuilder().build(composite);
        }

        // activate all of the composites just loaded
        for (Composite composite : contribution.getDeployables()) {
            nodeRuntime.getCompositeActivator().activate(composite);
        }

        // start all the composites just loaded
        for (Composite composite : contribution.getDeployables()) {
            nodeRuntime.getCompositeActivator().start(composite);
        }
    }

    protected Contribution contributeJAR(URL contributionJar, ContributionService contributionService) throws ActivationException {
        InputStream is;
        try {
            is = contributionJar.openStream();
        } catch (IOException e) {
            throw new ActivationException(e);
        }
        Contribution contribution;
        try {
            contribution = contributionService.contribute(contributionJar.toString(), contributionJar, is, modelResolver);
        } catch (Exception e) {
            throw new ActivationException(e);
        }
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                throw new ActivationException(e);
            }
        }
        return contribution;
    }
}
