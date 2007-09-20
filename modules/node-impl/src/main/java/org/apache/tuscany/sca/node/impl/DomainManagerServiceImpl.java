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

import java.lang.reflect.UndeclaredThrowableException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tuscany.sca.domain.DomainManagerService;
import org.apache.tuscany.sca.domain.NodeInfo;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;


/**
 * Stores details of services exposed and retrieves details of remote services
 * 
 * @version $Rev: 552343 $ $Date: 2007-09-07 12:41:52 +0100 (Fri, 07 Sep 2007) $
 */
@Scope("COMPOSITE")
public class DomainManagerServiceImpl implements DomainManagerService{
    
    private final static Logger logger = Logger.getLogger(DomainManagerServiceImpl.class.getName());    
    
    @Property
    protected int retryCount = 100;
    
    @Property 
    protected int retryInterval = 5000; //ms    
    
    @Reference
    protected DomainManagerService domainManager;

    public String registerNode(String domainUri, String nodeUri) {
        
        String returnValue = null;
        
        for (int i =0; i < retryCount; i++){
            try {        
                returnValue =  domainManager.registerNode(domainUri, nodeUri);
                break;
            } catch(UndeclaredThrowableException ex) {
                logger.log(Level.INFO, "Trying to regsiter node " + 
                                       nodeUri + 
                                       " with domain " +
                                       domainUri);
          
            }
            
            try {
                Thread.sleep(retryInterval);
            } catch(InterruptedException ex) {
            }
         }
        
        return returnValue;
    }

    public String removeNode(String domainUri, String nodeUri) {
        return domainManager.removeNode(domainUri, nodeUri);
    }
    
    public List<NodeInfo> getNodeInfo(){
        return domainManager.getNodeInfo();
    }
}
