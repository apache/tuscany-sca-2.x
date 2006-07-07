/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.launcher;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ResourceBundle;

/**
 * Launcher for launcher runtime environment that invokes a jar's Main class.
 *
 * @version $Rev: 412898 $ $Date: 2006-06-08 21:31:50 -0400 (Thu, 08 Jun 2006) $
 */
public class MainLauncherBooter {
    /**
     * Main method.
     *
     * @param args the command line args
     */
    public static void main(String[] args) throws Throwable {
        // The classpath to load the launcher should not contain any of
        // Tuscany jar files except the launcher.
        MainLauncherBooter booter = new MainLauncherBooter();
        ClassLoader tuscanyCL = booter.getTuscanyClassLoader();

        Class<?> launcherClass;
        try {
            String className = System.getProperty("tuscany.launcherClass",
                                                  "org.apache.tuscany.core.launcher.MainLauncher");
            launcherClass = tuscanyCL.loadClass(className);
        } catch (ClassNotFoundException e) {
            System.err.println("Tuscany bootstrap class not found: " + e.getMessage());
            System.exit(2);
            throw new AssertionError();
        }

        Method mainMethod;
        try {
            mainMethod = launcherClass.getMethod("boot", String[].class);
        } catch (NoSuchMethodException e) {
            // this is our class so the method should be there
            throw new AssertionError(e);
        }

        try {
            Object launcher = launcherClass.newInstance();
            mainMethod.invoke(launcher, args);
        } catch (InstantiationException e) {
            throw new AssertionError(e);
        } catch (IllegalAccessException e) {
            throw new AssertionError(e);
        } catch (IllegalArgumentException e) {
            throw new AssertionError(e);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    protected ClassLoader getTuscanyClassLoader() {
        File tuscanylib = findBootDir();
        URL[] urls = scanDirectory(tuscanylib);
        return new URLClassLoader(urls, getClass().getClassLoader());
    }

    /**
     * Scan a directory for jar files to be added to the classpath.
     *
     * @param tuscanylib the directory to scan
     * @return the URLs or jar files in that directory
     */
    protected URL[] scanDirectory(File tuscanylib) {
        File[] jars = tuscanylib.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
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
        return urls;
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
