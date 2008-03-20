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

import org.apache.tuscany.sca.domain.model.ServiceModel;

/**
 * A service.
 * 
 * @version $Rev: 552343 $ $Date: 2007-09-07 12:41:52 +0100 (Fri, 07 Sep 2007) $
 */
public class ServiceModelImpl implements ServiceModel {
    
    private String serviceURI;
    private String serviceURL;
    private String serviceBinding;
    
    /**
     * Retrieve the service URI
     * 
     * @return service URI
     */
    public String getServiceURI(){
        return serviceURI;
    }
    
    /**
     * Set the service URI
     * 
     * @param serviceURI
     */    
    public void setServiceURI(String serviceURI){
        this.serviceURI = serviceURI;
    }
    
    /**
     * Retrieve the service URL
     * 
     * @return service URL
     */    
    public String getServiceURL(){
        return serviceURL;
    }
   
    /**
     * Set the service URL
     * 
     * @param serviceURL
     */    
    public void setServiceURL(String serviceURL){
        this.serviceURL = serviceURL;
    }
    
   
    /**
     * Retrieve the service binding
     * 
     * @return service binding
     */    
    public String getServiceBinding(){
        return serviceBinding;
    }
   
    /**
     * Set the service binding
     * 
     * @param serviceBinding
     */    
    public void setServiceBinding(String serviceBinding){
        this.serviceBinding = serviceBinding;
    }
}
