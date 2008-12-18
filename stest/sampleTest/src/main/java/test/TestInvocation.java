package test;

import javax.jws.WebService;
import javax.jws.WebMethod;
import org.osoa.sca.annotations.Remotable;

/**
 * Basic interface to invoke testcases
 * @author MikeEdwards
 *
 */
@Remotable
public interface TestInvocation {
	
	/**
	 * Method for invoking testcase
	 * @param input - input parameter(s) as a String
	 * @return - output data as a String
	 */
	@WebMethod
	public String invokeTest( String input );

}
