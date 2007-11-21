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

package org.apache.tuscany.sca.domain.management.impl;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.domain.management.DomainInfo;
import org.apache.tuscany.sca.domain.model.CompositeModel;
import org.apache.tuscany.sca.domain.model.ContributionModel;
import org.apache.tuscany.sca.domain.model.NodeModel;

/**
 *  A data transport object for the management interface
 * 
 * @version $Rev: 552343 $ $Date: 2007-09-07 12:41:52 +0100 (Fri, 07 Sep 2007) $
 */
public class DomainInfoImpl implements DomainInfo, Serializable {
    
    static final long serialVersionUID = 7669181086005969428L;    
    
    private String domainURI;
    private String domainURL;
    private List<String> nodes = new ArrayList<String>();
    private List<String> contributions = new ArrayList<String>();
    private List<QName> composites = new ArrayList<QName>();
    
    /**
     * Retrieve the domain uri
     * 
     * @return domain uri
     */
    public String getDomainURI(){
        return domainURI;
    }
    
    /**
     * Set the domain uri
     * 
     * @param domainURI
     */    
    public void setDomainURI(String domainURI){
        this.domainURI = domainURI;
    }
    
    /**
     * Retrieve the domain url
     * 
     * @return domain url
     */    
    public String getDomainURL(){
        return domainURL;
    }
   
    /**
     * Set the domain url
     * 
     * @param domainURL
     */    
    public void setDomainURL(String domainURL){
        this.domainURL = domainURL;
    }
   
    public List<String> getNodes(){
        return nodes;
    }
    
    public List<String> getContributions(){
        return contributions;
    }
    
    public List<QName> getDeployedComposites(){
        return composites;
    }   
    
}
