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

package org.apache.tuscany.sca.node.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Map;

import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.service.util.FileHelper;
import org.apache.tuscany.sca.node.SCAContribution;

/**
 * NodeUtil
 *
 * @version $Rev: $ $Date: $
 */
public class NodeUtil {

    static URL getResource(final ClassLoader classLoader, final String compositeURI) {
        return AccessController.doPrivileged(new PrivilegedAction<URL>() {
            public URL run() {
                return classLoader.getResource(compositeURI);
            }
        });
    }

    static Contribution createContribution(ContributionFactory contributionFactory, SCAContribution c) {
        Contribution contribution = contributionFactory.createContribution();
        contribution.setURI(c.getURI());
        contribution.setLocation(c.getLocation());
        contribution.setUnresolved(true);
        return contribution;
    }

    /**
     * Escape the space in URL string
     * @param uri
     * @return
     */
    static URI createURI(String uri) {
        if (uri.indexOf(' ') != -1) {
            uri = uri.replace(" ", "%20");
        }
        return URI.create(uri);
    }

    /**
     * Collect JARs on the classpath of a URLClassLoader
     * @param urls
     * @param cl
     */
    static void collectJARs(Map<String, URL> urls, ClassLoader cl) {
        if (cl == null) {
            return;
        }
    
        // Collect JARs from the URLClassLoader's classpath
        if (cl instanceof URLClassLoader) {
            URL[] jarURLs = ((URLClassLoader)cl).getURLs();
            if (jarURLs != null) {
                for (URL jarURL : jarURLs) {
                    String file = jarURL.getPath();
                    int i = file.lastIndexOf('/');
                    if (i != -1 && i < file.length() - 1) {
                        file = file.substring(i + 1);
                        urls.put(file, jarURL);
                    }
                }
            }
        }
    
        // Collect JARs from the parent ClassLoader
        collectJARs(urls, cl.getParent());
    }

    static URL getContributionURL(URL contributionArtifactURL, String contributionArtifactPath) {
        URL contributionURL = null;
        // "jar:file://....../something.jar!/a/b/c/app.composite"
        try {
            String url = contributionArtifactURL.toExternalForm();
            String protocol = contributionArtifactURL.getProtocol();
            if ("file".equals(protocol)) {
                // directory contribution
                if (url.endsWith(contributionArtifactPath)) {
                    final String location = url.substring(0, url.lastIndexOf(contributionArtifactPath));
                    // workaround from evil URL/URI form Maven
                    // contributionURL = FileHelper.toFile(new URL(location)).toURI().toURL();
                    // Allow privileged access to open URL stream. Add FilePermission to added to
                    // security policy file.
                    try {
                        contributionURL = AccessController.doPrivileged(new PrivilegedExceptionAction<URL>() {
                            public URL run() throws IOException {
                                return FileHelper.toFile(new URL(location)).toURI().toURL();
                            }
                        });
                    } catch (PrivilegedActionException e) {
                        throw (MalformedURLException)e.getException();
                    }
                }
    
            } else if ("jar".equals(protocol)) {
                // jar contribution
                String location = url.substring(4, url.lastIndexOf("!/"));
                // workaround for evil URL/URI from Maven
                contributionURL = FileHelper.toFile(new URL(location)).toURI().toURL();
    
            } else if ("wsjar".equals(protocol)) {
                // See https://issues.apache.org/jira/browse/TUSCANY-2219
                // wsjar contribution 
                String location = url.substring(6, url.lastIndexOf("!/"));
                // workaround for evil url/uri from maven 
                contributionURL = FileHelper.toFile(new URL(location)).toURI().toURL();
    
            } else if ("zip".equals(protocol)) {
                // See https://issues.apache.org/jira/browse/TUSCANY-2598
                // zip contribution, remove the zip prefix and pad with file:
                String location = "file:"+url.substring(4, url.lastIndexOf("!/"));
                contributionURL = FileHelper.toFile(new URL(location)).toURI().toURL();
                
            } else if (protocol != null && (protocol.equals("bundle") || protocol.equals("bundleresource"))) {
                contributionURL =
                    new URL(contributionArtifactURL.getProtocol(), contributionArtifactURL.getHost(),
                            contributionArtifactURL.getPort(), "/");
            }
        } catch (MalformedURLException mfe) {
            throw new IllegalArgumentException(mfe);
        }
        return contributionURL;
    }

}
