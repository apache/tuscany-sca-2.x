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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.osgi.framework.Bundle;

/**
 * Common functions used by the plugin.
 *
 * @version $Rev$ $Date$
 */
final class BundleUtil {

    static File file(URL url) {
        if (url == null || !url.getProtocol().equals("file")) {
            return null;
        } else {
            String filename = url.getFile().replace('/', File.separatorChar);
            int pos = 0;
            while ((pos = filename.indexOf('%', pos)) >= 0) {
                if (pos + 2 < filename.length()) {
                    String hexStr = filename.substring(pos + 1, pos + 3);
                    char ch = (char)Integer.parseInt(hexStr, 16);
                    filename = filename.substring(0, pos) + ch + filename.substring(pos + 3);
                }
            }
            return new File(filename);
        }
    }

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
                    if (!("org.apache.commons.lang.enum".equals(pkg))) {
                        packages.add(pkg + version);
                    }
                }
            }
            is.close();
        } else {
            Set<String> exportedPackages = getExportedPackages(jarFile);
            if (exportedPackages != null) {
                packages.addAll(exportedPackages);
            }
        }
    }

    static Manifest libraryManifest(Set<File> jarFiles, String name, String version)
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
            attributes.putValue(BUNDLE_SYMBOLICNAME, name);
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

    static String dump(Manifest mf) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        mf.write(bos);
        return new String(bos.toByteArray());
    }

    static byte[] generateBundle(Manifest mf) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        JarOutputStream jos = new JarOutputStream(bos, mf);
        jos.close();
        return bos.toByteArray();
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
     * Returns the packages exported by a bundle.
     *  
     * @param file
     * @return
     * @throws IOException
     */
    static Set<String> getExportedPackages(File file) throws IOException {
        if (!file.exists()) {
            return null;
        }
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
        }
        if (exports == null) {
            return null;
        }
        Set<String> exportedPackages = new HashSet<String>();
        StringBuffer export = new StringBuffer();
        boolean q = false;
        for (int i =0, n = exports.length(); i <n; i++) {
            char c = exports.charAt(i);
            if (c == '\"') {
                q = !q;
            }
            if (!q) {
                if (c == ',') {
                    exportedPackages.add(export.toString());
                    export = new StringBuffer();
                    continue;
                }
            }
            export.append(c);
        }
        if (export.length() != 0) {
            exportedPackages.add(export.toString());
        }
        return exportedPackages;
    }

    public static String string(Bundle b, boolean verbose) {
        StringBuffer sb = new StringBuffer();
        sb.append(b.getBundleId()).append(" ").append(b.getSymbolicName());
        int s = b.getState();
        if ((s & Bundle.UNINSTALLED) != 0) {
            sb.append(" UNINSTALLED");
        }
        if ((s & Bundle.INSTALLED) != 0) {
            sb.append(" INSTALLED");
        }
        if ((s & Bundle.RESOLVED) != 0) {
            sb.append(" RESOLVED");
        }
        if ((s & Bundle.STARTING) != 0) {
            sb.append(" STARTING");
        }
        if ((s & Bundle.STOPPING) != 0) {
            sb.append(" STOPPING");
        }
        if ((s & Bundle.ACTIVE) != 0) {
            sb.append(" ACTIVE");
        }

        if (verbose) {
            sb.append(" ").append(b.getLocation());
            sb.append(" ").append(b.getHeaders());
        }
        return sb.toString();
    }
}
