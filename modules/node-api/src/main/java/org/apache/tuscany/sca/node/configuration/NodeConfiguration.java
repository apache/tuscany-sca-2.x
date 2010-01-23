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

import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;

/**
 * The configuration for a Node which represents the deployment of an SCA composite application
 */
public interface NodeConfiguration {
    String DEFAULT_DOMAIN_URI = "default";
    String DEFAULT_NODE_URI = "http://tuscany.apache.org/sca/1.1/nodes/default";
    String DEFAULT_DOMAIN_REGISTRY_URI = "default";
    
    /**
     * Get the URI of the SCA domain that manages the composite application
     * @return The URI of the SCA domain
     */
    String getDomainURI();

    /**
     * Get the name of the SCA domain
     * @return The name of the SCA domain
     */
    String getDomainName();

    /**
     * Set the URI of the SCA domain
     * @param domainURI The URI of the SCA domain
     */
    NodeConfiguration setDomainURI(String domainURI);
    
    /**
     * Return the URI of the domain registry
     * @return
     */
    String getDomainRegistryURI();
    
    /**
     * Set the URI of the domain registry
     * @param domainRegistryURI The URI of the domain registry. The scheme will be used
     * by Tusany to choose the implementation of DomainRegistry interface. Examples are:
     * <ul>
     * <li>vm://localhost (a JVM local registry)
     * <li>multicast://228.0.0.100:50000?timeout=50 (Tomcat Tribes multicast based registry)
     * </ul>
     * @return The NodeConfiguration
     */
    NodeConfiguration setDomainRegistryURI(String domainRegistryURI);

    /**
     * Get the URI of the node. It uniquely identifies a node within the SCA domain
     * @return The URI of the node
     */
    String getURI();

    /**
     * Set the URI of the node
     * @param uri The URI of the node
     */
    NodeConfiguration setURI(String uri);

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

    NodeConfiguration addContribution(ContributionConfiguration contribution);
    NodeConfiguration addContribution(String contributionURI, String location);
    NodeConfiguration addContribution(String contributionURI, URL location);
    NodeConfiguration addContribution(URI contributionURI, URL location);
    NodeConfiguration addContribution(URL...location);

    NodeConfiguration addDeploymentComposite(String contributionURI, String location);
    NodeConfiguration addDeploymentComposite(String contributionURI, Reader content);
    NodeConfiguration addDeploymentComposite(String contributionURI, InputStream content);

    NodeConfiguration addBinding(BindingConfiguration binding);
    NodeConfiguration addBinding(QName bindingType, String...baseURIs);
    NodeConfiguration addBinding(QName bindingType, URI...baseURIs);

    List<Object> getExtensions();
}
