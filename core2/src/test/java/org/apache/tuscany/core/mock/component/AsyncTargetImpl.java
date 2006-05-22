package org.apache.tuscany.core.mock.component;

import java.util.concurrent.CountDownLatch;

/**
 * @version $$Rev$$ $$Date$$
 */
public class AsyncTargetImpl implements AsyncTarget {

    private CountDownLatch startSignal;
    private CountDownLatch doneSignal;

    private String val;

    public String getString() {
        return val;
    }

    public void setString(String val) {
        try {
            startSignal.await();
            doneSignal.countDown();
            this.val = val;
        } catch (InterruptedException e) {
            throw new AssertionError();
        }
    }

    public void setLatches(CountDownLatch startSignal, CountDownLatch doneSignal) {
        this.startSignal = startSignal;
        this.doneSignal = doneSignal;
    }
}
