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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.osgi.framework.BundleContext;

public class EquinoxRuntime extends OSGiRuntime  {
    
    
    private static BundleContext bundleContext;
    
    private static EquinoxRuntime instance;
    
    private static Class eclipseStarterClass;
    
    public static OSGiRuntime getInstance() {
        if (instance == null)
            instance = new EquinoxRuntime();
        return instance;
    }
    
    
    protected BundleContext startRuntime() throws Exception {
        
        if (bundleContext != null)
            return bundleContext;
                    
        eclipseStarterClass = EquinoxRuntime.class.getClassLoader().loadClass("org.eclipse.core.runtime.adaptor.EclipseStarter");
        Method startupMethod = eclipseStarterClass.getMethod("startup", String [].class, Runnable.class);
        
        // Equinox version 3.2 upwards have a startup method which returns BundleContext
        if (startupMethod.getReturnType() == BundleContext.class) {
            bundleContext = (BundleContext) startupMethod.invoke(null, (Object)new String[] {"-clean", "-console"}, null );
        }
        else {
            
            // Older versions of Equinox dont have a public method to obtain system bundlecontext
            // Extract bundleContext from the private field 'context'. We are assuming that 
            // there is no access restriction
            Method mainMethod = eclipseStarterClass.getMethod("main", String [].class);
            mainMethod.invoke(null, (Object)new String[] {"-clean", "-console"});
            
            Field contextField = eclipseStarterClass.getDeclaredField("context");
            contextField.setAccessible(true);
            bundleContext = (BundleContext) contextField.get(null);
            
        }
            
        
        return bundleContext;
        
    }


    @Override
    protected void shutdownRuntime() throws Exception {

        if (bundleContext == null)
            return;
        bundleContext = null;
        if (eclipseStarterClass != null) {
            Method shutdownMethod = eclipseStarterClass.getMethod("shutdown");
            try {
                shutdownMethod.invoke(eclipseStarterClass);
            } catch (Exception e) {
                // Ignore errors.
            }
        }
    }
    
    

}
