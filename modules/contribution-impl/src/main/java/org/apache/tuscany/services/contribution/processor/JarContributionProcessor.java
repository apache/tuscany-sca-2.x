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
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.apache.tuscany.services.contribution.ContentTypeDescriberImpl;
import org.apache.tuscany.services.contribution.model.ContentType;
import org.apache.tuscany.services.contribution.model.Contribution;
import org.apache.tuscany.services.contribution.model.DeployedArtifact;
import org.apache.tuscany.services.contribution.util.IOHelper;
import org.apache.tuscany.services.spi.contribution.ContentTypeDescriber;
import org.apache.tuscany.services.spi.contribution.ContributionException;
import org.apache.tuscany.services.spi.contribution.ContributionPackageProcessor;
import org.apache.tuscany.services.spi.contribution.extension.ContributionPackageProcessorExtension;

public class JarContributionProcessor extends ContributionPackageProcessorExtension implements ContributionPackageProcessor {
    /**
     * Package-type that this package processor can handle
     */
    public static final String PACKAGE_TYPE = ContentType.JAR;

    @Override
    public String getContentType() {
        return PACKAGE_TYPE;
    }

    /**
     * Get a list of resources inside the jar
     * 
     * @return
     * @throws IOException
     */
    protected List<URL> getArtifacts(URL rootURL, InputStream sourceInputStream) throws IOException {
        List<URL> artifacts = new ArrayList<URL>();

        // Assume the root is a jar file
        JarInputStream jar = new JarInputStream(sourceInputStream);
        try {
            while (true) {
                JarEntry entry = jar.getNextJarEntry();
                if (entry == null) {
                    // EOF
                    break;
                }
                if (entry.isDirectory()) {
                    continue;
                }

                // FIXME: Maybe we should externalize the filter as a property
                if (!entry.getName().startsWith(".")) {
                    artifacts.add(new URL(rootURL, entry.getName()));
                }
            }
        } finally {
            jar.close();
        }
        return artifacts;
    }

    private URL forceJarURL(URL sourceURL) throws MalformedURLException {
        if (sourceURL.toString().startsWith("jar:")) {
            return sourceURL;
        } else {
            return new URL("jar:" + sourceURL.toExternalForm() + "!/");
        }

    }

    public void processContent(Contribution contribution, URI source, InputStream inputStream)
        throws ContributionException, IOException {
        if (contribution == null) {
            throw new IllegalArgumentException("Invalid null contribution.");
        }

        if (source == null) {
            throw new IllegalArgumentException("Invalid null source uri.");
        }

        if (inputStream == null) {
            throw new IllegalArgumentException("Invalid null source inputstream.");
        }

        URL sourceURL = contribution.getArtifact(source).getLocation();

        sourceURL = forceJarURL(sourceURL);

        for (URL artifactURL : getArtifacts(sourceURL, inputStream)) {
            URI artifactURI;

            String artifactPath = artifactURL.toExternalForm().substring(sourceURL.toExternalForm().length());
            artifactURI = contribution.getUri().resolve(artifactPath);
            DeployedArtifact artifact = new DeployedArtifact(artifactURI);
            artifact.setLocation(artifactURL);
            contribution.addArtifact(artifact);


            ContentTypeDescriber contentTypeDescriber = new ContentTypeDescriberImpl();
            String contentType = contentTypeDescriber.getContentType(artifactURL, null);

            // just process scdl for now
            if (ContentType.COMPOSITE.equals(contentType)) {
                InputStream is = IOHelper.getInputStream(artifactURL);
                try {
                    this.registry.processContent(contribution, artifactURI, is);
                } finally {
                    IOHelper.closeQuietly(is);
                    is = null;
                }
            }
        }

    }
}
