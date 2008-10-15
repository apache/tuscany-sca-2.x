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

package org.apache.tuscany.sca.tools.bundle.plugin;

import static org.osgi.framework.Constants.BUNDLE_CLASSPATH;
import static org.osgi.framework.Constants.BUNDLE_MANIFESTVERSION;
import static org.osgi.framework.Constants.BUNDLE_NAME;
import static org.osgi.framework.Constants.BUNDLE_SYMBOLICNAME;
import static org.osgi.framework.Constants.BUNDLE_VERSION;
import static org.osgi.framework.Constants.DYNAMICIMPORT_PACKAGE;
import static org.osgi.framework.Constants.EXPORT_PACKAGE;
import static org.osgi.framework.Constants.IMPORT_PACKAGE;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Common functions used by the plugin.
 *
 * @version $Rev$ $Date$
 */
final class BundleUtil {

    static private Pattern pattern = Pattern.compile("-([0-9.]+)");
    static private Pattern pattern2 = Pattern.compile("_([0-9.]+)");

    private static String version(String jarFile) {
        String version = "1.0.0";
        boolean found = false;
        Matcher matcher = pattern2.matcher(jarFile);
        if (matcher.find()) {
            found = true;
        } else {
            matcher = pattern.matcher(jarFile);
            found = matcher.find();
        }            
        if (found) {
            version = matcher.group();
            if (version.endsWith(".")) {
                version = version.substring(1, version.length() - 1);
            } else {
                version = version.substring(1);
            }
        }
        return version;
    }
    
    private static void addPackages(File jarFile, Set<String> packages) throws IOException {
        if (getBundleName(jarFile) == null) {
            String version = ";version=" + version(jarFile.getPath());
            addAllPackages(jarFile, packages, version);
        } else {
            addExportedPackages(jarFile, packages);
        }
    }

    static Manifest libraryManifest(Set<File> jarFiles, String name, String symbolicName, String version)
        throws IllegalStateException {
        try {

            // List exported packages and bundle classpath entries
            StringBuffer classpath = new StringBuffer();
            StringBuffer exports = new StringBuffer();
            StringBuffer imports = new StringBuffer();
            Set<String> packages = new HashSet<String>();
            for (File jarFile : jarFiles) {
                addPackages(jarFile, packages);
                classpath.append("lib/");
                classpath.append(jarFile.getName());
                classpath.append(",");
            }

            Set<String> importPackages = new HashSet<String>();
            for (String pkg : packages) {
                exports.append(pkg);
                exports.append(',');

                String importPackage = pkg;
                int index = pkg.indexOf(';');
                if (index != -1) {
                    importPackage = pkg.substring(0, index);
                }
                if (!importPackages.contains(importPackage)) {
                    imports.append(importPackage);
                    imports.append(',');
                    importPackages.add(importPackage);
                }
            }

            // Create a manifest
            Manifest manifest = new Manifest();
            Attributes attributes = manifest.getMainAttributes();
            attributes.putValue("Manifest-Version", "1.0");
            attributes.putValue(BUNDLE_MANIFESTVERSION, "2");
            attributes.putValue(BUNDLE_SYMBOLICNAME, symbolicName);
            attributes.putValue(BUNDLE_NAME, name);
            attributes.putValue(BUNDLE_VERSION, version);
            attributes.putValue(DYNAMICIMPORT_PACKAGE, "*");
            attributes.putValue(EXPORT_PACKAGE, exports.substring(0, exports.length() - 1));
            attributes.putValue(IMPORT_PACKAGE, imports.substring(0, imports.length() - 1));
            attributes.putValue(BUNDLE_CLASSPATH, classpath.substring(0, classpath.length() - 1));

            return manifest;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    static void write(Manifest manifest, OutputStream out) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        Attributes attributes = manifest.getMainAttributes();
        write(attributes, "Manifest-Version", dos);
        write(attributes, BUNDLE_MANIFESTVERSION, dos);
        write(attributes, BUNDLE_SYMBOLICNAME, dos);
        write(attributes, BUNDLE_NAME, dos);
        write(attributes, BUNDLE_VERSION, dos);
        write(attributes, DYNAMICIMPORT_PACKAGE, dos);
        write(attributes, BUNDLE_CLASSPATH, dos);
        write(attributes, IMPORT_PACKAGE, dos);
        write(attributes, EXPORT_PACKAGE, dos);
        dos.flush();
    }
    
    private static void write(Attributes attributes, String key, DataOutputStream dos) throws IOException {
        StringBuffer line = new StringBuffer();
        line.append(key);
        line.append(": ");
        String value = attributes.getValue(key); 
        line.append(new String(value.getBytes("UTF8")));
        line.append("\r\n");
        int l = line.length();
        if (l > 72) {
            for (int i = 70; i < l - 2;) {
                line.insert(i, "\r\n ");
                i += 72;
                l += 3;
            }
        }
        dos.writeBytes(line.toString());
    }

    /**
     * Returns the name of a bundle, or null if the given file is not a bundle.
     *  
     * @param file
     * @return
     * @throws IOException
     */
    static String getBundleName(File file) throws IOException {
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
    
    /**
     * Strip the uses:= statement out of an OSGi export.
     * 
     * @param export
     * @return
     */
    private static String stripExport(String export) {
        int sc = export.indexOf(';');
        if (sc == -1) {
            return export;
        }
        String base = export.substring(0, sc);
        int v = export.indexOf("version=");
        if (v != -1) {
            sc = export.indexOf(';', v+1);
            if (sc != -1) {
                return base + ";" + export.substring(v, sc);
            } else {
                return base + ";" + export.substring(v);
            }
        } else {
            return base;
        }
    }

    /**
     * Add all the packages out of a JAR.
     * 
     * @param jarFile
     * @param packages
     * @param version
     * @throws IOException
     */
    private static void addAllPackages(File jarFile, Set<String> packages, String version) throws IOException {
        ZipInputStream is = new ZipInputStream(new FileInputStream(jarFile));
        ZipEntry entry;
        while ((entry = is.getNextEntry()) != null) {
            String entryName = entry.getName();
            if (!entry.isDirectory() && entryName != null
                && entryName.length() > 0
                && !entryName.startsWith(".")
                && entryName.endsWith(".class") // Exclude resources from Export-Package
                && entryName.lastIndexOf("/") > 0
                && Character.isJavaIdentifierStart(entryName.charAt(0))) {
                String pkg = entryName.substring(0, entryName.lastIndexOf("/")).replace('/', '.');
                if (!pkg.endsWith(".enum")) {
                    packages.add(pkg + version);
                }
            }
        }
        is.close();
    }
    
    private static boolean isPresent(String export, Set<String> present) {
        if (present == null) {
            return true;
        }
        int sc = export.indexOf(';');
        if (sc != -1) {
            export = export.substring(0, sc);
        }
        return present.contains(export);
    }

    /**
     * Add the packages exported by a bundle.
     *  
     * @param file
     * @param packages
     * @return
     * @throws IOException
     */
    private static void addExportedPackages(File file, Set<String> packages) throws IOException {
        if (!file.exists()) {
            return;
        }
        Set<String> present = null;
        String exports = null;
        if (file.isDirectory()) {
            File mf = new File(file, "META-INF/MANIFEST.MF");
            if (mf.isFile()) {
                Manifest manifest = new Manifest(new FileInputStream(mf));
                exports = manifest.getMainAttributes().getValue(EXPORT_PACKAGE);
            }
        } else {
            JarFile jar = new JarFile(file, false);
            Manifest manifest = jar.getManifest();
            exports = manifest.getMainAttributes().getValue(EXPORT_PACKAGE);
            jar.close();
            present = new HashSet<String>();
            addAllPackages(file, present, "");
        }
        if (exports == null) {
            return;
        }
        StringBuffer export = new StringBuffer();
        boolean q = false;
        for (int i =0, n = exports.length(); i <n; i++) {
            char c = exports.charAt(i);
            if (c == '\"') {
                q = !q;
            }
            if (!q) {
                if (c == ',') {
                    if (isPresent(export.toString(), present)) {
                        packages.add(stripExport(export.toString()));
                    }
                    export = new StringBuffer();
                    continue;
                }
            }
            export.append(c);
        }
        if (export.length() != 0) {
            if (isPresent(export.toString(), present)) {
                packages.add(stripExport(export.toString()));
            }
        }
    }

}
