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

import java.util.logging.Logger;

import org.apache.tuscany.sca.domain.DomainException;
import org.apache.tuscany.sca.domain.spi.SCADomainAPIService;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;


/**
 * Stores details of services exposed and retrieves details of remote services
 * 
 * @version $Rev: 552343 $ $Date: 2007-09-07 12:41:52 +0100 (Fri, 07 Sep 2007) $
 */
@Scope("COMPOSITE")
public class SCADomainAPIServiceProxyImpl implements SCADomainAPIService{
    
    private static final Logger logger = Logger.getLogger(SCADomainAPIServiceProxyImpl.class.getName());    
    
    @Reference
    protected SCADomainAPIService domainManager;

    public void start() throws DomainException {
        domainManager.start();
    }

    public void stop() throws DomainException {
        domainManager.stop();
    }

    public void destroyDomain() throws DomainException {
        domainManager.destroyDomain();
    }

    public String getURI() {
        return domainManager.getURI();
    }
     
    public void addContribution(String contributionURI, String contributionURL) throws DomainException {
        domainManager.addContribution(contributionURI, contributionURL);
    }
    
    public void updateContribution(String contributionURI, String contributionURL) throws DomainException {
        domainManager.updateContribution(contributionURI, contributionURL);
    }
    
    public void removeContribution(String contributionURI) throws DomainException {
        domainManager.removeContribution(contributionURI);
    }
    
    public void addDeploymentComposite(String contributionURI, String compositeXML) throws DomainException {
        domainManager.addDeploymentComposite(contributionURI, compositeXML);
    }
    
    public void updateDeploymentComposite(String contributionURI, String compositeXML) throws DomainException {
        domainManager.updateDeploymentComposite(contributionURI, compositeXML);
    }

    public void addToDomainLevelComposite(String compositeQName) throws DomainException {
        domainManager.addToDomainLevelComposite(compositeQName);
    }
    
    public void removeFromDomainLevelComposite(String compositeQName) throws DomainException {
        domainManager.removeFromDomainLevelComposite(compositeQName);
    }
    
    public String getDomainLevelComposite() throws DomainException {
        return domainManager.getDomainLevelComposite();
    }
    
    public String getQNameDefinition(String artifact) throws DomainException {
        return domainManager.getQNameDefinition(artifact);
    }
    
    public void startComposite(String compositeQName) throws DomainException {
        domainManager.startComposite(compositeQName);
    }
    
    public void stopComposite(String compositeQName) throws DomainException {
        domainManager.stopComposite(compositeQName);
    }
    
}
