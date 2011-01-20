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
public class BusinessFault1 extends Exception {

	// Serialization UID
	private static final long serialVersionUID = 44240525335368929L;

	public BusinessFault1() {
		super();
	}

	public BusinessFault1(String arg0) {
		super(arg0);
	}

	public BusinessFault1(Throwable arg0) {
		super(arg0);
	}

	public BusinessFault1(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

} // end class BusinessFault1
