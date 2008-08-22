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
package org.apache.tuscany.sca.installer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
    
    private ArrayList<Bundle> tuscanyBundles = new ArrayList<Bundle>();
    
    private static final String[] immutableJars = {
        "bcprov"
    };
    
    private static final String[] tuscanyModulesToIgnore = {
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
    
    private static final String[] rebundleJars = {
        "org.apache.tuscany.sca.3rdparty.org.apache.tuscany.sdo",   // Recreate export statements
    };
    
	public void start(BundleContext bundleContext) throws Exception {
        
        String tuscanyHome = System.getProperty("TUSCANY_HOME");
        if (tuscanyHome == null) {
          File homeDir = new File("../tuscany-versioned/target/classes");
          if (homeDir.exists()) {
              tuscanyHome = homeDir.getCanonicalPath();
          }
        }
		
        System.out.println("Installing Tuscany from TUSCANY_HOME=" + tuscanyHome);
        installVersionedTuscanyIntoOSGi(bundleContext, tuscanyHome);
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
    
    private void installVersionedTuscanyIntoOSGi(BundleContext bundleContext, String tuscanyHome) {
        
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
            HashSet<File> thirdPartyJars = new HashSet<File>();
            

            File tuscanyInstallDir = new File(tuscanyHome).getCanonicalFile();
            findBundles(bundleContext, tuscanyInstallDir, tuscanyJars, thirdPartyJars);
            
            
            for (File bundleFile : thirdPartyJars) {
                
                String bundleName = bundleFile.getName();
                if (bundleName.startsWith("org.apache.felix"))
                    continue;
                
                boolean installed = false;
                for (String name : rebundleJars) {
                  if (bundleName.startsWith(name)) {
                      rebundleAndInstall(bundleContext, tuscanyInstallDir, bundleFile);
                      installed = true;
                  }
                }
                if (installed)
                    continue;
                
                bundleContext.installBundle(bundleFile.toURI().toURL().toString());
               
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
    
    private void rebundleAndInstall(BundleContext bundleContext, File tuscanyInstallDir, File bundleFile) throws Exception {
        String bundleSymbolicName = bundleFile.getName();
        if (bundleSymbolicName.endsWith(".jar")) bundleSymbolicName = bundleSymbolicName.substring(0, bundleSymbolicName.length()-4);
        
        String bundleLocation = bundleFile.toURI().toURL().toString();
        InputStream bundleManifestStream = updateBundleManifest(bundleFile, bundleSymbolicName);
        HashSet<File> jarSet = new HashSet<File>();
        jarSet.add(bundleFile);

        File newBundleFile = new File(tuscanyInstallDir, "org.apache.tuscany.sca." + bundleFile.getName());
        createAndInstallBundle(bundleContext, bundleLocation, newBundleFile, bundleManifestStream, jarSet);
        bundleManifestStream.close();
    }
	
    
    private void findBundles(BundleContext bundleContext, 
            File tuscanyInstallDir,
            HashSet<File> tuscanyJars, 
            HashSet<File> thirdPartyJars) 
    throws IOException
    {
        
        File[] jars = tuscanyInstallDir.listFiles();
        for (File jar : jars) {
            String jarName = jar.getName();
            if (!jarName.endsWith(".jar"))
                continue;
            
            if (!jarName.startsWith("org.apache.tuscany.sca")||jarName.startsWith("org.apache.tuscany.sca.3rdparty")) {
                if (jarName.endsWith(".jar"))
                { 
                    thirdPartyJars.add(jar);
                }
            } else {
                boolean installTuscanyJar = true;
                for (String name : tuscanyModulesToIgnore) {
                    name = name.replaceAll("-", ".");
                    if (jarName.startsWith("org.apache.tuscany.sca." + name)) {
                        installTuscanyJar = false;
                        break;
                    }
                }
                if (installTuscanyJar)
                    tuscanyJars.add(jar);
            }
        }
    }
    

    private InputStream updateBundleManifest(File jarFile, String bundleSymbolicName) throws Exception {
        
        if (!jarFile.exists())
            return null;
        JarInputStream jar = new JarInputStream(new FileInputStream(jarFile));
        Manifest manifest = jar.getManifest();
        if (manifest == null) {
            ZipEntry ze;
            while ((ze = jar.getNextEntry()) != null) {
                if (ze.getName().equals("META-INF/MANIFEST.MF"))
                    break;
            }
            if (ze != null) {
                byte[] bytes = new byte[(int)ze.getSize()];
                jar.read(bytes);
                manifest = new Manifest(new ByteArrayInputStream(bytes));
            }
        }
        if (manifest == null) {
            manifest = new Manifest();
        }
        
        String bundleName = jarFile.getName();
        boolean isImmutableJar = false;
        for (String immutableJar : immutableJars) {
            if (bundleName.startsWith(immutableJar)) {
                isImmutableJar = true;
                break;
            }
        }
        Attributes attributes = manifest.getMainAttributes();
        if (isImmutableJar)
            attributes.putValue("Bundle-ClassPath", bundleName);
        

        attributes.remove(new Attributes.Name("Require-Bundle"));
        attributes.putValue("DynamicImport-Package", "*");   
        
        // Existing export statements in bundles may contain versions, so they should be used as is
        // SDO exports are not sufficient, and should be changed
        if (attributes.getValue("Export-Package") == null || bundleName.startsWith("org.apache.tuscany.sca.3rdparty.org.apache.tuscany.sdo.tuscany-sdo-impl")) {
        
            HashSet<String> packages = getPackagesInJar(bundleName, jar);
            String version = getJarVersion(bundleName);
            
            attributes.putValue("Export-Package", packagesToString(packages, version));
            attributes.putValue("Import-Package", packagesToString(packages, null));
        }
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        manifest.write(out);
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        out.close();
               
        return in;
        
    }
    
    public Bundle createAndInstallBundle(BundleContext bundleContext, 
            String bundleLocation, 
            File bundleFile,
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

        Bundle bundle;
        if (System.getenv("TUSCANY_OSGI_DEBUG") != null) {
            FileOutputStream fileOut = new FileOutputStream(bundleFile);
            fileOut.write(out.toByteArray());
            bundle = bundleContext.installBundle(bundleFile.toURL().toString());
            
        } else {
            ByteArrayInputStream inStream = new ByteArrayInputStream(out.toByteArray());
            bundle = bundleContext.installBundle(bundleLocation, inStream);
            inStream.close();
        }
        return bundle;

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
