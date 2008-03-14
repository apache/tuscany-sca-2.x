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

import java.net.URL;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;


/*
 * Bundle activator for running the Tuscany SCA tests under OSGi
 * Tuscany samples are run inside an OSGi container when the bundle is started.
 */
public class TestBundleActivator implements BundleActivator {
    
    private ClassLoader myContextClassLoader;
    private ClassLoader origTCCL;

    public void start(BundleContext bundleContext) throws Exception {
        
        origTCCL = Thread.currentThread().getContextClassLoader();
        myContextClassLoader = new TestClassLoader(bundleContext.getBundle(), origTCCL);
        Thread.currentThread().setContextClassLoader(myContextClassLoader);
        
    }

    public void stop(BundleContext bundleContext) throws Exception {

        if (Thread.currentThread().getContextClassLoader() == myContextClassLoader)
            Thread.currentThread().setContextClassLoader(origTCCL);
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
