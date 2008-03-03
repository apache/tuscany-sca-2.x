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

package org.apache.tuscany.sca.workspace.scanner.impl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.contribution.scanner.ContributionScanner;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;

/**
 * Folder contribution processor.
 * 
 * @version $Rev$ $Date$
 */
public class DirectoryContributionScanner implements ContributionScanner {

    public DirectoryContributionScanner() {
    }

    public String getContributionType() {
        return "application/vnd.tuscany.folder";
    }

    public URL getArtifactURL(URL contributionURL, String artifact) throws ContributionReadException {
        File directory = directory(contributionURL);
        File file = new File(directory, artifact);
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new ContributionReadException(e);
        }
    }

    public List<String> getArtifacts(URL contributionURL) throws ContributionReadException {
        File directory = directory(contributionURL);
        List<String> artifacts = new ArrayList<String>();
        try {
            traverse(artifacts, directory, directory);
        } catch (IOException e) {
            throw new ContributionReadException(e);
        }
        return artifacts;
    }

    /**
     * Recursively traverse a root directory
     * 
     * @param fileList
     * @param file
     * @param root
     * @throws IOException
     */
    private static void traverse(List<String> fileList, File file, File root) throws IOException {
        if (file.isFile()) {
            fileList.add(root.toURI().relativize(file.toURI()).toString());
        } else if (file.isDirectory()) {
            String uri = root.toURI().relativize(file.toURI()).toString();
            if (uri.endsWith("/")) {
                uri = uri.substring(0, uri.length() - 1);
            }
            fileList.add(uri);
            
            File[] files = file.listFiles();
            for (File f: files) {
                if (!f.getName().startsWith(".")) {
                    traverse(fileList, f, root);
                }
            }
        }
    }

    private static File directory(URL url) throws ContributionReadException {
        File file;
        try {
            file = new File(url.toURI());
        } catch (URISyntaxException e) {
            throw new ContributionReadException(e);
        }
        if (!file.exists() || !file.isDirectory()) {
            throw new ContributionReadException(url.toString());
        }
        return file;
    }
}
