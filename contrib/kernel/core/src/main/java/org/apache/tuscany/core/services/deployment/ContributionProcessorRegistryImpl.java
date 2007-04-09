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
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

import org.apache.tuscany.spi.deployer.ContentTypeDescriber;
import org.apache.tuscany.spi.deployer.ContributionProcessor;
import org.apache.tuscany.spi.deployer.ContributionProcessorRegistry;
import org.apache.tuscany.spi.model.Contribution;

import org.apache.tuscany.host.deployment.DeploymentException;
import org.apache.tuscany.host.deployment.UnsupportedContentTypeException;

/**
 * Default implementation of ContributionProcessorRegistry
 *
 * @version $Rev$ $Date$
 */
@EagerInit
@Service(ContributionProcessorRegistry.class)
public class ContributionProcessorRegistryImpl implements ContributionProcessorRegistry {
    /**
     * Processor registry
     */
    private Map<String, ContributionProcessor> registry = new HashMap<String, ContributionProcessor>();
    /**
     * Helper method to describe contentType for each artifact
     */
    private ContentTypeDescriber contentTypeDescriber;

    public ContributionProcessorRegistryImpl(@Reference ContentTypeDescriber contentTypeDescriber) {
        if (contentTypeDescriber == null) {
            this.contentTypeDescriber = new ContentTypeDescriberImpl();
        } else {
            this.contentTypeDescriber = contentTypeDescriber;
        }
    }

    public void register(String contentType, ContributionProcessor processor) {
        registry.put(contentType, processor);
    }

    public void unregister(String contentType) {
        registry.remove(contentType);
    }

    public void processContent(Contribution contribution, URI source, InputStream inputStream)
        throws DeploymentException, IOException {

        URL locationURL = contribution.getArtifact(source).getLocation();
        String contentType = this.contentTypeDescriber.getContentType(locationURL, null);
        if (contentType == null) {
            throw new UnsupportedContentTypeException("Invalid contentType: null");
        }

        ContributionProcessor processor = this.registry.get(contentType);
        if (processor == null) {
            throw new UnsupportedContentTypeException(contentType, locationURL.getPath());
        }

        processor.processContent(contribution, source, inputStream);

    }

    public void processModel(Contribution contribution, URI source, Object modelObject) throws DeploymentException,
                                                                                               IOException {
    }
}
