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
package org.apache.tuscany.implementation.osgi.runtime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Hashtable;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

public class KnopflerfishRuntime extends OSGiRuntime  {
    
    private static BundleContext bundleContext;
    
    private static KnopflerfishRuntime instance;
    

    private static Class frameworkClass;
    
    private static Object framework;
    
    public static OSGiRuntime getInstance() {
        if (instance == null)
            instance = new KnopflerfishRuntime();
        return instance;
    }
    
   

    // FIXME: Knopflerfish does not expose the methods used for configuration as public methods
    //        It may be worth using the private methods in org.knopflerfish.framework.Main for
    //        configuring using reflection if security policies allow it.
    //        For now, a simple configuration routine reads sca.xargs from the directory in 
    //        the classpath which contains framework.jar. The entries in init.xargs starting with
    //        -install are assumed to be single-line entries with full bundle location.
    //
    protected BundleContext startRuntime() throws Exception {
        
        if (bundleContext != null)
            return bundleContext;
        
        
        
        System.setProperty("org.knopflerfish.framework.bundlestorage", "memory");
                    
        frameworkClass = KnopflerfishRuntime.class.getClassLoader().loadClass("org.knopflerfish.framework.Framework");
        Constructor frameworkConstructor = frameworkClass.getConstructor(Object.class);
        framework = frameworkConstructor.newInstance(new KnopflerfishRuntime());
        Method launchMethod = frameworkClass.getMethod("launch", long.class);
        launchMethod.invoke(framework, 0);
        Method getContextMethod = frameworkClass.getMethod("getSystemBundleContext");
        bundleContext = (BundleContext)getContextMethod.invoke(framework);
       
        System.setProperty("com.gatespace.bundle.cm.store", "knopflerfish.store");
        File xargsFile = null;
        String classpath = System.getProperty("java.class.path");
        String[] classpathEntries = classpath.split(System.getProperty("path.separator"));
        for (int i = 0; i < classpathEntries.length; i++) {
            if (classpathEntries[i].endsWith("framework.jar")) {
                String path = classpathEntries[i].substring(0, classpathEntries[i].length() - "framework.jar".length());
                path = path + "sca.xargs";
                xargsFile = new File(path);
                if (!xargsFile.exists())
                    xargsFile = null;
                break;
            }
        }
        if (xargsFile != null) {
            BufferedReader reader = new BufferedReader(new FileReader(xargsFile));
            String line;
            Hashtable<String, Bundle> bundles = new Hashtable<String, Bundle>();
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("-install")) {
                    try {

                        String bundleLocation = line.substring("-install".length()).trim();
                        Bundle bundle = bundleContext.installBundle(bundleLocation);
                        bundles.put(bundleLocation, bundle);
                        
                    } catch (BundleException e) {
                        e.printStackTrace();
                    }
                    
                }
                if (line.startsWith("-start")) {

                    try {
                        String bundleLocation = line.substring("-start".length()).trim();
                        Bundle bundle = bundles.get(bundleLocation);
                        bundle.start();
                    } catch (BundleException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        
        
        return bundleContext;
        
    }
    
    

    @Override
    protected void shutdownRuntime() throws Exception {

        if (bundleContext == null)
            return;
        bundleContext = null;
        if (framework != null) {
            Method shutdownMethod = frameworkClass.getMethod("shutdown");
            try {
                shutdownMethod.invoke(framework);
            } catch (Exception e) {
                // Ignore errors
            }
            framework = null;
        }
        
    }
}
