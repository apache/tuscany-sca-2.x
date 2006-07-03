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
package org.apache.tuscany.core.launcher;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Support class for launcher implementations.
 *
 * @version $Rev: 417136 $ $Date: 2006-06-26 03:54:48 -0400 (Mon, 26 Jun 2006) $
 */
public abstract class LauncherSupport {
    protected static final FilenameFilter FILE_FILTER = new FilenameFilter() {

        public boolean accept(File dir, String name) {
            return name.endsWith(".jar");
        }

    };

    protected static ClassLoader tuscanyClassLoader;

    private ClassLoader applicationLoader = ClassLoader.getSystemClassLoader();
    private String className;
    private String[] args;

    /**
     * Returns the classloader for application classes.
     *
     * @return the classloader for application classes
     */
    protected ClassLoader getApplicationLoader() {
        return applicationLoader;
    }

    /**
     * Set the classloader to be used for application classes.
     *
     * @param applicationLoader the classloader to be used for application classes
     */
    protected void setApplicationLoader(ClassLoader applicationLoader) {
        this.applicationLoader = applicationLoader;
    }

    /**
     * Create a classloader for the supplied classpath.
     *
     * @param path   a list of file/directory names separated by the platform path separator
     * @param parent the parent for the new classloader
     * @return a classloader that will load classes from the supplied path
     */
    protected static ClassLoader createClassLoader(ClassLoader parent, String path) {
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
    protected static ClassLoader createClassLoader(ClassLoader parent, String[] files) {
        URL[] urls = new URL[files.length];
        for (int i = 0; i < files.length; i++) {
            try {
                File file = new File(files[i]);
                urls[i] = file.toURI().toURL();
            } catch (MalformedURLException e) {
                // just ignore this value
                e.printStackTrace();
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
    protected ClassLoader createClassLoader(ClassLoader parent, File[] files) {
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

    /**
     * Returns the name of the application class.
     *
     * @return the name of the application class
     */
    protected String getClassName() {
        return className;
    }

    /**
     * Sets the name of the application class.
     *
     * @param className the name of the application class
     */
    protected void setClassName(String className) {
        this.className = className;
    }

    protected String[] getArgs() {
        return args;
    }

    protected void setArgs(String[] args) {
        this.args = args;
    }

    protected static ClassLoader getTuscanyClassLoader() {
        if (null == tuscanyClassLoader) {
            //assume that even though the the rest of tuscany jars are not loaded
            // it  co-located with the rest of the tuscany jars.
            File tuscanylib = findLoadLocation();
            String[] jars = tuscanylib.list(FILE_FILTER);
            String[] urls = new String[jars.length];
            int i = 0;
            for (String jar : jars) {

                urls[i++] = tuscanylib.getAbsolutePath() + "/" + jar;
                System.err.println("classpath '" + urls[i - 1] + "'");
            }

            tuscanyClassLoader = createClassLoader(LauncherSupport.class.getClassLoader(), urls);
        }
        return tuscanyClassLoader;
    }

    protected static File findLoadLocation() {

        String resourceName = LauncherSupport.class.getName().replace('.', '/') + ".class";
        URL dirURL = LauncherSupport.class.getClassLoader().getResource(resourceName);
        String location = dirURL.getFile();
//        boolean jarred = false;
        if ("jar".equals(dirURL.getProtocol())) {
//            jarred = true;
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
}
