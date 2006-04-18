package org.apache.tuscany.tools.java2wsdl.generate;

import java.io.File;

import junit.framework.TestCase;

/**
 * A JUnit test case to test the Tuscany Java 2 WSDL Generation
 * 
 * @author jjojo
 */
public class TuscanyJava2WsdlTestCase extends TestCase {

	/**
	 * setup the pre-requisites for the test case to run
	 * 
	 * @exception Exception
	 */
	protected void setUp() throws Exception {
		// System.out.println("inside setup");
		super.setUp();
	}

	/**
	 * @exception Exception
	 */
	protected void tearDown() throws Exception {
		// System.out.println("inside tearDown");
		super.tearDown();
	}

	/**
	 * Simple WSDL generation test.
	 */
	public void testSimpleWsdlGeneration() {
/*
		String[] arguments = new String[] { "-cn",
				"org.apache.tuscany.tools.java2wsdl.generate.CustomerValue",
				"-o", "target/GeneratedWSDLs", };

		Java2WSDL.main(arguments);

		File file = new File("target/GeneratedWSDLs/CustomerValue.wsdl");
		assertTrue(file.exists() && file.isFile());*/
	}

	/**
	 * Test WSDL generation where a parameter Object[] is involved.
	 */
	public void testWsdlGeneration2() {
/*
		String[] arguments = new String[] {
				"-cn",
				"org.apache.tuscany.tools.java2wsdl.generate.CustomerWithAccount",
				"-o", "target/GeneratedWSDLs", };
		Java2WSDL.main(arguments);

		File file = new File("target/GeneratedWSDLs/CustomerWithAccount.wsdl");
		assertTrue(file.exists() && file.isFile());*/
	}

	/**
	 * Test WSDL generation from a java interface and then generate the java
	 * interface using the generated WSDL.
	 */
	public void testRoundTrip() {
		// TODO implement round trip
		// this should re-generate java interfaces from the generated wsdl
		// and compile (?) the generated java code.
		// fail();

	}
}
