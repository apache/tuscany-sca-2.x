package calculator;

public class CalculatorServiceSyncImpl implements CalculatorService {

	@Override
	public String calculate(Integer n1) {
	    int result = n1 + n1;
		String retval = "sync service invoked: " + n1 + " + " + n1 + " = " + result;
		return retval;
	}

}
