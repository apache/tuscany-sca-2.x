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
package org.apache.tuscany.core.util;

import java.io.File;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.URLClassLoader;

/**
 * Functions to create & manipulate classloaders.
 * 
 * @version $$Rev$$ $$Date$$
 */

public final class ClassLoaderHelper {
    /**
     * Hide constructor
     */
    private ClassLoaderHelper() {
    }
    
    /**
     * Create a classloader for the supplied classpath.
     *
     * @param path   a list of file/directory names separated by the platform path separator
     * @param parent the parent for the new classloader
     * @return a classloader that will load classes from the supplied path
     */
    public static ClassLoader createClassLoader(ClassLoader parent, String path) {
        String[] files = path.split(File.pathSeparator);
        return createClassLoader(parent, files);
    }

    /**
     * Create a classloader for a classpath supplied as individual file names.
     *
     * @param files  a list of file/directory names
     * @param parent the parent for the new classloader
     * @return a classloader that will load classes from the supplied path
     */
    public static ClassLoader createClassLoader(ClassLoader parent, String[] files) {
        URL[] urls = new URL[files.length];
        for (int i = 0; i < files.length; i++) {
            try {
                File file = new File(files[i]);
                urls[i] = file.toURI().toURL();
            } catch (MalformedURLException e) {
                // just ignore this value
                continue;
            }
        }

        return new URLClassLoader(urls, parent);
    }

    /**
     * Create a classloader for a classpath supplied as a list of files.
     *
     * @param files  a list of files
     * @param parent the parent for the new classloader
     * @return a classloader that will load classes from the supplied path
     */
    public static ClassLoader createClassLoader(ClassLoader parent, File[] files) {
        URL[] urls = new URL[files.length];
        for (int i = 0; i < files.length; i++) {
            try {
                File file = files[i];
                urls[i] = file.toURI().toURL();
            } catch (MalformedURLException e) {

                continue;
            }
        }
        return new URLClassLoader(urls, parent);
    }
}
