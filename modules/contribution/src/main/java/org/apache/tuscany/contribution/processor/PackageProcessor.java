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
package org.apache.tuscany.contribution.processor;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;

import org.apache.tuscany.contribution.service.ContributionException;

/**
 * Interface for services that can process contributions.
 * 
 * @version $Rev$ $Date$
 */
public interface PackageProcessor {
    // /**
    // * Returns the type of package handled by this package processor.
    // * @return the type of package handled by this package processor
    // */
    // String getPackageType();

    /**
     * Retrieve a list of artifacts for the specific package type
     * 
     * @param packageSourceURL location of the artifact
     * @param inputStream optional content of the package
     * @return
     * @throws ContributionException
     * @throws IOException
     */
    List<URI> getArtifacts(URL packageSourceURL, InputStream inputStream) throws ContributionException, IOException;

    /**
     * Return the URL for an artifact in the package
     * 
     * @param packageSourceURL The package URL
     * @param artifact The relative URI for the artifact
     * @return
     */
    URL getArtifactURL(URL packageSourceURL, URI artifact) throws MalformedURLException;
}
