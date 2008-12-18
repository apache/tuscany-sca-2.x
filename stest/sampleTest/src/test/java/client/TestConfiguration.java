package client;

/** 
 * A class to hold the metadata about the test
 * @author MikeEdwards
 *
 */
class TestConfiguration {
	
	public String testName;
	public String input;
	public String output;
	public String composite;
	public String testServiceName;
	public Class<?> testClass;		//TODO - does the client need this??
	public Class<?> serviceInterface;

	public TestConfiguration() { }
	
	public String getTestName() { return testName; }
	public String getInput() { return input; }
	public String getExpectedOutput() { return output; }
	public String getComposite() { return composite; }
	public String getTestServiceName() { return testServiceName; }
	public Class<?> getTestClass() { return testClass; }
	public Class<?> getServiceInterface() { return serviceInterface; }
}