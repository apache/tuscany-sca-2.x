/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.tools.java2wsdl.generate; 

import org.apache.ws.java2wsdl.utils.Java2WSDLCommandLineOptionParser;
import org.apache.ws.java2wsdl.utils.Java2WSDLOptionsValidator;

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
		org.apache.ws.java2wsdl.Java2WSDL.main(args);
	}
	
	 private static void validateCommandLineOptions(Java2WSDLCommandLineOptionParser parser) {
	        if (parser.getAllOptions().size() == 0) {
                org.apache.ws.java2wsdl.Java2WSDL.printUsage();
	        } else if (parser.getInvalidOptions(new Java2WSDLOptionsValidator()).size() > 0) {
                org.apache.ws.java2wsdl.Java2WSDL.printUsage();
	        }

	    }
}
