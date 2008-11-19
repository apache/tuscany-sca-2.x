package org.apache.tuscany.sca.node.equinox.launcher;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;

/**
 * Bundle activator which installs Tuscany modules into an OSGi runtime.
 *
 */
public class EquinoxLauncherBundleHelper implements BundleListener {
    private static Logger logger = Logger.getLogger(EquinoxLauncherBundleHelper.class.getName());

    private List<Bundle> installedBundles = new ArrayList<Bundle>();
    private BundleContext bundleContext;

    public EquinoxLauncherBundleHelper() {
        super();
    }

    public void start(BundleContext bundleContext) throws Exception {
        this.bundleContext = bundleContext;
        this.bundleContext.addBundleListener(this);

        // Install the Tuscany bundles
        long start = System.currentTimeMillis();

        // FIXME: SDO bundles dont have the correct dependencies
        System.setProperty("commonj.sdo.impl.HelperProvider", "org.apache.tuscany.sdo.helper.HelperProviderImpl");

        // Get the list of JAR files to install
        String jarFilesProperty = System.getProperty("org.apache.tuscany.sca.node.launcher.equinox.jarFiles");
        String[] jarFiles = jarFilesProperty.split(";");
        
        // Create a single 'library' bundle for them
        long libraryStart = System.currentTimeMillis();
        //InputStream library = NodeLauncherUtil.libraryBundle(jarFiles);
        Bundle libraryBundle = bundleContext.installBundle("org.apache.tuscany.sca.node.launcher.equinox.libraries", new ByteArrayInputStream(new byte[0]));
        logger.info("Third-party library bundle installed in " + (System.currentTimeMillis() - libraryStart) + " ms: " + NodeLauncherUtil.string(libraryBundle, false));
        installedBundles.add(libraryBundle);
        
        // Get the set of already installed bundles
        Set<String> alreadyInstalledBundleNames = new HashSet<String>();
        for (Bundle bundle: bundleContext.getBundles()) {
            alreadyInstalledBundleNames.add(bundle.getSymbolicName());
        }

        // Get the list of bundle files and names to install
        String bundleFilesProperty = System.getProperty("org.apache.tuscany.sca.node.launcher.equinox.bundleFiles");
        String[] bundleFiles = bundleFilesProperty.split(";");
        String bundleNamesProperty = System.getProperty("org.apache.tuscany.sca.node.launcher.equinox.bundleNames");
        String[] bundleNames = bundleNamesProperty.split(";");
        
        // Install all the bundles that are not already installed
        for (int i =0, n = bundleFiles.length; i < n; i++) {
            String bundleFile = bundleFiles[i];
            String bundleName = bundleNames[i];
            if (!alreadyInstalledBundleNames.contains(bundleName)) {
                if (bundleName.contains("org.eclipse.jdt.junit")) {
                    continue;
                }
                long installStart = System.currentTimeMillis();
                Bundle bundle = bundleContext.installBundle(bundleFile);
                logger.info("Bundle installed in " + (System.currentTimeMillis() - installStart) + " ms: " + NodeLauncherUtil.string(bundle, false));
                installedBundles.add(bundle);
            }
        }

        long end = System.currentTimeMillis();
        logger.info("Tuscany bundles are installed in " + (end - start) + " ms.");
    }

    public void stop(BundleContext bundleContext) throws Exception {
        
        // Uninstall all the bundles we've installed
        for (int i = installedBundles.size() -1; i >= 0; i--) {
            Bundle bundle = installedBundles.get(i);
            try {
                //if (logger.isLoggable(Level.FINE)) {
                logger.info("Uninstalling bundle: " + NodeLauncherUtil.string(bundle, false));
                //}
                bundle.uninstall();
            } catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        installedBundles.clear();

        this.bundleContext.removeBundleListener(this);
        this.bundleContext = null;
    }

    public void bundleChanged(BundleEvent event) {
    }

}
