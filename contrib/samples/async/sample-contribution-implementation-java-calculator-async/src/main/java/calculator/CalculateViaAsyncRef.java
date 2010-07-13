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

	//public Response<String> calculate( Integer i1);
	
	// Sync
	public String calculate(Integer i1);
	
	// Aysnc Poll
	public Future<String> calculateAsync(Integer i1);
	
	// Async Callback
	public Future<String> calculateAsync(Integer i1, AsyncHandler<String> handler);
	
}

