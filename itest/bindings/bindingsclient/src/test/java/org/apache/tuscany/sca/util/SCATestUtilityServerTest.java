package org.apache.tuscany.sca.util;



import java.io.IOException;
import java.net.Socket;

import org.apache.tuscany.api.SCARuntime;

import junit.framework.TestCase;

public class SCATestUtilityServerTest extends TestCase {
	
	@Override
	protected void setUp() throws Exception {
		SCARuntime.start("bindingsutility-system.composite", "bindingsutility.composite");
	}
	
	public void testPing() throws IOException {
		new Socket("127.0.0.1", 8081);
	}
	
	@Override
	protected void tearDown() throws Exception {
		SCARuntime.stop();
	}

}
