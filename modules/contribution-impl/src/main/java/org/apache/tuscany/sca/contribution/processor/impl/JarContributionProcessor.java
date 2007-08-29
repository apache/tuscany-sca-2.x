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

package org.apache.tuscany.sca.contribution.processor.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.apache.tuscany.sca.contribution.ContentType;
import org.apache.tuscany.sca.contribution.processor.PackageProcessor;
import org.apache.tuscany.sca.contribution.service.ContributionException;

/**
 * Jar Contribution package processor
 * 
 * @version $Rev$ $Date$
 */
public class JarContributionProcessor implements PackageProcessor {
    /**
     * Package-type that this package processor can handle
     */
    public static final String PACKAGE_TYPE = ContentType.JAR;

    public JarContributionProcessor() {
    }

    public String getPackageType() {
        return PACKAGE_TYPE;
    }

    public URL getArtifactURL(URL sourceURL, URI artifact) throws MalformedURLException {
        if (sourceURL.toString().startsWith("jar:")) {
            return new URL(sourceURL, artifact.toString());
        } else {
            return new URL("jar:" + sourceURL.toExternalForm() + "!/" + artifact);
        }
    }

    public List<URI> getArtifacts(URL packageSourceURL, InputStream inputStream) throws ContributionException,
        IOException {
        if (packageSourceURL == null) {
            throw new IllegalArgumentException("Invalid null package source URL.");
        }

        if (inputStream == null) {
            throw new IllegalArgumentException("Invalid null source inputstream.");
        }

        // Assume the root is a jar file
        JarInputStream jar = new JarInputStream(inputStream);
        try {
            Set<String> names = new HashSet<String>();
            while (true) {
                JarEntry entry = jar.getNextJarEntry();
                if (entry == null) {
                    // EOF
                    break;
                }

                // FIXME: Maybe we should externalize the filter as a property
                String name = entry.getName(); 
                if (!name.startsWith(".")) {
                    
                    // Trim trailing /
                    if (name.endsWith("/")) {
                        name = name.substring(0, name.length() - 1);
                    }

                    // Add the entry name
                    if (!names.contains(name)) {
                        names.add(name);
                        
                        // Add parent folder names to the list too
                        for (;;) {
                            int s = name.lastIndexOf('/');
                            if (s == -1) {
                                name = "";
                            } else {
                                name = name.substring(0, s);
                            }
                            if (!names.contains(name)) {
                                names.add(name);
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
            
            // Return list of URIs
            List<URI> artifacts = new ArrayList<URI>();
            for (String name: names) {
                artifacts.add(URI.create(name));
            }
            return artifacts;
            
        } finally {
            jar.close();
        }
    }
}
