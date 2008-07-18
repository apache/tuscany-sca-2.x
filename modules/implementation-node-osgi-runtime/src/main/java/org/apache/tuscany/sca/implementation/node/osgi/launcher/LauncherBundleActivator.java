package org.apache.tuscany.sca.implementation.node.osgi.launcher;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

/**
 * Bundle activator which installs Tuscany modules and 3rd party jars into an OSGi runtime.
 *
 */
public class LauncherBundleActivator implements BundleActivator, Constants {

    private static Logger logger = Logger.getLogger(LauncherBundleActivator.class.getName());
    private ArrayList<Bundle> tuscanyBundles = new ArrayList<Bundle>();

    private static final String[] immutableJars = {"bcprov"};

    public void start(BundleContext bundleContext) throws Exception {
        installTuscany(bundleContext);
    }

    public void stop(BundleContext bundleContext) throws Exception {
        for (Bundle bundle : tuscanyBundles) {
            try {
                bundle.stop();
            } catch (Exception e) {
                // Ignore error
            }
        }

        for (Bundle bundle : tuscanyBundles) {
            try {
                bundle.uninstall();
            } catch (Exception e) {
                // Ignore error
            }
        }

    }

    public void installTuscany(BundleContext bundleContext) {
        long start = System.currentTimeMillis();

        try {

            // FIXME: SDO bundles dont have the correct dependencies
            System.setProperty("commonj.sdo.impl.HelperProvider", "org.apache.tuscany.sdo.helper.HelperProviderImpl");
            File tuscanyInstallDir = findTuscanyInstallDir(bundleContext.getBundle());

            List<URL> urls =
                JarFileFinder.findJarFiles(tuscanyInstallDir, new JarFileFinder.StandAloneJARFileNameFilter());

            for (URL url : urls) {
                File file = new File(url.toURI());
                if (file.getName().startsWith("org.apache.felix.") || file.getName().startsWith("org.osgi.")) {
                    continue;
                }
                try {
                    Bundle bundle = createAndInstallBundle(bundleContext, file);
                    tuscanyBundles.add(bundle);
                } catch (Exception e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
            }
            
            long end = System.currentTimeMillis();
            logger.info("Tuscany bundles are installed in " + (end - start) + " ms.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File findTuscanyInstallDir(Bundle bundle) throws IOException {
        String tuscanyDirName = JarFileFinder.getProperty(JarFileFinder.TUSCANY_HOME);
        if (tuscanyDirName != null) {
            File tuscanyInstallDir = new File(tuscanyDirName);
            if (tuscanyInstallDir.exists() && tuscanyInstallDir.isDirectory())
                return tuscanyInstallDir;
        }

        String location = bundle.getLocation();

        if (location != null && location.startsWith("file:")) {
            File file = new File(URI.create(location));
            File tuscanyInstallDir = file.getParentFile();
            if (tuscanyInstallDir.exists() && tuscanyInstallDir.isDirectory())
                return tuscanyInstallDir;
        }
        if (this.getClass().getProtectionDomain() != null) {
            CodeSource codeSource = this.getClass().getProtectionDomain().getCodeSource();
            if (codeSource != null) {
                try {
                    File tuscanyInstallDir = new File(codeSource.getLocation().toURI());
                    if (tuscanyInstallDir.exists() && tuscanyInstallDir.isDirectory())
                        return tuscanyInstallDir;
                } catch (Exception e) {
                    // ignore
                }
            }
        }
        return null;
    }

    public Bundle createAndInstallBundle(BundleContext bundleContext, File bundleFile) throws Exception {
        logger.info("Installing bundle: " + bundleFile);
        long start = System.currentTimeMillis();
        String bundleLocation = bundleFile.toURI().toString();
        Manifest manifest = createBundleManifest(bundleFile);

        InputStream inStream = null;
        if (manifest != null) {
            // We need to repackage the bundle
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            JarOutputStream jarOut = new JarOutputStream(out, manifest);

            String classpath = manifest.getMainAttributes().getValue("Bundle-ClassPath");
            boolean embedded = classpath != null && !classpath.trim().equals(".");
            if (embedded) {
                addFileToJar(bundleFile, jarOut);
            } else {
                copyJar(bundleFile, jarOut);
            }

            jarOut.close();
            inStream = new ByteArrayInputStream(out.toByteArray());
        } else {
            // The file itself is already a bundle
            inStream = new FileInputStream(bundleFile);
        }

        try {
            Bundle bundle = bundleContext.installBundle(bundleLocation, inStream);
            logger.info("Bundle installed in " + (System.currentTimeMillis() - start) + " ms: " + bundleLocation);
            return bundle;
        } finally {
            inStream.close();
        }

    }

    private void addFileToJar(File file, JarOutputStream jarOut) throws IOException {
        JarEntry ze = new JarEntry(file.getName());
        jarOut.putNextEntry(ze);
        FileInputStream inStream = new FileInputStream(file);
        copy(inStream, jarOut);
    }

    private void copy(InputStream in, OutputStream out) throws IOException {
        byte[] readBuf = new byte[4096];
        int bytesRead;
        while ((bytesRead = in.read(readBuf)) > 0) {
            out.write(readBuf, 0, bytesRead);
        }
    }

    private void copyJar(File file, JarOutputStream jarOut) throws IOException {
        JarInputStream jarIn = new JarInputStream(new FileInputStream(file));
        ZipEntry ze;
        while ((ze = jarIn.getNextEntry()) != null) {
            // Skip the MANIFEST.MF
            if (ze.getName().equals("META-INF/MANIFEST.MF"))
                continue;
            jarOut.putNextEntry(ze);
            copy(jarIn, jarOut);
        }
        jarIn.close();
    }

    private Manifest createBundleManifest(File jarFile) throws Exception {

        if (!jarFile.exists()) {
            return null;
        }
        JarInputStream jar = new JarInputStream(new FileInputStream(jarFile));
        // Read the Manifest from the jar file
        Manifest manifest = jar.getManifest();
        jar.close();

        if (manifest == null) {
            // Create a new one if no Manifest is found
            manifest = new Manifest();
        } else {
            Attributes attributes = manifest.getMainAttributes();
            if (attributes.getValue(BUNDLE_SYMBOLICNAME) != null) {
                return null;
            }
        }

        // Check if we have an associated .mf file
        String name = jarFile.getName();
        int index = name.lastIndexOf('.');
        if (index != -1) {
            File mf = new File(jarFile.getParentFile(), name.substring(0, index) + ".mf");
            if (mf.isFile()) {
                FileInputStream is = new FileInputStream(mf);
                manifest.read(is);
                is.close();
            }
        }

        String jarFileName = jarFile.getName();
        boolean isImmutableJar = false;
        for (String immutableJar : immutableJars) {
            if (jarFileName.startsWith(immutableJar)) {
                isImmutableJar = true;
                break;
            }
        }

        Attributes attributes = manifest.getMainAttributes();
        if (attributes.getValue(BUNDLE_SYMBOLICNAME) == null) {
            String bundleSymbolicName = jarFile.getName();
            if (bundleSymbolicName.endsWith(".jar")) {
                bundleSymbolicName = bundleSymbolicName.substring(0, bundleSymbolicName.length() - 4);
            }
            attributes.putValue(BUNDLE_SYMBOLICNAME, bundleSymbolicName);
        } else {
            // Assume the jar is already a bundle
            return null;
        }

        if (attributes.getValue("Manifest-Version") == null) {
            attributes.putValue("Manifest-Version", "1.0");
        }

        if (attributes.getValue(BUNDLE_MANIFESTVERSION) == null) {
            attributes.putValue(BUNDLE_MANIFESTVERSION, "2");
        }
        
        if (isImmutableJar && attributes.getValue(BUNDLE_CLASSPATH) == null) {
            attributes.putValue(BUNDLE_CLASSPATH, ".," + jarFileName);
        }

        jar = new JarInputStream(new FileInputStream(jarFile));
        HashSet<String> packages = getPackagesInJar(jarFileName, jar);
        jar.close();
        String version = getJarVersion(jarFileName);

        //        attributes.remove(new Attributes.Name("Require-Bundle"));
        //        attributes.remove(new Attributes.Name("Import-Package"));

        if (attributes.getValue(BUNDLE_VERSION) == null) {
            attributes.putValue(BUNDLE_VERSION, version);
        }
        // Existing export statements in bundles may contain versions, so they should be used as is
        // SDO exports are not sufficient, and should be changed
        if (attributes.getValue(EXPORT_PACKAGE) == null || jarFileName.startsWith("tuscany-sdo-impl")) {
            String pkgs = packagesToString(packages, version);
            if (pkgs.length() > 0) {
                attributes.putValue(EXPORT_PACKAGE, pkgs);
                attributes.putValue(IMPORT_PACKAGE, packagesToString(packages, null));
            }
            // attributes.putValue("Import-Package", packagesToString(packages, null));
        }

        attributes.putValue(DYNAMICIMPORT_PACKAGE, "*");
        return manifest;
    }

    private HashSet<String> getPackagesInJar(String bundleName, JarInputStream jar) throws Exception {
        HashSet<String> packages = new HashSet<String>();
        ZipEntry entry;
        while ((entry = jar.getNextEntry()) != null) {
            String entryName = entry.getName();
            if (!entry.isDirectory() && entryName != null && entryName.length() > 0 && !entryName.startsWith(".")
            // && !entryName.startsWith("META-INF")
                && entryName.lastIndexOf("/") > 0) {
                String pkg = entryName.substring(0, entryName.lastIndexOf("/")).replace('/', '.');
                packages.add(pkg);

            }
        }
        // FIXME: Split package
        if (bundleName.startsWith("axis2-adb")) {
            packages.remove("org.apache.axis2.util");
        } else if (bundleName.startsWith("axis2-codegen")) {
            packages.remove("org.apache.axis2.wsdl");
            packages.remove("org.apache.axis2.wsdl.util");
        } else if (bundleName.startsWith("bsf-all")) {
            packages.remove("org.mozilla.javascript");
        }

        return packages;
    }

    private String packagesToString(HashSet<String> packages, String version) {

        StringBuilder pkgBuf = new StringBuilder();
        for (String pkg : packages) {
            if (pkgBuf.length() > 0) {
                pkgBuf.append(',');
            }
            pkgBuf.append(pkg);
            if (version != null && !pkg.startsWith("META-INF.")) {
                pkgBuf.append(";version=\"");
                pkgBuf.append(version);
                pkgBuf.append('\"');
            }
        }
        return pkgBuf.toString();
    }

    private String getJarVersion(String bundleName) {
        Pattern pattern = Pattern.compile("-([0-9.]+)");
        Matcher matcher = pattern.matcher(bundleName);
        String version = "1.0.0";
        if (matcher.find()) {
            version = matcher.group();
            if (version.endsWith(".")) {
                version = version.substring(1, version.length() - 1);
            } else {
                version = version.substring(1);
            }
        }
        return version;
    }

}
