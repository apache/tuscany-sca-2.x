package org.apache.tuscany.core.mock.component;

import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Scope;

@Scope("SESSION")
public class SessionScopedOrderedInitPojo {

    private static final Object lock = new Object();
    private static int numberInstantied;
    private int initOrder;

    @Init
    public void init() {
        synchronized (lock) {
            ++SessionScopedOrderedInitPojo.numberInstantied;
            initOrder = SessionScopedOrderedInitPojo.numberInstantied;
        }
    }

    @Destroy
    public void destroy() throws OrderException {
        synchronized (lock) {
            if (initOrder != SessionScopedOrderedInitPojo.numberInstantied) {
                throw new OrderException("Instance shutdown done out of order");
            }
            --SessionScopedOrderedInitPojo.numberInstantied;
        }
    }

    public int getNumberInstantiated() {
        return SessionScopedOrderedInitPojo.numberInstantied;
    }

    public int getInitOrder() {
        return initOrder;
    }

}
