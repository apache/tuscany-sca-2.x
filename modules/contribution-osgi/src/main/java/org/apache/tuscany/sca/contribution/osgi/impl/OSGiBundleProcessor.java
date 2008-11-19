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
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.osgi.BundleReference;
import org.apache.tuscany.sca.osgi.runtime.OSGiRuntime;

/**
 * OSGi bundle processor
 *
 * @version $Rev$ $Date$
 */
public class OSGiBundleProcessor {

    private boolean initializedOSGi;
    private OSGiRuntime osgiRuntime;

    public OSGiBundleProcessor() {
    }

    public Object installContributionBundle(Contribution contribution) {

        JarInputStream jar = null;
        Object bundle = null;

        try {

            URL contribURL = new URL(contribution.getLocation());
            jar = new JarInputStream(contribURL.openStream());

            Manifest manifest = jar.getManifest();
            if (manifest != null && manifest.getMainAttributes()
                .containsKey(new Attributes.Name("Bundle-SymbolicName"))) {

                initialize();
                if (osgiRuntime != null)
                    bundle = osgiRuntime.installBundle(contribURL.toString(), null);
            }
        } catch (Exception e) {
            // If OSGi cannot process the jar, treat the bundle as a plain jar file.
        } finally {

            try {
                if (jar != null)
                    jar.close();
            } catch (IOException e) {
            }
        }

        return bundle;
    }

    public BundleReference installNestedBundle(Contribution contribution,
                                               String bundleSymbolicName,
                                               String bundleVersion) {

        BundleReference bundleReference = null;

        initialize();
        if (osgiRuntime == null)
            return null;

        List<Artifact> artifacts = contribution.getArtifacts();
        for (Artifact a : artifacts) {
            if (a.getURI().endsWith(".jar")) {

                InputStream stream;
                JarInputStream jar = null;
                Object name;
                Object version;
                try {

                    URL artifactURL = new URL(a.getLocation());
                    stream = artifactURL.openStream();
                    jar = new JarInputStream(artifactURL.openStream());
                    Manifest manifest = jar.getManifest();
                    name = manifest.getMainAttributes().get(new Attributes.Name("Bundle-SymbolicName"));
                    version = manifest.getMainAttributes().get(new Attributes.Name("Bundle-Version"));

                    if (bundleSymbolicName.equals(name) && (bundleVersion == null || version == null || bundleVersion
                        .equals(version))) {

                        Object bundle = osgiRuntime.installBundle(a.getLocation(), stream);

                        bundleReference = new BundleReference(bundle, bundleSymbolicName, bundleVersion, a.getURI());

                        break;
                    }

                } catch (Exception e) {

                    // If OSGi cannot process the jar, treat the bundle as a plain jar file.
                } finally {
                    try {
                        if (jar != null)
                            jar.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        return bundleReference;
    }

    private void initialize() {
        try {
            if (!initializedOSGi) {
                initializedOSGi = true;
                osgiRuntime = OSGiRuntime.getRuntime();
            }
        } catch (Exception e) {
        }
    }
}
