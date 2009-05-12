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
 * The configuration for a Node which represents the deployment of an SCA composite application
 */
public interface NodeConfiguration {
    /**
     * Get the URI of the SCA domain that manages the composite application
     * @return The URI of the SCA domain
     */
    String getDomainURI();

    /**
     * Set the URI of the SCA domain
     * @param domainURI The URI of the SCA domain
     */
    void setDomainURI(String domainURI);

    /**
     * Get the URI of the node. It uniquely identifies a node within the SCA domain
     * @return The URI of the node
     */
    String getURI();

    /**
     * Set the URI of the node
     * @param uri The URI of the node
     */
    void setURI(String uri);

    /**
     * Get a list of confiurations for SCA contributions
     * @return A list of configurations for SCA contributions
     */
    List<ContributionConfiguration> getContributions();

    /**
     * Get a list of configurations for SCA bindings
     * @return A list of configurations for SCA bindings
     */
    List<BindingConfiguration> getBindings();
}
