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

import junit.framework.TestCase;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.policy.matching.helloworld.HelloWorld;

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
public class MatchingTestCase extends TestCase {

    private Node node;

    @Override
    protected void setUp() throws Exception {
        node = NodeFactory.newInstance().createNode(new Contribution("test", "target/classes"));
        node.start();
    }
    
    public void testMutuallyExclusiveIntents() throws Exception {
        HelloWorld helloWorld = node.getService(HelloWorld.class, "HelloWorldClientMutuallyExclusiveIntents");
        try {
            helloWorld.getGreetings("petra");
        } catch (Exception ex) {
            assertEquals("Unable to bind", ex.getMessage().substring(0, 14));
        }
    }
    
    public void testNoIntentsOrPolicies() throws Exception {
        HelloWorld helloWorld = node.getService(HelloWorld.class, "HelloWorldClientNoIntentsOrPolicies");
        assertEquals("Hello petra", helloWorld.getGreetings("petra"));
    }    
   
    public void testUnresolvedIntentsOnReference() throws Exception {
        HelloWorld helloWorld = node.getService(HelloWorld.class, "HelloUnresolvedIntentsOnReference");
        try {
            helloWorld.getGreetings("petra");
        } catch (Exception ex) {
            assertEquals("Unable to bind", ex.getMessage().substring(0, 14));
        }
    }   
    
    public void testMatchingIntents() throws Exception {
        HelloWorld helloWorld = node.getService(HelloWorld.class, "HelloWorldClientMatchingIntents");
        assertEquals("Hello petra", helloWorld.getGreetings("petra"));
    }
    
    public void testIntentsButNoPolicies() throws Exception {
        
    } 
    
    public void testSomePoliciesOnOneSideButNoneOnTheOther() throws Exception {
        
    } 
    
    public void testPolicySetQNameMatch() throws Exception {
        
    } 
    
    public void testDifferentPolicyLanguage() throws Exception {
        
    } 
    
    public void testPolicySpecificMatch() throws Exception {
        
    }    
    
    @Override
    protected void tearDown() throws Exception {
        node.stop();
    }

}
