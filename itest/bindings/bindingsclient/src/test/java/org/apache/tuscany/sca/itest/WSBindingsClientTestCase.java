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

package org.apache.tuscany.sca.itest;

import java.lang.reflect.UndeclaredThrowableException;

import junit.framework.TestCase;

import org.apache.tuscany.api.SCARuntime;
import org.apache.tuscany.core.test.SCATestCaseRunner;
import org.apache.tuscany.sca.util.SCATestUtilityServerTest;
import org.osoa.sca.CompositeContext;
import org.osoa.sca.CurrentCompositeContext;

public class WSBindingsClientTestCase extends TestCase {
    private SCATestToolService scaTestTool;
    
    private SCATestCaseRunner toolServer;
    private SCATestCaseRunner utilityServer;

    // Hops over one composite
    public void testOneHopPing() throws Throwable {
        try {
            assertTrue(scaTestTool.doOneHopPing("brio").contains("brio"));
        } catch (UndeclaredThrowableException e) {
            throw (e.getCause());
        }
    }

    // Hops over two composites
    public void testTwoHopPing() {
        assertTrue(scaTestTool.doTwoHopPing("brio").contains("brio"));
    }

    protected void setUp() throws Exception {
        SCARuntime.start("bindingsclient.composite");
        
        toolServer = new SCATestCaseRunner(SCATestToolServerTest.class);
        toolServer.setUp();
        utilityServer = new SCATestCaseRunner(SCATestUtilityServerTest.class);
        utilityServer.setUp();
        
        CompositeContext cc = CurrentCompositeContext.getContext();
        System.out.println("Composite Name = " + cc.getName());
        System.out.println(CurrentCompositeContext.getContext());
        scaTestTool =
            (SCATestToolService)CurrentCompositeContext.getContext().locateService(SCATestToolService.class,
                                                                                   "SCATestToolWSReference");
        if (scaTestTool == null) {
            System.out.println("Yo Yo It is null");
        } else {
            System.out.println("Yo Yo It is not null: " + scaTestTool);
        }

    }
    
    @Override
    protected void tearDown() throws Exception {
    	SCARuntime.stop();
    	toolServer.tearDown();
    	utilityServer.tearDown();
    }
}
