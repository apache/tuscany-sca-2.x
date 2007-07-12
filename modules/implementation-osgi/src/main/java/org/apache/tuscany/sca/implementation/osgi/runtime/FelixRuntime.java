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
package org.apache.tuscany.sca.implementation.osgi.runtime;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class FelixRuntime extends OSGiRuntime implements BundleActivator {
    
    private static BundleContext bundleContext;
    
    private static FelixRuntime instance;
    
    private static Class felixClass;
    private static Object felix;
    
    public static OSGiRuntime getInstance() throws Exception {
        if (instance == null) {
            FelixRuntime runtime = new FelixRuntime();
            runtime.startRuntime();
            instance = runtime;
        }
        return instance;
    }
    
    
    private static void deleteDirectory(File dir) {
        
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory())
                deleteDirectory(files[i]);
            else
                files[i].delete();
        }
        dir.delete();
        
    }
    
    private BundleContext startRuntime() throws Exception {
        
        if (bundleContext != null)
            return bundleContext;
        
               
        ClassLoader cl = FelixRuntime.class.getClassLoader();
        
        Class felixMainClass = cl.loadClass("org.apache.felix.main.Main");
        felixClass = cl.loadClass("org.apache.felix.framework.Felix");
        Method propsMethod = felixMainClass.getMethod("loadConfigProperties");
        Properties props = (Properties)propsMethod.invoke(null);
        
        File profileDir = new File(".felix"); 
        if (profileDir.isDirectory()) 
            deleteDirectory(profileDir);
        else
            profileDir.delete();
        profileDir.mkdir();
        profileDir.deleteOnExit();
        
        props.put("felix.cache.profiledir", profileDir.getAbsolutePath());
        props.put("felix.embedded.execution", "true");
        props.put("org.osgi.framework.system.packages", 
                "org.osgi.framework; version=1.3.0," +
                "org.osgi.service.packageadmin; version=1.2.0, " +
                "org.osgi.service.startlevel; version=1.0.0, " +
                "org.osgi.service.url; version=1.0.0, " +
                "org.osoa.sca.annotations; version=1.0.0, " +
                "org.osoa.sca; version=1.0.0");
        
        
        try {
            Constructor felixConstructor = felixClass.getConstructor(Map.class, List.class);
            List<BundleActivator> activators = new ArrayList<BundleActivator>();
            felix = felixConstructor.newInstance(props, activators);
            ((Bundle)felix).start();
            bundleContext = ((Bundle)felix).getBundleContext();
            
            
        } catch (Exception e) {
            
            // This is the older Felix API which has been retained temporarily to avoid build break
            // TODO: Remove these once Felix 1.0.0 is released.
            
            Class propertyResolverClass = cl.loadClass("org.apache.felix.framework.util.MutablePropertyResolver");
            Class propertyResolverImplClass = cl.loadClass("org.apache.felix.framework.util.MutablePropertyResolverImpl");

            Constructor implConstructor = propertyResolverImplClass.getConstructor(Map.class);
            Object mutableProps = implConstructor.newInstance(props);
            
            
            try {
                Constructor felixConstructor = felixClass.getConstructor(propertyResolverClass, List.class);
                List<BundleActivator> activators = new ArrayList<BundleActivator>();
                felix = felixConstructor.newInstance(mutableProps, activators);
                ((Bundle)felix).start();
                bundleContext = ((Bundle)felix).getBundleContext();
            } catch (Exception e1) {
                
        
                felix = felixClass.newInstance();
                Method startMethod = felixClass.getMethod("start", propertyResolverClass, List.class);
                List<BundleActivator> activators = new ArrayList<BundleActivator>();
                BundleActivator activator = new FelixRuntime();
                activators.add(activator);
                startMethod.invoke(felix, mutableProps, activators);
        
                synchronized (activator) {
                    int retries = 0;
                    while (bundleContext == null && retries++ < 10) {
                        activator.wait(1000);
                    }
                }
            }
        }
        
        return bundleContext;
        
    }

    public void start(BundleContext context) throws Exception {
        
        bundleContext = context;
        synchronized (this) {
            this.notify();
        }
    }

    public void stop(BundleContext context) throws Exception {
        bundleContext = null;
    }
    
    
    
    @Override
    public BundleContext getBundleContext() {
        return bundleContext;
    }


    @Override
    public void shutdown() throws Exception {

        if (bundleContext == null)
            return;
        bundleContext = null;
        instance = null;
        
        if (felix instanceof Bundle) {
            ((Bundle)felix).stop();
        }
        else if (felix != null) {
            Method shutdownMethod = felixClass.getMethod("shutdown");
            try {
                shutdownMethod.invoke(felix);
            } catch (Exception e) {
                // Ignore errors
            }            
            felix = null;
        }
    }
    
    @Override
    public boolean supportsBundleFragments() {
        return false;
    }

}
