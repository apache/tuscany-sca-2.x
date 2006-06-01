package org.apache.tuscany.container.groovy.mock;

public interface Greeting {

    String setWire(Greeting ref);
    String greet(String name);
}
