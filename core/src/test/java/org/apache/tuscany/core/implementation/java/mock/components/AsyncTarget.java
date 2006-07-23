package org.apache.tuscany.core.implementation.java.mock.components;

import org.osoa.sca.annotations.OneWay;

/**
 * @version $Rev$ $Date$
 */
public interface AsyncTarget {
    @OneWay
    void invoke();

    int getCount();
}
