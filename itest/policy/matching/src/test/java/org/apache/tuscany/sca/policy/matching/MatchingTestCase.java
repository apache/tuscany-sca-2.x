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

package org.apache.tuscany.sca.policy.matching;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.policy.matching.helloworld.HelloWorld;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/* 
 * Test the various conditions in the matching algorithm
 *  1 - FAIL if there are intents that are mutually exclusive between reference and service
 *  2 - PASS if there are no intents or policies present at reference and service
 *  3 - FAIL if there are unresolved intents (intents with no policy set) at the reference (service should have been checked previously)
 *  4 - PASS if there are no policies at reference and service (now we know all intents are resolved)
 *  5 - FAIL if there are some policies on one side but not on the other
 *  6 - PASS if the QName of the policy sets on each side match
 *  7 - FAIL if the policy languages on both sides are different
 *  8 - Perform policy specific match
 */
public class MatchingTestCase {

    private static Node node;

    
    @BeforeClass
    public static void setUp() throws Exception {
        try {
            node = NodeFactory.newInstance().createNode(new Contribution("test", "target/classes"));
            node.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    @Test
    public void testMutuallyExclusiveIntents() throws Exception {
        HelloWorld helloWorld = node.getService(HelloWorld.class, "HelloWorldClientMutuallyExclusiveIntents");
        try {
            helloWorld.getGreetings("petra");
            fail("Exception expected");
        } catch (Exception ex) {
            assertTrue(ex.getMessage().indexOf("No match because the following intents are mutually exclusive {http://tuscany.apache.org/xmlns/sca/1.1}testIntent3 {http://tuscany.apache.org/xmlns/sca/1.1}testIntent1") > -1);
        }
    }
    
    @Test
    public void testNoIntentsOrPolicies() throws Exception {
        HelloWorld helloWorld = node.getService(HelloWorld.class, "HelloWorldClientNoIntentsOrPolicies");
        assertEquals("Hello petra", helloWorld.getGreetings("petra"));
    }    
   
    @Test
    public void testUnresolvedIntentsOnReference() throws Exception {
        HelloWorld helloWorld = node.getService(HelloWorld.class, "HelloUnresolvedIntentsOnReference");
        try {
            helloWorld.getGreetings("petra");
            fail("Exception expected");
        } catch (Exception ex) {
            assertTrue(ex.getMessage().indexOf("No match because there are unresolved intents [{http://tuscany.apache.org/xmlns/sca/1.1}testIntent2]") > -1);
        }
    }   
       
    @Test
    public void testIntentsButNoPolicies() throws Exception {
        HelloWorld helloWorld = node.getService(HelloWorld.class, "HelloWorldClientIntentsButNoPolicies1");
        assertEquals("Hello petra", helloWorld.getGreetings("petra"));
        
        helloWorld = node.getService(HelloWorld.class, "HelloWorldClientIntentsButNoPolicies2");
        assertEquals("Hello petra", helloWorld.getGreetings("petra"));        
    } 
    
    @Test
    public void testSomePoliciesOnOneSideButNoneOnTheOther() throws Exception {
        try {
            HelloWorld helloWorld = node.getService(HelloWorld.class, "HelloWorldClientSomePoliciesOnOneSideButNoneOnTheOther");
            helloWorld.getGreetings("petra");
            fail("Exception expected");
        } catch (Exception ex) {
            assertTrue(ex.getMessage().indexOf("No match because there are policy sets at the endpoint but not at the endpoint reference") > -1);
        }
    } 
    
    @Test
    public void testPolicySetQNameMatch() throws Exception {
        HelloWorld helloWorld = node.getService(HelloWorld.class, "HelloWorldClientPolicySetQNameMatch");
        assertEquals("Hello petra", helloWorld.getGreetings("petra"));
    } 
    
    @Test
    public void testDifferentPolicyLanguage() throws Exception {
        try {
            HelloWorld helloWorld = node.getService(HelloWorld.class, "HelloWorldClientDifferentPolicyLanguage");
            helloWorld.getGreetings("petra");
            fail("Exception expected");
        } catch (Exception ex) {
            assertTrue(ex.getMessage().indexOf("No match because the policy sets on either side have policies in differnt languages {http://www.w3.org/ns/ws-policy}ExactlyOne and {http://tuscany.apache.org/xmlns/sca/1.1}jdkLogger") > -1);
        }    
    } 
    
    @Test
    public void testPolicySpecificMatch() throws Exception {
        // TODO
    }    
    
    @AfterClass
    public static void tearDown() throws Exception {
        node.stop();
    }

}
