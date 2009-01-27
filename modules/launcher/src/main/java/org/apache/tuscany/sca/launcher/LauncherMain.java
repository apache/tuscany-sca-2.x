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

package org.apache.tuscany.sca.launcher;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class LauncherMain {
    
    private static final String DEFAULT_PROPERTY_FILENAME = "default.config";

    public static void main(String[] args) throws SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, URISyntaxException {

        Properties launcherProperties = getLauncherProperties(args);
        
        ClassLoader classLoader = getClassLoader(launcherProperties);

        String mainClassName = getMainClass(launcherProperties, classLoader);

        String[] mainArgs = getMainArgs(launcherProperties);

        invokeMainMethod(mainClassName, classLoader, mainArgs);
        
    }

    private static String[] getMainArgs(Properties launcherProperties) {
        String[] mainArgs = (String[])launcherProperties.get("launcherArgs");
        if (mainArgs == null) {
            mainArgs = new String[0];
        }
        return mainArgs;
    }

    private static String getMainClass(Properties launcherProperties, ClassLoader classLoader) {
        
        String mainClassName;
        String[] args = getMainArgs(launcherProperties);
        if (args.length > 0) {
            try {
                Class.forName(args[0], true, classLoader);
                mainClassName = args[0];
                String[] args2 = new String[args.length-1];
                System.arraycopy(args, 1, args2, 0, args.length-1);
                launcherProperties.put("launcherArgs", args2);
            } catch (ClassNotFoundException e) {
                mainClassName = launcherProperties.getProperty("mainClass");
            }
        } else {
            mainClassName = launcherProperties.getProperty("mainClass");
        }
        
        return mainClassName;
    }

    private static void invokeMainMethod(String className, ClassLoader classLoader, String[] args) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        try {

            Thread.currentThread().setContextClassLoader(classLoader);

            Class mainClass = Class.forName(className, true, classLoader);

            Method m = mainClass.getMethod("main", new Class[]{ args.getClass() });
            m.invoke(null, new Object[]{args});
            
        } finally {
            Thread.currentThread().setContextClassLoader(tccl);
        }
    }

    private static ClassLoader getClassLoader(Properties launcherProperties) {
        Set<URL> jarURLs = new HashSet<URL>(); 
        for (Enumeration<?> e = launcherProperties.propertyNames(); e.hasMoreElements();) {
            String pn = (String) e.nextElement();
            if (pn.startsWith("classpath")) {
                jarURLs.addAll(getJARs(launcherProperties.getProperty(pn)));
            }
        }
        ClassLoader parentCL = Thread.currentThread().getContextClassLoader();
        if (parentCL == null) {
            parentCL = LauncherMain.class.getClassLoader();
        }
        return new URLClassLoader(jarURLs.toArray(new URL[]{}), parentCL);
    }

    /**
     * Gets the jars matching a config classpath property
     * property values may be an explicit jar name or use an asterix wildcard for
     * all jars in a folder, or a double asterix '**' for all jars in a folder and its subfolders
     */
    private static Set<URL> getJARs(String classpathValue) {
        Set<URL> jarURLs = new HashSet<URL>(); 
        if (classpathValue.endsWith("**")) {
            File folder = new File(classpathValue.substring(0, classpathValue.length()-2));
            jarURLs.addAll(getFolderJars(folder));
            jarURLs.addAll(getSubFolderJars(folder));
        } else if (classpathValue.endsWith("*")) {
            File folder = new File(classpathValue.substring(0, classpathValue.length()-1));
            jarURLs.addAll(getFolderJars(folder));
        } else {
            File f = new File(classpathValue);
            try {
                jarURLs.add(f.toURI().toURL());
            } catch (MalformedURLException e) {
                throw new RuntimeException("Exception getting JAR URL", e);
            }
        }
        return jarURLs;
    }

    /**
     * Gets all the jars in a folder
     */
    private static Set<URL> getFolderJars(File folder) {
        Set<URL> jarURLs = new HashSet<URL>(); 
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles(new FilenameFilter(){
                public boolean accept(File dir, String name) {
                    return name.endsWith(".jar");
                }});
            for (File f : files) {
                try {
                    jarURLs.add(f.toURI().toURL());
                } catch (MalformedURLException e) {
                    throw new RuntimeException("Exception getting JAR URL", e);
                }
            }
        }
        return jarURLs;
    }

    /**
     * Recursively gets all the jars in a folder and its subfolders
     */
    private static Set<URL> getSubFolderJars(File folder) {
        Set<URL> jarURLs = new HashSet<URL>(); 
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles(new FileFilter(){
                public boolean accept(File pathname) {
                    return pathname.isDirectory();
                }});
            for (File f : files) {
                jarURLs.addAll(getFolderJars(f));
                jarURLs.addAll(getSubFolderJars(f));
            }
        }
        return jarURLs;
    }

    /**
     * Read the config properties for this launcher invocation
     * (Either default.config or the 1st cmd line argument suffixed with ".config" if that file exists 
     */
    private static Properties getLauncherProperties(String[] args) throws URISyntaxException {

        Properties properties = new Properties();

        String fileName;
        if (args.length > 0) {
            File f = new File(getLauncherFolder(), args[0] + ".config");
            if (f.exists()) {
                fileName = f.getName();
                String[] args2 = new String[args.length-1];
                System.arraycopy(args, 1, args2, 0, args.length-1);
                args = args2;
            } else {
                fileName = DEFAULT_PROPERTY_FILENAME;
            }
        } else {
            fileName = DEFAULT_PROPERTY_FILENAME;
        }
        
        File f = new File(fileName);
        if (!f.isAbsolute()) {
            f = new File(getLauncherFolder(), fileName);
        }

        try {
            FileInputStream fis = new FileInputStream(f);
            properties.load(fis);
            fis.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        properties.put("launcherArgs", args);
        
        return properties;
    }

    /**
     * Find the folder that contains the launcher jar
     */
    private static File getLauncherFolder() throws URISyntaxException {

        File folder = null;

        String resource = LauncherMain.class.getName().replace('.', '/') + ".class"; 
        URL url = LauncherMain.class.getClassLoader().getResource(resource);
        if (url != null) {
            URI uri = url.toURI();
            String scheme = uri.getScheme();
            if (uri.getScheme().equals("jar")) {
                String path = uri.toString().substring(4);
                int i = path.indexOf("!/");
                if (i != -1) {
                    path = path.substring(0, i);
                    uri = URI.create(path);
                }
        
                File file = new File(uri);
                if (file.exists()) {
                    File jarDirectory = file.getParentFile();
                    if (jarDirectory != null && jarDirectory.exists()) {
                        folder = file;
                    }
                }
            }
        }
        folder = folder.getParentFile();
        return folder;
    }
}
