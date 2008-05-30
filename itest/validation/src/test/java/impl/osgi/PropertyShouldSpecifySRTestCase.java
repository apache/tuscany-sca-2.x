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
package impl.osgi;

import junit.framework.TestCase;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.impl.DefaultLoggingMonitorImpl;
import org.apache.tuscany.sca.osgi.runtime.OSGiRuntime;
import domain.CustomCompositeBuilder;

/**
 * This shows how to test the Calculator service component.
 */
public class PropertyShouldSpecifySRTestCase extends TestCase {

    private CustomCompositeBuilder customDomain;
	
    @Override
    protected void setUp() throws Exception 
    {
        OSGiTestBundles.createBundle("src/main/resources/impl/osgi/PropertyShouldSpecifySR/OSGiTestService.jar", OSGiTestInterface.class, OSGiTestImpl.class);
    	
        customDomain = CustomCompositeBuilder.getInstance();
        try {
              customDomain.loadContribution("src/main/resources/impl/osgi/PropertyShouldSpecifySR/osgitest.composite", 
                        "TestContribution", "src/main/resources/impl/osgi/PropertyShouldSpecifySR/");
        } catch (Exception ex){
            //throw ex;
        }
    }

    @Override
    protected void tearDown() throws Exception {
    	//nothing to do
    	OSGiRuntime.stop();
    }

    public void testCalculator() {
    	Monitor monitor = customDomain.getMonitorInstance();
    	Problem problem = ((DefaultLoggingMonitorImpl)monitor).getLastLoggedProblem();
        
    	assertNotNull(problem);
       assertEquals("PropertyShouldSpecifySR", problem.getMessageId());
    }
}
