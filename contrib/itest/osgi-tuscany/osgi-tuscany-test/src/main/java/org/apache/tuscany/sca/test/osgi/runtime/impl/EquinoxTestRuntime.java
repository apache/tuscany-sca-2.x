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

import java.lang.reflect.Method;

import org.osgi.framework.BundleContext;

public class EquinoxTestRuntime extends OSGiTestRuntime  {
    
    
    private static BundleContext bundleContext;
    
    private static EquinoxTestRuntime instance;
    
    private static Class<?> eclipseStarterClass;
    
    public static OSGiTestRuntime getInstance() throws Exception {
        if (instance == null) {
            eclipseStarterClass = EquinoxTestRuntime.class.getClassLoader().loadClass("org.eclipse.core.runtime.adaptor.EclipseStarter");
            EquinoxTestRuntime runtime = new EquinoxTestRuntime();
            instance = runtime;
        }
        return instance;
    }
    
    
    protected BundleContext startRuntime() throws Exception {
        
        if (bundleContext != null)
            return bundleContext;
                    
        Method startupMethod = eclipseStarterClass.getMethod("startup", String [].class, Runnable.class);
        
        System.setProperty("org.osgi.framework.system.packages", getSystemPackages());
        // Equinox version 3.2 upwards have a startup method which returns BundleContext
        bundleContext = (BundleContext) startupMethod.invoke(null, 
                new String[] {/*"-clean", */"-console", "-configuration", "target/configuration"}, 
                null );        
        
        return bundleContext;
        
    }

    @Override
    public BundleContext getBundleContext() {
        return bundleContext;
    }

    @Override
    protected void setBundleContext(BundleContext bundleContext) {
        super.setBundleContext(bundleContext);
        EquinoxTestRuntime.bundleContext = bundleContext;
    }
    
    @Override
    public void shutdown() throws Exception {

        if (bundleContext == null)
            return;
        bundleContext = null;
        instance = null;
        if (eclipseStarterClass != null) {
            Method shutdownMethod = eclipseStarterClass.getMethod("shutdown");
            try {
                shutdownMethod.invoke(eclipseStarterClass);
            } catch (Exception e) {
                // Ignore errors.
            }
        }
        super.shutdown();
    }


   
}
