package org.apache.tuscany.container.java.mock.components;

/**
 * Mock system component implementation used in wiring tests
 *
 * @version $Rev$ $Date$
 */
public class TargetImpl implements Target {

    private String theString;

    public String getString() {
        return theString;
    }

    public void setString(String val) {
        theString = val;
    }

}
