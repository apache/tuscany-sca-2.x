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
package org.apache.tuscany.sca.osgi.runtime;

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

/**
 * Implementation of an OSGi Runtime using Felix.
 *
 * @version $Rev$ $Date$
 */
public class FelixRuntime extends OSGiRuntime implements BundleActivator {
    
    private static BundleContext bundleContext;
    
    private static FelixRuntime instance;
    
    private static Class<?> felixMainClass;
    private static Class<?> felixClass;
    private static Object felix;
    
    public static OSGiRuntime getInstance() throws Exception {
        if (instance == null) {
            felixMainClass = FelixRuntime.class.getClassLoader().loadClass("org.apache.felix.main.Main");
            FelixRuntime runtime = new FelixRuntime();
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

    @Override
    protected BundleContext startRuntime(boolean tuscanyRunningInOSGiContainer) throws Exception {
        
        if (bundleContext != null)
            return bundleContext;
        
               
        ClassLoader cl = FelixRuntime.class.getClassLoader();
        
        felixClass = cl.loadClass("org.apache.felix.framework.Felix");
        Method propsMethod = felixMainClass.getMethod("loadConfigProperties");
        Properties props = (Properties)propsMethod.invoke(null);
        
        String profileDirName = ".felix";
        File targetDir = new File("target");
        if (targetDir.exists() && targetDir.isDirectory())
            profileDirName = "target/" + profileDirName;
        File profileDir = new File(profileDirName);
        if (profileDir.isDirectory()) 
            deleteDirectory(profileDir);
        else
            profileDir.delete();
        profileDir.mkdir();
        profileDir.deleteOnExit();
        
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
                "org.apache.xerces.jaxp.datatype, " +
                "org.w3c.dom, " +
                "org.xml.sax, " +
                "org.xml.sax.ext, " +
                "org.xml.sax.helpers, " +
                "javax.security.auth, " +
                "javax.naming, " +
                "javax.naming.spi, " +
                "javax.naming.directory, " +
                "javax.management, " + 
                "sun.misc";
        
        
        if (!tuscanyRunningInOSGiContainer) {
            systemPackages = systemPackages + ", org.osoa.sca.annotations, org.osoa.sca";
            systemPackages = systemPackages + ", commonj.sdo, commonj.sdo.helper, org.apache.tuscany.sdo.helper, org.apache.tuscany.sdo.impl, org.apache.tuscany.sdo.model, org.apache.tuscany.sdo.model.impl";
            systemPackages = systemPackages + ", org.eclipse.emf.ecore, org.eclipse.emf.ecore.util, org.eclipse.emf.ecore.impl";
        }
        props.put("org.osgi.framework.system.packages", systemPackages);
        
        try {
            Constructor felixConstructor = felixClass.getConstructor(Map.class, List.class);
            List<BundleActivator> activators = new ArrayList<BundleActivator>();

            Class<?> autoActivatorClass = cl.loadClass("org.apache.felix.main.AutoActivator");
            Constructor autoActivatorConstructor = autoActivatorClass.getConstructor(Map.class);
            BundleActivator autoActivator = (BundleActivator)autoActivatorConstructor.newInstance(props);            
            activators.add(autoActivator);

            felix = felixConstructor.newInstance(props, activators);
            ((Bundle)felix).start();
            bundleContext = ((Bundle)felix).getBundleContext();
            
            
        } catch (Exception e) {
            
            // This is the older Felix API which has been retained temporarily to avoid build break
            // TODO: Remove these once Felix 1.0.0 is released.
            
            Class<?> propertyResolverClass = cl.loadClass("org.apache.felix.framework.util.MutablePropertyResolver");
            Class<?> propertyResolverImplClass = cl.loadClass("org.apache.felix.framework.util.MutablePropertyResolverImpl");

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
    protected void setBundleContext(BundleContext bundleContext) {
        super.setBundleContext(bundleContext);
        FelixRuntime.bundleContext = bundleContext;
    }


    @Override
    public void shutdown() throws Exception {

        if (bundleContext == null)
            return;
        bundleContext = null;
        instance = null;
        
        // We could potentially use Felix.stopAndWait, but use timed wait for now because
        // stopAndWait hangs with Felix 1.0.0
        if (felix instanceof Bundle) {
            Bundle felixBundle = (Bundle)felix;
            felixBundle.stop();
            int retries = 50;
            synchronized (felix) {
                while (retries-- > 0 && felixBundle.getState() != Bundle.UNINSTALLED) {
                    felix.wait(100);
                }
            }
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
        super.shutdown();
    }
    
    @Override
    public boolean supportsBundleFragments() {
        return false;
    }

}
