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
package org.apache.tuscany.host.deployment;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

/**
 * Service interface that manages artifacts contributed to a Tuscany runtime.
 *
 * @version $Rev$ $Date$
 */
public interface ContributionService {
    /**
     * Contribute an artifact to the SCA Domain.
     * The type of the contribution is determined by the Content-Type of the resource
     * or, if that is undefined, by some implementation-specific means (such as
     * mapping an extension in the URL's path).
     *
     * @param contribution the location of the resource containing the artifact
     * @return a URI that uniquely identifies this contribution within the SCA Domain
     * @throws DeploymentException if there was a problem with the contribution
     * @throws IOException         if there was a problem reading the resource
     */
    URI contribute(URL contribution) throws DeploymentException, IOException;

    /**
     * Contribute an artifact to the SCA Domain.
     *
     * @param contribution a stream containing the resource being contributed; the stream will not be closed
     *                     but the read position after the call is undefined
     * @param contentType  the type of contribution being made; must be a valid Content-Type value
     *                     as specified by <a href="http://www.ietf.org/rfc/rfc2045.txt">RFC2045</a>
     *                     and must not be null
     * @return a URI that uniquely identifies this contribution within the SCA Domain
     * @throws DeploymentException if there was a problem with the contribution
     * @throws IOException         if there was a problem reading the stream
     */
    URI contribute(InputStream contribution, String contentType) throws DeploymentException, IOException;
}
