package sample;

import org.oasisopen.sca.annotation.Scope;
import org.oasisopen.sca.annotation.Service;

@Scope("COMPOSITE")
@Service(CompositeService.class)
public class CompositeServiceImpl implements CompositeService {

	public CompositeServiceImpl() {
		super();
		System.out.println("Constructing CompositeServiceImpl instance.");
	}

	@Override
	public void hello() {
		System.out.println("Saying hello to CompositeServiceImpl instance.");
	}

}
