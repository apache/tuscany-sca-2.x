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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;

/**
 * The Bundle Activator that receives the BundleContext
 */
public class OSGiBundleActivator implements BundleActivator {
    private static BundleContext bundleContext;

    public static BundleContext getBundleContext() {
        return bundleContext;
    }

    public void start(BundleContext context) throws Exception {
        bundleContext = context;
    }

    public void stop(BundleContext context) throws Exception {
        bundleContext = null;
    }

    public static Bundle findBundle(URL bundleURL) {
        if (bundleContext == null) {
            return null;
        }
        Bundle[] installedBundles = bundleContext.getBundles();
        for (Bundle bundle : installedBundles) {
            URL root = bundle.getEntry("/");
            if (root != null && root.getHost() != null && root.getHost().equals(bundleURL.getHost())) {
                return bundle;
            }
        }
        return null;
    }

    public static Bundle findBundle(String symbolicName, String version) {
        if (bundleContext == null) {
            return null;
        }
        Bundle[] bundles = bundleContext.getBundles();
        if (version == null) {
            version = "0.0.0";
        }
        for (Bundle b : bundles) {
            String v = (String)b.getHeaders().get(Constants.BUNDLE_VERSION);
            if (v == null) {
                v = "0.0.0";
            }
            if (b.getSymbolicName().equals(symbolicName) && (version.equals("0.0.0") || v.equals(version))) {
                return b;
            }
        }
        return null;
    }

    public static Bundle installBundle(String location, InputStream is) throws BundleException {
        getBundleContext();
        return bundleContext.installBundle(location, is);
    }

    public static Bundle installBundle(String location) throws BundleException, IOException {
        getBundleContext();

        URL url = new URL(location);
        Bundle bundle = null;
        InputStream is = url.openStream();
        JarInputStream jar = new JarInputStream(is);

        Manifest manifest = jar.getManifest();
        jar.close();
        if (manifest != null) {
            String symbolicName = manifest.getMainAttributes().getValue(Constants.BUNDLE_SYMBOLICNAME);
            String version = manifest.getMainAttributes().getValue(Constants.BUNDLE_VERSION);
            if (symbolicName != null) {
                bundle = findBundle(symbolicName, version);
                if (bundle != null) {
                    return bundle;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
        try {
            is = url.openStream();
            bundle = bundleContext.installBundle(location, is);
        } finally {
            is.close();
        }
        return bundle;
    }

    public static Bundle findBundleByLocation(String bundleLocation) {
        if (bundleContext != null) {
            Bundle[] installedBundles = bundleContext.getBundles();
            for (Bundle bundle : installedBundles) {
                if (bundle.getLocation().equals(bundleLocation))
                    return bundle;
            }
        }
        return null;
    }

    public static Bundle findBundle(String bundleLocation) {
        if (bundleContext != null) {
            if (bundleLocation.startsWith("bundle:") || bundleLocation.startsWith("bundleresource:")
                || bundleLocation.startsWith("bundleentry:")) {
                try {
                    return findBundle(new URL(bundleLocation));
                } catch (MalformedURLException e) {
                    // ignore
                }
            } else {
                return findBundleByLocation(bundleLocation);
            }
        }
        return null;
    }

}
