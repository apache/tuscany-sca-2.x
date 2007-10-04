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
import org.osoa.sca.annotations.Remotable;


/**
 * The management interface for a node
 * 
 * @version $Rev: 552343 $ $Date: 2007-09-07 12:41:52 +0100 (Fri, 07 Sep 2007) $
 */
@Remotable
public interface NodeManagerService {
     
    /**
     * Returns the URI of the SCA node. That URI is the endpoint of the
     * SCA node administration service.
     * 
     * @return the URI of the SCA node
     */
    public String getURI();
    
    /**
     * Add an SCA contribution into the node.
     *  
     * @param uri the URI of the contribution
     * @param url the URL of the contribution
     */
    public void addContribution(String contributionURI, String contributionURL);
   
    /**
     * Start the specified deployable composite on the node.
     * 
     * @param composite
     */
    public void startComposite(String compositeName);
    
    /**
     * Start the SCA node service.
     */
    public void start();    
    
    /**
     * Stop the SCA node service.
     */
    public void stop();    
 
}
