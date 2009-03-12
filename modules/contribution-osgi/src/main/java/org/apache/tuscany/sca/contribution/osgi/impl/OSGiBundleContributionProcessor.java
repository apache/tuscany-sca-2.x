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

package org.apache.tuscany.sca.contribution.osgi.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.apache.tuscany.sca.contribution.PackageType;
import org.apache.tuscany.sca.contribution.processor.ContributionException;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.scanner.ContributionScanner;
import org.osgi.framework.Bundle;

/**
 * Bundle Contribution package processor.
 *
 * @version $Rev$ $Date$
 */
public class OSGiBundleContributionProcessor implements ContributionScanner {

    public OSGiBundleContributionProcessor() {
    }

    public String getContributionType() {
        return PackageType.BUNDLE;
    }

    public URL getArtifactURL(URL sourceURL, String artifact) throws ContributionReadException {
        Bundle bundle = null;
        try {
            bundle = OSGiBundleActivator.findBundle(sourceURL);
            if (bundle != null) {
                URL url = bundle.getResource(artifact);
                if (url == null)
                    System.out.println("Could not load resource " + artifact);
                return url;
            }
        } catch (Exception e) {
        }
        return null;
    }
    
    public List<String> getJarArtifacts(URL packageSourceURL, InputStream inputStream) throws ContributionException,
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
            if (!name.startsWith(".") && !entry.isDirectory()) {
                
                // Trim trailing /
                if (name.endsWith("/")) {
                    name = name.substring(0, name.length() - 1);
                }

                // Add the entry name
                if (!names.contains(name) && name.length() > 0) {
                    names.add(name);
                    
                }
            }
        }
        
        // Return list of URIs
        List<String> artifacts = new ArrayList<String>();
        for (String name: names) {
            artifacts.add(name);
        }
        return artifacts;
        
    } finally {
        jar.close();
    }
}

    public List<String> getArtifacts(URL packageSourceURL) throws ContributionReadException {
        
        if (packageSourceURL == null) {
            throw new IllegalArgumentException("Invalid null package source URL.");
        }
        Bundle bundle = OSGiBundleActivator.findBundle(packageSourceURL);
        
        if (bundle == null) {
            throw new IllegalArgumentException("Could not find OSGi bundle " + packageSourceURL);
        }

        List<String> artifacts = new ArrayList<String>();

        try {
            Enumeration<?> entries = bundle.findEntries("/", "*", true);
            while (entries.hasMoreElements()) {
                URL entry = (URL)entries.nextElement();
                String entryName = entry.getPath();
                if (entryName.startsWith("/"))
                    entryName = entryName.substring(1);
                artifacts.add(entryName);
                
                if (entryName.endsWith(".jar")) {

                    URL jarResource = bundle.getResource(entryName);
                    artifacts.addAll(getJarArtifacts(jarResource, jarResource.openStream()));
                }
            
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return artifacts;
    }
}
