package org.apache.tuscany.sca.binding.http;

public class HelloworldImpl implements Helloworld {

	@Override
	public String sayHello(String name) {
		return "Hello " + name;
	}

}
