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

package org.apache.tuscany.sca.domain.model.impl;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.domain.model.CompositeModel;
import org.apache.tuscany.sca.domain.model.ContributionModel;
import org.apache.tuscany.sca.domain.model.NodeModel;
import org.apache.tuscany.sca.domain.model.DomainModel;


/**
 * A model of the domain and the artifacts that it managers. Acts as a holder for the 
 * various other Tuscany models involved
 * 
 * @version $Rev: 552343 $ $Date: 2007-09-07 12:41:52 +0100 (Fri, 07 Sep 2007) $
 */
public class DomainModelImpl implements DomainModel {
    
    private String domainURI;
    private String domainURL;
    private Map<String, NodeModel> nodes = new HashMap<String, NodeModel>();
    private Map<String, ContributionModel> contributions = new HashMap<String, ContributionModel>();    
    private Map<QName, CompositeModel> deployedComposites = new HashMap<QName, CompositeModel>();
    private Composite domainLevelComposite;
       
    
    /**
     * Retrieve the domain URI
     * 
     * @return domain URI
     */
    public String getDomainURI(){
        return domainURI;
    }
    
    /**
     * Set the domain URI
     * 
     * @param domainURI
     */    
    public void setDomainURI(String domainURI){
        this.domainURI = domainURI;
    }
    
    /**
     * Retrieve the domain URL
     * 
     * @return domain URL
     */    
    public String getDomainURL(){
        return domainURL;
    }
    
    /**
     * Set the domain level composite
     * 
     * @param domainLevelComposite
     */    
    public void setDomainLevelComposite(Composite domainLevelComposite){
        this.domainLevelComposite = domainLevelComposite;
    }
    
    /**
     * Retrieve the domain level composite
     * 
     * @return domainLevelComposite 
     */    
    public Composite getDomainLevelComposite(){
        return domainLevelComposite;
    }
   
    /**
     * Set the domain URL
     * 
     * @param domainURL
     */    
    public void setDomainURL(String domainURL){
        this.domainURL = domainURL;
    }
   
    public Map<String, NodeModel> getNodes(){
        return nodes;
    }
    
    public Map<String, ContributionModel> getContributions(){
        return contributions;
    }
    
    public Map<QName, CompositeModel> getDeployedComposites(){
        return deployedComposites;
    }    
}
