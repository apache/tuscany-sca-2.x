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

package org.apache.tuscany.sca.itest.lifecycle;


import helloworld.Helloworld;
import helloworld.HelloworldClientImpl;
import helloworld.StatusImpl;
import junit.framework.Assert;

import org.apache.tuscany.sca.Node;
import org.apache.tuscany.sca.TuscanyRuntime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class LifecycleTestCase {
    
    public Node node = null;

    
    @Before
    public void setUp() throws Exception {   
        StatusImpl.statusString = "";
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testNormalShutdownNoMessage() throws Exception{
        
        StatusImpl.statusString = "";
        
        TuscanyRuntime tuscanyRuntime = TuscanyRuntime.newInstance();

        // create a Tuscany node
        node = tuscanyRuntime.createNode();
        
        // install a contribution
        node.installContribution("HelloworldContrib", "target/classes", null, null);
        
        // start a composite
        node.startComposite("HelloworldContrib", "lifecycle.composite");
        
        // stop a composite
        node.stopComposite("HelloworldContrib", "lifecycle.composite");
        
        // uninstall a constribution
        node.uninstallContribution("HelloworldContrib");
        
        // stop a Tuscany node
        node.stop();
        
        // stop the runtime
        tuscanyRuntime.stop();
        
        // see what happened
        System.out.println(StatusImpl.statusString);
        Assert.assertEquals("Service binding start - Endpoint:  URI = HelloworldService1#service-binding(Helloworld/lifecycle)\n" + 
                            "Implementation start - HelloworldService2\n" +
                            "Service binding start - Endpoint:  URI = HelloworldService2#service-binding(Helloworld/lifecycle)\n" +
                            "Init - HelloworldClientImpl\n" +
                            "Reference binding start - EndpointReference:  URI = HelloworldClient#reference-binding(service/lifecycle) WIRED_TARGET_FOUND_AND_MATCHED Target = Endpoint:  URI = HelloworldService1#service-binding(Helloworld/lifecycle)\n" + 
                            "Service binding stop - Endpoint:  URI = HelloworldService1#service-binding(Helloworld/lifecycle)\n" + 
                            "Service binding stop - Endpoint:  URI = HelloworldService2#service-binding(Helloworld/lifecycle)\n" + 
                            "Implementation stop - HelloworldService2\n" +
                            "Reference binding stop - EndpointReference:  URI = HelloworldClient#reference-binding(service/lifecycle) WIRED_TARGET_FOUND_AND_MATCHED Target = Endpoint:  URI = HelloworldService1#service-binding(Helloworld/lifecycle)\n" +
                            "Destroy - HelloworldClientImpl\n", 
                            StatusImpl.statusString);
    }
    
    @Test
    public void testNormalShutdownAfterMessage() throws Exception{
        
        TuscanyRuntime tuscanyRuntime = TuscanyRuntime.newInstance();

        // create a Tuscany node
        node = tuscanyRuntime.createNode();
        
        // install a contribution
        node.installContribution("HelloworldContrib", "target/classes", null, null);
        
        // start a composite
        node.startComposite("HelloworldContrib", "lifecycle.composite");
        
        // send a message
        Helloworld hw = node.getService(Helloworld.class, "HelloworldService1");
        System.out.println(hw.sayHello("name"));
        
        // stop a composite
        node.stopComposite("HelloworldContrib", "lifecycle.composite");
        
        // uninstall a constribution
        node.uninstallContribution("HelloworldContrib");
        
        // stop a Tuscany node
        node.stop();
        
        // stop the runtime
        tuscanyRuntime.stop();
        
        // see what happened
        System.out.println(StatusImpl.statusString);
        Assert.assertEquals("Service binding start - Endpoint:  URI = HelloworldService1#service-binding(Helloworld/lifecycle)\n" + 
                            "Implementation start - HelloworldService2\n" +
                            "Service binding start - Endpoint:  URI = HelloworldService2#service-binding(Helloworld/lifecycle)\n" +
                            "Init - HelloworldClientImpl\n" +
                            "Reference binding start - EndpointReference:  URI = HelloworldClient#reference-binding(service/lifecycle) WIRED_TARGET_FOUND_AND_MATCHED Target = Endpoint:  URI = HelloworldService1#service-binding(Helloworld/lifecycle)\n" +
                            /*extra ref start for getService reference*/
                            "Reference binding start - EndpointReference:  URI = HelloworldService1#reference-binding($self$.Helloworld/lifecycle) WIRED_TARGET_FOUND_AND_MATCHED Target = Endpoint:  URI = HelloworldService1#service-binding(Helloworld/lifecycle)\n" +
                            "Service binding stop - Endpoint:  URI = HelloworldService1#service-binding(Helloworld/lifecycle)\n" + 
                            "Service binding stop - Endpoint:  URI = HelloworldService2#service-binding(Helloworld/lifecycle)\n" + 
                            "Implementation stop - HelloworldService2\n" +
                            "Reference binding stop - EndpointReference:  URI = HelloworldClient#reference-binding(service/lifecycle) WIRED_TARGET_FOUND_AND_MATCHED Target = Endpoint:  URI = HelloworldService1#service-binding(Helloworld/lifecycle)\n" +
                            /*$self$ reference is not stopped */
                            "Destroy - HelloworldClientImpl\n", 
                            StatusImpl.statusString);
    }    
    
    @Test
    public void testInitExceptionShutdown() throws Exception{
        
        HelloworldClientImpl.throwTestExceptionOnInit = true;
        
        TuscanyRuntime tuscanyRuntime = TuscanyRuntime.newInstance();

        // create a Tuscany node
        node = tuscanyRuntime.createNode();
        
        // install a contribution
        node.installContribution("HelloworldContrib", "target/classes", null, null);
        
        // start a composite
        try {
            node.startComposite("HelloworldContrib", "lifecycle.composite");
        } catch (Exception exception) {
            // it's thrown from the HelloworldClientImpl @Init method
        }
        
        // stop a composite
        try {
            node.stopComposite("HelloworldContrib", "lifecycle.composite");
        } catch (Exception exception) {
            // it will complain about the composite not being started
        }            
        
        // uninstall a constribution
        node.uninstallContribution("HelloworldContrib");
        
        // stop a Tuscany node
        node.stop();
        
        // stop the runtime
        tuscanyRuntime.stop();
        
        // see what happened
        System.out.println(StatusImpl.statusString);
        Assert.assertEquals("Service binding start - Endpoint:  URI = HelloworldService1#service-binding(Helloworld/lifecycle)\n" + 
                            "Implementation start - HelloworldService2\n" +
                            "Service binding start - Endpoint:  URI = HelloworldService2#service-binding(Helloworld/lifecycle)\n" +
                            "Exception on init - HelloworldClientImpl\n" + 
                            /* is it right that the destroy happens directly?*/
                            "Destroy - HelloworldClientImpl\n" +
                            "Service binding stop - Endpoint:  URI = HelloworldService1#service-binding(Helloworld/lifecycle)\n" + 
                            "Service binding stop - Endpoint:  URI = HelloworldService2#service-binding(Helloworld/lifecycle)\n" + 
                            "Implementation stop - HelloworldService2\n", 
                            StatusImpl.statusString);
        
        HelloworldClientImpl.throwTestExceptionOnInit = false;
    }    
    
    @Test
    public void testDestroyExceptionShutdown() throws Exception{
        
        HelloworldClientImpl.throwTestExceptionOnDestroy = true;
        
        TuscanyRuntime tuscanyRuntime = TuscanyRuntime.newInstance();

        // create a Tuscany node
        node = tuscanyRuntime.createNode();
        
        // install a contribution
        node.installContribution("HelloworldContrib", "target/classes", null, null);
        
        // start a composite
        try {
            node.startComposite("HelloworldContrib", "lifecycle.composite");
        } catch (Exception exception) {
            // it's thrown from the HelloworldClientImpl @Destroy method
        }
        
        // stop a composite
        try {
            node.stopComposite("HelloworldContrib", "lifecycle.composite");
        } catch (Exception exception) {
            // it will complain about the composite not being started
        }   
        
        // uninstall a constribution
        node.uninstallContribution("HelloworldContrib");
        
        // stop a Tuscany node
        node.stop();
        
        // stop the runtime
        tuscanyRuntime.stop();
        
        // see what happened
        System.out.println(StatusImpl.statusString);
        Assert.assertEquals("Service binding start - Endpoint:  URI = HelloworldService1#service-binding(Helloworld/lifecycle)\n" + 
                            "Implementation start - HelloworldService2\n" +
                            "Service binding start - Endpoint:  URI = HelloworldService2#service-binding(Helloworld/lifecycle)\n" +
                            "Init - HelloworldClientImpl\n" +
                            "Reference binding start - EndpointReference:  URI = HelloworldClient#reference-binding(service/lifecycle) WIRED_TARGET_FOUND_AND_MATCHED Target = Endpoint:  URI = HelloworldService1#service-binding(Helloworld/lifecycle)\n" + 
                            "Service binding stop - Endpoint:  URI = HelloworldService1#service-binding(Helloworld/lifecycle)\n" + 
                            "Service binding stop - Endpoint:  URI = HelloworldService2#service-binding(Helloworld/lifecycle)\n" + 
                            "Implementation stop - HelloworldService2\n" +
                            "Reference binding stop - EndpointReference:  URI = HelloworldClient#reference-binding(service/lifecycle) WIRED_TARGET_FOUND_AND_MATCHED Target = Endpoint:  URI = HelloworldService1#service-binding(Helloworld/lifecycle)\n" +
                            "Exception on destroy - HelloworldClientImpl\n",  
                            StatusImpl.statusString);
        
        HelloworldClientImpl.throwTestExceptionOnDestroy = false;
    }     
    
    @Test
    public void testAppExceptionShutdown() throws Exception{
        
        TuscanyRuntime tuscanyRuntime = TuscanyRuntime.newInstance();

        // create a Tuscany node
        node = tuscanyRuntime.createNode();
        
        // install a contribution
        node.installContribution("HelloworldContrib", "target/classes", null, null);
        
        // start a composite
        node.startComposite("HelloworldContrib", "lifecycle.composite");
        
        try {
            Helloworld hw = node.getService(Helloworld.class, "Helloworld1");
            hw.throwException("name");
        } catch (Exception ex) {
            // do nothing
        }
        
        // stop a composite
        node.stopComposite("HelloworldContrib", "lifecycle.composite");
        
        // uninstall a constribution
        node.uninstallContribution("HelloworldContrib");
        
        // stop a Tuscany node
        node.stop();
        
        // stop the runtime
        tuscanyRuntime.stop();
        
        // see what happened
        System.out.println(StatusImpl.statusString);
        Assert.assertEquals("Service binding start - Endpoint:  URI = HelloworldService1#service-binding(Helloworld/lifecycle)\n" + 
                            "Implementation start - HelloworldService2\n" +
                            "Service binding start - Endpoint:  URI = HelloworldService2#service-binding(Helloworld/lifecycle)\n" +
                            "Init - HelloworldClientImpl\n" +
                            "Reference binding start - EndpointReference:  URI = HelloworldClient#reference-binding(service/lifecycle) WIRED_TARGET_FOUND_AND_MATCHED Target = Endpoint:  URI = HelloworldService1#service-binding(Helloworld/lifecycle)\n" + 
                            "Service binding stop - Endpoint:  URI = HelloworldService1#service-binding(Helloworld/lifecycle)\n" + 
                            "Service binding stop - Endpoint:  URI = HelloworldService2#service-binding(Helloworld/lifecycle)\n" + 
                            "Implementation stop - HelloworldService2\n" +
                            "Reference binding stop - EndpointReference:  URI = HelloworldClient#reference-binding(service/lifecycle) WIRED_TARGET_FOUND_AND_MATCHED Target = Endpoint:  URI = HelloworldService1#service-binding(Helloworld/lifecycle)\n" +
                            "Destroy - HelloworldClientImpl\n",  
                            StatusImpl.statusString);
    }    
    
}
