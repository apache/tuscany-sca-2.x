package org.apache.tuscany.core.mock.component;

/**
 * @version $$Rev$$ $$Date$$
 */
public class AsyncTargetImpl implements AsyncTarget {

    private String val;

    public String getString() {
        return val;
    }

    public void setString(String val) {
        this.val = val;
    }
}
