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

package org.apache.tuscany.sca.node.configuration.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.node.configuration.ContributionConfiguration;
import org.apache.tuscany.sca.node.configuration.DeploymentComposite;

/**
 * Configuration for an SCA contribution used by the SCA node
 */
class ContributionConfigurationImpl implements ContributionConfiguration {
    private List<DeploymentComposite> deploymentComposites = new ArrayList<DeploymentComposite>();
    private Contribution contribution;
    private String uri;
    private String location;

    public Contribution getContribution() {
        return contribution;
    }

    public void setContribution(Contribution contribution) {
        this.contribution = contribution;
    }

    /**
     * Get the URI of the contribution
     * @return The URI of the contribution
     */
    public String getURI() {
        return uri;
    }

    /**
     * Set the URI of the contribution
     * @param uri The URI of the contribution
     */
    public void setURI(String uri) {
        this.uri = uri;
    }

    /**
     * Get the location of the contribution
     * @return The location of the contribution
     */
    public String getLocation() {
        return location;
    }

    /**
     * Set the location of the contribution
     * @param location The location of the contribution
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Get the list of deployment composites that are attached to the contribution
     * @return
     */
    public List<DeploymentComposite> getDeploymentComposites() {
        return deploymentComposites;
    }
}
