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

import org.apache.tuscany.sca.contribution.Contribution;

public class ExtensibleContributionListener implements ContributionListener {
    private final ContributionListenerExtensionPoint listeners;

    /**
     * Default constructor receiving the listener extension point
     * 
     * @param listeners
     */
    public ExtensibleContributionListener(ContributionListenerExtensionPoint listeners) {
        this.listeners = listeners;
    }

    /**
     * Process "contributionAdded" event to all resigtered listeners
     */
    public void contributionAdded(ContributionRepository repository, Contribution contribution) {
        for (ContributionListener listener : listeners.getContributionListeners()) {
            try {
                listener.contributionAdded(repository, contribution);
            } catch (Exception e) {
                // ignore, contiue to the next listener
            }
        }
    }

    /**
     * Process "contributionRemoved" event to all registered listeners
     */
    public void contributionRemoved(ContributionRepository repository, Contribution contribution) {
        for (ContributionListener listener : listeners.getContributionListeners()) {
            try {
                listener.contributionRemoved(repository, contribution);
            } catch (Exception e) {
                // ignore, contiue to the next listener
            }

        }
    }

    /**
     * Process "contributionUpdated" event to all registered listeners
     */
    public void contributionUpdated(ContributionRepository repository, Contribution oldContribution, Contribution contribution) {
        for (ContributionListener listener : listeners.getContributionListeners()) {
            try {
                listener.contributionUpdated(repository, oldContribution, contribution);
            } catch (Exception e) {
                // ignore, contiue to the next listener
            }

        }
    }

}
