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

package org.apache.tuscany.sca.contribution.service;

import java.util.EventListener;

import org.apache.tuscany.sca.contribution.Contribution;

/**
 * A listener interface used to listen to contribution repository events.
 *
 * @version $Rev$ $Date$
 */
public interface ContributionListener extends EventListener {

    /**
     * Notifies the listener that a contribution has been added.
     * 
     * @param repository The contribution repository 
     * @param contribution The new contribution
     */
    void contributionAdded(ContributionRepository repository, Contribution contribution);
    
    /**
     * Notifies the listener that a contribution has been removed.
     * 
     * @param repository The contribution repository 
     * @param contribution The removed contribution.
     */
    void contributionRemoved(ContributionRepository repository, Contribution contribution);
    
    /**
     * Notifies the listener that a contribution has been updated.
     * 
     * @param repository The contribution repository 
     * @param oldContribution The old contribution 
     * @param contribution The new contribution
     */
    void contributionUpdated(ContributionRepository repository, Contribution oldContribution, Contribution contribution);

}
