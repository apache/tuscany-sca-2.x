package calculator;

public class CalculatorAsyncServiceImpl implements CalculatorService {

	@Override
	public String calculate(Integer n1) {
		String retval = "sync service invoked";
		return retval;
	}

}
