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
package org.apache.tuscany.core.services.deployment;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.osoa.sca.annotations.Reference;

import org.apache.tuscany.spi.deployer.ContributionProcessorRegistry;
import org.apache.tuscany.spi.deployer.ContributionRepository;
import org.apache.tuscany.spi.model.Contribution;

import org.apache.tuscany.core.util.IOHelper;
import org.apache.tuscany.host.deployment.ContributionService;
import org.apache.tuscany.host.deployment.DeploymentException;

/**
 * @version $Rev$ $Date$
 */
public class ContributionServiceImpl implements ContributionService {
    /**
     * Repository where contributions are stored. Usually set by injection.
     */
    protected ContributionRepository contributionRepository;
    /**
     * Registry of available processors. Usually set by injection.
     */
    protected ContributionProcessorRegistry processorRegistry;
    /**
     * Contribution registry This is a registry of processed Contributios index by URI
     */
    protected Map<URI, Contribution> contributionRegistry = new HashMap<URI, Contribution>();

    public ContributionServiceImpl(@Reference ContributionRepository repository,
                                   @Reference ContributionProcessorRegistry processorRegistry) {
        super();
        this.contributionRepository = repository;
        this.processorRegistry = processorRegistry;
    }

    public URI contribute(URL contribution, boolean storeInRepository) throws DeploymentException, IOException {
        if (contribution == null) {
            throw new IllegalArgumentException("contribution is null");
        }

        URI source;
        try {
            source = contribution.toURI();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("contribution cannot be converted to a URI", e);
        }

        InputStream is = contribution.openConnection().getInputStream();
        try {
            return contribute(source, is, storeInRepository);
        } finally {
            IOHelper.closeQuietly(is);
        }
    }

    public URI contribute(URI source, InputStream contributionStream, boolean storeInRepository)
        throws DeploymentException, IOException {
        if (source == null) {
            throw new IllegalArgumentException("source URI for contribution is null");
        }

        if (contributionStream == null) {
            throw new IllegalArgumentException("Invalid contribution stream : null");
        }

        // store the contribution in the contribution repository
        URI contributionURI = URI.create("sca://contribution/" + UUID.randomUUID());
        URL locationURL;
        if (storeInRepository) {
            locationURL = this.contributionRepository.store(source, contributionStream);
        } else {
            locationURL = source.toURL();
        }


        Contribution contribution = null;
        contribution = new Contribution(contributionURI);
        contribution.setLocation(locationURL);

        //process the contribution
        this.processorRegistry.processContent(contribution, contributionURI, locationURL.openStream());

        //store the contribution on the registry
        this.contributionRegistry.put(contribution.getUri(), contribution);

        return contribution.getUri();
    }

    public Contribution getContribution(URI id) {
        return this.contributionRegistry.get(id);
    }

    public void remove(URI contribution) throws DeploymentException {
        //remove from repository
        this.contributionRegistry.remove(contribution);
        //remove from registry
        this.contributionRegistry.remove(contribution);
    }

    public <T> T resolve(URI contribution, Class<T> definitionType, String namespace, String name) {
        // TODO Auto-generated method stub
        return null;
    }

    public URL resolve(URI contribution, String namespace, URI uri, URI baseURI) {
        // TODO Auto-generated method stub
        return null;
    }
}
