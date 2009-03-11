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

package org.apache.tuscany.sca.implementation.osgi.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.Constants;

/**
 * 
 * Utility class to create OSGi bundles
 *
 * @version $Rev$ $Date$
 */
public class OSGiTestBundles {
    private static String getPackageName(Class<?> cls) {
        String name = cls.getName();
        int index = name.lastIndexOf('.');
        return index == -1 ? "" : name.substring(0, index);
    }

    public static URL createBundle(String jarName, String bundleName, String imports, Class<?>... classes)
        throws Exception {

        Class<?> activator = null;
        Set<String> packages = new HashSet<String>();
        StringBuffer exports = new StringBuffer();
        for (Class<?> cls : classes) {
            if (BundleActivator.class.isAssignableFrom(cls)) {
                activator = cls;
            }
            if (cls.isInterface()) {
                String pkg = getPackageName(cls);
                if (packages.add(pkg)) {
                    exports.append(pkg).append(",");
                }
            }
        }
        if (exports.length() > 0) {
            exports.deleteCharAt(exports.length() - 1);
        }

        Manifest manifest = new Manifest();
        // This attribute Manifest-Version is required so that the MF will be added to the jar
        manifest.getMainAttributes().putValue("Manifest-Version", "1.0");
        manifest.getMainAttributes().putValue(Constants.BUNDLE_MANIFESTVERSION, "2");
        manifest.getMainAttributes().putValue(Constants.BUNDLE_SYMBOLICNAME, bundleName);
        manifest.getMainAttributes().putValue(Constants.BUNDLE_VERSION, "1.0.0");
        manifest.getMainAttributes().putValue(Constants.BUNDLE_NAME, bundleName);
        manifest.getMainAttributes().putValue(Constants.EXPORT_PACKAGE, exports.toString());
        String importPackages = null;
        if (imports != null) {
            importPackages = "org.osgi.framework," + imports;
        } else {
            importPackages = "org.osgi.framework";
        }
        manifest.getMainAttributes().putValue(Constants.IMPORT_PACKAGE, importPackages);

        if (activator != null) {
            manifest.getMainAttributes().putValue(Constants.BUNDLE_ACTIVATOR, activator.getName());
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JarOutputStream jarOut = new JarOutputStream(out, manifest);

        for (Class<?> cls : classes) {
            addClass(jarOut, cls);
        }

        jarOut.close();
        out.close();

        File jar = new File(jarName);
        FileOutputStream fileOut = new FileOutputStream(jar);
        fileOut.write(out.toByteArray());
        fileOut.close();

        return jar.toURI().toURL();
    }

    private static void addClass(JarOutputStream jarOut, Class<?> javaClass) throws IOException, FileNotFoundException {
        String interfaceClassName = javaClass.getName().replaceAll("\\.", "/") + ".class";

        URL url = javaClass.getClassLoader().getResource(interfaceClassName);
        String path = url.getPath();

        ZipEntry ze = new ZipEntry(interfaceClassName);

        jarOut.putNextEntry(ze);
        FileInputStream file = new FileInputStream(path);
        byte[] fileContents = new byte[file.available()];
        file.read(fileContents);
        jarOut.write(fileContents);
        jarOut.closeEntry();
    }
}
