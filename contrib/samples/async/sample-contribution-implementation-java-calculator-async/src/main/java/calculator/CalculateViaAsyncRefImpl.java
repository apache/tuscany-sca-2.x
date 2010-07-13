package calculator;

import java.util.concurrent.ExecutionException;

import javax.xml.ws.Response;

import org.oasisopen.sca.annotation.Reference;

public class CalculateViaAsyncRefImpl implements CalculatorService {

	@Reference CalculateViaAsyncRef calculatorRef = null;

	@Override
	public String calculate(Integer n1) {
		Response<String> r = calculatorRef.calculate(n1);
		String result=null;
		try {
			result = r.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return result;
	}
}
