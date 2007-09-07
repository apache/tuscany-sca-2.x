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

package org.apache.tuscany.sca.distributed.node;

import org.osoa.sca.annotations.Remotable;


/**
 * The management interface for a node
 * 
 * @version $Rev: 552343 $ $Date$
 */
@Remotable
public interface NodeManagerService {
     
    /** 
     * Return the Uri that identifies the node
     * 
     * @return
     */
    public String getNodeUri();
    
    /**
     * The configuration of a domain running on this node has changed.
     * It is the responsibility of the node to respond to this and retrieve
     * any relevent configuration changes 
     *  
     * @param domainUri the string uri for the distributed domain
     */
   // public void addContribution(String domainUri);   
 
}
