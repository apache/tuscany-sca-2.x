package org.apache.tuscany.sca.test;

import junit.framework.TestCase;

import org.apache.tuscany.host.embedded.SCARuntime;
import org.osoa.sca.CurrentCompositeContext;

public class CallBackBasicTestCase extends TestCase  {

    private CallBackBasicClient aCallBackClient;
	
    public void testCallBackBasic() {
    	aCallBackClient.run(); 
    }

    protected void setUp() throws Exception {
      SCARuntime.start("CallBackBasicTest.composite");
      
      aCallBackClient = CurrentCompositeContext.getContext().locateService(CallBackBasicClient.class, "CallBackBasicClient");
    }
   
    @Override
    protected void tearDown() throws Exception {
    	SCARuntime.stop();
    }
       
   
}
