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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.felix.framework.Felix;
import org.apache.felix.main.AutoActivator;
import org.apache.felix.main.Main;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class FelixTestRuntime extends OSGiTestRuntime implements BundleActivator {
    
    private static BundleContext bundleContext;
    
    private static FelixTestRuntime instance;
    
    private static Felix felix;
    
    
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
        
        Properties props = Main.loadConfigProperties();
        
        //deleteProfile();
        // Create profile directory
        String profileDirName = System.getProperty("felix.cache.profiledir");
        if (profileDirName == null) profileDirName = ".felix";
        File profileDir = new File(profileDirName); 
        profileDir.mkdir();
        
        props.put("felix.cache.profiledir", profileDir.getAbsolutePath());
        props.put("felix.embedded.execution", "true");
        String systemPackages =
                "org.osgi.framework; version=1.3.0," +
                "org.osgi.service.packageadmin; version=1.2.0, " +
                "org.osgi.service.startlevel; version=1.0.0, " +
                "org.osgi.service.url; version=1.0.0, " +
                "org.osgi.util.tracker; version=1.3.2, " +
                "javax.xml, " +
                "javax.xml.datatype, " +               
                "javax.xml.namespace, " +
                "javax.xml.parsers, " +
                "javax.xml.transform, " +
                "javax.xml.transform.dom, " +
                "javax.xml.transform.sax, " +
                "javax.xml.transform.stream, " +
                "javax.xml.validation, " +
                "javax.xml.xpath, " +
                "javax.sql," +
                "org.w3c.dom, " +
                "org.xml.sax, " +
                "org.xml.sax.ext, " +
                "org.xml.sax.helpers, " +
                "javax.security.auth, " +
                "javax.security.auth.login, " +
                "javax.security.auth.callback, " +
                "javax.naming, " +
                "javax.naming.spi, " +
                "javax.naming.directory, " +
                "javax.management, " + 
                "sun.misc, " +
                "javax.net, " +
                "javax.crypto, " +
                "javax.rmi, " +
                "javax.transaction, " +
                "javax.transaction.xa, " +
                
                "org.apache.felix.main";
        
        
        props.put("org.osgi.framework.system.packages", systemPackages);

        List<BundleActivator> activators = new ArrayList<BundleActivator>();
        AutoActivator autoActivator = new AutoActivator(props);
        activators.add(autoActivator);
        felix = new Felix(props, activators);
        ((Bundle)felix).start();
        bundleContext = ((Bundle)felix).getBundleContext();
                   
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
