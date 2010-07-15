package calculator;

public class CalculatorSyncServiceImpl implements CalculatorService {

	@Override
	public String calculate(Integer n1) {
		String retval = "sync service invoked";
		return retval;
	}

}
