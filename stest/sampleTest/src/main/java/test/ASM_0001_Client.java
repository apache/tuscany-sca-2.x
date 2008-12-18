package test;

import org.osoa.sca.annotations.Service;

import test.TestInvocation;

/**
 * Basic test initiation class
 * @author MikeEdwards
 *
 */
@Service(TestInvocation.class)
public class ASM_0001_Client implements TestInvocation {
	
	private String testName = "ASM_0001";
	
	/**
	 * This method is offered as a service and is 
	 * invoked by the test client to run the test
	 */
	public String invokeTest( String input ) {
		String response = null;
		
		response = runTest( input );
		
		return response;
	} // end method invokeTest
	
	/**
	 * This method actually runs the test - and is subclassed by classes that run other tests.
	 * @param input - an input string
	 * @return - a response string = "ASM_0001 inputString invoked ok";
	 * 
	 */
	public String runTest( String input ){
		String response = null;
		
		response = testName + " " + input + " invoked ok";
		
		return response;
	} // end method runTest
	
	/**
	 * Sets the name of the test
	 * @param name - the test name
	 */
	protected void setTestName( String name ) {
		testName = name;
	}

} // 
