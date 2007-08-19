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

package org.apache.tuscany.sca.distributed.domain;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.distributed.management.ServiceDiscovery;


/**
 * A local representation of the sca domain distributed across a number
 * of separate nodes. This provides access to various information relating
 * to the distributed domain
 * 
 * @version $Rev: 552343 $ $Date: 2007-07-01 18:43:40 +0100 (Sun, 01 Jul 2007) $
 */
public abstract interface DistributedSCADomain {
    
    /**
     * Returns the name of the node that this part of the
     * distributed domain is running on
     * 
     * @return the node name
     */
    public abstract String getNodeName();
    public abstract void setNodeName(String nodeName);    
    
    /**
     * Returns the name of the distributed domain that this node
     * is part of.
     * 
     * @return the domain name
     */
    public abstract String getDomainName();
    public abstract void setDomainName(String domainName);
    
    /**
     * Associates this distributed domain representation to all of the 
     * sca binding objects within a composite. The sca binding uses this
     * distributed domain representation for domain level operations like
     * find the enpoints of remote services. 
     * 
     * @param composite the composite that this object will be added to 
     */    
    public void addDistributedDomainToBindings(Composite composite);
    
    /**
     * Return an interface for registering and looking up remote services
     * 
     * @return The service discovery interface
     */
    public abstract ServiceDiscovery getServiceDiscovery();
    
}
