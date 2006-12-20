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
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ResourceBundle;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.osoa.sca.SCA;

import org.apache.tuscany.host.runtime.TuscanyRuntime;
import org.apache.tuscany.host.util.LaunchHelper;
import org.apache.tuscany.runtime.standalone.StandaloneRuntimeInfo;
import org.apache.tuscany.runtime.standalone.StandaloneRuntimeInfoImpl;

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
        if (args.length == 0) {
            usage();
        }
        MainLauncherBooter booter = new MainLauncherBooter();

        File installDir = getInstallDirectory();
        URL baseUrl = installDir.toURI().toURL();
        File bootDir = getBootDirectory(installDir);

        boolean online = !Boolean.parseBoolean(System.getProperty("offline", Boolean.FALSE.toString()));
        StandaloneRuntimeInfo runtimeInfo = new StandaloneRuntimeInfoImpl(baseUrl, installDir, installDir, online);

        File applicationJar = new File(args[0]);
        URL applicationURL = applicationJar.toURI().toURL();
        String[] appArgs = new String[args.length - 1];
        System.arraycopy(args, 1, appArgs, 0, appArgs.length);

        ClassLoader hostClassLoader = ClassLoader.getSystemClassLoader();
        ClassLoader bootClassLoader = booter.getTuscanyClassLoader(bootDir);
        ClassLoader applicationClassLoader = new URLClassLoader(new URL[]{applicationURL}, hostClassLoader);

        URL systemScdl = booter.getSystemScdl(bootClassLoader);
        URL applicationScdl = booter.getApplicationScdl(applicationClassLoader);

        String className = System.getProperty("tuscany.launcherClass",
            "org.apache.tuscany.runtime.standalone.host.StandaloneRuntimeImpl");
        TuscanyRuntime runtime = (TuscanyRuntime) Beans.instantiate(bootClassLoader, className);
        runtime.setMonitorFactory(runtime.createDefaultMonitorFactory());
        runtime.setSystemScdl(systemScdl);
        runtime.setHostClassLoader(hostClassLoader);
        runtime.setApplicationName("application");
        runtime.setApplicationScdl(applicationScdl);
        runtime.setApplicationClassLoader(applicationClassLoader);
        runtime.setRuntimeInfo(runtimeInfo);
        runtime.initialize();
        SCA context = runtime.getContext();

        try {
            context.start();
            booter.runApplication(applicationJar, applicationClassLoader, appArgs);
        } finally {
            context.stop();
            runtime.destroy();
        }
    }

    private static void usage() {
        ResourceBundle bundle = ResourceBundle.getBundle(MainLauncherBooter.class.getName());
        System.err.println(bundle.getString("org.apache.tuscany.launcher.Usage"));
        System.exit(1);
    }

    protected void runApplication(File applicationJar, ClassLoader applicationClassLoader, String[] args)
        throws Throwable {

        Manifest manifest = new JarFile(applicationJar).getManifest();
        String mainClassName = manifest.getMainAttributes().getValue("Main-Class");
        if (mainClassName == null) {
            ResourceBundle bundle = ResourceBundle.getBundle(MainLauncherBooter.class.getName());
            String s = bundle.getString("org.apache.tuscany.launcher.NoMain-Class");
            throw new IllegalArgumentException(String.format(s, applicationJar.toString()));
        }
        Class<?> mainClass = applicationClassLoader.loadClass(mainClassName);
        Method main = mainClass.getMethod("main", String[].class);


        ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(applicationClassLoader);
            main.invoke(null, new Object[]{args});
        } catch (InvocationTargetException e) {
            throw e.getCause();
        } finally {
            Thread.currentThread().setContextClassLoader(oldCL);
        }
    }

    protected ClassLoader getTuscanyClassLoader(File bootDir) {
        URL[] urls = LaunchHelper.scanDirectoryForJars(bootDir);
        return new URLClassLoader(urls, getClass().getClassLoader());
    }

    /**
     * Find the directory containing the bootstrap jars. If the <code>tuscany.bootDir</code> system property is set then
     * its value is used as the boot directory. Otherwise, we locate a jar file containing this class and return a
     * "boot" directory that is a sibling to the directory that contains it. This class must be loaded from a jar file
     * located on the local filesystem.
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

    protected URL getSystemScdl(ClassLoader bootClassLoader) {
        String resource = System.getProperty("tuscany.systemScdlPath", "META-INF/tuscany/system.scdl");
        return bootClassLoader.getResource(resource);
    }

    protected URL getApplicationScdl(ClassLoader applicationClassLoader) {
        String resource = System.getProperty("tuscany.applicationScdlPath", "META-INF/sca/default.scdl");
        return applicationClassLoader.getResource(resource);
    }

    public static File getInstallDirectory() {
        // use system property if defined
        String property = System.getProperty("tuscany.installDir");
        if (property != null) {
            return new File(property);
        }

        // use the parent of directory containing this command
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
        return jarFile.getParentFile().getParentFile();
    }

    public static File getBootDirectory(File installDirectory) {
        // use system property if defined
        String property = System.getProperty("tuscany.bootDir");
        if (property != null) {
            return new File(property);
        }
        return new File(installDirectory, "boot");
    }
}
