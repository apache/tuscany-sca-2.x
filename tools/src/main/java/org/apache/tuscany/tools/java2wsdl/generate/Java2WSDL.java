package org.apache.tuscany.tools.java2wsdl.generate;

/**
 * This class provides the tooling abstraction to Tuscany Java2WSDL and can be
 * invoked from command line with the follwing options as with Axis2 Java2WSDL
 * 
 */
public class Java2WSDL {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Java2WSDLGeneratorFactory.getInstance().createGenerator().generateWSDL(
				args);

		// Uncomment the following statement to directly run the Axis2 tool
		// without
		// runAxis2Tool(args);
	}

	private static void runAxis2Tool(String[] args) {
		org.apache.axis2.wsdl.Java2WSDL.main(args);
	}
}
