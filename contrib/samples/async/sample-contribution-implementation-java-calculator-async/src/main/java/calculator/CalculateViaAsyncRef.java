package calculator;

import java.util.concurrent.Future;

import javax.xml.ws.AsyncHandler;

import org.oasisopen.sca.annotation.Remotable;

/**
 * Async client version of the CalculatorService interface
 *
 */

@Remotable
public interface CalculateViaAsyncRef {

	//public Response<String> calculate( Integer i1);
	
	// Sync
	public String calculate(Integer i1);
	
	// Aysnc Poll
	public Future<String> calculateAsync(Integer i1);
	
	// Async Callback
	public Future<String> calculateAsync(Integer i1, AsyncHandler<String> handler);
	
}

