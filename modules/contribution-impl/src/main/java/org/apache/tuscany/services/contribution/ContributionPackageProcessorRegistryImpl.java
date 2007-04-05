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

package org.apache.tuscany.services.contribution;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.services.contribution.model.Contribution;
import org.apache.tuscany.services.spi.contribution.ContentTypeDescriber;
import org.apache.tuscany.services.spi.contribution.ContributionException;
import org.apache.tuscany.services.spi.contribution.ContributionPackageProcessor;
import org.apache.tuscany.services.spi.contribution.ContributionPackageProcessorRegistry;
import org.apache.tuscany.services.spi.contribution.UnsupportedContentTypeException;

/**
 * Default implementation of ContributionProcessorRegistry
 *
 * @version $Rev$ $Date$
 */
public class ContributionPackageProcessorRegistryImpl implements ContributionPackageProcessorRegistry {
    /**
     * Processor registry
     */
    private Map<String, ContributionPackageProcessor> registry = new HashMap<String, ContributionPackageProcessor>();
    /**
     * Helper method to describe contentType for each artifact
     */
    private ContentTypeDescriber packageTypeDescriber;

    public ContributionPackageProcessorRegistryImpl(ContentTypeDescriber contentTypeDescriber) {
        if (contentTypeDescriber == null) {
            this.packageTypeDescriber = new PackageTypeDescriberImpl();
        } else {
            this.packageTypeDescriber = contentTypeDescriber;
        }
    }

    public void register(String contentType, ContributionPackageProcessor processor) {
        registry.put(contentType, processor);
    }

    public void unregister(String contentType) {
        registry.remove(contentType);
    }

    public void processContent(Contribution contribution, URI source, InputStream inputStream)
        throws ContributionException, IOException {

        URL locationURL = contribution.getArtifact(source).getLocation();
        String contentType = this.packageTypeDescriber.getContentType(locationURL, null);
        if (contentType == null) {
            throw new UnsupportedContentTypeException("Unsupported contribution package", source.toString());
        }

        ContributionPackageProcessor processor = this.registry.get(contentType);
        if (processor == null) {
            throw new UnsupportedContentTypeException(contentType, locationURL.getPath());
        }

        processor.processContent(contribution, source, inputStream);
    }
}
