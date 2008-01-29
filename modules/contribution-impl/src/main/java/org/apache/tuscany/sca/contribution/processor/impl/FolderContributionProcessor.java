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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.contribution.PackageType;
import org.apache.tuscany.sca.contribution.processor.PackageProcessor;
import org.apache.tuscany.sca.contribution.service.ContributionException;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;

/**
 * Folder contribution package processor.
 * 
 * @version $Rev$ $Date$
 */
public class FolderContributionProcessor implements PackageProcessor {

    public FolderContributionProcessor() {
    }

    public String getPackageType() {
        return PackageType.FOLDER;
    }

    /**
     * Recursively traverse a root directory
     * 
     * @param fileList
     * @param file
     * @param root
     * @throws IOException
     */
    private static void traverse(List<URI> fileList, File file, File root) throws IOException {
        if (file.isFile()) {
            fileList.add(root.toURI().relativize(file.toURI()));
        } else if (file.isDirectory()) {
            String uri = root.toURI().relativize(file.toURI()).toString();
            if (uri.endsWith("/")) {
                uri = uri.substring(0, uri.length() - 1);
            }
            fileList.add(URI.create(uri));
            
            File[] files = file.listFiles();
            for (File f: files) {
                if (!f.getName().startsWith(".")) {
                    traverse(fileList, f, root);
                }
            }
        }
    }
    
    public URL getArtifactURL(URL sourceURL, URI artifact) throws MalformedURLException {
        return new URL(sourceURL, artifact.toString());
    }

    public List<URI> getArtifacts(URL packageSourceURL, InputStream inputStream) throws ContributionException,
        IOException {
        if (packageSourceURL == null) {
            throw new IllegalArgumentException("Invalid null package source URL.");
        }

        List<URI> artifacts = new ArrayList<URI>();

        // Assume the root is a jar file
        File rootFolder;

        try {
            rootFolder = new File(packageSourceURL.toURI());
            if (rootFolder.isDirectory()) {
                if (!rootFolder.exists()) {
                    throw new ContributionReadException(rootFolder.getAbsolutePath());
                }

                traverse(artifacts, rootFolder, rootFolder);
            }

        } catch (URISyntaxException e) {
            throw new ContributionReadException(packageSourceURL.toExternalForm(), e);
        }

        return artifacts;
    }
}
