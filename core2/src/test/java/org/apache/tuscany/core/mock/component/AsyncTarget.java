package org.apache.tuscany.core.mock.component;

import org.osoa.sca.annotations.OneWay;

/**
 * @version $$Rev$$ $$Date$$
 */
public interface AsyncTarget {

    @OneWay
    void setString(String val);

    String getString();

}
