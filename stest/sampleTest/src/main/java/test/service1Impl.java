package test;

import org.osoa.sca.annotations.*;

/**
 * Simple Java component implementation for business interface Service1
 * @author MikeEdwards
 *
 */
@Service(Service1.class)
public class service1Impl implements Service1 {
	
	@Property
	public String serviceName = "service1";

	public String operation1(String input) {
		return serviceName + " operation1 invoked";
	}

}
