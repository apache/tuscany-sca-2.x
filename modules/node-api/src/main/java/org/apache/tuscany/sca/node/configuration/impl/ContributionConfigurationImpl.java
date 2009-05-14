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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.node.configuration.ContributionConfiguration;
import org.apache.tuscany.sca.node.configuration.DeploymentComposite;

/**
 * Configuration for an SCA contribution used by the SCA node
 */
class ContributionConfigurationImpl implements ContributionConfiguration {
    private List<DeploymentComposite> deploymentComposites = new ArrayList<DeploymentComposite>();
    private String uri;
    private String location;

    public ContributionConfigurationImpl() {
        super();
    }

    public ContributionConfigurationImpl(String uri, String location) {
        super();
        this.uri = uri;
        this.location = location;
    }

    public ContributionConfigurationImpl(String location) {
        super();
        this.uri = location;
        this.location = location;
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
    public ContributionConfiguration setURI(String uri) {
        this.uri = uri;
        return this;
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
    public ContributionConfiguration setLocation(String location) {
        this.location = location;
        return this;
    }

    /**
     * Get the list of deployment composites that are attached to the contribution
     * @return
     */
    public List<DeploymentComposite> getDeploymentComposites() {
        return deploymentComposites;
    }

    public ContributionConfiguration addDeploymentComposite(DeploymentComposite deploymentComposite) {
        deploymentComposites.add(deploymentComposite);
        if (uri != null) {
            deploymentComposite.setContributionURI(uri);
        }
        return this;
    }

    public ContributionConfiguration addDeploymentComposite(Reader reader) {
        try {
            DeploymentComposite composite = new DeploymentCompositeImpl();
            char[] buf = new char[8192];
            StringWriter sw = new StringWriter();
            int size = 0;
            while (size >= 0) {
                size = reader.read(buf);
                if (size > 0) {
                    sw.write(buf, 0, size);
                }
            }
            reader.close();
            composite.setContent(sw.toString());
            return addDeploymentComposite(composite);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public ContributionConfiguration addDeploymentComposite(InputStream content) {
        try {
            InputStreamReader reader = new InputStreamReader(content, "UTF-8");
            return addDeploymentComposite(reader);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public ContributionConfiguration addDeploymentComposite(String content) {
        DeploymentComposite composite = new DeploymentCompositeImpl();
        composite.setContent(content);
        return addDeploymentComposite(composite);
    }

    public ContributionConfiguration addDeploymentComposite(URI location) {
        DeploymentComposite composite = new DeploymentCompositeImpl();
        composite.setLocation(location.toString());
        return addDeploymentComposite(composite);
    }

    public ContributionConfiguration addDeploymentComposite(URL location) {
        DeploymentComposite composite = new DeploymentCompositeImpl();
        composite.setLocation(location.toString());
        return addDeploymentComposite(composite);
    }
}
