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

package org.apache.tuscany.sca.contribution.jee.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

import org.apache.tuscany.sca.contribution.PackageType;
import org.apache.tuscany.sca.contribution.processor.PackageProcessor;
import org.apache.tuscany.sca.contribution.processor.impl.JarContributionProcessor;
import org.apache.tuscany.sca.contribution.service.ContributionException;

/**
 * Ear Contribution package processor.
 * 
 * @version $Rev$ $Date$
 */
public class EarContributionProcessor implements PackageProcessor {

    public URL getArtifactURL(URL packageSourceURL, URI artifact)
            throws MalformedURLException {
        if (packageSourceURL.toString().startsWith("archive:")) {
            return new URL(packageSourceURL, artifact.toString());
        } else {
            return new URL("archive:" + packageSourceURL.toExternalForm() + "!/" + artifact);
        }
    }

    public List<URI> getArtifacts(URL packageSourceURL, InputStream inputStream) throws ContributionException, IOException {
        if (packageSourceURL == null) {
            throw new IllegalArgumentException("Invalid null package source URL.");
        }

        if (inputStream == null) {
            throw new IllegalArgumentException("Invalid null source inputstream.");
        }

        // The root is a jar file
        JarInputStream jar = new JarInputStream(inputStream);
        try {
            Set<String> names = new HashSet<String>();
            while (true) {
                JarEntry entry = jar.getNextJarEntry();
                if (entry == null) {
                    // EOF
                    break;
                }

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
                if(entry.getName().indexOf("/") == -1 && (entry.getName().toLowerCase().endsWith(".war") || entry.getName().toLowerCase().endsWith(".jar"))) {
                    // A WAR or an EJB JAR file in the root of the archive.
                    // Get entries from the nested archive.
                    ByteArrayOutputStream bout = new ByteArrayOutputStream();
                    int b;
                    while((b = jar.read()) != -1) {
                        bout.write(b);
                    }
                    bout.close();
                    ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
                    
                    PackageProcessor archiveProcessor = entry.getName().toLowerCase().endsWith(".war") ? new WarContributionProcessor() : new JarContributionProcessor();
                    List<URI> artifacts = archiveProcessor.getArtifacts(packageSourceURL, bin);
                    bin.close();
                    for(URI artifact : artifacts) {
                        names.add(entry.getName()+"!/"+artifact);
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

    public String getPackageType() {
        return PackageType.EAR;
    }
}
