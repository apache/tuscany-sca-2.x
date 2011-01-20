/*
 *
 * Copyright(C) OASIS(R) 2009,2010. All Rights Reserved.
 * OASIS trademark, IPR and other policies apply.    
 *   
 */
 package org.apache.tuscany.sca.itest;

/**
 * A business exception
 *
 */
public class BusinessFault2 extends Exception {

	// Serialization UID
	private static final long serialVersionUID = 44240525335368929L;

	public BusinessFault2() {
		super();
	}

	public BusinessFault2(String arg0) {
		super(arg0);
	}

	public BusinessFault2(Throwable arg0) {
		super(arg0);
	}

	public BusinessFault2(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

} // end class BusinessFault1
