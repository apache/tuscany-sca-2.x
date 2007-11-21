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

package org.apache.tuscany.sca.node;

import java.net.URL;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.domain.SCADomain;

/**
 * Represents an SCA processing node. An SCA node belongs to an SCA domain.
 * A node is loaded with one or mode SCA composites. It can start and stop that composite. 
 * 
 * @version $Rev: 580520 $ $Date: 2007-09-29 00:50:25 +0100 (Sat, 29 Sep 2007) $
 */
public interface SCANode {



    /**
     * Returns the URI of the SCA node. That URI is the endpoint of the
     * SCA node administration service.
     * 
     * @return the URI of the SCA node
     */
    public String getURI();
    
    /**
     * Returns the SCA domain that the node belongs to.
     * 
     * @return the SCA domain that the node belongs to 
     */
    public SCADomain getDomain(); 
    
    /**
     * Add an SCA contribution into the node.
     *  
     * @param contributionURI the URI of the contribution
     * @param contributionURL the URL of the contribution
     */
    public void addContribution(String contributionURI, URL contributionURL) throws NodeException;
   
    /**
     * Remove an SCA contribution from the node.
     *  
     * @param contributionURI the URI of the contribution
     */
    public void removeContribution(String contributionURI) throws NodeException;

    /**
     * Add the named deployable composite to the domain level composite
     * 
     * @param compositeQName the name of the composite
     */
    public void addToDomainLevelComposite(QName compositeQName) throws NodeException;
    
    /**
     * Add the specified deployable composite to the domain level composite
     * 
     * @param compositePath the path of the composite file
     */
    public void addToDomainLevelComposite(String compositePath) throws NodeException;
    
    /**
     * Start all the deployed composites
     */
    public void start() throws NodeException;    
    
    /**
     * Stop all of the deployed composites
     */
    public void stop() throws NodeException;    

    /**
     * Destroy the node.
     */
    public void destroy() throws NodeException;    
    
        
}
