package org.apache.tuscany.container.javascript.mock;

public interface Greeting {

    String setWire(Greeting ref);

    String greet(String name);
}
