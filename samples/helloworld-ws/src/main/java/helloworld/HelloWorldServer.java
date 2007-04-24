package helloworld;


import java.io.IOException;

import org.apache.tuscany.host.embedded.SCARuntime;

public class HelloWorldServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		SCARuntime.start("helloworldws.composite");
		
		try {
			System.out.println("HelloWorld server started");
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		SCARuntime.stop();
		System.out.println("HelloWorld server stopped");
	}

}
