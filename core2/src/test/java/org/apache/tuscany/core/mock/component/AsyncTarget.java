package org.apache.tuscany.core.mock.component;

import java.util.concurrent.CountDownLatch;

import org.osoa.sca.annotations.OneWay;

/**
 * @version $$Rev$$ $$Date$$
 */
public interface AsyncTarget {

    @OneWay
    void setString(String val);

    String getString();

    public void setLatches(CountDownLatch startSignal, CountDownLatch doneSignal);

}
