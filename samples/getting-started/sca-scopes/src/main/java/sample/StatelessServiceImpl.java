package sample;

import org.oasisopen.sca.annotation.Scope;
import org.oasisopen.sca.annotation.Service;

@Scope("STATELESS")
@Service(StatelessService.class)
public class StatelessServiceImpl implements StatelessService {

	public StatelessServiceImpl() {
		super();
		System.out.println("Constructing StatelessServiceImpl instance.");
	}

	@Override
	public void hello() {
		System.out.println("Saying hello to StatelessServiceImpl.");
	}
}
