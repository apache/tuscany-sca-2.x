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


/**
 * Service interface that manages artifacts contributed to a Tuscany runtime.
 *
 * @version $Rev$ $Date$
 */
public interface ContributionService {
    /**
     * Contribute an artifact to the SCA Domain. The type of the contribution is
     * determined by the Content-Type of the resource or, if that is undefined,
     * by some implementation-specific means (such as mapping an extension in
     * the URL's path).
     * 
     * @param contributionURI The URI that is used as the contribution unique ID. 
     * @param sourceURL the location of the resource containing the artifact
     * @param storeInRepository flag that identifies if you want to copy the
     *            contribution to the repository
     * @throws DeploymentException if there was a problem with the contribution
     * @throws IOException if there was a problem reading the resource
     */
    void contribute(URI contributionURI, URL sourceURL, boolean storeInRepository) throws ContributionException,
        IOException;

    /**
     * Contribute an artifact to the SCA Domain.
     * 
     * @param contributionURI The URI that is used as the contribution unique ID.
     * @param contributionContent a stream containing the resource being
     *            contributed; the stream will not be closed but the read
     *            position after the call is undefined
     * @throws DeploymentException if there was a problem with the contribution
     * @throws IOException if there was a problem reading the stream
     */
    void contribute(URI contributionURI, InputStream contributionContent)
        throws ContributionException, IOException;

    /**
     * Get the model for an installed contribution
     * 
     * @param contribution The URI of an installed contribution
     * @return The model for the contribution or null if there is no such
     *         contribution
     */
    Object getContribution(URI contribution);

    /**
     * Adds or updates a deployment composite using a supplied composite
     * ("composite by value" – a data structure, not an existing resource in the
     * domain) to the contribution identified by a supplied contribution URI.
     * The added or updated deployment composite is given a relative URI that
     * matches the "name" attribute of the composite, with a ".composite"
     * suffix.
     */
    void addDeploymentComposite(URI contribution, Object composite);

    /**
     * Remove a contribution from the SCA domain
     * 
     * @param contribution The URI of the contribution
     * @throws DeploymentException
     */
    void remove(URI contribution) throws ContributionException;

    /**
     * Resolve an artifact by QName within the contribution
     * 
     * @param <T> The java type of the artifact such as javax.wsdl.Definition
     * @param contribution The URI of the contribution
     * @param definitionType The java type of the artifact
     * @param namespace The namespace of the artifact
     * @param name The name of the artifact
     * @return The resolved artifact
     */
    <T> T resolve(URI contribution, Class<T> definitionType, String namespace, String name);

    /**
     * Resolve the reference to an artifact by the location URI within the given
     * contribution. Some typical use cases are:
     * <ul>
     * <li>Reference a XML schema using
     * {http://www.w3.org/2001/XMLSchema-instance}schemaLocation or
     * <li>Reference a list of WSDLs using
     * {http://www.w3.org/2004/08/wsdl-instance}wsdlLocation
     * </ul>
     * 
     * @param contribution The URI of the contribution
     * @param namespace The namespace of the artifact. This is for validation
     *            purpose. If the namespace is null, then no check will be
     *            performed.
     * @param uri The location URI
     * @param baseURI The URI of the base artifact where the reference is
     *            declared
     * @return The URL of the resolved artifact
     */
    URL resolve(URI contribution, String namespace, URI uri, URI baseURI);
}