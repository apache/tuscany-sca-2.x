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

import org.apache.tuscany.sca.distributed.management.ServiceDiscovery;
import org.apache.tuscany.sca.distributed.management.impl.ServiceDiscoveryMemoryImpl;
import org.apache.tuscany.sca.host.embedded.SCADomain;

/**
 * A local representation of the sca domain distributed across a number
 * of separate nodes. This provides access to various information relating
 * to the distributed domain
 * 
 * @version $Rev: 552343 $ $Date: 2007-07-01 18:43:40 +0100 (Sun, 01 Jul 2007) $
 */
public class DistributedSCADomainMemoryImpl extends DistributedSCADomainImpl {
    
    private static ServiceDiscovery serviceDiscovery;
    
    public DistributedSCADomainMemoryImpl(String domainName){
        super(domainName); 
        
        if (serviceDiscovery == null) {
            serviceDiscovery = new ServiceDiscoveryMemoryImpl();
        }
    }
    
    public ServiceDiscovery getServiceDiscovery(){
        return serviceDiscovery;
    }
    
}
