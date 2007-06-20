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


import java.lang.reflect.Method;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

public abstract class OSGiRuntime  {
    
    protected abstract BundleContext startRuntime() throws Exception;
    
    protected abstract void shutdownRuntime() throws Exception;
    
    
    /**
     * System property org.apache.tuscany.implementation.osgi.runtime.OSGiRuntime can be set to the
     * name of the OSGiRuntime class (eg. EquinoxRuntime). If set, start this runtime and return the
     * system bundlecontext. If not set, start Equinox/Felix/Knopflerfish (in that order) from the
     * classpath.
     * 
     * @return
     * @throws BundleException
     */
    public static BundleContext startAndGetBundleContext() throws BundleException {
        
        String runtimeClassName = System.getProperty(OSGiRuntime.class.getName());
        if (runtimeClassName != null) {
            try {
                Class<?> runtimeClass = OSGiRuntime.class.getClassLoader().loadClass(runtimeClassName);
                Method method = runtimeClass.getMethod("getInstance");
                OSGiRuntime runtime = (OSGiRuntime) method.invoke(null);
                return runtime.startRuntime();
                
            } catch (Exception e) {
                throw new BundleException("Could not start OSGi runtime " + runtimeClassName, e);
            }
        }
        
        try {
            
            return EquinoxRuntime.getInstance().startRuntime();
            
        } catch (ClassNotFoundException e) {
        } catch (Throwable e) {   
            e.printStackTrace();
        } 
        
        try {
            
            return FelixRuntime.getInstance().startRuntime();
            
        } catch (ClassNotFoundException e) {
        } catch (Throwable e) {   
            e.printStackTrace();
        } 
        
        try {
            
            return KnopflerfishRuntime.getInstance().startRuntime();
            
        } catch (ClassNotFoundException e) {
        } catch (Throwable e) {   
            e.printStackTrace();
        } 
        
        throw new BundleException("Could not start OSGi runtime from the classpath");
    }
    
    
    public static void shutdown() throws BundleException {
        
        try {
            String runtimeClassName = System.getProperty(OSGiRuntime.class.getName());
            if (runtimeClassName != null) {

                Class<?> runtimeClass = OSGiRuntime.class.getClassLoader().loadClass(runtimeClassName);
                Method method = runtimeClass.getMethod("getInstance");
                OSGiRuntime runtime = (OSGiRuntime) method.invoke(null);
                runtime.shutdownRuntime();
                
            }
            
            try {
                
                EquinoxRuntime.getInstance().shutdownRuntime();
                
            } catch (ClassNotFoundException e) {
            }
            
            try {
                
                FelixRuntime.getInstance().shutdownRuntime();
                
            } catch (ClassNotFoundException e) {
            } 
            try {
                
                KnopflerfishRuntime.getInstance().shutdownRuntime();
                
            } catch (ClassNotFoundException e) {
            }
        } catch (Exception e) {
            throw new BundleException("Could not shutdown OSGi runtime", e);
        } 
    }

}
