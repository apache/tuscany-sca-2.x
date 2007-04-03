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

import java.util.Vector;

/**
 * Factory that creates Java2WSDL Generators. Presently the there is a Default
 * Generator that wraps around the AXIS2 Java2WSDL implementation. The factory
 * can be extended to create generators that wrap around other implementations
 * if required.
 */

public class Java2WSDLGeneratorFactory {
	/*
	 * singleton instance of this factory class
	 */
	private static Java2WSDLGeneratorFactory factory = null;

	/**
	 * code for the default generator
	 */
	public static final int DEFAULT_GENERATOR = 0;

	/**
	 * Default Generator class name
	 */
	public static final String DEFAULT_GENERATOR_CLASSNAME = "org.apache.tuscany.tools.java2wsdl.generate.Java2WSDLGeneratorImpl";

	/**
	 * list of generator classnames in a position that corresponds to their
	 * code. For example the default generator's code is 0 and hence this
	 * generator's classname is stored at index '0' of the list
	 */
	protected Vector<String> generatorClassNames = new Vector<String>();

	/**
	 * @return the singleton instance of this generator factory
	 */
	public static Java2WSDLGeneratorFactory getInstance() {
		if (factory == null) {
			factory = new Java2WSDLGeneratorFactory();
		}
		return factory;
	}

	private Java2WSDLGeneratorFactory() {
		generatorClassNames.addElement(DEFAULT_GENERATOR_CLASSNAME);
	}

	public Java2WSDLGenerator createGenerator() {
		return createGenerator(DEFAULT_GENERATOR);
	}

	/**
	 * creates an instance of a Java2WSDL Generator based on the input type
	 * 
	 * @param genType
	 *            type of the generator to be created
	 * @return an instance of a Java2WSDL Generator
	 */
	public Java2WSDLGenerator createGenerator(int genType) {
		try {
			return (Java2WSDLGenerator) (Class.forName(generatorClassNames
					.elementAt(genType)).newInstance());
		} catch (Exception e) {
			System.out
					.println(" Unable to create Java2WSDL generator due to .....");
			System.out.println(e);
			return null;
		}
	}
}
