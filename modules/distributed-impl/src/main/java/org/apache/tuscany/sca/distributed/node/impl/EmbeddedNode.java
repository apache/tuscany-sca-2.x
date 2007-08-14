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

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.binding.sca.impl.SCABindingImpl;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.distributed.domain.DistributedSCADomain;
import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.apache.tuscany.sca.host.embedded.impl.EmbeddedSCADomain;

/**
 * This client program shows how to run a distributed SCA node. In this case a 
 * calculator node has been constructed specifically for running the calculator 
 * composite. 
 */
public class EmbeddedNode {
    
    private String nodeName;
    private String domainName; 
    private EmbeddedSCADomain domain;
    
    // should be a collection but assuming only one domain
    // per node for now
    private DistributedSCADomain distributedDomain;
    
    public EmbeddedNode(String nodeName){
        this.nodeName = nodeName;
    }
    
    public SCADomain createDomain(DistributedSCADomain distributedDomain){
        this.distributedDomain = distributedDomain;
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
        
        // add node information into the distributed domain
        this.distributedDomain.setNodeName(nodeName);
        
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
                File currentDirectory = new File (".");
                contributionURL = new URL("file:/" + currentDirectory.getCanonicalPath() + "/src/test/resources/" + nodeName + "/");
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
