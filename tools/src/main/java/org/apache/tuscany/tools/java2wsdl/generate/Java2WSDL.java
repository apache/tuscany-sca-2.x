package org.apache.tuscany.tools.java2wsdl.generate; 

import org.apache.ws.java2wsdl.utils.Java2WSDLOptionsValidator;
import org.apache.ws.java2wsdl.utils.Java2WSDLCommandLineOptionParser;

/**
 * This class provides the tooling abstraction to Tuscany Java2WSDL and can be
 * invoked from command line with the follwing options as with Axis2 Java2WSDL
 * 
 */
public class Java2WSDL {
	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		//parse the cmd line args
		Java2WSDLCommandLineOptionParser commandLineOptionParser = 
														new Java2WSDLCommandLineOptionParser(args);
        //  validate the arguments
        validateCommandLineOptions(commandLineOptionParser);
		
		Java2WSDLGeneratorFactory.getInstance().createGenerator().
												generateWSDL(commandLineOptionParser.getAllOptions());

		// Uncomment the following statement to directly run the Axis2 tool
		// without
		// runAxis2Tool(args);
	}

	private static void runAxis2Tool(String[] args) {
		org.apache.axis2.wsdl.Java2WSDL.main(args);
	}
	
	 private static void validateCommandLineOptions(Java2WSDLCommandLineOptionParser parser) {
	        if (parser.getAllOptions().size() == 0) {
	        	org.apache.axis2.wsdl.Java2WSDL.printUsage();
	        } else if (parser.getInvalidOptions(new Java2WSDLOptionsValidator()).size() > 0) {
	        	org.apache.axis2.wsdl.Java2WSDL.printUsage();
	        }

	    }
}
