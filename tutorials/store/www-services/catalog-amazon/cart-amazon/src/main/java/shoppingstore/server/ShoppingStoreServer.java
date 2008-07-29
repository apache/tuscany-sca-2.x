package shoppingstore.server;

import java.io.IOException;

import org.apache.tuscany.sca.host.embedded.SCADomain;

public class ShoppingStoreServer {

	public static void main(String[] args) {

		SCADomain scaDomain = SCADomain.newInstance("shoppingstore.composite");

		try {
			System.out.println("ToyApp server started (press enter to shutdown)");
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}

		scaDomain.close();
		System.out.println("ToyApp server stopped");
	}
	
	
}
