package org.apache.tuscany.binding.celtix;

public interface Greeter {
    public java.lang.String sayHi();

    public java.lang.String greetMe(
        java.lang.String requestType
    );

    public void greetMeOneWay(
        java.lang.String requestType
    );

}
