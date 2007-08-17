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

import java.net.URL;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.core.runtime.ActivationException;
import org.apache.tuscany.sca.distributed.domain.DistributedSCADomain;
import org.apache.tuscany.sca.distributed.domain.impl.DistributedSCADomainImpl;
import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.apache.tuscany.sca.host.embedded.impl.EmbeddedSCADomain;

/**
 * An embeddable node implementation. 
 */
public class EmbeddedNode {
    
    // The data required to get the domain up and running on this node
    private String nodeName;
    private String domainName; 
    private EmbeddedSCADomain domain;
    private DistributedSCADomainImpl distributedDomain;
    
    // Node management 
    private EmbeddedSCADomain management;
    
    public EmbeddedNode(String nodeName)
      throws ActivationException {
        this.nodeName = nodeName;
      
        try {
            // This is starting a local domain just to use component references to 
            // talk to the domain node. It maybe that we can slim this down when we have the 
            // remote service reference code working.
            
            ClassLoader cl = EmbeddedNode.class.getClassLoader(); 
            
            // start a local domain to run management components
            management = new EmbeddedSCADomain(cl, "management");   
            management.start();
            
            // add management composite to the management domain
            ContributionService contributionService = management.getContributionService();
            URL contributionURL = Thread.currentThread().getContextClassLoader().getResource("management/");
            
            if ( contributionURL != null){
                System.err.println(contributionURL.toString());
                Contribution contribution = contributionService.contribute("http://management", 
                                                                           contributionURL, 
                                                                           null, //resolver, 
                                                                           false);
                Composite composite = contribution.getDeployables().get(0);
                management.getDomainComposite().getIncludes().add(composite);
                management.getCompositeBuilder().build(composite); 
                management.getCompositeActivator().activate(composite); 
                management.getCompositeActivator().start(composite);
                
                // get the management components out of the domain so that they 
                // can be configured/used. None are yet but this would be the place to 
                // get components out of the management domain and give them access to 
                // useful parts of the node
            
            } else {
                throw new ActivationException("Can't find the management contribution on the classpath");
            }
                        
        } catch(Exception ex) {
            System.err.println("Exception when creating management components " + ex.getMessage());
            domain = null;
            throw new ActivationException(ex);
        } 

    }
    
    public SCADomain attachDomain(DistributedSCADomain distributedDomain){
        this.distributedDomain = (DistributedSCADomainImpl) distributedDomain;
        domainName = distributedDomain.getDomainName();
        
        try {
            ClassLoader cl = EmbeddedNode.class.getClassLoader(); 
            
            // create and start the local domain
            domain = new EmbeddedSCADomain(cl, domainName);   
            domain.start();
                     
        } catch(Exception ex) {
            System.err.println("Exception when creating domain " + ex.getMessage());
            ex.printStackTrace(System.err);
            domain = null;
        }
        
        // add local information into the distributed domain
        this.distributedDomain.setNodeName(nodeName);
        this.distributedDomain.setLocalDomain(management); 
        
        // add domain information into the management components that need it
        
        
        return domain;
    }
      
    
    protected void loadContribution(String domainName, URL contributionURL){
        
        try {        
            // Get ready to add contributions to the domain
            ContributionService contributionService = domain.getContributionService();
            
            // Contribute the SCA application
            Contribution contribution = contributionService.contribute("http://calculator", 
                                                          contributionURL, 
                                                          null, //resolver, 
                                                          false);
            Composite composite = contribution.getDeployables().get(0);
            
            // Add the deployable composite to the domain
            domain.getDomainComposite().getIncludes().add(composite);
            domain.getCompositeBuilder().build(composite);
            
            distributedDomain.addDistributedDomainToBindings(composite);
            
            domain.getCompositeActivator().activate(composite);            
        } catch(Exception ex) {
            System.err.println("Exception when loading contribution " + ex.getMessage());
            ex.printStackTrace(System.err);
            domain = null;
        }             
    }
    
    public void addContribution(String domainName, String contributionURLString) {
        try {
            
            URL contributionURL = null;
            
            if (contributionURLString == null){
                // find the current directory as a URL. This is where our contribution 
                // will come from
                contributionURL = Thread.currentThread().getContextClassLoader().getResource(nodeName + "/");
            } else {
                contributionURL = new URL(contributionURLString);
            }
         
            loadContribution(domainName, contributionURL);
        
        } catch(Exception ex) {
            System.err.println("Exception when adding contribution " + ex.getMessage());
            ex.printStackTrace(System.err);
            domain = null;
        }         
        
    }
    
    public void start(){
        
        // start the domain composite
        try {        
            for (Composite composite : domain.getDomainComposite().getIncludes() ){
                domain.getCompositeActivator().start(composite);
            }
        } catch(Exception ex) {
            System.err.println("Exception when loading contribution " + ex.getMessage());
            ex.printStackTrace(System.err);
            domain = null;
        } 
    }

    public void stop(){
        try {
            domain.stop();
        } catch(Exception ex) {
            System.err.println("Exception when stopping domain " + ex.getMessage());
            ex.printStackTrace(System.err);
            domain = null;
        }        
    }
    
}
