package org.apache.tuscany.binding.axis2;

public interface Greeter {

    String sayHi();

    String greetMe(String requestType);

    void greetMeOneWay(String requestType);

}
