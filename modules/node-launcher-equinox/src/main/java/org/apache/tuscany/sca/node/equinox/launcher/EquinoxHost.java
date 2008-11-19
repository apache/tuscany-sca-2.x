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

package org.apache.tuscany.sca.node.equinox.launcher;

import static java.lang.System.currentTimeMillis;
import static java.lang.System.setProperty;
import static org.apache.tuscany.sca.node.equinox.launcher.NodeLauncherUtil.bundleName;
import static org.apache.tuscany.sca.node.equinox.launcher.NodeLauncherUtil.file;
import static org.apache.tuscany.sca.node.equinox.launcher.NodeLauncherUtil.fixupBundle;
import static org.apache.tuscany.sca.node.equinox.launcher.NodeLauncherUtil.runtimeClasspathEntries;
import static org.apache.tuscany.sca.node.equinox.launcher.NodeLauncherUtil.string;
import static org.apache.tuscany.sca.node.equinox.launcher.NodeLauncherUtil.thirdPartyLibraryBundle;
import static org.apache.tuscany.sca.node.equinox.launcher.NodeLauncherUtil.thisBundleLocation;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.adaptor.EclipseStarter;
import org.eclipse.core.runtime.adaptor.LocationManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * Wraps the Equinox runtime.
 */
class EquinoxHost {
    private static Logger logger = Logger.getLogger(EquinoxHost.class.getName());

    private BundleContext bundleContext;
    private Bundle launcherBundle;
    private boolean startedEclipse;
    private List<String> bundleFiles =  new ArrayList<String>();
    private List<String> bundleNames =  new ArrayList<String>();
    private List<String> jarFiles = new ArrayList<String>();
    private Map<String, Bundle> allBundles = new HashMap<String, Bundle>();
    private List<Bundle> installedBundles = new ArrayList<Bundle>();

    private final static String systemPackages =
            "org.osgi.framework; version=1.3.0,"
            + "org.osgi.service.packageadmin; version=1.2.0, "
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
            + "javax.xml.stream, "
            + "javax.xml.stream.util, "
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
            //+ "javax.transaction, "
            //+ "javax.transaction.xa, "
            + "org.omg.CosNaming, "
            + "org.omg.CORBA, "
            + "org.omg.CORBA.portable, "
            + "org.omg.PortableServer, "
            + "org.omg.CosNaming, "
            + "org.omg.CosNaming.NamingContextExtPackage, "
            + "org.omg.CosNaming.NamingContextPackage, "
            + "org.omg.CORBA_2_3.portable, "
            + "org.omg.IOP, "
            + "org.omg.PortableInterceptor, "
            + "org.omg.stub.java.rmi, "
            + "javax.rmi.CORBA";
    
    /**
     * Start the Equinox host.
     * 
     * @return
     */
    BundleContext start() {
        try {
            if (!EclipseStarter.isRunning()) {

                String version = System.getProperty("java.specification.version");
                String profile = "J2SE-1.5.profile";
                if (version.startsWith("1.6")) {
                    profile = "JavaSE-1.6.profile";
                }
                Properties props = new Properties();
                InputStream is = getClass().getResourceAsStream(profile);
                if (is != null) {
                    props.load(is);
                    is.close();
                }
                // Configure Eclipse properties
                
                // Use the boot classloader as the parent classloader
                props.put("osgi.contextClassLoaderParent", "boot");
                
                // Set startup properties
                props.put(EclipseStarter.PROP_CLEAN, "true");
                
                if (logger.isLoggable(Level.FINE)) {
                    props.put("osgi.console", "8085");
                }
                
                // Set location properties
                // FIXME Use proper locations
                props.put(LocationManager.PROP_INSTANCE_AREA, new File("target/workspace").toURI().toString());
                props.put(LocationManager.PROP_INSTALL_AREA, new File("target/eclipse/install").toURI().toString());
                props.put(LocationManager.PROP_CONFIG_AREA, new File("target/eclipse/config").toURI().toString());
                props.put(LocationManager.PROP_USER_AREA, new File("target/eclipse/user").toURI().toString());
                
                EclipseStarter.setInitialProperties(props);
                
                // Start Eclipse
                bundleContext = EclipseStarter.startup(new String[]{}, null);
                startedEclipse = true;
                
            } else {
                
                // Get bundle context from the running Eclipse instance 
                bundleContext = EclipseStarter.getSystemBundleContext();
            }
            
            // Determine the runtime classpath entries
            Set<URL> urls;
            if (!startedEclipse) {
                
                // Use classpath entries from a distribution if there is one and the modules
                // directories available in a dev environment for example
                urls = runtimeClasspathEntries(true, false, true);
            } else {
                
                // Use classpath entries from a distribution if there is one and the classpath
                // entries on the current application's classloader
                urls = runtimeClasspathEntries(true, true, false);
            }

            // Sort out which are bundles (and not already installed) and which are just
            // regular JARs
            for (URL url : urls) {
                File file = file(url);
                String bundleName = bundleName(file);
                if (bundleName != null) {
                    bundleFiles.add(url.toString());
                    bundleNames.add(bundleName);
                } else {
                    if (file.isFile()) {
                        jarFiles.add(url.toString());
                    }
                }
            }

            // Get the already installed bundles
            for (Bundle bundle: bundleContext.getBundles()) {
                allBundles.put(bundle.getSymbolicName(), bundle);
            }

            // Install the launcher bundle if necessary
            String launcherBundleName = "org.apache.tuscany.sca.node.launcher.equinox";
            String launcherBundleLocation;
            launcherBundle = allBundles.get(launcherBundleName);
            if (launcherBundle == null) {
                launcherBundleLocation = thisBundleLocation();
                logger.info("Installing launcher bundle: " + launcherBundleLocation);
                fixupBundle(launcherBundleLocation);
                launcherBundle = bundleContext.installBundle(launcherBundleLocation);
                allBundles.put(launcherBundleName, launcherBundle);
                installedBundles.add(launcherBundle);
            } else {
                logger.info("Launcher bundle is already installed: " + string(launcherBundle, false));
                launcherBundleLocation = thisBundleLocation(launcherBundle);
            }
            
            // Install the Tuscany bundles
            long start = currentTimeMillis();

            // FIXME: SDO bundles dont have the correct dependencies
            setProperty("commonj.sdo.impl.HelperProvider", "org.apache.tuscany.sdo.helper.HelperProviderImpl");

            // Install a single 'library' bundle for the third-party JAR files
            String libraryBundleName = "org.apache.tuscany.sca.node.launcher.equinox.libraries";
            Bundle libraryBundle = allBundles.get(libraryBundleName);
            if (libraryBundle == null) {
                logger.info("Generating third-party library bundle.");
                for (String jarFile: jarFiles) {
                    if (logger.isLoggable(Level.FINE)) {
                        logger.fine("Adding third-party jar: " + jarFile);
                    }
                }
                long libraryStart = currentTimeMillis();
                InputStream library = thirdPartyLibraryBundle(jarFiles);
                logger.info("Third-party library bundle generated in " + (currentTimeMillis() - libraryStart) + " ms.");
                libraryStart = currentTimeMillis();
                libraryBundle = bundleContext.installBundle("org.apache.tuscany.sca.node.launcher.equinox.libraries", library);
                allBundles.put(libraryBundleName, libraryBundle);
                installedBundles.add(libraryBundle);
                logger.info("Third-party library bundle installed in " + (currentTimeMillis() - libraryStart) + " ms: " + string(libraryBundle, false));
            } else {
                logger.info("Third-party library bundle is already installed: " + string(libraryBundle, false));
            }
            
            // Install all the other bundles that are not already installed
            for (int i =0, n = bundleFiles.size(); i < n; i++) {
                String bundleFile = bundleFiles.get(i);
                fixupBundle(bundleFile);
            }
            for (int i =0, n = bundleFiles.size(); i < n; i++) {
                String bundleFile = bundleFiles.get(i);
                String bundleName = bundleNames.get(i);
                if (bundleName.contains("org.eclipse.jdt.junit")) {
                    continue;
                }
                Bundle bundle = allBundles.get(bundleName);
                if (bundle == null) {
                    long installStart = currentTimeMillis();
                    String location = bundleFile;
                    if (bundleFile.startsWith("file:")) {
                        File target = file(new URL(bundleFile));
                        // Use a special "reference" scheme to install the bundle as a reference
                        // instead of copying the bundle 
                        location = "reference:file:/" + target.getPath();
                    }
                    bundle = bundleContext.installBundle(location);
                    if (logger.isLoggable(Level.FINE)) {
                        logger.fine("Bundle installed in " + (currentTimeMillis() - installStart) + " ms: " + string(bundle, false));
                    }
                    logger.info("Bundle installed in " + (currentTimeMillis() - installStart) + " ms: " + string(bundle, false));
                    allBundles.put(bundleName, bundle);
                    installedBundles.add(bundle);
                }
            }

            long end = currentTimeMillis();
            logger.info("Tuscany bundles are installed in " + (end - start) + " ms.");
            
            // Start the extensiblity and launcher bundles
            long activateStart = System.currentTimeMillis();
            String extensibilityBundleName = "org.apache.tuscany.sca.extensibility.equinox";
            Bundle extensibilityBundle = allBundles.get(extensibilityBundleName);
            if ((extensibilityBundle.getState() & Bundle.ACTIVE) == 0) {
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("Starting bundle: " + string(extensibilityBundle, false));
                }
                extensibilityBundle.start();
            } else if (logger.isLoggable(Level.FINE)) {
                logger.fine("Bundle is already started: " + string(extensibilityBundle, false));
            }
            if ((launcherBundle.getState() & Bundle.ACTIVE) == 0) {
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("Starting bundle: " + string(launcherBundle, false));
                }
                launcherBundle.start();
            } else if (logger.isLoggable(Level.FINE)) {
                logger.fine("Bundle is already started: " + string(launcherBundle, false));
            }

            // Start all our bundles for now to help diagnose any class loading issues
//            for (Bundle bundle: bundleContext.getBundles()) {
//                if (bundle.getSymbolicName().startsWith("org.apache.tuscany.sca")) {
//                    if ((bundle.getState() & Bundle.ACTIVE) == 0) {
//                        if (logger.isLoggable(Level.FINE)) {
//                            logger.fine("Starting bundle: " + string(bundle, false));
//                        }
//                        try {
//                            //bundle.start();
//                        } catch (Exception e) {
//                            logger.log(Level.SEVERE, e.getMessage(), e);
//                            // throw e;
//                        }
//                        if (logger.isLoggable(Level.FINE)) {
//                            logger.fine("Bundle: " + string(bundle, false));
//                        }
//                    }
//                }
//            }
//            logger.info("Tuscany bundles are started in " + (System.currentTimeMillis() - activateStart) + " ms.");
            return bundleContext;
            
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Stop the Equinox host.
     */
    void stop() {
        try {
            
            // Uninstall all the bundles we've installed
            for (int i = installedBundles.size() -1; i >= 0; i--) {
                Bundle bundle = installedBundles.get(i);
                try {
                    if (logger.isLoggable(Level.FINE)) {
                        logger.fine("Uninstalling bundle: " + string(bundle, false));
                    }
                    bundle.uninstall();
                } catch (Exception e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
            }
            installedBundles.clear();
            
            // Shutdown Eclipse if we started it ourselves
            if (startedEclipse) {
                startedEclipse = false;
                EclipseStarter.shutdown();
            }
            
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
