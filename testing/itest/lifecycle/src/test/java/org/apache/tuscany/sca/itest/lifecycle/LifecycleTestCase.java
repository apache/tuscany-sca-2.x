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

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testNormalShutdown() throws Exception{
        
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
        Assert.assertEquals("Service binding start " + 
                            "Implementation start " +
                            "Service binding start " +
                            "HelloworldClientImpl init " +
                            "Reference binding start " + 
                            "Service binding stop " + 
                            "Service binding stop " + 
                            "Implementation stop " +
                            "Reference binding stop " +
                            "HelloworldClientImpl destroy ", 
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
        node.stopComposite("HelloworldContrib", "lifecycle.composite");
        
        // uninstall a constribution
        node.uninstallContribution("HelloworldContrib");
        
        // stop a Tuscany node
        node.stop();
        
        // stop the runtime
        tuscanyRuntime.stop();
        
        // see what happened
        System.out.println(StatusImpl.statusString);
        Assert.assertEquals("Service binding start " + 
                            "Implementation start " +
                            "Service binding start " +
                            "HelloworldClientImpl init " +
                            "Reference binding start " + 
                            "Service binding stop " + 
                            "Service binding stop " + 
                            "Implementation stop " +
                            "Reference binding stop " +
                            "HelloworldClientImpl destroy ", 
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
        Assert.assertEquals("Service binding start " + 
                            "Implementation start " +
                            "Service binding start " +
                            "HelloworldClientImpl init " +
                            "Reference binding start " + 
                            "Service binding stop " + 
                            "Service binding stop " + 
                            "Implementation stop " +
                            "Reference binding stop " +
                            "HelloworldClientImpl destroy ", 
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
        Assert.assertEquals("Service binding start " + 
                            "Implementation start " +
                            "Service binding start " +
                            "HelloworldClientImpl init " +
                            "Reference binding start " + 
                            "Service binding stop " + 
                            "Service binding stop " + 
                            "Implementation stop " +
                            "Reference binding stop " +
                            "HelloworldClientImpl destroy ", 
                            StatusImpl.statusString);
    }    
    
}
