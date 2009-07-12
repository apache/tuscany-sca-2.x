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

package org.apache.tuscany.sca.contribution.scanner.impl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.PackageType;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.scanner.ContributionScanner;

/**
 * Folder contribution processor.
 *
 * @version $Rev$ $Date$
 */
public class DirectoryContributionScanner implements ContributionScanner {

    public DirectoryContributionScanner() {
    }

    public String getContributionType() {
        return PackageType.FOLDER;
    }

    public URL getArtifactURL(Contribution contribution, String artifact) throws ContributionReadException {
        File directory = directory(contribution);
        File file = new File(directory, artifact);
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new ContributionReadException(e);
        }
    }

    public List<String> scan(Contribution contribution) throws ContributionReadException {
        File directory = directory(contribution);
        List<String> artifacts = new ArrayList<String>();
        try {
            traverse(artifacts, directory, directory);
        } catch (IOException e) {
            throw new ContributionReadException(e);
        }
        contribution.getTypes().add(getContributionType());
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

    private static File directory(Contribution contribution) throws ContributionReadException {
        File file;
        URI uri = null;
        try {
            uri = new URI(contribution.getLocation());
            file = new File(uri);
        } catch (URISyntaxException e) {
            throw new ContributionReadException(e);
        } catch(IllegalArgumentException e) {
            // Hack for file:./a.txt or file:../a/c.wsdl
            return new File(uri.getPath());
        }
        if (!file.exists() || !file.isDirectory()) {
            throw new ContributionReadException(contribution.getLocation());
        }
        return file;
    }

}
