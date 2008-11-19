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

package org.apache.tuscany.sca.node.osgi.launcher;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.felix.framework.Felix;
import org.apache.felix.framework.cache.BundleCache;
import org.apache.felix.framework.util.FelixConstants;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;

/**
 * 
 */
public class FelixOSGiHost implements OSGiHost {

    private Felix felix;
    private LauncherBundleActivator activator;
    //private ClassLoader tccl;

    private final static String systemPackages =
        "org.osgi.framework; version=1.3.0," + "org.osgi.service.packageadmin; version=1.2.0, "
            + "org.osgi.service.startlevel; version=1.0.0, "
            + "org.osgi.service.url; version=1.0.0, "
            + "org.osgi.util.tracker; version=1.3.2, "
            + "javax.xml, "
            + "javax.xml.datatype, "
            + "javax.xml.namespace, "
            + "javax.xml.parsers, "
            + "javax.xml.transform, "
            + "javax.xml.transform.dom, "
            + "javax.xml.transform.sax, "
            + "javax.xml.transform.stream, "
            + "javax.xml.validation, "
            + "javax.xml.xpath, "
            // Force the classes to be imported from the system bundle
            // + "javax.xml.stream, "
            // + "javax.xml.stream.util, "
            + "javax.sql,"
            + "org.w3c.dom, "
            + "org.xml.sax, "
            + "org.xml.sax.ext, "
            + "org.xml.sax.helpers, "
            + "javax.security.auth, "
            + "javax.security.cert, "
            + "javax.security.auth.login, "
            + "javax.security.auth.callback, "
            + "javax.naming, "
            + "javax.naming.spi, "
            + "javax.naming.directory, "
            + "javax.management, "
            + "javax.imageio, "
            + "sun.misc, "
            + "javax.net, "
            + "javax.net.ssl, "
            + "javax.crypto, "
            + "javax.rmi, "
            + "javax.transaction, "
            + "javax.transaction.xa";

    public LauncherBundleActivator getActivator() {
        if (activator == null) {
            activator = new LauncherBundleActivator();
        }
        return activator;
    }

    public void setActivator(LauncherBundleActivator activator) {
        this.activator = activator;
    }

    public BundleContext start() {
        try {
            startup();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        BundleContext bundleContext = felix.getBundleContext();
        return bundleContext;
    }

    public void stop() {
        try {
            shutdown();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private void startup() throws BundleException {
        if (felix != null) {
            throw new IllegalStateException("Felix is already running.");
        }

        // Create a configuration property map.
        Map<String, String> configMap = new HashMap<String, String>();
        // Configure the Felix instance to be embedded.
        configMap.put(FelixConstants.EMBEDDED_EXECUTION_PROP, "true");
        // Add core OSGi packages to be exported from the class path
        // via the system bundle.
        configMap.put(Constants.FRAMEWORK_SYSTEMPACKAGES, systemPackages);
        // Explicitly specify the directory to use for caching bundles.
        configMap.put(BundleCache.CACHE_PROFILE_DIR_PROP, "target/.felix");
        List<BundleActivator> list = new ArrayList<BundleActivator>();

        list.add(getActivator());

        // Now create an instance of the framework with
        // our configuration properties and activator.
        felix = new Felix(configMap, list);

        // Now start Felix instance.
        felix.start();

        //tccl = Thread.currentThread().getContextClassLoader();
        //Thread.currentThread().setContextClassLoader(getContextClassLoader(felix.getBundleContext()));

    }

    private ClassLoader getContextClassLoader(BundleContext bundleContext) {
        for (Bundle b : bundleContext.getBundles()) {
            if ("org.apache.tuscany.sca.extensibility.osgi".equals(b.getSymbolicName())) {
                try {
                    b.start();
                    Class<?> discovererClass = b.loadClass("org.apache.tuscany.sca.extensibility.ServiceDiscovery");
                    Method getInstance = discovererClass.getMethod("getInstance");
                    Object instance = getInstance.invoke(null);
                    Method getter = discovererClass.getMethod("getServiceDiscoverer");
                    Object discoverer = getter.invoke(instance);

                    Method getCL = discoverer.getClass().getMethod("getContextClassLoader");
                    ClassLoader cl = (ClassLoader)getCL.invoke(discoverer);
                    return cl;
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
        }
        return null;
    }

    private void shutdown() throws BundleException {
        if (felix != null) {
            felix.stopAndWait();
        }
        //Thread.currentThread().setContextClassLoader(tccl);
    }

}
