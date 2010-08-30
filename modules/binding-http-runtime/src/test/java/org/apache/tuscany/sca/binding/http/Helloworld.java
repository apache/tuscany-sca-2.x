package org.apache.tuscany.sca.binding.http;

import org.oasisopen.sca.annotation.Remotable;

@Remotable
public interface Helloworld {
	String sayHello(String name);
}
