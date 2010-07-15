package calculator;

import org.oasisopen.sca.ResponseDispatch;

public class CalculatorServiceAsyncImpl implements CalculatorServiceAsync {

	@Override
	public void calculateAsync(Integer n1, ResponseDispatch<String> response) {
	    int result = n1 + n1;
	    String retval = "async service invoked: " + n1 + " + " + n1 + " = " + result;
		
	    response.sendResponse(retval);
	}

}
