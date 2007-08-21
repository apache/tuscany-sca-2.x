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
import org.apache.tuscany.sca.binding.sca.axis2.helloworld.HelloWorldServiceLocal;
import org.apache.tuscany.sca.binding.sca.axis2.helloworld.HelloWorldServiceRemote;
import org.junit.Test;

public class SimpleTestCase extends BaseTest {
    
    @Test
    public void testHelloWorldLocal() throws Exception {  
        HelloWorldClient helloWorldClientA = domainA.getService(HelloWorldClient.class, "AHelloWorldClientLocal");
        Assert.assertEquals(helloWorldClientA.getGreetings("fred"), "Hello fred");
        
    }
    
    @Test
    public void testHelloWorldRemote() throws Exception {  
        HelloWorldClient helloWorldClientA;
        helloWorldClientA = domainA.getService(HelloWorldClient.class, "AHelloWorldClientRemote");
        Assert.assertEquals(helloWorldClientA.getGreetings("fred"), "Hello fred");
        
    }    
    
    @Test
    public void testHelloWorldLocalAndRemote() throws Exception {
        HelloWorldClient helloWorldClientA = domainA.getService(HelloWorldClient.class, "AHelloWorldClientLocalAndRemote"); 
        HelloWorldClient helloWorldClientB = domainB.getService(HelloWorldClient.class, "BHelloWorldClientLocalAndRemote"); 
        Assert.assertEquals(helloWorldClientA.getGreetings("fred"), "Hello fred");
        Assert.assertEquals(helloWorldClientB.getGreetings("fred"), "Hello fred");
    }   
    
    @Test
    public void testHelloWorldMultipleServices() throws Exception {
        HelloWorldClient helloWorldClientA = domainA.getService(HelloWorldClient.class, "AHelloWorldClientMultipleServices"); 
        HelloWorldClient helloWorldClientA2 = domainA.getService(HelloWorldClient.class, "AHelloWorldClientMultipleServices2");
        HelloWorldClient helloWorldClientB = domainB.getService(HelloWorldClient.class, "BHelloWorldClientMultipleServices");        
        Assert.assertEquals(helloWorldClientA.getGreetings("fred"), "Hello fred");
        Assert.assertEquals(helloWorldClientA2.getGreetings("fred"), "Hello fred");
        Assert.assertEquals(helloWorldClientB.getGreetings("fred"), "Hello fred");
    }    
  
}
