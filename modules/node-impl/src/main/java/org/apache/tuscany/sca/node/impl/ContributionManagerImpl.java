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

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.DeployedArtifact;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionException;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.core.assembly.ActivationException;
import org.apache.tuscany.sca.node.CompositeManager;
import org.apache.tuscany.sca.node.ContributionManager;
import org.apache.tuscany.sca.host.embedded.impl.ReallySmallRuntime;
import org.osoa.sca.ServiceRuntimeException;

public class ContributionManagerImpl implements ContributionManager, CompositeManager {

    protected String domainURI;
    protected String nodeName;
    protected Composite nodeComposite;
    protected ReallySmallRuntime nodeRuntime;
    private ClassLoader classLoader;

    private ModelResolver modelResolver;
    
    private Map<String, Composite> compositeArtifacts = new HashMap<String, Composite>();
    private Map<URL, Contribution> contributions = new HashMap<URL, Contribution>(); 
    
    public ContributionManagerImpl(String domainURI, String nodeName, Composite nodeComposite, ReallySmallRuntime nodeRuntime, ClassLoader classLoader, ModelResolver modelResolver) {
        this.domainURI = domainURI;
        this.nodeName = nodeName;
        this.nodeComposite = nodeComposite;
        this.nodeRuntime = nodeRuntime;
        this.classLoader = classLoader;
        this.modelResolver = modelResolver;
    }
    
    public void addContribution(URL contributionURL)
      throws ActivationException, ContributionException, IOException, CompositeBuilderException {        
    
        if (contributionURL != null) {
	        // Get ready to add contributions to the domain
	        ContributionService contributionService = nodeRuntime.getContributionService();
	        
	        // Contribute the SCA application
	        Contribution contribution = contributionService.contribute(contributionURL.toExternalForm(), 
	                                                                   contributionURL, 
	                                                                   modelResolver, 
	                                                                   false);
	        
	        contributions.put(contributionURL, contribution);
	        
            for (DeployedArtifact artifact : contribution.getArtifacts()) {
                if (artifact.getModel() instanceof Composite) {
                    compositeArtifacts.put(artifact.getURI(), (Composite)artifact.getModel());
                }
            }

        } else {
        	throw new ActivationException("Contribution " + contributionURL + " not found");
        }              
    }
    
    /**
     * Add all composite in a contribution
     * 
     * @param contributionURL
     */
    public void addAllComposites(URL contributionURL)
      throws ActivationException, ContributionException, IOException, CompositeBuilderException {
        Contribution contribution = contributions.get(contributionURL);
            
        if ( contribution != null) {
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
            throw new ActivationException("Contribution " + contributionURL + " not found");
        }              
    } 
    
    /**
     * adds a named composite
     * 
     * @param compositeName the name of the composite to be added
     */
    public void addComposite(QName compositeName){
        //TODO
    }
    
    /**
     * adds a named composite
     * 
     * @param compositePath the path to the composite to be added
     */    
    public void addComposite(String compositePath)
      throws ActivationException, ContributionException, IOException, CompositeBuilderException {
        
        Composite composite = compositeArtifacts.get(compositePath);
        
        if (composite == null) {
            throw new ServiceRuntimeException("Composite not found: " + compositePath);
        }
        
        nodeComposite.getIncludes().add(composite);
        nodeRuntime.getCompositeBuilder().build(composite); 
        nodeRuntime.getCompositeActivator().activate(composite);         
    }   
    
    /**
     * Starts all composites in a contribution
     * 
     * @param contributionURL
     */
    public void startAllComposites(URL contributionURL)
      throws ActivationException {
        Contribution contribution = contributions.get(contributionURL);
        
        if (contribution != null) {
            for(Composite composite: contribution.getDeployables()) {
                 nodeRuntime.getCompositeActivator().start(composite);
            }
        } else {
            throw new ActivationException("Contribution " + contributionURL+ " not started");
        }
    }
       
    /**
     * Starts a named composite
     * 
     * @param compositeName the name of the composite to be started
     */
    public void startComposite(QName compositeName){
        
    }  
    
    /**
     * Starts a named composite
     * 
     * @param compositePath the path to the composite to be started
     */
    public void startComposite(String compositePath)
      throws ActivationException {        
        Composite composite = compositeArtifacts.get(compositePath);
        
        if (composite == null) {
            throw new ServiceRuntimeException("Composite not found: " + compositePath);
        }
        
        nodeRuntime.getCompositeActivator().start(composite);        
    } 
    
    /**
     * Stops a all composites in a contribution
     * 
     * @param compositeURL 
     */
    public void stopAllComposites(URL contributionURL)
      throws ActivationException {
        Contribution contribution = contributions.get(contributionURL);
        
        if (contribution != null) {
            for(Composite composite: contribution.getDeployables()) {
                 nodeRuntime.getCompositeActivator().stop(composite);
            }
        } else {
            throw new ActivationException("Contribution " + contributionURL+ " not stopped");
        }
    }    
    
    /**
     * Stops a named composite
     * 
     * @param compositeName the name of the composite to be stopped
     */
    public void stopComposite(QName compositeName){
        
    }  
    
    /**
     * Stops a named composite
     * 
     * @param compositeName the name of the composite to be stopped
     */
    public void stopComposite(String compositePath){
        
    }      
    

    public void removeContribution(URL contributionURL)
      throws ActivationException, ContributionException {
        
        // Remove contribution
        nodeRuntime.getContributionService().remove(contributionURL.toExternalForm());
    }
    
    /**
     * Removes all composites
     * 
     * @param compositeName the name of the composite to be added
     */
    public void removeAllComposites(URL contributionURL)
      throws ActivationException {
        Contribution contribution = contributions.get(contributionURL);
        
        stopAllComposites(contributionURL);
        
        // Deactivate the composites
         for(Composite composite: contribution.getDeployables()) {
            nodeRuntime.getCompositeActivator().deactivate(composite);
        }

        // Remove the composites
         for(Composite composite: contribution.getDeployables()) {
            nodeComposite.getIncludes().remove(composite);
        }
         
        contributions.remove(contributionURL);
    }    

    /**
     * Removes a named composite
     * 
     * @param compositeName the name of the composite to be added
     */
    public void removeComposite(QName compositeName){
        
    }
    
    /**
     * Removes a named composite
     * 
     * @param compositeName the name of the composite to be added
     */
    public void removeComposite(String compositePath){
        
    }  
    
    
    /**
     * Stop and remove everything
     */
    public void stop()
      throws ActivationException, ContributionException {        
        for (URL contributionURL : contributions.keySet()){
            stopAllComposites(contributionURL);
            removeContribution(contributionURL);
        }
    }
    
/*    
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
 */
}
