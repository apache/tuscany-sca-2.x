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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.List;

import org.apache.tuscany.sca.contribution.Contribution;

/**
 * Contribution repository 
 * 
 * @version $Rev$ $Date$
 */
public interface ContributionRepository {
    
    /**
     * Get the URI of the SCA domain 
     * 
     * @return The domain URI
     */
    URI getDomain();
    
    /**
     * Copies a contribution to the repository.
     * 
     * @param contribution A URl pointing to the contribution being copied to
     *            the repository
     * @param sourceURL url of the source. this would be used to calculate the right
     *            filename to be stored on the repository when a inputStream is being
     *            provided
     * @param contributionStream InputStream with the content of the
     *            distribution
     * @return A URL pointing to the content of the contribution in the
     *         repository
     * @throws IOException
     */
    URL store(String contribution, URL sourceURL, InputStream contributionStream) throws IOException;
    /**
     * Copy a contribution from the source URL to the repository
     * 
     * @param contribution A URl pointing to the contribution being copied to
     *            the repository
     * @param sourceURL url of the source. this would be used to calculate the right
     *            filename to be stored on the repository when a inputStream is being
     *            provided
     * @return A URL pointing to the content of the contribution in the
     *         repository
     * @throws IOException
     */
    URL store(String contribution, URL sourceURL) throws IOException;

    /**
     * Look up the contribution by URI
     * 
     * @param contribution The URI of the contribution
     * @return A URL pointing to the content of the contribution in the
     *         repository, it will be null if the contribution cannot be found
     *         in the repository
     */
    URL find(String contribution);

    /**
     * Remove a contribution from the repository
     * 
     * @param contribution The URI of the contribution to be removed
     */
    void remove(String contribution);

    /**
     * Get list of URIs for all the contributions in the repository
     * 
     * @return A list of contribution URIs
     */
    List<String> list();
    
    /**
     * Returns the contributions available in the repository.
     * @return The list of contributions.
     */
    List<Contribution> getContributions();
    
    /**
     * Adds a contribution to the repository.
     * @param contribution The new contribution.
     */
    void addContribution(Contribution contribution);
    
    /**
     * Removes a contribution from the repository.
     * @param contribution The contribution to remove
     */
    void removeContribution(Contribution contribution);
    
    /**
     * Updates a contribution in the repository
     * @param contribution The contribution to update
     */
    void updateContribution(Contribution contribution);
    
    /**
     * Returns the contribution with the given URI.
     * @param uri the URI of the contribution
     * @return The contribution
     */
    Contribution getContribution(String uri);

}
