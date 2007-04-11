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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.contribution.ContentType;
import org.apache.tuscany.services.contribution.util.FileHelper;
import org.apache.tuscany.services.spi.contribution.ContributionException;
import org.apache.tuscany.services.spi.contribution.ContributionPackageProcessor;
import org.apache.tuscany.services.spi.contribution.ContributionPackageProcessorRegistry;
import org.apache.tuscany.services.spi.contribution.extension.ContributionPackageProcessorExtension;

public class FolderContributionProcessor extends ContributionPackageProcessorExtension implements
    ContributionPackageProcessor {
    /**
     * Package-type that this package processor can handle
     */
    public static final String PACKAGE_TYPE = ContentType.FOLDER;

    public FolderContributionProcessor(ContributionPackageProcessorRegistry registry) {
        super(registry);
    }

    public String getPackageType() {
        return PACKAGE_TYPE;
    }

    /**
     * Recursively traverse a root directory
     * 
     * @param fileList
     * @param file
     * @throws IOException
     */
    private void traverse(List<URI> fileList, File file, File root) throws IOException {
        if (file.isFile()) {
            fileList.add(root.toURI().relativize(file.toURI()));
        } else if (file.isDirectory()) {
            // FIXME: Maybe we should externalize it as a property
            // Regular expression to exclude .xxx files
            File[] files = file.listFiles(FileHelper.getFileFilter("[^\u002e].*", true));
            for (int i = 0; i < files.length; i++) {
                traverse(fileList, files[i], root);
            }
        }
    }
    
    public URL getArtifactURL(URL sourceURL, URI artifact) throws MalformedURLException {
        return new URL(sourceURL, artifact.toString());
    }

    /**
     * Get a list of files from the directory
     * 
     * @return
     * @throws IOException
     */
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
                    throw new InvalidFolderContributionException(rootFolder.getAbsolutePath());
                }

                this.traverse(artifacts, rootFolder, rootFolder);
            }

        } catch (URISyntaxException e) {
            throw new InvalidFolderContributionURIException(packageSourceURL.toExternalForm(), e);
        }

        return artifacts;
    }
}
