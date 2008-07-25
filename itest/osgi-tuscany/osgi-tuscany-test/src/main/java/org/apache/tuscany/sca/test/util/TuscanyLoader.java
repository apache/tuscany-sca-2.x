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
package org.apache.tuscany.sca.test.util;


import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

/**
 * Load Tuscany into an OSGi runtime
 *
 */
public class TuscanyLoader {
    
    private static final String tuscanyInstallerDir = "../tuscany-osgi-installer";
    private static final String tuscanyInstallerJar = "tuscany-sca-osgi-installer.jar";
    
    // 5-bundle version of Tuscany
    private static final String scaApiDir = "../sca-api";
    private static final String tuscanySpiDir = "../tuscany-spi";
    private static final String tuscanyRuntimeDir = "../tuscany-runtime";
    private static final String tuscanyExtensionsDir = "../tuscany-extensions";
    private static final String thirdPartyDir = "../tuscany-3rdparty";
    
    private static String findBundle(String subDirName, final String jarPrefix) throws Exception {
        
        File dir = new File(subDirName + "/target");
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles(new FilenameFilter() {

                public boolean accept(File dir, String name) {
                    return name.endsWith(".jar") && (jarPrefix == null || name.startsWith(jarPrefix));
                }
                
            });
            
            if (files != null && files.length > 0)
                return files[0].toURI().toURL().toString();
        }
        return null;
    }
    
    public static Bundle loadTuscanyIntoOSGi(BundleContext bundleContext) throws Exception {
        
        String tuscanyInstallerBundleName = new File(tuscanyInstallerDir + "/target/" + tuscanyInstallerJar).toURI().toURL().toString();
        Bundle tuscanyInstallerBundle = bundleContext.installBundle(tuscanyInstallerBundleName); 
        tuscanyInstallerBundle.start();
        Bundle[] bundles = bundleContext.getBundles();
        Bundle tuscanyRuntimeBundle = tuscanyInstallerBundle;
        for (Bundle bundle : bundles) {
            if ("org.apache.tuscany.sca.osgi.runtime".equals(bundle.getSymbolicName())) {
                tuscanyRuntimeBundle = bundle;
                break;
            }
        }
        setThreadContextClassLoader(tuscanyRuntimeBundle);
        
        return tuscanyRuntimeBundle;
    }
    
    /**
     * Load four Tuscany bundles (API, Core-SPI, Runtime, Extensions)  and combined third party bundle
     * 
     * @param bundleContext
     */
    public static Bundle load5BundleTuscanyIntoOSGi(BundleContext bundleContext) throws Exception {
        

        String thirdPartyBundleName = findBundle(thirdPartyDir, null);
        Bundle thirdPartyBundle = bundleContext.installBundle(thirdPartyBundleName);        
        thirdPartyBundle.start();
            

        String scaApiBundleName = findBundle(scaApiDir, null);
        Bundle scaApiBundle = bundleContext.installBundle(scaApiBundleName);            
        scaApiBundle.start();            

        String tuscanySpiBundleName = findBundle(tuscanySpiDir, null);
        Bundle tuscanySpiBundle = bundleContext.installBundle(tuscanySpiBundleName);    
        
        String tuscanyRuntimeBundleName = findBundle(tuscanyRuntimeDir, null);
        Bundle tuscanyRuntimeBundle = bundleContext.installBundle(tuscanyRuntimeBundleName);
        
        String tuscanyExtensionsBundleName = findBundle(tuscanyExtensionsDir, null);
        Bundle tuscanyExtensionsBundle = bundleContext.installBundle(tuscanyExtensionsBundleName);
        

        tuscanySpiBundle.start();      
        tuscanyExtensionsBundle.start();    
        
        setThreadContextClassLoader(tuscanyRuntimeBundle);
        return tuscanyRuntimeBundle;
    
    }
    

    // Tuscany runtime is started on a different thread when previously cached bundle is used.
    // Set this thread's TCCL to the one used by the runtime.
    public static void setThreadContextClassLoader(Bundle tuscanyRuntimeBundle) throws BundleException {
        if (tuscanyRuntimeBundle != null) {
            
            try {
                Class<?> runtimeClass = tuscanyRuntimeBundle.loadClass("org.apache.tuscany.sca.osgi.runtime.OSGiRuntime");
                Method getRuntimeMethod = runtimeClass.getMethod("findRuntime");
                Object runtime = getRuntimeMethod.invoke(runtimeClass);
                Method getTCCLMethod = runtimeClass.getMethod("getContextClassLoader");
                ClassLoader runtimeTCCL = (ClassLoader) getTCCLMethod.invoke(runtime);
                Thread.currentThread().setContextClassLoader(runtimeTCCL);               
                
            } catch (Throwable e) {
            }
        }
    }
    
}
