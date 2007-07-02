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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;


/**
 * 
 * Utility class to create OSGi bundles
 *
 */
public class OSGiTestBundles {
    
    public static void createBundle(String jarName,
            Class<?> interfaceClass, Class<?> implClass) throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        String EOL = System.getProperty("line.separator");
        
        String packageName = interfaceClass.getPackage().getName();
        String bundleName = interfaceClass.getName();

        String manifestStr = "Manifest-Version: 1.0" + EOL
                + "Bundle-ManifestVersion: 2" + EOL + "Bundle-Name: "
                + bundleName + EOL + "Bundle-SymbolicName: " + bundleName + EOL
                + "Bundle-Version: " + "1.0.0" + EOL
                + "Bundle-Localization: plugin" + EOL;

        StringBuilder manifestBuf = new StringBuilder();
        manifestBuf.append(manifestStr);
        manifestBuf.append("Export-Package: " + packageName + EOL);
        manifestBuf.append("Import-Package: org.osgi.framework" + EOL);
        manifestBuf.append("Bundle-Activator: " + implClass.getName() + EOL);

        ByteArrayInputStream manifestStream = new ByteArrayInputStream(manifestBuf.toString().getBytes());
        Manifest manifest = new Manifest();
        manifest.read(manifestStream);
        

        JarOutputStream jarOut = new JarOutputStream(out, manifest);

        String interfaceClassName = interfaceClass.getName().replaceAll("\\.",
                "/")
                + ".class";

        URL url = interfaceClass.getClassLoader().getResource(
                interfaceClassName);
        String path = url.getPath();

        ZipEntry ze = new ZipEntry(interfaceClassName);

        jarOut.putNextEntry(ze);
        FileInputStream file = new FileInputStream(path);
        byte[] fileContents = new byte[file.available()];
        file.read(fileContents);
        jarOut.write(fileContents);
        
        String implClassName = implClass.getName().replaceAll("\\.",
                "/")
                + ".class";

        url = implClass.getClassLoader().getResource(implClassName);
        path = url.getPath();

        ze = new ZipEntry(implClassName);

        jarOut.putNextEntry(ze);
        file = new FileInputStream(path);
        fileContents = new byte[file.available()];
        file.read(fileContents);
        jarOut.write(fileContents);

        file.close();

        jarOut.close();
        out.close();

        FileOutputStream fileOut = new FileOutputStream(jarName);
        fileOut.write(out.toByteArray());
        fileOut.close();


    }
}
