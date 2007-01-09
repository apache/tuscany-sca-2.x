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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.osoa.sca.CompositeContext;
import org.osoa.sca.SCA;

import org.apache.tuscany.host.runtime.TuscanyRuntime;
import org.apache.tuscany.runtime.standalone.DirectoryHelper;
import org.apache.tuscany.runtime.standalone.StandaloneRuntimeInfo;
import org.apache.tuscany.runtime.standalone.StandaloneRuntimeInfoImpl;

/**
 * Launcher for launcher runtime environment that invokes a jar's Main class.
 *
 * @version $Rev$ $Date$
 */
public class Main {
    /**
     * Main method.
     *
     * @param args the command line args
     * @throws Throwable if there are problems launching the runtime or application
     */
    public static void main(String[] args) throws Throwable {
        if (args.length == 0) {
            usage();
        }

        StandaloneRuntimeInfo runtimeInfo = createRuntimeInfo();
        TuscanyRuntime runtime = createRuntime(runtimeInfo);
        runtime.initialize();
        try {
            File applicationJar = new File(args[0]);
            URL applicationURL = applicationJar.toURI().toURL();
            String[] appArgs = new String[args.length - 1];
            System.arraycopy(args, 1, appArgs, 0, appArgs.length);

            ClassLoader applicationClassLoader =
                new URLClassLoader(new URL[]{applicationURL}, runtime.getHostClassLoader());

            URL applicationScdl = getApplicationScdl(applicationClassLoader);

            final CompositeContext context = runtime.deployApplication("application",
                                                                       applicationScdl,
                                                                       applicationClassLoader);

            // FIXME JNB we should replace this with CurrentCompositeContext.setContext(...)
            SCA sca = new SCA() {

                public void start() {
                    setCompositeContext(context);
                }

                public void stop() {
                    setCompositeContext(null);
                }
            };

            sca.start();
            try {
                runApplication(applicationJar, applicationClassLoader, appArgs);
            } finally {
                sca.stop();
            }
        } finally {
            runtime.destroy();
        }

    }

    static StandaloneRuntimeInfo createRuntimeInfo() throws IOException {
        // get profile to use, defaulting to "launcher"
        String profile = System.getProperty("tuscany.profile", "launcher");

        File installDir = DirectoryHelper.getInstallDirectory(Main.class);
        File profileDir = DirectoryHelper.getProfileDirectory(installDir, profile);

        // load properties for this runtime
        File propFile = new File(profileDir, "etc/runtime.properties");
        Properties props = DirectoryHelper.loadProperties(propFile, System.getProperties());

        // online unless the offline property is set
        boolean online = !Boolean.parseBoolean(props.getProperty("offline", "false"));

        return new StandaloneRuntimeInfoImpl(profile, installDir, profileDir, null, online, props);
    }

    static TuscanyRuntime createRuntime(StandaloneRuntimeInfo runtimeInfo) throws Exception {
        File installDir = runtimeInfo.getInstallDirectory();
        File profileDir = runtimeInfo.getProfileDirectory();
        URL profileURL = DirectoryHelper.toURL(profileDir);
        ClassLoader hostClassLoader = ClassLoader.getSystemClassLoader();

        // create the classloader for booting the runtime
        String bootPath = runtimeInfo.getProperty("tuscany.bootDir", null);
        File bootDir = DirectoryHelper.getBootDirectory(installDir, profileDir, bootPath);
        ClassLoader bootClassLoader = DirectoryHelper.createClassLoader(hostClassLoader, bootDir);

        // locate the system SCDL
        URL systemSCDL = new URL(profileURL, runtimeInfo.getProperty("tuscany.systemSCDL", "system.scdl"));

        // locate the implementation
        String className = runtimeInfo.getProperty("tuscany.runtimeClass",
                                                   "org.apache.tuscany.runtime.standalone.host.StandaloneRuntimeImpl");
        Class<?> implClass = Class.forName(className, true, bootClassLoader);

        TuscanyRuntime runtime = (TuscanyRuntime) implClass.newInstance();
        runtime.setMonitorFactory(runtime.createDefaultMonitorFactory());
        runtime.setHostClassLoader(hostClassLoader);
        runtime.setSystemScdl(systemSCDL);
        runtime.setRuntimeInfo(runtimeInfo);
        return runtime;
    }

    private static void usage() {
        ResourceBundle bundle = ResourceBundle.getBundle(Main.class.getName());
        System.err.println(bundle.getString("org.apache.tuscany.launcher.Usage"));
        System.exit(1);
    }

    static void runApplication(File applicationJar, ClassLoader applicationClassLoader, String[] args)
        throws Throwable {

        Manifest manifest = new JarFile(applicationJar).getManifest();
        String mainClassName = manifest.getMainAttributes().getValue("Main-Class");
        if (mainClassName == null) {
            ResourceBundle bundle = ResourceBundle.getBundle(Main.class.getName());
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

    static URL getApplicationScdl(ClassLoader applicationClassLoader) {
        String resource = System.getProperty("tuscany.applicationScdlPath", "META-INF/sca/default.scdl");
        return applicationClassLoader.getResource(resource);
    }
}
