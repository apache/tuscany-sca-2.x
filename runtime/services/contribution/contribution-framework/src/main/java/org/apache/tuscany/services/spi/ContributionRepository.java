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

package org.apache.tuscany.services.contribution.spi;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.List;

public interface ContributionRepository {
    /**
     * Get the URI of the SCA domain 
     * 
     * @return
     */
    URI getDomain();
    
    /**
     * Copies a contribution to the repository.
     * 
     * @param contribution A URl pointing to the contribution being copied to
     *            the repository
     * @param contributionStream InputStream with the content of the
     *            distribution
     */
    URL store(URI contribution, InputStream contributionStream) throws IOException;
    /**
     * Copy a contribution from the source URL to the repository
     * @param contribution
     * @param sourceURL
     * @return
     * @throws IOException
     */
    URL store(URI contribution, URL sourceURL) throws IOException;

    /**
     * Look up the contribution by URI
     * 
     * @param contribution The URI of the contribution
     * @return A URL pointing to the content of the contribution in the
     *         repository, it will be null if the contribution cannot be found
     *         in the repository
     */
    URL find(URI contribution);

    /**
     * Remove a contribution from the repository
     * 
     * @param contribution The URI of the contribution to be removed
     */
    void remove(URI contribution);

    /**
     * Get list of URIs for all the contributions in the repository
     * 
     * @return A list of contribution URIs
     */
    List<URI> list();
}
