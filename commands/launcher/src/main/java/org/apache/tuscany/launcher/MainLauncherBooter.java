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
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ResourceBundle;

/**
 * Launcher for launcher runtime environment that invokes a jar's Main class.
 *
 * @version $Rev: 412898 $ $Date: 2006-06-08 21:31:50 -0400 (Thu, 08 Jun 2006) $
 */
public class MainLauncherBooter {
    protected static final FilenameFilter FILE_FILTER = new FilenameFilter() {
        public boolean accept(File dir, String name) {
            return name.endsWith(".jar");
        }
    };

    /**
     * Main method.
     *
     * @param args the command line args
     */
    public static void main(String[] args) throws Throwable {
        // The classpath to load the launcher should not contain any of
        // Tuscany jar files except the launcher.
        ClassLoader currentContextClassLoader = Thread.currentThread().getContextClassLoader();
        try {

            ClassLoader tuscanyCL = getTuscanyClassLoader();
            Class<?> launcherClass = tuscanyCL.loadClass("org.apache.tuscany.core.launcher.MainLauncher");

            Method mainMethod = launcherClass.getMethod("main", String[].class);
            Thread.currentThread().setContextClassLoader(tuscanyCL);
            mainMethod.invoke(null, new Object[]{args});

        } catch (ClassNotFoundException e) {
            System.err.println(e);
            e.printStackTrace();
            System.err.println("Main-Class not found: " + e.getMessage());
            System.exit(1);

        } catch (InvocationTargetException e) {
            e.getCause().printStackTrace(System.err);
            System.exit(2);
        } finally {
            Thread.currentThread().setContextClassLoader(currentContextClassLoader);
        }
    }

    protected static ClassLoader getTuscanyClassLoader() {
        // assume that even though the the rest of tuscany jars are not loaded
        // it co-located with the rest of the tuscany jars.
        File tuscanylib = findLoadLocation();
        File[] jars = tuscanylib.listFiles(FILE_FILTER);
        URL[] urls = new URL[jars.length];
        for (int i = 0; i < jars.length; i++) {
            try {
                urls[i] = jars[i].toURI().toURL();
            } catch (MalformedURLException e) {
                // toURI should have escaped the URL
                throw new AssertionError();
            }
        }
        return new URLClassLoader(urls, MainLauncherBooter.class.getClassLoader());
    }

    protected static File findLoadLocation() {

        String resourceName = MainLauncherBooter.class.getName().replace('.', '/') + ".class";
        URL dirURL = MainLauncherBooter.class.getClassLoader().getResource(resourceName);
        String location = dirURL.getFile();
        // boolean jarred = false;
        if ("jar".equals(dirURL.getProtocol())) {
            // jarred = true;
            int sep = location.indexOf('!');
            if (sep != -1) {
                location = location.substring(0, sep);
            }
        }
        if (location.startsWith("file:")) {
            location = location.substring(5);
        }
        if (File.separatorChar == '\\'
                && location.length() > 2
                && location.charAt(0) == '/'
                && location.charAt(2) == ':') {
            location = location.substring(1);
        }
        File locfile = new File(location);
        return locfile.getParentFile();
    }

    protected void usage() {
        ResourceBundle bundle = ResourceBundle.getBundle(MainLauncherBooter.class.getName());
        System.err.print(bundle.getString("org.apache.tuscany.launcher.Usage"));
        System.exit(1);
    }
}
