/*
 *
 * Copyright(C) OASIS(R) 2009,2010. All Rights Reserved.
 * OASIS trademark, IPR and other policies apply.    
 *   
 */
package org.apache.tuscany.sca.itest;

import javax.jws.WebMethod;
import org.oasisopen.sca.annotation.Remotable;
import org.apache.tuscany.sca.itest.TestException;


/**
 * Basic interface to invoke testcases
 * 1 operation
 * - "invokeTest", string input, string output
 *
 */
@Remotable
public interface TestInvocation {
	
	/**
	 * Method for invoking testcase
	 * @param input - input parameter(s) as a String
	 * @return - output data as a String
	 * @throws - a TestException is thrown in cases where the test service fails internally
	 */
	@WebMethod
	public String invokeTest( String input ) throws TestException ;

}
