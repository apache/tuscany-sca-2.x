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

package org.apache.tuscany.sca.distributed.domain.impl;

import java.io.Serializable;

import org.apache.tuscany.sca.distributed.domain.ServiceInfo;

/**
 * Information relating to an exposed service
 * 
 * @version $Rev: 552343 $ $Date$
 */
public class ServiceInfoImpl implements ServiceInfo, Serializable {
    
    private String domainUri;
    private String nodeUri;
    private String serviceName;
    private String bindingName;
    private String url;
    
    public ServiceInfoImpl(String domainUri, String nodeUri, String serviceName, String bindingName, String URL){
        this.domainUri = domainUri;
        this.nodeUri = nodeUri;
        this.serviceName = serviceName;
        this.bindingName = bindingName;
        this.url = URL;
    }
    
    public boolean match(String domainUri, String serviceName, String bindingName) {
        // trap the case where the we are trying to map
        //   ComponentName/Service name with a registered ComponentName             - this is OK
        //   ComponentName              with a registered ComponentName/ServiceName - this should fail
        
        boolean serviceNameMatch = false;
        
        if (this.serviceName.equals(serviceName)) {
            serviceNameMatch = true;
        } else {
            int s = serviceName.indexOf('/');
            if ((s != -1) &&
                (this.serviceName.equals(serviceName.substring(0, s)))){
                serviceNameMatch = true;
            }
        }
        
        return ((this.domainUri.equals(domainUri)) &&
                (serviceNameMatch) &&
                (this.bindingName.equals(bindingName)));
    }
    
    public String getUrl() {
        return url;
    }     
    
    @Override
    public String toString (){
        return "[" +
               domainUri + " " +
               nodeUri + " " +
               serviceName + " " +
               bindingName + " " + 
               url +
               "]";
    }
    
}
