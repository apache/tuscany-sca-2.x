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
import java.net.URL;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;

/**
 * Service interface that manages artifacts contributed to a Tuscany runtime.
 *
 * @version $Rev: 527398 $ $Date: 2007-04-10 23:43:31 -0700 (Tue, 10 Apr 2007) $
 */
public interface ContributionService {
    /**
     * Contribute an artifact to the SCA Domain. The type of the contribution is
     * determined by the Content-Type of the resource or, if that is undefined,
     * by some implementation-specific means (such as mapping an extension in
     * the URL's path).
     * 
     * @param contributionURI The URI that is used as the contribution unique ID. 
     * @param sourceURL The location of the resource containing the artifact
     * @param modelResolver The model resolver to use to resolve models in the
     *             scope of this contribution
     * @param storeInRepository Flag that identifies if you want to copy the
     *            contribution to the repository
     * @return The contribution model representing the contribution 
     * @throws DeploymentException if there was a problem with the contribution
     * @throws IOException if there was a problem reading the resource
     */
    Contribution contribute(String contributionURI, URL sourceURL, ModelResolver modelResolver, boolean storeInRepository) throws ContributionException,
        IOException;

    /**
     * Contribute an artifact to the SCA Domain.
     * 
     * @param contributionURI The URI that is used as the contribution unique ID.
     * @param sourceURL The location of the resource containing the artifact. 
     *            This is used to identify what name should be used when storing
     *            the contribution on the repository 
     * @param modelResolver The model resolver to use to resolve models in the
     *             scope of this contribution
     * @param contributionContent A stream containing the resource being
     *            contributed; the stream will not be closed but the read
     *            position after the call is undefined
     * @return The contribution model representing the contribution 
     * @throws DeploymentException if there was a problem with the contribution
     * @throws IOException if there was a problem reading the stream
     */
    Contribution contribute(String contributionURI, URL sourceURL, InputStream contributionContent, ModelResolver modelResolver)
        throws ContributionException, IOException;

    /**
     * Get the model for an installed contribution
     * 
     * @param contribution The URI of an installed contribution
     * @return The model for the contribution or null if there is no such
     *         contribution
     */
    Contribution getContribution(String contribution);

    /**
     * Adds or updates a deployment composite using a supplied composite
     * ("composite by value" - a data structure, not an existing resource in the
     * domain) to the contribution identified by a supplied contribution URI.
     * The added or updated deployment composite is given a relative URI that
     * matches the "name" attribute of the composite, with a ".composite"
     * suffix.
     * 
     * @param contribution The contribution to where 
     * @param composite
     * @throws ContributionException
     */
    void addDeploymentComposite(Contribution contribution, Composite composite) throws ContributionException;

    /**
     * Remove a contribution from the SCA domain
     * 
     * @param contribution The URI of the contribution
     * @throws DeploymentException
     */
    void remove(String contribution) throws ContributionException;

}