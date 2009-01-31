package org.apache.tuscany.sca.itest.admin;

import java.io.PrintStream;

public class MyLogger {

	private PrintStream outStream;

	public MyLogger(PrintStream out) {
		this.outStream = out;
	}
	
	public void println(String text) {
		outStream.println("MyLog: " + text);
	}

}
