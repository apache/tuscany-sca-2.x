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
import helloworld.HelloworldClientImplC;
import helloworld.HelloworldClientImplCE;
import helloworld.HelloworldClientImplS;
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

    /*
     * Start up the composite and don't send any messages. No exception
     * should be thrown.
     */
    @Test
    public void testNoExceptionNoMessageShutdown() throws Exception{
        
        StatusImpl.statusString = "";
        
        TuscanyRuntime tuscanyRuntime = TuscanyRuntime.newInstance();

        // create a Tuscany node
        node = tuscanyRuntime.createNode();
        
        // install a contribution
        node.installContribution("HelloworldContrib", "target/classes", null, null);
        
        // start a composite
        node.startComposite("HelloworldContrib", "lifecycle.composite");
        
        // we don't send any messages in this case and just shut down directly
        
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
        Assert.assertEquals("Service binding start - Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" + 
                            "Implementation start - HelloworldServiceTestImpl\n" +
                            "Service binding start - Endpoint:  URI = HelloworldServiceTestImpl#service-binding(Helloworld/lifecycle)\n" +
                            "Init - HelloworldClientImplCE\n" +
                            "Reference binding start - EndpointReference:  URI = HelloworldClientCE#reference-binding(service/lifecycle) WIRED_TARGET_FOUND_AND_MATCHED Target = Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" +
                            "Service binding stop - Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" + 
                            "Service binding stop - Endpoint:  URI = HelloworldServiceTestImpl#service-binding(Helloworld/lifecycle)\n" + 
                            "Implementation stop - HelloworldServiceTestImpl\n" +
                            "Reference binding stop - EndpointReference:  URI = HelloworldClientCE#reference-binding(service/lifecycle) WIRED_TARGET_FOUND_AND_MATCHED Target = Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" +
                            "Destroy - HelloworldClientImplCE\n",
                            StatusImpl.statusString);
    }
    
    /*
     * Start up the composite and send a message. No exception
     * should be thrown.
     */    
    @Test
    public void testNoExceptionMessageShutdown() throws Exception{
        
        TuscanyRuntime tuscanyRuntime = TuscanyRuntime.newInstance();

        // create a Tuscany node
        node = tuscanyRuntime.createNode();
        
        // install a contribution
        node.installContribution("HelloworldContrib", "target/classes", null, null);
        
        // start a composite
        node.startComposite("HelloworldContrib", "lifecycle.composite");
        
        // send a message to each client
        Helloworld hwCE = node.getService(Helloworld.class, "HelloworldClientCE");
        System.out.println(hwCE.sayHello("name"));
        Helloworld hwC = node.getService(Helloworld.class, "HelloworldClientC");
        System.out.println(hwC.sayHello("name"));
        Helloworld hwS = node.getService(Helloworld.class, "HelloworldClientC");
        System.out.println(hwS.sayHello("name"));        
        
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
        Assert.assertEquals("Service binding start - Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" + 
                            "Implementation start - HelloworldServiceTestImpl\n" +
                            "Service binding start - Endpoint:  URI = HelloworldServiceTestImpl#service-binding(Helloworld/lifecycle)\n" +
                            "Init - HelloworldClientImplCE\n" +
                            "Reference binding start - EndpointReference:  URI = HelloworldClientCE#reference-binding(service/lifecycle) WIRED_TARGET_FOUND_AND_MATCHED Target = Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" +
                            "Init - HelloworldClientImplC\n" +
                            "Reference binding start - EndpointReference:  URI = HelloworldClientC#reference-binding(service/lifecycle) WIRED_TARGET_FOUND_AND_MATCHED Target = Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" +
                            "Service binding stop - Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" +
                            "Service binding stop - Endpoint:  URI = HelloworldServiceTestImpl#service-binding(Helloworld/lifecycle)\n" +
                            "Implementation stop - HelloworldServiceTestImpl\n" +
                            "Reference binding stop - EndpointReference:  URI = HelloworldClientC#reference-binding(service/lifecycle) WIRED_TARGET_FOUND_AND_MATCHED Target = Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" +
                            "Destroy - HelloworldClientImplC\n" +                            
                            "Reference binding stop - EndpointReference:  URI = HelloworldClientCE#reference-binding(service/lifecycle) WIRED_TARGET_FOUND_AND_MATCHED Target = Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" +
                            "Destroy - HelloworldClientImplCE\n",
                            StatusImpl.statusString);
        
        /*$self$ reference is not stopped here - should it be? */
    } 
    
    /*
     * Start up the composite. Exception thrown in constructor of composite 
     * scoped component with eager init set 
     */    
    @Test
    public void testConstructorExceptionShutdownCE() throws Exception{
        
        HelloworldClientImplCE.throwTestExceptionOnConstruction = true;
        
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
        
        // don't need to send a message as eager init ensures that 
        // the component instance is created at start time
        
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
        Assert.assertEquals("Service binding start - Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" +
                            "Implementation start - HelloworldServiceTestImpl\n" +
                            "Service binding start - Endpoint:  URI = HelloworldServiceTestImpl#service-binding(Helloworld/lifecycle)\n" +
                            "Exception on construction - HelloworldClientImplCE\n" +
                            "Service binding stop - Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" +
                            "Service binding stop - Endpoint:  URI = HelloworldServiceTestImpl#service-binding(Helloworld/lifecycle)\n" +
                            "Implementation stop - HelloworldServiceTestImpl\n",
                            StatusImpl.statusString);
        
        HelloworldClientImplCE.throwTestExceptionOnConstruction = false;
    } 
    
    /*
     * Start up the composite. Exception thrown in constructor of composite 
     * scoped component
     */    
    @Test
    public void testConstructorExceptionShutdownC() throws Exception{
        
        HelloworldClientImplC.throwTestExceptionOnConstruction = true;
        
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
        
        // send a message to the appropriate client
        try {
            Helloworld hwCE = node.getService(Helloworld.class, "HelloworldClientC");
            System.out.println(hwCE.sayHello("name"));
        } catch (Exception exception) {
            // the component throws an error on construction
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
        Assert.assertEquals("Service binding start - Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" +
                            "Implementation start - HelloworldServiceTestImpl\n" +
                            "Service binding start - Endpoint:  URI = HelloworldServiceTestImpl#service-binding(Helloworld/lifecycle)\n" +
                            "Init - HelloworldClientImplCE\n" +
                            "Reference binding start - EndpointReference:  URI = HelloworldClientCE#reference-binding(service/lifecycle) WIRED_TARGET_FOUND_AND_MATCHED Target = Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" +
                            "Exception on construction - HelloworldClientImplC\n" +
                            "Service binding stop - Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" +
                            "Service binding stop - Endpoint:  URI = HelloworldServiceTestImpl#service-binding(Helloworld/lifecycle)\n" +
                            "Implementation stop - HelloworldServiceTestImpl\n" +
                            "Reference binding stop - EndpointReference:  URI = HelloworldClientCE#reference-binding(service/lifecycle) WIRED_TARGET_FOUND_AND_MATCHED Target = Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" +
                            "Destroy - HelloworldClientImplCE\n", 
                            StatusImpl.statusString);
        
        HelloworldClientImplC.throwTestExceptionOnConstruction = false;
    }   
    
    /*
     * Start up the composite. Exception thrown in constructor of stateless 
     * scoped component
     */     
    @Test
    public void testConstructorExceptionShutdownS() throws Exception{
        
        HelloworldClientImplS.throwTestExceptionOnConstruction = true;
        
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
        
        // send a message to the appropriate client
        try {
            Helloworld hwCE = node.getService(Helloworld.class, "HelloworldClientS");
            System.out.println(hwCE.sayHello("name"));
        } catch (Exception exception) {
            // the component throws an error on construction
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
        Assert.assertEquals("Service binding start - Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" +
                            "Implementation start - HelloworldServiceTestImpl\n" +
                            "Service binding start - Endpoint:  URI = HelloworldServiceTestImpl#service-binding(Helloworld/lifecycle)\n" +
                            "Init - HelloworldClientImplCE\n" +
                            "Reference binding start - EndpointReference:  URI = HelloworldClientCE#reference-binding(service/lifecycle) WIRED_TARGET_FOUND_AND_MATCHED Target = Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" +
                            "Exception on construction - HelloworldClientImplS\n" +
                            "Service binding stop - Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" +
                            "Service binding stop - Endpoint:  URI = HelloworldServiceTestImpl#service-binding(Helloworld/lifecycle)\n" +
                            "Implementation stop - HelloworldServiceTestImpl\n" +
                            "Reference binding stop - EndpointReference:  URI = HelloworldClientCE#reference-binding(service/lifecycle) WIRED_TARGET_FOUND_AND_MATCHED Target = Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" +
                            "Destroy - HelloworldClientImplCE\n",
                            StatusImpl.statusString);
        
        HelloworldClientImplS.throwTestExceptionOnConstruction = false;
    } 
      
    /*
     * Start up the composite. Exception thrown in init of composite 
     * scoped component with eager init
     */       
    @Test
    public void testInitExceptionShutdownCE() throws Exception{
        
        HelloworldClientImplCE.throwTestExceptionOnInit = true;
        
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
        
        // don't need to send a message as eager init ensures that 
        // the component instance is created and inited at start time        
        
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
        Assert.assertEquals("Service binding start - Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n"+
                            "Implementation start - HelloworldServiceTestImpl\n"+
                            "Service binding start - Endpoint:  URI = HelloworldServiceTestImpl#service-binding(Helloworld/lifecycle)\n"+
                            "Exception on init - HelloworldClientImplCE\n"+
                            "Destroy - HelloworldClientImplCE\n"+
                            "Service binding stop - Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n"+
                            "Service binding stop - Endpoint:  URI = HelloworldServiceTestImpl#service-binding(Helloworld/lifecycle)\n"+
                            "Implementation stop - HelloworldServiceTestImpl\n", 
                            StatusImpl.statusString);
        
        HelloworldClientImplCE.throwTestExceptionOnInit = false;
    } 
    
    /*
     * Start up the composite. Exception thrown in init of composite 
     * scoped component
     */       
    @Test
    public void testInitExceptionShutdownC() throws Exception{
        
        HelloworldClientImplC.throwTestExceptionOnInit = true;
        
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
        
        // send a message to the appropriate client
        try {
            Helloworld hwCE = node.getService(Helloworld.class, "HelloworldClientC");
            System.out.println(hwCE.sayHello("name"));
        } catch (Exception exception) {
            // the component throws an error on init
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
        Assert.assertEquals("Service binding start - Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" +
                            "Implementation start - HelloworldServiceTestImpl\n" +
                            "Service binding start - Endpoint:  URI = HelloworldServiceTestImpl#service-binding(Helloworld/lifecycle)\n" +
                            "Init - HelloworldClientImplCE\n" +
                            "Reference binding start - EndpointReference:  URI = HelloworldClientCE#reference-binding(service/lifecycle) WIRED_TARGET_FOUND_AND_MATCHED Target = Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" +
                            "Exception on init - HelloworldClientImplC\n" +
                            "Destroy - HelloworldClientImplC\n" +
                            "Service binding stop - Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" +
                            "Service binding stop - Endpoint:  URI = HelloworldServiceTestImpl#service-binding(Helloworld/lifecycle)\n" +
                            "Implementation stop - HelloworldServiceTestImpl\n" +
                            "Reference binding stop - EndpointReference:  URI = HelloworldClientCE#reference-binding(service/lifecycle) WIRED_TARGET_FOUND_AND_MATCHED Target = Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" +
                            "Destroy - HelloworldClientImplCE\n",
                            StatusImpl.statusString);
        
        HelloworldClientImplC.throwTestExceptionOnInit = false;
    }   
    
    /*
     * Start up the composite. Exception thrown in init of stateless 
     * scoped component
     */      
    @Test
    public void testInitExceptionShutdownS() throws Exception{
        
        HelloworldClientImplS.throwTestExceptionOnInit = true;
        
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
        
        // send a message to the appropriate client
        try {
            Helloworld hwCE = node.getService(Helloworld.class, "HelloworldClientS");
            System.out.println(hwCE.sayHello("name"));
        } catch (Exception exception) {
            // the component throws an error on init
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
        Assert.assertEquals("Service binding start - Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" +
                            "Implementation start - HelloworldServiceTestImpl\n" +
                            "Service binding start - Endpoint:  URI = HelloworldServiceTestImpl#service-binding(Helloworld/lifecycle)\n" +
                            "Init - HelloworldClientImplCE\n" +
                            "Reference binding start - EndpointReference:  URI = HelloworldClientCE#reference-binding(service/lifecycle) WIRED_TARGET_FOUND_AND_MATCHED Target = Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" +
                            "Exception on init - HelloworldClientImplS\n" +
                            "Destroy - HelloworldClientImplS\n" +
                            "Service binding stop - Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" +
                            "Service binding stop - Endpoint:  URI = HelloworldServiceTestImpl#service-binding(Helloworld/lifecycle)\n" +
                            "Implementation stop - HelloworldServiceTestImpl\n" +
                            "Reference binding stop - EndpointReference:  URI = HelloworldClientCE#reference-binding(service/lifecycle) WIRED_TARGET_FOUND_AND_MATCHED Target = Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" +
                            "Destroy - HelloworldClientImplCE\n",
                            StatusImpl.statusString);
        
        HelloworldClientImplS.throwTestExceptionOnInit = false;
    }    
    
    /*
     * Start up the composite and then stop it. Exception thrown in destory of composite 
     * scoped component with eager init set
     */  
    @Test
    public void testDestroyExceptionShutdownCE() throws Exception{
        
        HelloworldClientImplCE.throwTestExceptionOnDestroy = true;
        
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
        
        // don't need to send a message as eager init ensures that 
        // the component instance is created start time and hence should
        // be destroyed
        
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
        Assert.assertEquals("Service binding start - Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" +
                            "Implementation start - HelloworldServiceTestImpl\n" +
                            "Service binding start - Endpoint:  URI = HelloworldServiceTestImpl#service-binding(Helloworld/lifecycle)\n" +
                            "Init - HelloworldClientImplCE\n" +
                            "Reference binding start - EndpointReference:  URI = HelloworldClientCE#reference-binding(service/lifecycle) WIRED_TARGET_FOUND_AND_MATCHED Target = Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" +
                            "Service binding stop - Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" +
                            "Service binding stop - Endpoint:  URI = HelloworldServiceTestImpl#service-binding(Helloworld/lifecycle)\n" +
                            "Implementation stop - HelloworldServiceTestImpl\n" +
                            "Reference binding stop - EndpointReference:  URI = HelloworldClientCE#reference-binding(service/lifecycle) WIRED_TARGET_FOUND_AND_MATCHED Target = Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" +
                            "Exception on destroy - HelloworldClientImplCE\n",
                            StatusImpl.statusString);
        
        HelloworldClientImplCE.throwTestExceptionOnDestroy = false;
    }  
  
    /*
     * Start up the composite and then stop it. Exception thrown in destory of composite 
     * scoped component
     */      
    @Test
    public void testDestroyExceptionShutdownC() throws Exception{
        
        HelloworldClientImplC.throwTestExceptionOnDestroy = true;
        
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
        
        // send a message to the appropriate client
        Helloworld hwCE = node.getService(Helloworld.class, "HelloworldClientC");
        System.out.println(hwCE.sayHello("name"));  
        // don't need to catch exception here as it component instance won't
        // be destroyed until shutdown
        
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
        Assert.assertEquals("Service binding start - Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" +
                            "Implementation start - HelloworldServiceTestImpl\n" +
                            "Service binding start - Endpoint:  URI = HelloworldServiceTestImpl#service-binding(Helloworld/lifecycle)\n" +
                            "Init - HelloworldClientImplCE\n" +
                            "Reference binding start - EndpointReference:  URI = HelloworldClientCE#reference-binding(service/lifecycle) WIRED_TARGET_FOUND_AND_MATCHED Target = Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" +
                            "Init - HelloworldClientImplC\n" +
                            "Reference binding start - EndpointReference:  URI = HelloworldClientC#reference-binding(service/lifecycle) WIRED_TARGET_FOUND_AND_MATCHED Target = Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" +
                            "Service binding stop - Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" +
                            "Service binding stop - Endpoint:  URI = HelloworldServiceTestImpl#service-binding(Helloworld/lifecycle)\n" +
                            "Implementation stop - HelloworldServiceTestImpl\n" +
                            "Reference binding stop - EndpointReference:  URI = HelloworldClientC#reference-binding(service/lifecycle) WIRED_TARGET_FOUND_AND_MATCHED Target = Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" +
                            "Exception on destroy - HelloworldClientImplC\n" +
                            "Reference binding stop - EndpointReference:  URI = HelloworldClientCE#reference-binding(service/lifecycle) WIRED_TARGET_FOUND_AND_MATCHED Target = Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" +
                            "Destroy - HelloworldClientImplCE\n",
                            StatusImpl.statusString);
        
        HelloworldClientImplC.throwTestExceptionOnDestroy = false;
    }  
    
    /*
     * Start up the composite and then stop it. Exception thrown in destory of stateless 
     * scoped component
     */     
    @Test
    public void testDestroyExceptionShutdownS() throws Exception{
        
        HelloworldClientImplS.throwTestExceptionOnDestroy = true;
        
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
        
        // send a message to the appropriate client
        try {
            Helloworld hwCE = node.getService(Helloworld.class, "HelloworldClientS");
            System.out.println(hwCE.sayHello("name")); 
        } catch (Exception exception) {
            // exception will be thown when component instance is discarded
            // after the message has been processed
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
        Assert.assertEquals("Service binding start - Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" +
                            "Implementation start - HelloworldServiceTestImpl\n" +
                            "Service binding start - Endpoint:  URI = HelloworldServiceTestImpl#service-binding(Helloworld/lifecycle)\n" +
                            "Init - HelloworldClientImplCE\n" +
                            "Reference binding start - EndpointReference:  URI = HelloworldClientCE#reference-binding(service/lifecycle) WIRED_TARGET_FOUND_AND_MATCHED Target = Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" +
                            "Init - HelloworldClientImplS\n" +
                            "Reference binding start - EndpointReference:  URI = HelloworldClientS#reference-binding(service/lifecycle) WIRED_TARGET_FOUND_AND_MATCHED Target = Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" +
                            "Exception on destroy - HelloworldClientImplS\n" +
                            "Service binding stop - Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" +
                            "Service binding stop - Endpoint:  URI = HelloworldServiceTestImpl#service-binding(Helloworld/lifecycle)\n" +
                            "Implementation stop - HelloworldServiceTestImpl\n" +
                            "Reference binding stop - EndpointReference:  URI = HelloworldClientCE#reference-binding(service/lifecycle) WIRED_TARGET_FOUND_AND_MATCHED Target = Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" +
                            "Destroy - HelloworldClientImplCE\n" +
                            "Reference binding stop - EndpointReference:  URI = HelloworldClientS#reference-binding(service/lifecycle) WIRED_TARGET_FOUND_AND_MATCHED Target = Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n", 
                            StatusImpl.statusString);
        
        HelloworldClientImplS.throwTestExceptionOnDestroy = false;
    }     
    
    /*
     * Start up the composite. Send a message where the processing sends an
     * exception. App exception has no material affect and the scenario is the
     * same as just sending normal messages and stopping the runtime
     */  
    @Test
    public void testAppExceptionShutdown() throws Exception{
        
        TuscanyRuntime tuscanyRuntime = TuscanyRuntime.newInstance();

        // create a Tuscany node
        node = tuscanyRuntime.createNode();
        
        // install a contribution
        node.installContribution("HelloworldContrib", "target/classes", null, null);
        
        // start a composite
        node.startComposite("HelloworldContrib", "lifecycle.composite");
        
        // send a message to each client. The last one throws and exception
        Helloworld hwCE = node.getService(Helloworld.class, "HelloworldClientCE");
        System.out.println(hwCE.sayHello("name"));
        Helloworld hwC = node.getService(Helloworld.class, "HelloworldClientC");
        System.out.println(hwC.sayHello("name"));
        Helloworld hwS = node.getService(Helloworld.class, "HelloworldClientC");
        System.out.println(hwS.sayHello("name"));   
        try {
            Helloworld hw = node.getService(Helloworld.class, "HelloworldC");
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
        Assert.assertEquals("Service binding start - Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" +
                            "Implementation start - HelloworldServiceTestImpl\n" +
                            "Service binding start - Endpoint:  URI = HelloworldServiceTestImpl#service-binding(Helloworld/lifecycle)\n" +
                            "Init - HelloworldClientImplCE\n" +
                            "Reference binding start - EndpointReference:  URI = HelloworldClientCE#reference-binding(service/lifecycle) WIRED_TARGET_FOUND_AND_MATCHED Target = Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" +
                            "Init - HelloworldClientImplC\n" +
                            "Reference binding start - EndpointReference:  URI = HelloworldClientC#reference-binding(service/lifecycle) WIRED_TARGET_FOUND_AND_MATCHED Target = Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" +
                            "Service binding stop - Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" +
                            "Service binding stop - Endpoint:  URI = HelloworldServiceTestImpl#service-binding(Helloworld/lifecycle)\n" +
                            "Implementation stop - HelloworldServiceTestImpl\n" +
                            "Reference binding stop - EndpointReference:  URI = HelloworldClientC#reference-binding(service/lifecycle) WIRED_TARGET_FOUND_AND_MATCHED Target = Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" +
                            "Destroy - HelloworldClientImplC\n" +
                            "Reference binding stop - EndpointReference:  URI = HelloworldClientCE#reference-binding(service/lifecycle) WIRED_TARGET_FOUND_AND_MATCHED Target = Endpoint:  URI = HelloworldService#service-binding(Helloworld/lifecycle)\n" +
                            "Destroy - HelloworldClientImplCE\n",
                            StatusImpl.statusString);
    }    
    
}
