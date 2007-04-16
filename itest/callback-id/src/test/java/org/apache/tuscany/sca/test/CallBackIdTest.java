package org.apache.tuscany.sca.test;

import junit.framework.TestCase;

import org.apache.tuscany.host.embedded.SCARuntime;
import org.osoa.sca.CurrentCompositeContext;

public class CallBackIdTest extends TestCase  {

    private CallBackIdClient aCallBackClient;
	
    public void testCallBackBasic() {
    	aCallBackClient.run(); 
    }

    protected void setUp() throws Exception {
    	SCARuntime.start("CallBackIdClient.composite");
    	
    	aCallBackClient = CurrentCompositeContext.getContext().locateService(CallBackIdClient.class, "CallBackIdClient");
    }
    
    protected void tearDown() throws Exception {
    	SCARuntime.stop();
    }
}
