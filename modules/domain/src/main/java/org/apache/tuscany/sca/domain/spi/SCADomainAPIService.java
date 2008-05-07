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

package org.apache.tuscany.sca.domain.spi;

import org.apache.tuscany.sca.domain.DomainException;
import org.osoa.sca.annotations.Remotable;


/**
 * Represents an SCA domain API to be accessed remotely
 * 
 * @version $Rev: 580520 $ $Date: 2007-09-29 00:50:25 +0100 (Sat, 29 Sep 2007) $
 */
@Remotable
public interface SCADomainAPIService {
    
    /**
     * Start all of the services in the domain.
     */
    void start() 
      throws DomainException;  

    /**
     * Stop all of the services in the domain.
     */
    void stop()
      throws DomainException; 

    /**
     * Destroy the SCA domain.
     */
    void destroyDomain()
      throws DomainException;

    /**
     * Returns the URI of the SCA Domain. That URI is the endpoint of the
     * SCA domain administration service.
     * 
     * @return the URI of the SCA Domain
     */
    String getURI();
    
    /**
     * Add an SCA contribution to the domain.
     *  
     * @param contributionURI the URI of the contribution
     * @param contributionURL the URL of the contribution
     * @throws DomainException
     */  
    void addContribution(String contributionURI, String contributionURL)
      throws DomainException;
    
    /**
     * Update an SCA contribution that has previously been added to the domain.
     *  
     * @param contributionURI the URI of the contribution
     * @param contributionURL the URL of the contribution
     * @throws DomainException
     */  
    void updateContribution(String contributionURI, String contributionURL)
      throws DomainException;    
    
    /**
     * Remove a contribution from the domain.
     * 
     * @param contributionURI the URI of the contribution
     * @throws DomainException
     */
    void removeContribution(String contributionURI)
      throws DomainException; 
    
    /**
     * Add the supplied composite XML to the identified contribution
     * 
     * @param contributionURI the URI of the contribution
     * @param compositeXML the XML string of the composite 
     * @throws DomainException
     */
    void addDeploymentComposite(String contributionURI, String compositeXML)
      throws DomainException;
    
    /**
     * Use the supplied composite XML to update the identified contribution
     * 
     * @param contributionURI the URI of the contribution
     * @param compositeXML the XML string of the composite 
     * @throws DomainException
     */
    void updateDeploymentComposite(String contributionURI, String compositeXML)
      throws DomainException;    

    /**
     * Add a deployable composite to the domain.
     * 
     * @param compositeQName the QName of the composite
     * @throws DomainException     
     */
    void addToDomainLevelComposite(String compositeQName)
      throws DomainException;
    
    /**
     * Remove a deployable composite from the domain.
     * 
     * @param compositeQName the QName of the composite
     * @throws DomainException     
     */
    void removeFromDomainLevelComposite(String compositeQName)
      throws DomainException; 
    
    /**
     * Returns an XML string representation of the domain level composite
     * 
     * @return XML representing the domain level composite
     */
    String getDomainLevelComposite()
      throws DomainException;
    
    /**
     * Returns an XML String representation of a artifact from within the 
     * domain namespace formed by the domain level composite
     * 
     * @return XML representing the specified artifact
     */
    String getQNameDefinition(String artifact)
      throws DomainException;
    
    /**
     * Start a composite. The domain is responsible for starting all the
     * components in the composite. It may decompose the composite into
     * one or more smaller composites and load these composites into
     * an appropriate SCA node for execution.
     * 
     * @param compositeQName The QName of the composite
     * @throws DomainException
     */
    void startComposite(String compositeQName)
      throws DomainException;
    
    /**
     * Stop a composite. The domain will stop all the components from the
     * specified composite.
     * 
     * @param compositeQName The QName of the composite
     * @throws DomainException
     */
    void stopComposite(String compositeQName)
      throws DomainException;
}