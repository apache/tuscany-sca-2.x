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
package util;

import org.apache.tuscany.sca.implementation.osgi.runtime.OSGiRuntime;


/**
 * OSGi Test Utils
 */
public  class OSGiTestUtil  {
    
    public static void setUpOSGiTestRutime() throws Exception {

        setUpFelixTestRutime();
    }
    
    
    private static void setUpFelixTestRutime() throws Exception {

        String felixConfigFileName = "file:target/test-classes/osgi/felix/felix.config.properties";
        
        System.setProperty("felix.config.properties", felixConfigFileName);
        
        try {
            
            ClassLoader cl = OSGiTestUtil.class.getClassLoader();
            
            Class felixMainClass = cl.loadClass("org.apache.felix.main.Main");
            if (felixMainClass != null) {
                String felixDir = felixMainClass.getProtectionDomain().getCodeSource().getLocation().getPath();
                int index = 0;
                if ((index = felixDir.indexOf("/org.apache.felix.main")) >= 0) {
                    felixDir = felixDir.substring(0, index);
                    System.setProperty("FELIX_DIR", felixDir);
                }
            }
            
        } catch (Exception e) {
            // Ignore
        }
        
        
    }
    
    public static void shutdownOSGiRuntime()  {
        try {
            OSGiRuntime.getRuntime().shutdown();
        
        } catch (Exception e) {
            // Ignore
        }
    }
    
}
