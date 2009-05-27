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
package org.apache.tuscany.sca.contribution.scanner;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;

/**
 * Interface for contribution package scanners
 *
 * Contribution scanners understand the format of the contribution and how to get the
 * artifacts in the contribution.
 *
 * @version $Rev$ $Date$
 */
public interface ContributionScanner {

    /**
     * Returns the type of package supported by this package scanner.
     *
     * @return the package type
     */
    String getContributionType();

    /**
     * Returns a list of artifacts in the contribution.
     *
     * @param contribution Contribution URL
     * @return List of artifact URIs
     * @throws ContributionReadException
     * @throws IOException
     */
    List<String> scan(Contribution contribution) throws ContributionReadException;

    /**
     * Return the URL for an artifact in the contribution.
     *
     * This is needed for archives such as jar files that have specific URL schemes
     * for the artifacts they contain.
     *
     * @param contribution Contribution URL
     * @param artifact The relative URI for the artifact
     * @throws ContributionReadException
     * @return The artifact URL
     */
    URL getArtifactURL(Contribution contribution, String artifact) throws ContributionReadException;
}
