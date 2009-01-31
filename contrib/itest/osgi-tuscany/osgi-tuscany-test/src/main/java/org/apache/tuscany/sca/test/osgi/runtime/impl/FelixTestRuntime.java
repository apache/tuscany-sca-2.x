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
package org.apache.tuscany.sca.test.osgi.runtime.impl;

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

public class FelixTestRuntime extends OSGiTestRuntime implements BundleActivator {
    
    private static BundleContext bundleContext;
    
    private static FelixTestRuntime instance;
    
    private static Bundle felix;
    
    
    public static OSGiTestRuntime getInstance() throws Exception {
        if (instance == null) {
            
            FelixTestRuntime runtime = new FelixTestRuntime();
            instance = runtime;
        }
        return instance;
    }
    
    
//    private void deleteDirectory(File dir) {
//        File[] files = dir.listFiles();
//        for (int i = 0; i < files.length; i++) {
//            if (files[i].isDirectory()) 
//                deleteDirectory(files[i]);
//            else {
//                files[i].delete();
//            }
//        }
//        dir.delete();
//        
//    }
    
//    private void deleteProfile() {
//        String profileDirName = System.getProperty("felix.cache.profiledir");
//        if (profileDirName == null) profileDirName = ".felix";
//        File profileDir = new File(profileDirName); 
//        if (profileDir.isDirectory())  
//            deleteDirectory(profileDir);
//        else
//            profileDir.delete();
//    }
    
    protected BundleContext startRuntime() throws Exception {
        
        if (bundleContext != null)
            return bundleContext;
        
        ClassLoader cl = this.getClass().getClassLoader();
        Class<?> felixMainClass = cl.loadClass("org.apache.felix.main.Main");
        Class<?> felixClass = cl.loadClass("org.apache.felix.framework.Felix");
        Method propsMethod = felixMainClass.getMethod("loadConfigProperties");
        Properties props = (Properties)propsMethod.invoke(null);
        
        //deleteProfile();
        // Create profile directory
        String profileDirName = System.getProperty("felix.cache.profiledir");
        if (profileDirName == null) profileDirName = ".felix";
        File profileDir = new File(profileDirName); 
        profileDir.mkdir();
        
        props.put("felix.cache.profiledir", profileDir.getAbsolutePath());
        props.put("felix.embedded.execution", "true");
        String systemPackages = getSystemPackages() + 
                ", org.apache.felix.main";
        
        
        props.put("org.osgi.framework.system.packages", systemPackages);
        
        Constructor felixConstructor = felixClass.getConstructor(Map.class, List.class);
        List<BundleActivator> activators = new ArrayList<BundleActivator>();

        Class<?> autoActivatorClass = cl.loadClass("org.apache.felix.main.AutoActivator");
        Constructor autoActivatorConstructor = autoActivatorClass.getConstructor(Map.class);
        BundleActivator autoActivator = (BundleActivator)autoActivatorConstructor.newInstance(props);            
        activators.add(autoActivator);
        felix = (Bundle)felixConstructor.newInstance(props, activators);
        felix.start();
        bundleContext = felix.getBundleContext();
                   
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
    protected void setBundleContext(BundleContext bundleContext) {
        super.setBundleContext(bundleContext);
        FelixTestRuntime.bundleContext = bundleContext;
    }


    @Override
    public void shutdown() throws Exception {

        if (bundleContext == null)
            return;
        
        bundleContext = null;
        instance = null;
        
        felix.stop();
        int retries = 50;
        synchronized (felix) {
            while (retries-- > 0 && felix.getState() != Bundle.UNINSTALLED) {
                felix.wait(100);
            }
        }
        
        super.shutdown();
    }
    
    
}
