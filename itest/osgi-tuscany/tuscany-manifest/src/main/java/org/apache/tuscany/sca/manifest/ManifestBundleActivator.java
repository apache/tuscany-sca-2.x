package org.apache.tuscany.sca.manifest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * OSGi bundle activator, which is run when Tuscany is run inside an OSGi runtime.
 *
 */
public class ManifestBundleActivator implements BundleActivator {
    
    private static final String TUSCANY_MANIFEST = "tuscany-sca-manifest.jar";
    private static final String TUSCANY_OSGI_MANIFEST_DIR = "org/apache/tuscany/sca/manifest";
    
    private ArrayList<Bundle> virtualBundles = new ArrayList<Bundle>();

	public void start(BundleContext bundleContext) throws Exception {
		
        install3rdPartyJarsIntoOSGi(bundleContext);
	}

	public void stop(BundleContext bundleContext) throws Exception {
        
        for (Bundle virtualBundle : virtualBundles) {
            try {
                virtualBundle.uninstall();
            } catch (Exception e) {
                // Ignore error
            }
        }
	}
	
    private void install3rdPartyJarsIntoOSGi(BundleContext bundleContext) {
    
        try {
            HashMap<String, InputStream> separateBundles = new HashMap<String, InputStream>();
            HashSet<String> thirdPartyJars = new HashSet<String>();
            
            File tuscanyInstallDir = find3rdPartyJars(bundleContext, thirdPartyJars, separateBundles);
            
            String thirdPartyBundleLocation = tuscanyInstallDir.toURI().toURL().toString() + "/tuscany-3rdparty.jar";
            
            InputStream manifestStream = this.getClass().getClassLoader().getResourceAsStream(TUSCANY_OSGI_MANIFEST_DIR + "/MANIFEST.MF");
            
            if (manifestStream != null) {
            
                createAndInstallBundle(bundleContext, thirdPartyBundleLocation, manifestStream, tuscanyInstallDir, thirdPartyJars);
            }
            
            for (String bundleName : separateBundles.keySet()) {
                                
                String bundleLocation = tuscanyInstallDir.toURI().toURL().toString() + "/" + bundleName;
                InputStream bundleManifestStream = separateBundles.get(bundleName);
                HashSet<String> jarSet = new HashSet<String>();
                jarSet.add(bundleLocation);
                
                createAndInstallBundle(bundleContext, bundleLocation, bundleManifestStream, tuscanyInstallDir, jarSet);
                
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
        
    private File find3rdPartyJars(BundleContext bundleContext, 
            HashSet<String> thirdPartyJars, 
            HashMap<String, InputStream> individual3rdPartyBundles) 
    throws IOException
    {
        
        Bundle manifestBundle = bundleContext.getBundle();
        
        String tuscanyDirName;
        File tuscanyInstallDir = null;
        String location = manifestBundle.getLocation();
        
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
            
        if (tuscanyInstallDir == null && location != null && location.startsWith("file:") && location.endsWith(TUSCANY_MANIFEST)) {
            tuscanyDirName = location.substring(5, location.length()-TUSCANY_MANIFEST.length()); // strip "file:" and manifest jar name
            tuscanyInstallDir = new File(tuscanyDirName);
            if (!tuscanyInstallDir.exists() || !tuscanyInstallDir.isDirectory())
                tuscanyInstallDir = null;
        }
        if (tuscanyInstallDir == null) {
            System.out.println("TUSCANY_HOME not set, could not locate Tuscany install directory");
            return null;
        }
            
        String classPath = (String)manifestBundle.getHeaders().get("Class-Path");
        String[] classPathEntries = classPath.split(" ");
        for (String classPathEntry : classPathEntries) {
            classPathEntry = classPathEntry.trim();
            if (!classPathEntry.startsWith("tuscany") || classPathEntry.startsWith("tuscany-sdo")) {
                if (classPathEntry.endsWith(".jar")) {
                    String manifestName = TUSCANY_OSGI_MANIFEST_DIR + "/" + classPathEntry.substring(0, classPathEntry.length()-4) + ".mf";
                    InputStream manifestStream;
                    if ((manifestStream = this.getClass().getClassLoader().getResourceAsStream(manifestName)) != null)
                        individual3rdPartyBundles.put(classPathEntry, manifestStream);
                }
                thirdPartyJars.add(classPathEntry);
            }
        }
        
        return tuscanyInstallDir;
        
    }
    
    public Bundle createAndInstallBundle(BundleContext bundleContext, 
            String bundleLocation, 
            InputStream manifestStream,
            File tuscanyDir,
            final HashSet<String> thirdPartyJars) throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Manifest manifest = new Manifest();
        manifest.read(manifestStream);
        
        StringBuilder bundleClassPath = new StringBuilder(".");
        for (String jar : thirdPartyJars) {
            bundleClassPath.append(',');
            bundleClassPath.append(jar);           
        }
        
        manifest.getMainAttributes().putValue("Bundle-ClassPath", bundleClassPath.toString());

        JarOutputStream jarOut = new JarOutputStream(out, manifest);
        
        File[] jars = tuscanyDir.listFiles(new FilenameFilter() {

            public boolean accept(File dir, String name) {
                return thirdPartyJars.contains(name);
            }
            
        });
        
        for (File jar : jars) {
            addFileToJar(jar, jarOut);
        }

        jarOut.close();
        out.close();
        
        ByteArrayInputStream inStream = new ByteArrayInputStream(out.toByteArray());
        return bundleContext.installBundle(bundleLocation, inStream);

    }
    
    private void addFileToJar(File file, JarOutputStream jarOut) throws Exception {
        
        if (file.isDirectory()) {
            return;
        }
        
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
	
}
