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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.apache.tuscany.sca.common.java.io.IOHelper;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.PackageType;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.scanner.ContributionScanner;

/**
 * JAR Contribution processor.
 *
 * @version $Rev$ $Date$
 */
public class JarContributionScanner implements ContributionScanner {

    public JarContributionScanner() {
    }

    public String getContributionType() {
        return PackageType.JAR;
    }

    public URL getArtifactURL(Contribution contribution, String artifact) throws ContributionReadException {
        try {
            URL url;
            if (contribution.toString().startsWith("jar:")) {
                url = new URL(new URL(contribution.getLocation()), artifact.toString());
            } else {
                url = new URL("jar:" + contribution.getLocation() + "!/" + artifact);
            }
            return url;
        } catch (MalformedURLException e) {
            throw new ContributionReadException(e);
        }
    }

    public List<String> scan(Contribution contribution) throws ContributionReadException {

        // Assume the URL references a JAR file
        try {
            URL url = new URL(contribution.getLocation());
            JarInputStream jar = new JarInputStream(IOHelper.openStream(url));
            try {
                Set<String> names = new HashSet<String>();
                while (true) {
                    JarEntry entry = jar.getNextJarEntry();
                    if (entry == null) {
                        // EOF
                        break;
                    }

                    String name = entry.getName();
                    if (name.length() != 0 && !name.startsWith(".")) {

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
                                if (name.length() != 0 && !names.contains(name)) {
                                    names.add(name);
                                } else {
                                    break;
                                }
                            }
                        }
                    }
                }

                // Return list of URIs
                List<String> artifacts = new ArrayList<String>(names);
                contribution.getTypes().add(getContributionType());
                return artifacts;

            } finally {
                jar.close();
            }
        } catch (IOException e) {
            throw new ContributionReadException(e);
        }
    }

    public void postProcess(Contribution contribution) {
    }
}
