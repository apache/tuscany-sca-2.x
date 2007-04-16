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

package org.apache.tuscany.host.embedded.impl;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.apache.tuscany.contribution.service.util.FileHelper;
import org.apache.tuscany.host.AbstractRuntimeInfo;

/**
 * @version $Rev$ $Date$
 */
public class SimpleRuntimeInfoImpl extends AbstractRuntimeInfo implements SimpleRuntimeInfo {
    private ClassLoader classLoader;
    private String compositePath;
    private URL applicationSCDL;
    private URL contributionURL;
    private URI contributionURI;

    /**
     * @param classLoader
     * @param compositePath
     * @param extensions
     * @param applicationSCDL
     * @param systemSCDL
     */
    public SimpleRuntimeInfoImpl(ClassLoader classLoader,
                                 URI contributionURI,
                                 URL applicationSCDL,
                                 String compositePath) {
        this(classLoader, compositePath);
        this.applicationSCDL = applicationSCDL;
        this.contributionURI = contributionURI;
    }

    public SimpleRuntimeInfoImpl(ClassLoader classLoader, String compositePath) {
        super(URI.create("sca://domain/local"), null, null, false, "simple");
        if (classLoader != null) {
            this.classLoader = classLoader;
        } else {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        this.compositePath = compositePath != null ? compositePath : APPLICATION_SCDL;
        getApplicationSCDL();
        this.contributionURI = URI.create("/default");
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public String getCompositePath() {
        return compositePath;
    }

    public final URL getApplicationSCDL() {
        if (applicationSCDL == null) {
            applicationSCDL = classLoader.getResource(compositePath);
            if (applicationSCDL == null) {
                applicationSCDL = classLoader.getResource(APPLICATION_SCDL);
                if (applicationSCDL != null) {
                    if (compositePath == null) {
                        compositePath = APPLICATION_SCDL;
                    }
                }
                if (applicationSCDL == null) {
                    throw new IllegalArgumentException("Application composite not found: " + APPLICATION_SCDL);
                }
            }
        }
        return applicationSCDL;
    }

    public URL getContributionRoot() {
        if (contributionURL == null) {
            contributionURL = getContributionLocation(getApplicationSCDL(), compositePath);
        }
        return contributionURL;
    }

    private static URL getContributionLocation(URL applicationSCDL, String compositePath) {
        URL root = null;
        // "jar:file://....../something.jar!/a/b/c/app.composite"
        try {
            String scdlUrl = applicationSCDL.toExternalForm();
            String protocol = applicationSCDL.getProtocol();
            if ("file".equals(protocol)) {
                // directory contribution
                if (scdlUrl.endsWith(compositePath)) {
                    String location = scdlUrl.substring(0, scdlUrl.lastIndexOf(compositePath));
                    // workaround from evil url/uri form maven
                    root = FileHelper.toFile(new URL(location)).toURI().toURL();
                }

            } else if ("jar".equals(protocol)) {
                // jar contribution
                String location = scdlUrl.substring(4, scdlUrl.lastIndexOf("!/"));
                // workaround from evil url/uri form maven
                root = FileHelper.toFile(new URL(location)).toURI().toURL();
            }
        } catch (MalformedURLException mfe) {
            throw new IllegalArgumentException(mfe);
        }

        return root;
    }

    public URI getContributionURI() {
        return contributionURI;
    }
}
