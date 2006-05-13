package org.apache.tuscany.container.java.mock.components;

/**
 * A target used for testing wires with a different source and target interface
 *
 * @version $Rev$ $Date$
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
