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

package org.apache.tuscany.services.contribution.processor;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import javax.xml.stream.XMLInputFactory;

import org.apache.tuscany.services.contribution.ContributionDeploymentContext;
import org.apache.tuscany.services.contribution.model.ContentType;
import org.apache.tuscany.services.contribution.model.Contribution;
import org.apache.tuscany.services.spi.contribution.ContributionException;
import org.apache.tuscany.services.spi.contribution.ContributionProcessor;
import org.apache.tuscany.services.spi.contribution.extension.ContributionProcessorExtension;

public class CompositeContributionProcessor extends ContributionProcessorExtension implements ContributionProcessor {
    /**
     * Content-type that this processor can handle
     */
    public static final String CONTENT_TYPE = ContentType.COMPOSITE;

    protected XMLInputFactory xmlFactory;
    private final LoaderRegistry registry;


    public CompositeContributionProcessor(LoaderRegistry registry) {
        super();
        this.registry = registry;
        this.xmlFactory = XMLInputFactory.newInstance("javax.xml.stream.XMLInputFactory", getClass().getClassLoader());
    }
    
    @Override
    public String getContentType() {
        return CONTENT_TYPE;
    }

    
    public void processContent(Contribution contribution, URI artifactURI, InputStream inputStream)
        throws ContributionException, IOException {
        if (artifactURI == null) {
            throw new IllegalArgumentException("Invalid null source uri.");
        }

        if (inputStream == null) {
            throw new IllegalArgumentException("Invalid null source inputstream.");
        }

        try {
            URI contributionId = contribution.getUri();
            URL scdlLocation = contribution.getArtifact(artifactURI).getLocation();
            CompositeClassLoader cl = new CompositeClassLoader(null, getClass().getClassLoader());
            cl.addURL(contribution.getLocation());

            DeploymentContext deploymentContext =
                new ContributionDeploymentContext(cl, scdlLocation, contributionId, this.xmlFactory, null,
                    false);

            CompositeComponentType componentType =
                this.registry.load(null, scdlLocation, CompositeComponentType.class, deploymentContext);

            CompositeImplementation implementation = new CompositeImplementation();
            implementation.setComponentType(componentType);
            ComponentDefinition<CompositeImplementation> componentDefinition =
                new ComponentDefinition<CompositeImplementation>(implementation);
            
            //FIXME this changed in trunk.... 
            //componentDefinition.setName(componentType.getName());

            contribution.getArtifact(artifactURI).addModelObject(CompositeComponentType.class, null, componentDefinition);

        } catch (LoaderException le) {
            throw new InvalidComponentDefinitionlException(contribution.getArtifact(artifactURI).getLocation()
                .toExternalForm(), le);
        }
    }

    public void processModel(Contribution contribution, URI source, Object modelObject) throws ContributionException,
                                                                                               IOException {
        // TODO Auto-generated method stub

    }

}