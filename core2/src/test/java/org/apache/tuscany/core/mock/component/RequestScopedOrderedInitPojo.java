package org.apache.tuscany.core.mock.component;

import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Scope;

@Scope("REQUEST")
public class RequestScopedOrderedInitPojo {

    private static final Object lock = new Object();
    private static int numberInstantied;
    private int initOrder;

    @Init
    public void init() {
        synchronized (lock) {
            ++RequestScopedOrderedInitPojo.numberInstantied;
            initOrder = RequestScopedOrderedInitPojo.numberInstantied;
        }
    }

    @Destroy
    public void destroy() throws OrderException {
        synchronized (lock) {
            if (initOrder != RequestScopedOrderedInitPojo.numberInstantied) {
                throw new OrderException("Instance shutdown done out of order");
            }
            --RequestScopedOrderedInitPojo.numberInstantied;
        }
    }

    public int getNumberInstantiated() {
        return RequestScopedOrderedInitPojo.numberInstantied;
    }

    public int getInitOrder() {
        return initOrder;
    }

}
