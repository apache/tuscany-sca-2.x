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

import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.node.configuration.BindingConfiguration;
import org.apache.tuscany.sca.node.configuration.ContributionConfiguration;
import org.apache.tuscany.sca.node.configuration.NodeConfiguration;

/**
 *
 */
class NodeConfigurationImpl implements NodeConfiguration {
    private String uri;
    private String domainURI;
    private List<ContributionConfiguration> contributions = new ArrayList<ContributionConfiguration>();
    private List<BindingConfiguration> bindings = new ArrayList<BindingConfiguration>();

    public String getURI() {
        return uri;
    }

    public NodeConfiguration setURI(String uri) {
        this.uri = uri;
        return this;
    }

    public String getDomainURI() {
        return domainURI;
    }

    public NodeConfiguration setDomainURI(String domainURI) {
        this.domainURI = domainURI;
        return this;
    }

    public List<ContributionConfiguration> getContributions() {
        return contributions;
    }

    public List<BindingConfiguration> getBindings() {
        return bindings;
    }

    public NodeConfiguration addBinding(BindingConfiguration bindingConfiguration) {
        bindings.add(bindingConfiguration);
        return this;
    }

    public NodeConfiguration addContribution(ContributionConfiguration contributionConfiguration) {
        contributions.add(contributionConfiguration);
        return this;
    }

    public NodeConfiguration addBinding(QName bindingType, String... baseURIs) {
        BindingConfiguration binding = new BindingConfigurationImpl().setBindingType(bindingType);
        for (String u : baseURIs) {
            binding.addBaseURI(u);
        }
        return addBinding(binding);
    }

    public NodeConfiguration addBinding(QName bindingType, URI... baseURIs) {
        BindingConfiguration binding = new BindingConfigurationImpl().setBindingType(bindingType);
        for (URI u : baseURIs) {
            binding.addBaseURI(u.toString());
        }
        return addBinding(binding);
    }

    public NodeConfiguration addContribution(String contributionURI, String location) {
        ContributionConfiguration contribution = new ContributionConfigurationImpl(contributionURI, location);
        return addContribution(contribution);
    }

    public NodeConfiguration addContribution(String contributionURI, URL location) {
        return addContribution(contributionURI, location.toString());
    }

    public NodeConfiguration addContribution(URI contributionURI, URL location) {
        return addContribution(contributionURI.toString(), location.toString());
    }

    public NodeConfiguration addContribution(URL... locations) {
        for (URL url : locations) {
            ContributionConfiguration contribution = new ContributionConfigurationImpl(url.toString(), url.toString());
            addContribution(contribution);
        }
        return this;
    }

    public NodeConfiguration addDeploymentComposite(String contributionURI, InputStream content) {
        findContribution(contributionURI).addDeploymentComposite(content);
        return this;
    }

    public NodeConfiguration addDeploymentComposite(String contributionURI, Reader content) {
        findContribution(contributionURI).addDeploymentComposite(content);
        return this;
    }

    public NodeConfiguration addDeploymentComposite(String contributionURI, String location) {
        findContribution(contributionURI).addDeploymentComposite(URI.create(location));
        return this;
    }

    private ContributionConfiguration findContribution(String uri) {
        for (ContributionConfiguration c : contributions) {
            if (c.getURI() != null && c.getURI().equals(uri)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Contribution is not found (uri=" + uri + ")");
    }

    public String toString() {
        if (domainURI != null) {
            return "{" + domainURI + "}" + uri;
        } else {
            return uri;
        }
    }

}
