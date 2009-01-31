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
package org.apache.tuscany.sca.test.osgi.tuscany;


import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;

import junit.framework.Assert;

import org.apache.tuscany.sca.test.osgi.runtime.impl.OSGiTestRuntime;
import org.apache.tuscany.sca.test.util.OSGiRuntimeLoader;
import org.apache.tuscany.sca.test.util.TuscanyLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/*
 * Test Tuscany running inside OSGi
 */
public class OSGiTuscanyRuntimeTestCase {
    
    private OSGiTestRuntime osgiRuntime;
    private Bundle tuscanyRuntime;
    

    @Before
    public void setUp() throws Exception {
        
        osgiRuntime = OSGiRuntimeLoader.startOSGiTestRuntime();
        BundleContext bundleContext = osgiRuntime.getBundleContext();
        
        // Uninstall any previously installed test bundles
        for (Bundle bundle : bundleContext.getBundles()) {
            String bundleName = bundle.getSymbolicName();
            if (bundleName != null && 
               (bundleName.equals("org.apache.tuscany.sca.test.samples")||
               bundleName.startsWith("supplychain"))) {
                  bundle.uninstall();
            }
        }
    }
    

    @After
    public void tearDown() throws Exception {

        if (tuscanyRuntime != null) {
            tuscanyRuntime.stop();
        }
        OSGiRuntimeLoader.shutdownOSGiRuntime();
    }

   
    
    @Test
    public void startTuscanyRuntimeInOSGi() throws Exception {
        
        tuscanyRuntime = TuscanyLoader.loadTuscanyIntoOSGi(osgiRuntime.getBundleContext());
        Assert.assertNotNull(tuscanyRuntime);
        Bundle[] bundles = osgiRuntime.getBundleContext().getBundles();
        Bundle runtimeBundle = null;
        for (Bundle bundle : bundles) {
            if ("org.apache.tuscany.sca.osgi.runtime".equals(bundle.getSymbolicName())) {
                runtimeBundle = bundle;
                break;
            }
        }
        Assert.assertNotNull(runtimeBundle);
        Class<?> clazz = runtimeBundle.loadClass("org.apache.tuscany.sca.osgi.runtime.OSGiRuntime");
        Assert.assertNotNull(clazz);
               
    }
    
    @Test
    public void testOSGiTuscany_ImplementationJava() throws Exception {
        testOSGiTuscanyUsingOSGiClient("SupplyChain.jar");
    }
    
    @Test
    public void testOSGiTuscany_BindingWS() throws Exception {
        testOSGiTuscanyUsingOSGiClient("SupplyChainWS.jar");
    }
   
    
    private void testOSGiTuscanyUsingOSGiClient(String contributionJarName) throws Exception {
        
        tuscanyRuntime = TuscanyLoader.loadTuscanyIntoOSGi(osgiRuntime.getBundleContext());
        
        String folderName = "../test-bundles/target/"; 
        String supplychainJarName = contributionJarName;
        String supplychainClientJarName = "SupplyChainClient.jar";
        URL supplyChainURL = new File(folderName + supplychainJarName).toURI().toURL();        
        URL supplyChainClientURL = new File(folderName + supplychainClientJarName).toURI().toURL();
    
        Bundle supplyChainBundle = osgiRuntime.getBundleContext().installBundle(supplyChainURL.toString());
        supplyChainBundle.start();
        Bundle clientBundle = osgiRuntime.getBundleContext().installBundle(supplyChainClientURL.toString());
        clientBundle.start();
        
        String clientClassName = "supplychain.client.SupplyChainClient";
        Class<?> clientClass = clientBundle.loadClass(clientClassName);
        ServiceReference testServiceRef = clientBundle.getBundleContext().getServiceReference(clientClassName);
        Object testService = clientBundle.getBundleContext().getService(testServiceRef);
        
        Method m = clientClass.getMethod("runTest", String.class);
        m.invoke(testService, contributionJarName);
        
        System.out.println("OSGi Client test completed successfully.");
        
        supplyChainBundle.uninstall();
        clientBundle.uninstall();
    }
    
   

}
