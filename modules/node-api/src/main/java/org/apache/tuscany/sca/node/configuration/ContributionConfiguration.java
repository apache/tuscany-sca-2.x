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

package org.apache.tuscany.sca.node.configuration;

import java.util.List;

/**
 * Configuration for an SCA contribution used by the SCA node
 */
public interface ContributionConfiguration {
    /**
     * Get the URI of the contribution
     * @return The URI of the contribution
     */
    String getURI();

    /**
     * Set the URI of the contribution
     * @param uri The URI of the contribution
     */
    void setURI(String uri);

    /**
     * Get the location of the contribution
     * @return The location of the contribution
     */
    String getLocation();

    /**
     * Set the location of the contribution
     * @param location The location of the contribution
     */
    void setLocation(String location);

    /**
     * Get the list of deployment composites that are attached to the contribution
     * @return
     */
    List<DeploymentComposite> getDeploymentComposites();
}
