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
package org.apache.tuscany.tools.java2wsdl.generate;

import java.io.File;

import junit.framework.TestCase;

/**
 * A JUnit test case to test the Tuscany Java 2 WSDL Generation
 */
public class TuscanyJava2WSDLTestCase extends TestCase {

	/**
	 * setup the pre-requisites for the test case to run
	 * 
	 * @exception Exception
	 */
	@Override
    protected void setUp() throws Exception {
		// System.out.println("inside setup");
		super.setUp();
	}

	/**
	 * @exception Exception
	 */
	@Override
    protected void tearDown() throws Exception {
		// System.out.println("inside tearDown");
		super.tearDown();
	}

   
	/**
	 * Simple WSDL generation test.
	 */
	public void testSimpleWSDLGeneration() {
		String[] arguments = new String[] { "-cn","org.apache.tuscany.tools.java2wsdl.generate.CustomerValue",
				"-o", "target/java2wsdl-source",
                "-xc", "org.apache.tuscany.tools.java2wsdl.generate.extra.GoldCustomer"};
                

		Java2WSDL.main(arguments);

		File file = new File("target/java2wsdl-source/CustomerValue.wsdl");
		assertTrue(file.exists() && file.isFile());
	}

	/**
	 * Test WSDL generation where a parameter Object[] is involved.
	 */
	public void testWsdlGeneration2() {

		String[] arguments = new String[] 
        {
				"-cn",
				"org.apache.tuscany.tools.java2wsdl.generate.CustomerWithAccount",
				"-o", "target/java2wsdl-source", };
		Java2WSDL.main(arguments);

		File file = new File("target/java2wsdl-source/CustomerWithAccount.wsdl");
		assertTrue(file.exists() && file.isFile());
	}

    public void testWsdlGeneration_SDO_Static()
    {
        //tests for SDOs where XSD exist.  Hence no XSDs must be generated
        String[] arguments = new String[] {
                "-cn",
                "org.soapinterop.CreditScoreDocLit",
                "-o", "target/java2wsdl-source", 
                "-ixsd", "[http://www.example.org/creditscore/doclit/," +
                "http://www.example.org/creditscore/doclit/xsd]"};
        Java2WSDL.main(arguments);

        File file = new File("target/java2wsdl-source/CreditScoreDocLit.wsdl");
        assertTrue(file.exists() && file.isFile());
    }
    
    
    
	/**
	 * Test WSDL generation from a java interface and then generate the java
	 * interface using the generated WSDL.
	 */
	public void _testRoundTrip() {
		// TODO implement round trip
		// this should re-generate java interfaces from the generated wsdl
		// and compile (?) the generated java code.
		// fail();

	}
}
