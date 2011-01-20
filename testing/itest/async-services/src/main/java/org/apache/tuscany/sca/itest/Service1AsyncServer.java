/*
 *
 * Copyright(C) OASIS(R) 2009,2010. All Rights Reserved.
 * OASIS trademark, IPR and other policies apply.    
 *   
 */
package org.apache.tuscany.sca.itest;

import org.oasisopen.sca.ResponseDispatch;
import org.oasisopen.sca.annotation.AsyncFault;
import org.oasisopen.sca.annotation.AsyncInvocation;
import org.oasisopen.sca.annotation.Remotable;

/**
 * Service1 service interface
 * - Asynchronous server version
 */
@AsyncInvocation
@Remotable
public interface Service1AsyncServer {
	
	/**
	 * Synchronous method for invoking testcase service
	 * @param input - input parameter as a String
	 * @return - output data as a String
	 * Listed here for documentation purposes - this is the operation that the async server operation maps to
	 */
	// public String operation1( String input );
	
	/**
	 * Async server version of the synchronous operation1 method
	 * @param input - input parameter as a String
	 * @param handler - the ResponseDispatch<String> handler used to send the response message (a String in this case)
	 */
	@AsyncFault( {BusinessFault1.class, BusinessFault2.class} )
	public void operation1Async( String input, ResponseDispatch<String> handler );

} // end interface Service1AsyncServer
