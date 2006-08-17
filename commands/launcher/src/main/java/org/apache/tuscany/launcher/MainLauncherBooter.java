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
package org.apache.tuscany.launcher;

import java.beans.Beans;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ResourceBundle;

import org.apache.tuscany.hostutil.LaunchHelper;

/**
 * Launcher for launcher runtime environment that invokes a jar's Main class.
 *
 * @version $Rev$ $Date$
 */
public class MainLauncherBooter {
    /**
     * Main method.
     *
     * @param args the command line args
     */
    public static void main(String[] args) throws Throwable {
        // The classpath to load the launcher should not any jars from the Tuscany runtime
        MainLauncherBooter booter = new MainLauncherBooter();
        ClassLoader tuscanyCL = booter.getTuscanyClassLoader();

        String className = System.getProperty("tuscany.launcherClass",
                                              "org.apache.tuscany.core.launcher.MainLauncherImpl");
        Object launcher = Beans.instantiate(tuscanyCL, className);
        try {
            LaunchHelper.invoke(launcher, "boot", new Class<?>[]{String[].class}, (Object[]) new Object[]{args});
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    protected ClassLoader getTuscanyClassLoader() {
        File tuscanylib = findBootDir();
        URL[] urls = LaunchHelper.scanDirectoryForJars(tuscanylib);
        return new URLClassLoader(urls, getClass().getClassLoader());
    }


    /**
     * Find the directory containing the bootstrap jars.
     * If the <code>tuscany.bootDir</code> system property is set then its value is used as the boot directory.
     * Otherwise, we locate a jar file containing this class and return a "boot" directory that is a sibling
     * to the directory that contains it. This class must be loaded from a jar file located on the local filesystem.
     *
     * @return the directory of the bootstrap jars
     */
    protected File findBootDir() {
        String property = System.getProperty("tuscany.bootDir");
        if (property != null) {
            return new File(property);
        }

        URL url = MainLauncherBooter.class.getResource("MainLauncherBooter.class");
        if (!"jar".equals(url.getProtocol())) {
            throw new IllegalStateException("Must be run from a jar: " + url);
        }
        String jarLocation = url.toString();
        jarLocation = jarLocation.substring(4, jarLocation.lastIndexOf("!/"));
        if (!jarLocation.startsWith("file:")) {
            throw new IllegalStateException("Must be run from a local filesystem: " + jarLocation);
        }

        File jarFile = new File(URI.create(jarLocation));
        return new File(jarFile.getParentFile().getParentFile(), "boot");
    }

    protected void usage() {
        ResourceBundle bundle = ResourceBundle.getBundle(MainLauncherBooter.class.getName());
        System.err.print(bundle.getString("org.apache.tuscany.launcher.Usage"));
        System.exit(1);
    }
}
