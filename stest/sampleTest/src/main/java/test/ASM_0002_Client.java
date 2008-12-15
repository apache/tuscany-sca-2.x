package test;

import org.osoa.sca.annotations.Service;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Property;

/**
 * Test initiation class with a single reference of multiplicity 1..1
 * @author MikeEdwards
 *
 */
@Service(TestInvocation.class)
public class ASM_0002_Client implements TestInvocation {
	
	@Property
	public String testName = "ASM_xxxx";
	
	@Reference
	public Service1 reference1;
	
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
		
		String response1 = reference1.operation1(input);
		
		response = testName + " " + input + " " + response1;
		
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
