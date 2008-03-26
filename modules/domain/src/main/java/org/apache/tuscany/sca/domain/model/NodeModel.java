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

package org.apache.tuscany.sca.domain.model;

import java.io.Externalizable;
import java.util.Map;

import javax.xml.namespace.QName;


/**
 * A node. Runs SCA composites
 * 
 * @version $Rev: 552343 $ $Date: 2007-09-07 12:41:52 +0100 (Fri, 07 Sep 2007) $
 */
public interface NodeModel {
    public enum LifecyleState {AVAILABLE, DEPLOYED, RUNNING, UNAVAILABLE }; 
    
    /**
     * Retrieve the node URI
     * 
     * @return node URI
     */
    String getNodeURI();
    
    /**
     * Set the node URI
     * 
     * @param nodeURI
     */    
    void setNodeURI(String nodeURI);    
    
    /**
     * Retrieve the node URL
     *
     * @return node URL
     */    
    String getNodeURL();
   
    /**
     * Set the node URL
     * 
     * @param nodeURL
     */    
    void setNodeURL(String nodeURL);
    
    /**
     * Returns the state of the node
     *
     * @return state
     */    
    LifecyleState getLifecycleState();
   
    /**
     * Set the state of the node
     * 
     * @param state
     */    
    void setLifecycleState(LifecyleState state);    
    
    /**
     * Retrieve the node manager reference
     *
     * @return node manager reference
     */    
    Externalizable getNodeManagerReference();
   
    /**
     * Set the node URL
     * 
     * @param nodeURL
     */    
    void setNodeManagerReference(Externalizable nodeManagerReference);    
   
    Map<String, ContributionModel> getContributions();
    Map<QName, CompositeModel> getDeployedComposites();
    Map<String, ServiceModel> getServices();
}
