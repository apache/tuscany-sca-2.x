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
package org.apache.tuscany.sca.binding.sca.axis2;

import junit.framework.Assert;

import org.apache.tuscany.sca.binding.sca.axis2.helloworld.HelloWorldClient;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class CallbackTestCase {
    
    public static TestNode nodeA;
    public static TestNode nodeB;

    @BeforeClass
    public static void init() throws Exception {
        System.out.println("Setting up nodes");

        try {
            // create and start domains
            nodeA = new TestNode("nodeE");
            nodeB = new TestNode("nodeF");

            nodeA.start();
            nodeB.start();

        } catch (Exception ex) {
            System.err.println("Exception when creating domain " + ex.getMessage());
            ex.printStackTrace(System.err);
            throw ex;
        }     
    }

    @AfterClass
    public static void destroy() throws Exception {
        nodeA.stop();
        nodeB.stop();
    } 
    
    //@Test
    public void testKeepServerRunning() throws Exception {
        System.out.println("press enter to continue");
        System.in.read();
    } 
    
    @Test
    public void testHelloWorldCallbackLocal() throws Exception {  
        HelloWorldClient helloWorldClientB;
        helloWorldClientB = nodeB.getService(HelloWorldClient.class, "BHelloWorldClientCallbackLocal");
        Assert.assertEquals("Hello callback fred", helloWorldClientB.getGreetings("fred"));  
    }     
    
    @Test
    public void testHelloWorldCallbackRemote() throws Exception {  
        HelloWorldClient helloWorldClientA;
        helloWorldClientA = nodeA.getService(HelloWorldClient.class, "AHelloWorldClientCallbackRemote");
        Assert.assertEquals("Hello callback fred", helloWorldClientA.getGreetings("fred"));
    }    
}
