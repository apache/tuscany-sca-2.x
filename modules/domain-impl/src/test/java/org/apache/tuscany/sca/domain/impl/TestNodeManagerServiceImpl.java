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

package org.apache.tuscany.sca.domain.impl;

import org.apache.tuscany.sca.databinding.impl.XSDDataTypeConverter.Base64Binary;
import org.apache.tuscany.sca.node.NodeException;
import org.apache.tuscany.sca.node.management.SCANodeManagerService;

/**
 * A dummy interface for the domain to talk to when testing the domain and no nodes are running
 * 
 * @version $Rev: 552343 $ $Date: 2007-09-11 18:45:36 +0100 (Tue, 11 Sep 2007) $
 */
public class TestNodeManagerServiceImpl implements SCANodeManagerService {


    
    // SCANodeManagerService methods
    
    public String getURI() {
        return null;
    }
   
    public void addContribution(String contributionURI, String contributionURL) throws NodeException {
        System.out.println("addContribution " + contributionURI + " " + contributionURL);           
    }
   
    public void removeContribution(String contributionURI) throws NodeException {
        System.out.println("addContribution " + contributionURI);
    }

    public void addToDomainLevelComposite(String compositeName) throws NodeException {
        System.out.println("addToDomainLevelComposite " + compositeName);     
    }
    
    public void start() throws NodeException {
        System.out.println("start");
    }
    
    public void stop() throws NodeException {
        System.out.println("stop");
    }
    
    public void destroyNode() {
        System.out.println("destroy");
    } 
    
    public void updateComposite(String compositeQName, String compositeXMLBase64) throws NodeException {
        System.out.println("updateComposite " + compositeQName + " " + Base64Binary.decode(compositeXMLBase64).toString());
    }
}
