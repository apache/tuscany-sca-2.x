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

import org.osoa.sca.annotations.Remotable;


/**
 * A collection of info for a registered node
 * 
 * @version $Rev: 552343 $ $Date$
 */
public interface NodeInfo {
    
    /**
     * Retrieve the domain uri
     * 
     * @return domain uri
     */
    public String getDomainUri();
    
    /**
     * Retrieve the node uri
     * 
     * @return node uri
     */    
    public String getNodeUri();
    
    /**
     * Ser the node manager url
     * 
     * @param nodeManagerUrl
     */    
    public void setNodeManagerUrl(String nodeManagerUrl);
    
    /**
     * Retrieve the node manager url
     * 
     * @return node manager url
     */     
    public String getNodeManagerUrl();
    
    /**
     * Returns true if this node info object matches the provided data
     */
    public boolean match(String domainUri, String nodeUri);
      
    /** 
     * Returns a string representation of the information for a service
     * 
     * @return
     */
    public String toString();
 
}
