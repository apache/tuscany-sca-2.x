package calculator;

import java.util.concurrent.Future;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;

/**
 * client interface for async reference
 * @author kgoodson
 *
 */

public interface CalculateViaAsyncRef {

	public Response<String> calculate( Integer i1);
	
}
