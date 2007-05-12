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

package org.apache.tuscany.sca.contribution.processor;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;

import org.apache.tuscany.sca.contribution.service.ContributionException;
import org.apache.tuscany.sca.contribution.service.TypeDescriber;
import org.apache.tuscany.sca.contribution.service.UnsupportedContentTypeException;

/**
 * Default implementation of PackageProcessor.
 * 
 * @version $Rev$ $Date$
 */
public class ExtensiblePackageProcessor implements PackageProcessor {

    private PackageProcessorExtensionPoint processors;

    /**
     * Helper method to describe contentType for each artifact
     */
    private TypeDescriber packageTypeDescriber;

    public ExtensiblePackageProcessor(PackageProcessorExtensionPoint processors, TypeDescriber packageTypeDescriber) {
        this.processors = processors; 
        this.packageTypeDescriber = packageTypeDescriber;
    }

    public List<URI> getArtifacts(URL packageSourceURL, InputStream inputStream) 
        throws ContributionException, IOException {
        String contentType = this.packageTypeDescriber.getType(packageSourceURL, null);
        if (contentType == null) {
            throw new UnsupportedContentTypeException("Unsupported contribution package", packageSourceURL.toString());
        }

        PackageProcessor packageProcessor = this.processors.getPackageProcessor(contentType);
        if (packageProcessor == null) {
            throw new UnsupportedContentTypeException(contentType, packageSourceURL.getPath());
        }

        return packageProcessor.getArtifacts(packageSourceURL, inputStream);
    }

    public URL getArtifactURL(URL packageSourceURL, URI artifact) throws MalformedURLException {
        String contentType = this.packageTypeDescriber.getType(packageSourceURL, null);
        PackageProcessor packageProcessor = this.processors.getPackageProcessor(contentType);
        return packageProcessor.getArtifactURL(packageSourceURL, artifact);
    }
    
    public String getPackageType() {
        return null;
    }
}
