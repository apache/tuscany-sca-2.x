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

import java.io.IOException;

import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.contribution.service.ContributionException;
import org.apache.tuscany.sca.core.assembly.ActivationException;


/**
 * A management interface for contributions in the distributed domain
 * 
 * @version $Rev: 552343 $ $Date$
 */
public interface ContributionManager {
       
    /**
     * Accepts a new contribution and passes it onto the domain implementation
     * 
     * @throws ActivationException
     */
    public void addContribution(String contributionLocation)
     throws ActivationException, ContributionException, IOException, CompositeBuilderException;
    
    /**
     * Removes the specified contribution from the domain
     * 
     * throws ActivationException
     */
    public void removeContribution(String contributionLocation)
      throws ActivationException, ContributionException;
    
    
    /**
     * starts the contribution
     * 
     * @throws ActivationException
     */
    public void startContribution(String contributionLocation)
      throws ActivationException, ContributionException, IOException, CompositeBuilderException;
    
    /**
     * stops the contribution
     * 
     * @throws ActivationException
     */
    public void stopContribution(String contributionLocation)
      throws ActivationException;
 
}
