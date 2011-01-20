/*
 *
 * Copyright(C) OASIS(R) 2009,2010. All Rights Reserved.
 * OASIS trademark, IPR and other policies apply.    
 *     
 */
package org.apache.tuscany.sca.itest;

/*
 * Exception thrown by SCA Test services
 */
public class TestException extends Exception {

	/**
	 * Required serialVersionUID field
	 */
	private static final long serialVersionUID = -6978058912756564824L;
	
	public TestException() { super(); };
	
	public TestException( String msg ) { super( msg ); };
	
	public TestException( String msg, Throwable cause ) { super( msg, cause); };
	
	public TestException( Throwable cause ) { super( cause ); };

}
