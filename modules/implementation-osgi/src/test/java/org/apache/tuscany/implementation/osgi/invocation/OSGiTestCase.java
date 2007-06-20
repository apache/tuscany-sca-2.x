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

package org.apache.tuscany.implementation.osgi.invocation;

import java.lang.reflect.Proxy;

import junit.framework.TestCase;

import org.apache.tuscany.implementation.osgi.test.OSGiTestBundles;
import org.apache.tuscany.implementation.osgi.test.OSGiTestImpl;
import org.apache.tuscany.implementation.osgi.test.OSGiTestInterface;
import org.apache.tuscany.sca.host.embedded.SCADomain;


/**
 * 
 * Test the execution of an OSGi implementation type
 *
 */
public class OSGiTestCase extends TestCase {
    
   
    protected void setUp() throws Exception {

        OSGiTestBundles.createBundle("target/OSGiTestService.jar", OSGiTestInterface.class, OSGiTestImpl.class);        
        
    }
    
    
    public void testOSGiComponent() {
        
        SCADomain scaDomain = SCADomain.newInstance("osgitest.composite");
        OSGiTestInterface testService = scaDomain.getService(OSGiTestInterface.class, "OSGiTestServiceComponent");
        assert(testService != null);
        
        assert(testService instanceof Proxy);
        
        String str = testService.testService();
        
        assert(OSGiTestImpl.class.getName().equals(str));

        scaDomain.close();
              
    }

}
