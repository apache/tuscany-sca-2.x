package sample;

import org.osoa.sca.annotations.Service;

@Service(HelloworldService.class)
public class HelloworldServiceImpl implements HelloworldService {

	public String sayHello(String name) {
		return "Hello " + name;
	}

}
