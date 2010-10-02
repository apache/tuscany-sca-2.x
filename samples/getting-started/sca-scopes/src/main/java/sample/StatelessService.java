package sample;

import org.oasisopen.sca.annotation.Remotable;

@Remotable
public interface StatelessService {

	void hello();
	
}
