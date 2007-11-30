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

import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.contribution.Contribution;

/**
 * A contribution.
 * 
 * @version $Rev: 552343 $ $Date: 2007-09-07 12:41:52 +0100 (Fri, 07 Sep 2007) $
 */
public interface ContributionModel {
    
    /**
     * Retrieve the contribution object
     * 
     * @return contribution 
     */
    public Contribution getContribution();
    
    /**
     * Set the contribution object
     * 
     * @param contribution
     */    
    public void setContribution(Contribution contribution);        
    
    /**
     * Retrieve the contribution uri
     * 
     * @return contribution uri
     */
    public String getContributionURI();
    
    /**
     * Set the contribution uri
     * 
     * @param contributionURI
     */    
    public void setContributionURI(String contributionURI);    
    
    /**
     * Retrieve the contribution url
     * 
     * @return contribution url
     */    
    public String getContributionURL();
   
    /**
     * Set the contribution url
     * 
     * @param contributionURL
     */    
    public void setContributionURL(String contributionURL);
    
    public Map<QName, CompositeModel> getComposites();       
    public Map<QName, CompositeModel> getDeployableComposites(); 
    public Map<QName, CompositeModel> getDeployedComposites(); 
}
