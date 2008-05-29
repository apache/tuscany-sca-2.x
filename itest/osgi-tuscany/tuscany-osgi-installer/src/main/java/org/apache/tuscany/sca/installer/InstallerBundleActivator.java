package org.apache.tuscany.sca.installer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Bundle activator which installs Tuscany modules and 3rd party jars into an OSGi runtime.
 *
 */
public class InstallerBundleActivator implements BundleActivator {
    
    private static final String TUSCANY_INSTALLER_JAR = "tuscany-sca-osgi-installer.jar";
    private static final String TUSCANY_CLASSPATH = "org/apache/tuscany/sca/installer/.classpath";

    private static final String TUSCANY_OSGI_MANIFEST_DIR = "org/apache/tuscany/sca/manifest";
    
    private ArrayList<Bundle> tuscanyBundles = new ArrayList<Bundle>();
    
    private static final String[] immutableJars = {
        "bcprov"
    };
    
    private static final String[] tuscanyModulesToIgnore = {
        "node2-api",
        "node2-impl",
        "node2-launcher",
        "node2-launcher-webapp",
        "implementation-node-runtime", // uses node2
        "saxon",
        "runtime",
        "runtime-webapp",
        "runtime-tomcat",
        "runtime-war",
        "host-webapp",
        "host-tomcat",
        "policy-transaction",
        "implementation-bpel",
        "binding-ejb",
        "implementation-ejb",
        "implementation-ejb-xml",
        
    };
    
	public void start(BundleContext bundleContext) throws Exception {
		
        installTuscanyIntoOSGi(bundleContext);
	}

	public void stop(BundleContext bundleContext) throws Exception {
        
        for (Bundle bundle : tuscanyBundles) {
            try {
                bundle.stop();
            } catch (Exception e) {
                // Ignore error
            }
        }
	}
	
    private void installTuscanyIntoOSGi(BundleContext bundleContext) {
    
        try {
            Bundle[] installedBundles = bundleContext.getBundles();
            HashSet<String> installedBundleSet = new HashSet<String>();
            for (Bundle bundle : installedBundles) {
                if (bundle.getSymbolicName() != null)
                    installedBundleSet.add(bundle.getSymbolicName());
            }
            
            // FIXME: SDO bundles dont have the correct dependencies
            System.setProperty("commonj.sdo.impl.HelperProvider", "org.apache.tuscany.sdo.helper.HelperProviderImpl");
            
            HashSet<File> tuscanyJars = new HashSet<File>();
            HashMap<File, InputStream> thirdPartyJarsWithManifests = new HashMap<File, InputStream>();
            HashSet<File> thirdPartyJars = new HashSet<File>();
            
            findJars(bundleContext, tuscanyJars, thirdPartyJars, thirdPartyJarsWithManifests);
            
            for (File bundleFile : thirdPartyJarsWithManifests.keySet()) {
                                
                String bundleLocation = bundleFile.toURI().toURL().toString();
                InputStream bundleManifestStream = thirdPartyJarsWithManifests.get(bundleFile);
                HashSet<File> jarSet = new HashSet<File>();
                jarSet.add(bundleFile);
                
                createAndInstallBundle(bundleContext, bundleLocation, bundleManifestStream, jarSet);
                bundleManifestStream.close();
                
            }
            
            for (File bundleFile : thirdPartyJars) {
                
                String bundleName = bundleFile.getName();
                if (bundleName.startsWith("org.apache.felix"))
                    continue;
                
                String bundleSymbolicName = "org.apache.tuscany.sca.3rdparty." + bundleName;
                if (bundleSymbolicName.endsWith(".jar")) bundleSymbolicName = bundleSymbolicName.substring(0, bundleSymbolicName.length()-4);
                if (installedBundleSet.contains(bundleSymbolicName))
                    continue;
                
                String bundleLocation = bundleFile.toURI().toURL().toString();
                InputStream bundleManifestStream = createBundleManifest(bundleFile, bundleSymbolicName);
                HashSet<File> jarSet = new HashSet<File>();
                jarSet.add(bundleFile);
                
                createAndInstallBundle(bundleContext, bundleLocation, bundleManifestStream, jarSet);
                bundleManifestStream.close();
               
            }
            
            Bundle osgiRuntimeBundle = null;
            for (File bundleFile : tuscanyJars) {
                Bundle bundle = bundleContext.installBundle(bundleFile.toURI().toURL().toString());
                if ("org.apache.tuscany.sca.osgi.runtime".equals(bundle.getSymbolicName())) 
                    osgiRuntimeBundle = bundle;
            }
            if (osgiRuntimeBundle != null)
                osgiRuntimeBundle.start();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
        
    private void findJars(BundleContext bundleContext, 
            HashSet<File> tuscanyJars, 
            HashSet<File> thirdPartyJars, 
            HashMap<File, InputStream> thirdPartyBundleManifests) 
    throws IOException
    {
        
        Bundle installerBundle = bundleContext.getBundle();   
        File tuscanyInstallDir = findTuscanyInstallDir(installerBundle);
        
        URL classPathURL = installerBundle.getResource(TUSCANY_CLASSPATH);
        InputStream stream = classPathURL.openStream();
        byte[] classPathBytes = new byte[stream.available()];
        stream.read(classPathBytes);
        String classPath = new String(classPathBytes);
        
        // Path separator overrides are not supported by older versions of maven
        String pathSeparator = ":";
        if (classPath.indexOf(";") > 0) pathSeparator = ";";
        
        String[] classPathEntries = classPath.split(pathSeparator);
        for (String classPathEntry : classPathEntries) {
            classPathEntry = classPathEntry.trim();
            File jar = new File(classPathEntry);
            if (!jar.isAbsolute()&&!jar.exists()) {
                jar = new File(tuscanyInstallDir, jar.getName());
            }

            String jarName = jar.getName();
            if (!jarName.startsWith("tuscany") || jarName.startsWith("tuscany-sdo") || jarName.startsWith("tuscany-das")) {
                if (jarName.endsWith(".jar")) {
                    String manifestName = TUSCANY_OSGI_MANIFEST_DIR + "/" + jarName.substring(0, jarName.length()-4) + ".mf";
                    InputStream manifestStream;
                    if ((manifestStream = this.getClass().getClassLoader().getResourceAsStream(manifestName)) != null)
                        thirdPartyBundleManifests.put(jar, manifestStream);
                    else
                        thirdPartyJars.add(jar);
                }
            } else {
                boolean installTuscanyJar = true;
                for (String name : tuscanyModulesToIgnore) {
                    if (jarName.startsWith("tuscany-" + name)) {
                        installTuscanyJar = false;
                        break;
                    }
                }
                if (installTuscanyJar)
                    tuscanyJars.add(jar);
            }
        }
        
        
    }
    
    private File findTuscanyInstallDir(Bundle installerBundle) 
    throws IOException
    {
        
        File tuscanyInstallDir = null;
        String location = installerBundle.getLocation();
        
        String tuscanyDirName;
        if ((tuscanyDirName = System.getenv("TUSCANY_HOME")) != null) {
            tuscanyInstallDir = new File(tuscanyDirName);
            if (!tuscanyInstallDir.exists() || !tuscanyInstallDir.isDirectory())
                tuscanyInstallDir = null;
        }
        if (tuscanyInstallDir == null && System.getProperty("TUSCANY_HOME") != null) {
            tuscanyInstallDir = new File(tuscanyDirName);
            if (!tuscanyInstallDir.exists() || !tuscanyInstallDir.isDirectory())
                tuscanyInstallDir = null;
        }
            
        if (tuscanyInstallDir == null && location != null && location.startsWith("file:") && location.endsWith(TUSCANY_INSTALLER_JAR)) {
            tuscanyDirName = location.substring(5, location.length()-TUSCANY_INSTALLER_JAR.length()); // strip "file:" and installer jar name
            tuscanyInstallDir = new File(tuscanyDirName);
            if (!tuscanyInstallDir.exists() || !tuscanyInstallDir.isDirectory())
                tuscanyInstallDir = null;
        }
        return tuscanyInstallDir;
    }
    
    public Bundle createAndInstallBundle(BundleContext bundleContext, 
            String bundleLocation, 
            InputStream manifestStream,
            final HashSet<File> thirdPartyJars) throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Manifest manifest = new Manifest();
        manifest.read(manifestStream);
        
        StringBuilder bundleClassPath = new StringBuilder(".");
        for (File jar : thirdPartyJars) {
            bundleClassPath.append(',');
            bundleClassPath.append(jar.getName());           
        }
        
        if (thirdPartyJars.size() > 1)
            manifest.getMainAttributes().putValue("Bundle-ClassPath", bundleClassPath.toString());

        JarOutputStream jarOut = new JarOutputStream(out, manifest);
        
        String classpath = manifest.getMainAttributes().getValue("Bundle-ClassPath");
        boolean embed = classpath != null && !classpath.trim().equals(".");
        for (File jarFile : thirdPartyJars) {
            if (embed)
                addFileToJar(jarFile, jarOut);
            else {
                copyJar(jarFile, jarOut);
            }
        }

        jarOut.close();
        out.close();
        
        ByteArrayInputStream inStream = new ByteArrayInputStream(out.toByteArray());
        return bundleContext.installBundle(bundleLocation, inStream);

    }
    
    private void addFileToJar(File file, JarOutputStream jarOut) throws Exception {
        
        ZipEntry ze = new ZipEntry(file.getName());

        try {
            jarOut.putNextEntry(ze);
            FileInputStream inStream = new FileInputStream(file);
            byte[] fileContents = new byte[inStream.available()];
            inStream.read(fileContents);
            jarOut.write(fileContents);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    private void copyJar(File file, JarOutputStream jarOut) throws Exception {
        
        try {
            JarInputStream jarIn = new JarInputStream(new FileInputStream(file));
            ZipEntry ze;
            byte[] readBuf = new byte[1000];
            int bytesRead;
            while ((ze = jarIn.getNextEntry()) != null) {
                if (ze.getName().equals("META-INF/MANIFEST.MF"))
                    continue;
                jarOut.putNextEntry(ze);
                while ((bytesRead = jarIn.read(readBuf)) > 0) {
                    jarOut.write(readBuf, 0, bytesRead);
                }
            }
            jarIn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private InputStream createBundleManifest(File jarFile, String bundleSymbolicName) throws Exception {
        
        if (!jarFile.exists())
            return null;
        JarInputStream jar = new JarInputStream(new FileInputStream(jarFile));
        Manifest manifest = jar.getManifest();
        if (manifest == null)
            manifest = new Manifest();
        
        String bundleName = jarFile.getName();
        boolean isImmutableJar = false;
        for (String immutableJar : immutableJars) {
            if (bundleName.startsWith(immutableJar)) {
                isImmutableJar = true;
                break;
            }
        }
        Attributes attributes = manifest.getMainAttributes();
        if (attributes.getValue("Manifest-Version") == null) {
            attributes.putValue("Manifest-Version", "1.0");
        }
        if (isImmutableJar)
            attributes.putValue("Bundle-ClassPath", bundleName);
        
        HashSet<String> packages = getPackagesInJar(bundleName, jar);
        String version = getJarVersion(bundleName);

        attributes.remove(new Attributes.Name("Require-Bundle"));
        attributes.remove(new Attributes.Name("Import-Package"));
        
        if (attributes.getValue("Bundle-SymbolicName") == null)
            attributes.putValue("Bundle-SymbolicName", bundleSymbolicName);
        if (attributes.getValue("Bundle-Version") == null)
            attributes.putValue("Bundle-Version", version);
        // Existing export statements in bundles may contain versions, so they should be used as is
        // SDO exports are not sufficient, and should be changed
        if (attributes.getValue("Export-Package") == null || bundleName.startsWith("tuscany-sdo-impl")) {
            attributes.putValue("Export-Package", packagesToString(packages, version));
            attributes.putValue("Import-Package", packagesToString(packages, null));
        }
        
        attributes.putValue("DynamicImport-Package", "*");       
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        manifest.write(out);
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        out.close();
               
        return in;
        
    }
    
    private HashSet<String> getPackagesInJar(String bundleName, JarInputStream jar) throws Exception {
        HashSet<String> packages = new HashSet<String>();
        ZipEntry entry;
        while ((entry = jar.getNextEntry()) != null) {
            String entryName = entry.getName();
            if (!entry.isDirectory() && entryName != null && entryName.length() > 0 && 
                    !entryName.startsWith(".") && !entryName.startsWith("META-INF") &&
                    entryName.lastIndexOf("/") > 0) {
                String pkg = entryName.substring(0, entryName.lastIndexOf("/")).replace('/', '.');
                packages.add(pkg);
                
            }
        }
        // FIXME: Split package
        if (bundleName.startsWith("axis2-adb"))
            packages.remove("org.apache.axis2.util");
        else if (bundleName.startsWith("axis2-codegen")) {
            packages.remove("org.apache.axis2.wsdl");
            packages.remove("org.apache.axis2.wsdl.util");
        }
        else if (bundleName.startsWith("bsf-all"))
            packages.remove("org.mozilla.javascript");
        
        return packages;
    }
    
    private String packagesToString(HashSet<String> packages, String version) {
        
        StringBuilder pkgBuf = new StringBuilder();
        for (String pkg : packages) {
            if (pkgBuf.length() >0) pkgBuf.append(',');
            pkgBuf.append(pkg);
            if (version != null) {
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
            if (version.endsWith("."))
                version = version.substring(1, version.length()-1);
            else
                version = version.substring(1);
        }
        return version;
    }
	
}
