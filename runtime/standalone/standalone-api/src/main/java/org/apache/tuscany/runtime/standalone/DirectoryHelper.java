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
package org.apache.tuscany.runtime.standalone;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;

/**
 * Utility class for installation directory related operations.
 *
 * @version $Revision$ $Date$
 */
public final class DirectoryHelper {

    /**
     * Installation directory system property name.
     */
    private static final String INSTALL_DIRECTORY_PROPERTY = "tuscany.installDir";

    private DirectoryHelper() {
    }

    /**
     * Gets the installation directory based on the location of a class file.
     * If the system property <code>tuscany.installDir</code> is set then its value is used as the
     * location of the installation directory. Otherwise, we assume we are running from an
     * executable jar containing the supplied class and the installation directory is assumed to
     * be the parent of the directory containing that jar.
     *
     * @param clazz the class to use as a way to find the executable jar
     * @return directory where tuscany standalone server is installed.
     * @throws IllegalArgumentException if the property is set but its value is not an existing directory
     * @throws IllegalStateException    if the location could not be determined from the location of the class file
     */
    public static File getInstallDirectory(Class clazz) throws IllegalStateException, IllegalArgumentException {

        String installDirectoryPath = System.getProperty(INSTALL_DIRECTORY_PROPERTY);

        if (installDirectoryPath != null) {
            File installDirectory = new File(installDirectoryPath);
            if (!installDirectory.exists()) {
                throw new IllegalArgumentException(INSTALL_DIRECTORY_PROPERTY
                    + " property does not refer to an existing directory: " + installDirectory);
            }
            return installDirectory;
        }

        // get the name of the Class's bytecode
        String name = clazz.getName();
        int last = name.lastIndexOf('.');
        if (last != -1) {
            name = name.substring(last);
        }
        name = name + ".class";

        // get location of the bytecode - should be a jar: URL
        URL url = clazz.getResource(name);
        if (url == null) {
            throw new IllegalStateException("Unable to get location of bytecode resource " + name);
        }

        String jarLocation = url.toString();
        if (!jarLocation.startsWith("jar:")) {
            throw new IllegalStateException("Must be run from a jar: " + url);
        }

        // extract the location of thr jar from the resource URL 
        jarLocation = jarLocation.substring(4, jarLocation.lastIndexOf("!/"));
        if (!jarLocation.startsWith("file:")) {
            throw new IllegalStateException("Must be run from a local filesystem: " + jarLocation);
        }

        File jarFile = new File(URI.create(jarLocation));
        return jarFile.getParentFile().getParentFile();
    }

    /**
     * Gets the boot directory where all the boot libraries are stored. This
     * is expected to be a directory named <code>boot</code> under the install
     * directory.
     *
     * @param installDirectory Tuscany install directory.
     * @param bootPath         Boot path for the runtime.
     * @return Tuscany boot directory.
     */
    public static File getBootDirectory(File installDirectory, String bootPath) {

        File bootDirectory = new File(installDirectory, bootPath);
        if (!bootDirectory.exists()) {
            throw new IllegalStateException("Boot directory doesn't exist: " + bootDirectory.getAbsolutePath());
        }
        return bootDirectory;

    }

    /**
     * Create a classloader from all the jar files or subdirectories in a directory.
     * The classpath for the returned classloader will comprise all jar files and subdirectories
     * of the supplied directory. Hidden files and those that do not contain a valid manifest will
     * be silently ignored.
     *
     * @param parent    the parent for the new classloader
     * @param directory the directory to scan
     * @return a classloader whose classpath includes all jar files and subdirectories of the supplied directory
     */
    public static ClassLoader createClassLoader(ClassLoader parent, File directory) {
        File[] jars = directory.listFiles(new FileFilter() {
            public boolean accept(File file) {
                if (file.isHidden()) {
                    return false;
                }
                if (file.isDirectory()) {
                    return true;
                }
                try {
                    JarFile jar = new JarFile(file);
                    return jar.getManifest() != null;
                } catch (IOException e) {
                    return false;
                }
            }
        });

        URL[] urls = new URL[jars.length];
        for (int i = 0; i < jars.length; i++) {
            try {
                urls[i] = jars[i].toURI().toURL();
            } catch (MalformedURLException e) {
                // toURI should have escaped the URL
                throw new AssertionError();
            }
        }

        return new URLClassLoader(urls, parent);
    }
}
