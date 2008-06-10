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
package test.sca.tests;

import org.apache.tuscany.sca.host.embedded.SCADomain;

import mysca.test.myservice.MySimpleTotalService;
import junit.framework.TestCase;


/**
 * Tests to make sure that autowiring and recusive composite work together
 *
 */
public class MultiLevelTestCase extends TestCase
{
    private SCADomain domain1;
    private SCADomain domain2;
    private SCADomain domain3;
    private MySimpleTotalService myService1;
    private MySimpleTotalService myService2;
    private MySimpleTotalService myService3;

    protected void setUp() throws Exception {
        super.setUp();
        domain1 = SCADomain.newInstance("TotalService1Auto.composite");
        domain2 = SCADomain.newInstance("TotalService2Auto.composite");
        domain3 = SCADomain.newInstance("TotalService3Auto.composite");

        myService1 = domain1.getService(MySimpleTotalService.class, "TotalServiceComponentLevel1Auto");
        myService2 = domain2.getService(MySimpleTotalService.class, "TotalServiceInRecursive2Auto/MyServiceLevel1Auto");
        myService3 = domain3.getService(MySimpleTotalService.class, "TotalServiceInRecursive3Auto/MyServiceLevel2Auto");
    }
   
    public void testLevel1()
    {
        assertEquals("Level 1",myService1.getLocation());
        assertEquals("2001",myService1.getYear());
    }
    
    public void testLevel2()
    {
        assertEquals("Default 2",myService2.getLocation());
        assertEquals("1992",myService2.getYear());
    }

    public void testLevel3()
    {
        assertEquals("Default 3",myService3.getLocation());
        assertEquals("1993",myService3.getYear());
    }
}
