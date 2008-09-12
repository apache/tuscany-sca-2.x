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

import static org.apache.tuscany.sca.node.equinox.launcher.NodeLauncherUtil.bundleLocation;
import static org.apache.tuscany.sca.node.equinox.launcher.NodeLauncherUtil.string;
import static org.osgi.framework.Constants.BUNDLE_SYMBOLICNAME;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.adaptor.EclipseStarter;
import org.eclipse.core.runtime.adaptor.LocationManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * Wraps the Equinox runtime.
 */
public class EquinoxHost {
    private static Logger logger = Logger.getLogger(EquinoxHost.class.getName());

    private BundleContext bundleContext;
    private Bundle launcherBundle;
    private EquinoxLauncherBundleHelper launcherActivator;

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

    public BundleContext start() {
        try {
            // Configure Eclipse properties
            Map<Object, Object> props = new HashMap<Object, Object>();
            
            // Set system packages
            props.put("org.osgi.framework.system.packages", systemPackages);

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
            
            // Find the Tuscany JARs
            File tuscanyInstallDir = findTuscanyInstallDir();
            List<URL> urls;
            if (tuscanyInstallDir != null) {
                urls = JarFileFinder.findJarFiles(tuscanyInstallDir, new JarFileFinder.StandAloneJARFileNameFilter());
            } else {
                urls = JarFileFinder.getClassPathEntries(JarFileFinder.class.getClassLoader(), false);
            }

            // Sort out which are bundles (and not already installed) and which are just
            // regular JARs
            StringBuffer bundleFiles =  new StringBuffer();
            StringBuffer bundleNames =  new StringBuffer();
            StringBuffer jarFiles = new StringBuffer();
            for (URL url : urls) {
                File file = NodeLauncherUtil.file(url);
                String bundleName = getBundleName(file);
                if (bundleName != null) {
                    bundleFiles.append(url.toString() + ";");
                    bundleNames.append(bundleName + ";");
                } else {
                    if (file.isFile()) {
                        jarFiles.append(url.toString() + ";");
                    }
                }
            }
            props.put("org.apache.tuscany.sca.node.launcher.equinox.bundleFiles", bundleFiles.toString());
            props.put("org.apache.tuscany.sca.node.launcher.equinox.bundleNames", bundleNames.toString());
            props.put("org.apache.tuscany.sca.node.launcher.equinox.jarFiles", jarFiles.toString());
            
            EclipseStarter.setInitialProperties(props);
            
            // Start Eclipse
            bundleContext = EclipseStarter.startup(new String[]{}, null);
            
            // Install the launcher bundle
            String bundleLocation = bundleLocation();
            logger.info("Installing launcher bundle: " + bundleLocation);
            launcherBundle = bundleContext.installBundle(bundleLocation);
            logger.info("Starting bundle: " + string(launcherBundle, false));
            launcherBundle.start();
            
            // Manually call the LauncherBundleActivator for now
            launcherActivator = new EquinoxLauncherBundleHelper();
            launcherActivator.start(launcherBundle.getBundleContext());
            
            // Start all bundles for now to help diagnose any class loading issues
            long activateStart = System.currentTimeMillis();
            for (Bundle bundle: bundleContext.getBundles()) {
                if ((bundle.getState() & Bundle.ACTIVE) == 0) {
                    logger.info("Starting bundle: " + string(bundle, false));
                    try {
                        bundle.start();
                    } catch (Exception e) {
                        logger.log(Level.SEVERE, e.getMessage(), e);
                    }
                    logger.info("Bundle: " + string(bundle, false));
                }
            }
            logger.info("Tuscany bundles are started in " + (System.currentTimeMillis() - activateStart) + " ms.");
            return bundleContext;
            
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
    
    public void stop() {
        try {
            
            // Uninstall the launcher bundle
            if (launcherActivator != null) {
                launcherActivator.stop(launcherBundle.getBundleContext());
            }
            if (launcherBundle != null) {
                logger.info("Uninstalling bundle: " + string(launcherBundle, false));
                launcherBundle.uninstall();
            }

            // Shutdown Eclipse
            EclipseStarter.shutdown();
            
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static File findTuscanyInstallDir() throws IOException {
        String tuscanyDirName = JarFileFinder.getProperty(JarFileFinder.TUSCANY_HOME);
        if (tuscanyDirName != null) {
            File tuscanyInstallDir = new File(tuscanyDirName);
            if (tuscanyInstallDir.exists() && tuscanyInstallDir.isDirectory())
                return tuscanyInstallDir;
        }
        return null;
    }

    /**
     * Returns the name of a bundle, or null if the given file is not a bundle.
     *  
     * @param file
     * @return
     * @throws IOException
     */
    private static String getBundleName(File file) throws IOException {
        if (!file.exists()) {
            return null;
        }
        String bundleName = null;
        if (file.isDirectory()) {
            File mf = new File(file, "META-INF/MANIFEST.MF");
            if (mf.isFile()) {
                Manifest manifest = new Manifest(new FileInputStream(mf));
                bundleName = manifest.getMainAttributes().getValue(BUNDLE_SYMBOLICNAME);
            }
        } else {
            JarFile jar = new JarFile(file, false);
            Manifest manifest = jar.getManifest();
            bundleName = manifest.getMainAttributes().getValue(BUNDLE_SYMBOLICNAME);
            jar.close();
        }
        if (bundleName == null) {
            return bundleName;
        }
        int sc = bundleName.indexOf(';');
        if (sc != -1) {
            bundleName = bundleName.substring(0, sc);
        }
        return bundleName;
    }

}
