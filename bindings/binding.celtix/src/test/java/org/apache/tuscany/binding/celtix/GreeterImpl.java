package org.apache.tuscany.binding.celtix;

public class GreeterImpl implements Greeter {
    public java.lang.String sayHi() {
		return "sayHi";
    }

    public java.lang.String greetMe(String requestType) {
		return "Hello " + requestType;
    }

    public void greetMeOneWay(String requestType) {
    }

}
