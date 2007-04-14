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

package org.apache.tuscany.contribution.processor.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.contribution.processor.PackageProcessor;
import org.apache.tuscany.contribution.processor.PackageProcessorExtensionPoint;
import org.apache.tuscany.contribution.service.ContributionException;
import org.apache.tuscany.contribution.service.TypeDescriber;
import org.apache.tuscany.contribution.service.UnsupportedContentTypeException;
import org.apache.tuscany.contribution.service.impl.PackageTypeDescriberImpl;

/**
 * Default implementation of ContributionProcessorRegistry
 *
 * @version $Rev$ $Date$
 */
public class DefaultPackageProcessorExtensionPoint implements PackageProcessorExtensionPoint {
    /**
     * Processor registry
     */
    private Map<String, PackageProcessor> registry = new HashMap<String, PackageProcessor>();
    /**
     * Helper method to describe contentType for each artifact
     */
    private TypeDescriber packageTypeDescriber;

    public DefaultPackageProcessorExtensionPoint(TypeDescriber packageTypeDescriber) {
        if (packageTypeDescriber == null) {
            this.packageTypeDescriber = new PackageTypeDescriberImpl();
        } else {
            this.packageTypeDescriber = packageTypeDescriber;
        }
    }

    public void register(String contentType, PackageProcessor processor) {
        registry.put(contentType, processor);
    }

    public void unregister(String contentType) {
        registry.remove(contentType);
    }

    public List<URI> getArtifacts(URL packageSourceURL,InputStream inputStream) throws ContributionException, IOException{
        String contentType = this.packageTypeDescriber.getType(packageSourceURL, null);
        if (contentType == null) {
            throw new UnsupportedContentTypeException("Unsupported contribution package", packageSourceURL.toString());
        }

        PackageProcessor packageProcessor = this.registry.get(contentType);
        if (packageProcessor == null) {
            throw new UnsupportedContentTypeException(contentType, packageSourceURL.getPath());
        }

        return packageProcessor.getArtifacts(packageSourceURL, inputStream);
    }
    
    /**
     * @see org.apache.tuscany.contribution.processor.PackageProcessor#getArtifactURL(java.net.URL, java.net.URI)
     */
    public URL getArtifactURL(URL packageSourceURL, URI artifact) throws MalformedURLException {
        String contentType = this.packageTypeDescriber.getType(packageSourceURL, null);
        PackageProcessor packageProcessor = this.registry.get(contentType);
        return packageProcessor.getArtifactURL(packageSourceURL, artifact);
    }    
}
