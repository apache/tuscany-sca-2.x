package org.apache.tuscany.core.implementation.java.mock.components;

/**
 * Mock system component implementation used in wiring tests
 *
 * @version $Rev: 411441 $ $Date: 2006-06-03 07:52:56 -0700 (Sat, 03 Jun 2006) $
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
