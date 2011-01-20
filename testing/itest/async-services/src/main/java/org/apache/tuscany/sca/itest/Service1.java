/*
 *
 * Copyright(C) OASIS(R) 2009,2010. All Rights Reserved.
 * OASIS trademark, IPR and other policies apply.    
 *   
 */
package org.apache.tuscany.sca.itest;

import org.oasisopen.sca.annotation.Remotable;

/**
 * A test service interface
 * @author MikeEdwards
 *
 */
@Remotable
public interface Service1 {
	
	/**
	 * Method for invoking testcase service
	 * @param input - input parameter(s) as a String
	 * @return - output data as a String
	 */
	public String operation1( String input );

}
