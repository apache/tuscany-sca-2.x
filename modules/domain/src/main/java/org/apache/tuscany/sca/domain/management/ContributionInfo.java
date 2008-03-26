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

package org.apache.tuscany.sca.domain.management;

import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;


/**
 * A collection of info for a contribution
 * 
 * @version $Rev: 552343 $ $Date: 2007-09-07 12:41:52 +0100 (Fri, 07 Sep 2007) $
 */
public interface ContributionInfo {
    
    
    /**
     * Retrieve the contribution URI
     * 
     * @return contribution URI
     */
    String getContributionURI();
    
    /**
     * Set the contribution URI
     * 
     * @param contributionURI
     */    
    void setContributionURI(String contributionURI);    
    
    /**
     * Retrieve the contribution URL
     * 
     * @return contribution URL
     */    
    URL getContributionURL();
   
    /**
     * Set the contribution URL
     * 
     * @param contributionURL
     */    
    void setContributionURL(URL contributionURL);
    
    List<QName> getComposites();       
    List<QName> getDeployableComposites();
 
}
