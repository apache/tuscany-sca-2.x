package org.apache.tuscany.container.java.scopes;

import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Destroy;

@Scope("REQUEST")
public class RequestScopedOrderedInitPojo {

    private static Object lock = new Object();
    private static int numberInstantied;
    private int initOrder;

    @Init
    public void init() {
        synchronized (RequestScopedOrderedInitPojo.lock) {
            ++RequestScopedOrderedInitPojo.numberInstantied;
            initOrder = RequestScopedOrderedInitPojo.numberInstantied;
        }
    }

    @Destroy
    public void destroy() throws OrderException {
        synchronized (RequestScopedOrderedInitPojo.lock) {
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
