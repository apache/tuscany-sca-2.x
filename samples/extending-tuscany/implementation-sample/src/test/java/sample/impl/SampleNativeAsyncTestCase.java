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

package sample.impl;

import static org.junit.Assert.assertEquals;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sample.Upper;

/**
 * Test how to run an SCA contribution containing a test composite on a
 * Tuscany runtime node.
 * 
 * @version $Rev$ $Date$
 */
public class SampleNativeAsyncTestCase {
    static Node node;

/*    
    @BeforeClass
    public static void setUp() throws Exception {
        final NodeFactory nf = NodeFactory.getInstance();
        String here = SampleNativeAsyncTestCase.class.getProtectionDomain().getCodeSource().getLocation().toString();
        // Create the node using the pattern "name of composite file to start" / Contribution to use
        node = nf.createNode("testnativeasync.composite", new Contribution("test", here));
        node.start();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        node.stop();
    }
*/    
    
    @Before
    public void setUp() throws Exception {
        final NodeFactory nf = NodeFactory.getInstance();
        String here = SampleNativeAsyncTestCase.class.getProtectionDomain().getCodeSource().getLocation().toString();
        // Create the node using the pattern "name of composite file to start" / Contribution to use
        node = nf.createNode("testnativeasync.composite", new Contribution("test", here));
        node.start();
    }

    @After
    public void tearDown() throws Exception {
        node.stop();
    }

    /**
     * Show that we can make a basic call
     */
    @Test
    public void testUpper() {
        System.out.println("SampleNaiveAsyncTestCase.testUpper");
        Upper upper = node.getService(Upper.class, "SampleNativeAsyncReference");
        final String r = upper.upper("async"); 
        System.out.println(r);
        assertEquals("ASYNC", r);
    }
    
    /**
     * Show that we can make a call that requires us to persist the
     * AsyncResponseInvoker
     */
    @Test
    public void testPersistAsyncResponseInvoker() {
        System.out.println("SampleNaiveAsyncTestCase.testUpper2");
        Upper upper = node.getService(Upper.class, "SampleNativeAsyncReference");
        // call upper to write out the async response invoker
        String r = upper.upper("async");
        // call upper2 to read it back in again 
        r = upper.upper2("async2"); 
        System.out.println(r);
        assertEquals("ASYNC2", r);
    }   
    
    /**
     * Show that we can make a call that works over service restarts
     */
    @Test
    public void testServiceRestart() {
        System.out.println("SampleNaiveAsyncTestCase.testUpper2");
        System.out.println("Starting first node");
        Upper upper = node.getService(Upper.class, "SampleNativeAsyncReference");
        String r = upper.upper("async"); 
        System.out.println(r);
        assertEquals("ASYNC", r);
        
        System.out.println("Stopping first node");
        node.stop();
        
        // now start another node and try call back in to get the 
        // async response to come back
        
        System.out.println("Starting second node");
        final NodeFactory nf = NodeFactory.getInstance();
        String here = SampleNativeAsyncTestCase.class.getProtectionDomain().getCodeSource().getLocation().toString();
        // Create the node using the pattern "name of composite file to start" / Contribution to use
        node = nf.createNode("testnativeasync.composite", new Contribution("test", here));
        node.start();
        upper = node.getService(Upper.class, "SampleNativeAsyncReference");
        r = upper.upper2("async2"); 
        System.out.println(r);
        assertEquals("ASYNC2", r);
    }      
    
    /**
     * Show that one-way operations work in the async case
     */
    @Test
    public void testVoid() {
        System.out.println("SampleNaiveAsyncTestCase.testUpperVoid");
        Upper upper = node.getService(Upper.class, "SampleNativeAsyncReference");
        final String r = upper.upperVoid("asyncVoid");
        System.out.println(r);
        assertEquals("ASYNCVOID", r);
    }    
}
