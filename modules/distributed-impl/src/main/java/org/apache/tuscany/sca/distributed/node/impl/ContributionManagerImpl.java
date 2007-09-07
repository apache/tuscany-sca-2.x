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

package org.apache.tuscany.sca.distributed.node.impl;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.resolver.impl.ModelResolverImpl;
import org.apache.tuscany.sca.contribution.service.ContributionException;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.core.assembly.ActivationException;
import org.apache.tuscany.sca.core.assembly.RuntimeComponentImpl;
import org.apache.tuscany.sca.distributed.domain.Domain;
import org.apache.tuscany.sca.distributed.node.ContributionManager;
import org.apache.tuscany.sca.host.embedded.impl.EmbeddedSCADomain;
import org.apache.tuscany.sca.host.embedded.impl.ReallySmallRuntime;
import org.apache.tuscany.sca.host.embedded.management.variation.ComponentListener;
import org.apache.tuscany.sca.host.embedded.management.variation.ComponentManager;

public class ContributionManagerImpl implements ContributionManager {

    protected String domainURI;
    protected String nodeName;
    protected Composite nodeComposite;
    protected ReallySmallRuntime nodeRuntime;
    private ClassLoader classLoader;

    private String contributionLocation;
    private ModelResolver modelResolver;
    private Contribution contribution; 
    
    public ContributionManagerImpl(String domainURI, String nodeName, Composite nodeComposite, ReallySmallRuntime nodeRuntime, ClassLoader classLoader, ModelResolver modelResolver) {
        this.domainURI = domainURI;
        this.nodeName = nodeName;
        this.nodeComposite = nodeComposite;
        this.nodeRuntime = nodeRuntime;
        this.classLoader = classLoader;
        this.modelResolver = modelResolver;
        
    	// work out what the contribution string if its not supplied 
        if (contributionLocation == null){
        	if (nodeName != null){
        		// guess that it's in a directory with the node name
        		contributionLocation = nodeName + "/";
        	} else {
        		// guess that it's in the current directory
        		contributionLocation = "/";
        	}
        }

        this.contributionLocation = contributionLocation;
    }
    
    public String getContributionLocation(){
    	return contributionLocation;
    }
   
    public void addContribution(String contributionLocation)
      throws ActivationException, ContributionException, IOException, CompositeBuilderException {        

        URL contributionURL = classLoader.getResource(contributionLocation);
    
        if (contributionURL != null) {
	        // Get ready to add contributions to the domain
	        ContributionService contributionService = nodeRuntime.getContributionService();
	        
	        // Contribute the SCA application
	        contribution = contributionService.contribute(contributionLocation, 
	                                                      contributionURL, 
	                                                      modelResolver, 
	                                                      false);
	        
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

    public void removeContribution(String contributionLocation)
      throws ActivationException, ContributionException {
    	
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
        nodeRuntime.getContributionService().remove(contributionLocation);
    }
    
    public void startContribution(String contributionLocation)
      throws ActivationException, ContributionException, IOException, CompositeBuilderException  {
    	
    	if (contribution == null){
    		addContribution(contributionLocation);
    	}
    	
    	if (contribution != null) {
	        for(Composite composite: contribution.getDeployables()) {
	        	 nodeRuntime.getCompositeActivator().start(composite);
	        }
    	} else {
    		throw new ActivationException("Contribution " + contributionLocation + " not added");
    	}
    }
    
    public void stopContribution(String contributionLocation)
      throws ActivationException {
    	if (contribution != null) {
	        for(Composite composite: contribution.getDeployables()) {
	       	 nodeRuntime.getCompositeActivator().stop(composite);
	       }
    	} else {
    		throw new ActivationException("Contribution " + contributionLocation + " not added");
    	}
    }

}
