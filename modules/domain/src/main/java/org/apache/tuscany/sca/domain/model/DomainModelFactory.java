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

package org.apache.tuscany.sca.domain.model;

/**
 * A node. Runs SCA composites
 * 
 * @version $Rev: 552343 $ $Date: 2007-09-07 12:41:52 +0100 (Fri, 07 Sep 2007) $
 */
public interface DomainModelFactory {
    
    /**
     * Create a new domain model
     * 
     * @return new domain model
     */
    public DomainModel createDomain();
   
    /**
     * Create a new node model
     * 
     * @return new node model
     */
    public NodeModel createNode();  
    
    /**
     * Create a new contribution model
     * 
     * @return new contribution model
     */
    public ContributionModel createContribution();    
    
    /**
     * Create a new composite model
     * 
     * @return new composite model
     */
    public CompositeModel createComposite();   
    
    /**
     * Create a new service model
     * 
     * @return new service model
     */
    public ServiceModel createService();     

}
