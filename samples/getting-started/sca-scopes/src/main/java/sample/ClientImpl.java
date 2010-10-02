package sample;

import org.oasisopen.sca.annotation.Reference;

public class ClientImpl implements Client {

	private static final int TIMES = 5;
	
	@Reference
	private CompositeService compositeService;

	@Reference
	private StatelessService statelessService;

	public void setCompositeService(CompositeService compositeService) {
		this.compositeService = compositeService;
	}

	public void setStatelessService(StatelessService statelessService) {
		this.statelessService = statelessService;
	}
	
	@Override
	public void run() {
		System.out.println("Calling CompositeService " + TIMES + " times...");
		for (int i = 0 ; i < TIMES; i++) {
			compositeService.hello();
		}
		System.out.println("Calling StatelessService " + TIMES + " times...");
		for (int i = 0 ; i < TIMES; i++) {
			statelessService.hello();
		}
	}


}
