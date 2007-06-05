package org.apache.tuscany.implementation.spring.itests.mock;

/**
 * A simple test Spring bean which provides the HelloWorld service
 * @author MikeEdwards
 *
 */

import org.apache.tuscany.implementation.spring.itests.helloworld.HelloWorld;

public class TestHelloWorldBean implements HelloWorld {
	
	static String hello = "Hello ";
	
	// Classic "Hello xxx" response to any input message
	public String sayHello( String message ) {
		System.out.println("TestHelloWorldBean - sayHello called");
		return( hello + message );
	}

} // end class TestHelloWorldBean
