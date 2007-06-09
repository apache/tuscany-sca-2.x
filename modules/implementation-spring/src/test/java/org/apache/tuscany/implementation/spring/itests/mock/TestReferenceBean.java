package org.apache.tuscany.implementation.spring.itests.mock;

/**
 * A test Spring bean which provides the HelloWorld service by calling a reference
 * to provide the content of the response
 * @author MikeEdwards
 *
 */

import org.apache.tuscany.implementation.spring.itests.helloworld.HelloWorld;

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
