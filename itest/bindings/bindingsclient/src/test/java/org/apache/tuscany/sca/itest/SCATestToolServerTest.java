package org.apache.tuscany.sca.itest;


import java.io.IOException;
import java.net.Socket;

import junit.framework.TestCase;

import org.apache.tuscany.api.SCARuntime;

public class SCATestToolServerTest extends TestCase {
	
	@Override
	protected void setUp() throws Exception {
		SCARuntime.start("bindingscomposite-system.composite", "bindingscomposite.composite");
	}
	
	public void testPing() throws IOException {
		new Socket("127.0.0.1", 8080);
	}
	
	@Override
	protected void tearDown() throws Exception {
		SCARuntime.stop();
	}

}
