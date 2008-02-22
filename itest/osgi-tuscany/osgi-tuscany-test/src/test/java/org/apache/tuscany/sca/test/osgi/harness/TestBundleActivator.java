/**
 *
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.tuscany.sca.test.osgi.harness;

import java.net.MalformedURLException;
import java.net.URL;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;


/*
 * Bundle activator for running the Tuscany SCA tests under OSGi
 * Tuscany samples are run inside an OSGi container when the bundle is started.
 */
public class TestBundleActivator implements BundleActivator {
    
    private ClassLoader contextClassLoader;

    public void start(BundleContext bundleContext) throws Exception {
        
        contextClassLoader = Thread.currentThread().getContextClassLoader();
        ClassLoader cl = new TestClassLoader(bundleContext.getBundle(), contextClassLoader);
        Thread.currentThread().setContextClassLoader(cl);
    }

    public void stop(BundleContext bundleContext) throws Exception {

        Thread.currentThread().setContextClassLoader(contextClassLoader);
    }


    
    private class TestClassLoader extends ClassLoader {
        
        private Bundle bundle;
        
        private TestClassLoader(Bundle bundle, ClassLoader parent) {
            super(parent);
            this.bundle = bundle;
        }

        @Override
        public URL getResource(String resName) {
            URL resource = getParent().getResource(resName);
            if (resource == null) {
                resource = bundle.getResource(resName);
            
            }

            /* FIXME: Workaround Tuscany's handling of URLs
             * Convert resource URLs using bundle: protocol into file: URLs
             * This code can be removed when URL manipulation in Tuscany is fixed.
             */
            if (resource != null && resource.getProtocol().startsWith("bundle") && 
                    (resName.endsWith(".composite") || resName.endsWith(".xml"))) {
                try {
                    String bundleId = resource.getHost();
                    if (bundleId.indexOf('.') > 0)
                        bundleId  = bundleId.substring(0, bundleId.indexOf('.'));
                    long id = Long.parseLong(bundleId);
                    Bundle[] allBundles = bundle.getBundleContext().getBundles();
                    Bundle resourceBundle = bundle;
                    for (Bundle b : allBundles) {
                        if (b.getBundleId() == id) {
                            resourceBundle = b;
                            break;
                        }
                    }
                    resource = new URL(resourceBundle.getLocation() + "/" + resource.getPath());
                } catch (MalformedURLException e) {
                }
            }
            
            return resource;
        }

        @Override
        public Class<?> loadClass(String className) throws ClassNotFoundException {
            Class clazz = findLoadedClass(className);            
            if (clazz != null)
                return clazz;

            try {
                return getParent().loadClass(className);                    
            } catch (Exception e) {
                return bundle.loadClass(className);
            }
        }
        
        
    }
}
