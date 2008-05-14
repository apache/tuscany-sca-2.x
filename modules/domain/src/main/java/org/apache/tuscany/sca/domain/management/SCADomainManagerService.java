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

import org.osoa.sca.annotations.Remotable;



/**
 * Connects the domain management operations
 * 
 * @version $Rev: 552343 $ $Date: 2007-09-11 18:45:36 +0100 (Tue, 11 Sep 2007) $
 */
@Remotable
public interface SCADomainManagerService {
     
    /**
     * Return description of the domain
     * 
     */
    DomainInfo getDomainDescription();
    
    /**
     * Return description of the node
     * 
     * @param nodeURI
     */
    NodeInfo getNodeDescription(String nodeURI); 
    
}
