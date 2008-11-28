package test;

/**
 * Basic interface to invoke testcases
 * @author MikeEdwards
 *
 */
public interface TestInvocation {
	
	/**
	 * Method for invoking testcase
	 * @param input - input parameter(s) as a String
	 * @return - output data as a String
	 */
	public String invokeTest( String input );

}
