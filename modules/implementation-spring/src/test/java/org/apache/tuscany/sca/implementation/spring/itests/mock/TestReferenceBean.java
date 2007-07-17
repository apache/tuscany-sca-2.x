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
package org.apache.tuscany.sca.implementation.spring.itests.mock;

/**
 * A test Spring bean which provides the HelloWorld service by calling a reference
 * to provide the content of the response
 *
 */

import org.apache.tuscany.sca.implementation.spring.itests.helloworld.HelloWorld;

public class TestReferenceBean implements HelloWorld {

	// The reference
	private HelloWorld bean;

	// Classic "Hello xxx" response to any input message
	public String sayHello( String message ) {
		System.out.println("TestReferenceBean - sayHello called");
		return( bean.sayHello( message ) );
	}

	/**
	 * Setter for the bean reference
	 * @param theBean
	 */
	public void setBean( HelloWorld theBean ) {
		this.bean = theBean;
	}

	/**
	 * Getter for the reference
	 * @return
	 */
	public HelloWorld getBean( ) {
		return this.bean;
	}

} // end class TestReferenceBean
