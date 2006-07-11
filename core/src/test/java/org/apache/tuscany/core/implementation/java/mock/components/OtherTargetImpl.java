package org.apache.tuscany.core.implementation.java.mock.components;

/**
 * A target used for testing wires with a different source and target interface
 *
 * @version $Rev: 411441 $ $Date: 2006-06-03 07:52:56 -0700 (Sat, 03 Jun 2006) $
 */
public class OtherTargetImpl implements OtherTarget {

    private String theString;

    public String getString() {
        return theString;
    }

    public void setString(String val) {
        theString = val;
    }


}
