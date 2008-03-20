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

import java.io.Externalizable;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.domain.model.CompositeModel;
import org.apache.tuscany.sca.domain.model.ContributionModel;
import org.apache.tuscany.sca.domain.model.NodeModel;
import org.apache.tuscany.sca.domain.model.ServiceModel;
import org.apache.tuscany.sca.node.management.SCANodeManagerService;
import org.osoa.sca.CallableReference;


/**
 * A node. Runs SCA composites
 * 
 * @version $Rev: 552343 $ $Date: 2007-09-07 12:41:52 +0100 (Fri, 07 Sep 2007) $
 */
public class NodeModelImpl implements NodeModel {
    
    private LifecyleState state = LifecyleState.AVAILABLE;
    private String nodeURI;
    private String nodeURL;
    private Externalizable nodeManagerReference;
    private Map<String, ContributionModel> contributions = new HashMap<String, ContributionModel>();
    private Map<QName, CompositeModel> deployedComposites = new HashMap<QName, CompositeModel>();
    private Map<String, ServiceModel> services = new HashMap<String, ServiceModel>();
    
    /**
     * Retrieve the node URI
     * 
     * @return node URI
     */
    public String getNodeURI(){
        return nodeURI;
    }
    
    /**
     * Set the node URI
     * 
     * @param nodeURI
     */    
    public void setNodeURI(String nodeURI){
        this.nodeURI = nodeURI;
    }
    
    /**
     * Retrieve the node URL
     *
     * @return node URL
     */    
    public String getNodeURL() {
        return nodeURL;
    }
   
    /**
     * Set the node URL
     * 
     * @param nodeURL
     */    
    public void setNodeURL(String nodeURL){
        this.nodeURL = nodeURL;
    }
    
    /**
     * Returns the state of the node
     *
     * @return state
     */    
    public LifecyleState getLifecycleState(){
        return state;
    }
   
    /**
     * Set the state of the node
     * 
     * @param state
     */    
    public void setLifecycleState(LifecyleState state){
        this.state = state;
    }
    
    /**
     * Retrieve the node manager reference
     *
     * @return node manager reference
     */    
    public Externalizable getNodeManagerReference(){
        return nodeManagerReference;
    }
   
    /**
     * Set the node URL
     * 
     * @param nodeURL
     */    
    public void setNodeManagerReference(Externalizable nodeManagerReference){
        this.nodeManagerReference = nodeManagerReference;
    }
    
    /** 
     * Get the service proxy for the node manager
     * 
     * @return node manager service proxy
     */
    public SCANodeManagerService getSCANodeManagerService(){
        return ((CallableReference<SCANodeManagerService>)nodeManagerReference).getService();
    }
   
    public Map<String, ContributionModel> getContributions(){
        return contributions;
    }
    
    public Map<QName, CompositeModel> getDeployedComposites(){
        return deployedComposites;
    }
    
    public Map<String, ServiceModel> getServices(){
        return services;
    }
}
