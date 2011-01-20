/*
 * Copyright(C) OASIS(R) 2009,2010. All Rights Reserved.
 * OASIS trademark, IPR and other policies apply.    
 */
package org.apache.tuscany.sca.itest;

import static org.junit.Assert.assertEquals;

//import org.apache.tuscany.sca.node.equinox.launcher.Contribution;
//import org.apache.tuscany.sca.node.equinox.launcher.NodeLauncher;
import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;

import org.apache.tuscany.sca.itest.TestInvocation;

/**
 * Client for Async_002_TestCase
 * Async service invocation test #001
 * Tests that an async service can be invoked over the SCA Binding
 */
public class Async_002_TestCase {
	
	//protected NodeLauncher launcher;
    protected Node node;

    private String input 		 	 = "request";
    private String[] output 		 = new String[] { "Async_002 request service1 operation1 invoked asynchronously" };
    private String composite 		 = "Test_Async_002.composite";
    
    @Before
    public void setUp() throws Exception {
    	
    	final NodeFactory nf = NodeFactory.newInstance();
        String here = ASM_0002_Client.class.getProtectionDomain().getCodeSource().getLocation().toString();
        // Create the node using the pattern "name of composite file to start" / Contribution to use
        node = nf.createNode(this.composite, new Contribution("test", here));
        
        node.start();
    }

    @After
    public void tearDown() throws Exception {
        node.stop();
    }

    @Test
    public void testReference() {
        TestInvocation test = node.getService(TestInvocation.class, "TestClient/TestInvocation");
        try {
	        final String response = test.invokeTest(this.input); 
	        System.out.println(response);
	        assertEquals(this.output[0], response);
        } catch (Throwable t) {
        	assertEquals("exception", this.output[0]);
        } // end try
    } // end testReference
    
    /**
     * Dummy method to ensure that things work with JUnit 3 eg within Eclipse Ganymede
     */
    @Ignore
    @Test
    public void testFoo() throws Exception {  	
    }
    
} // end class Async_001_TestCase
