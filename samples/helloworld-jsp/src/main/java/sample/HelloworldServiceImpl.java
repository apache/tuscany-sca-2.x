package sample;

import org.oasisopen.sca.annotation.Service;

@Service(HelloworldService.class)
public class HelloworldServiceImpl implements HelloworldService {

	public String sayHello(String name) {
		return "Hello " + name;
	}

}
