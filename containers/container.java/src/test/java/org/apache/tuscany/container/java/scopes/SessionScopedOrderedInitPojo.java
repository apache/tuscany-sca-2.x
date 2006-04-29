package org.apache.tuscany.container.java.scopes;

import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Destroy;

@Scope("SESSION")
public class SessionScopedOrderedInitPojo {

    private static Object lock = new Object();
    private static int numberInstantied;
    private int initOrder;

    @Init
    public void init() {
        synchronized (SessionScopedOrderedInitPojo.lock) {
            ++SessionScopedOrderedInitPojo.numberInstantied;
            initOrder = SessionScopedOrderedInitPojo.numberInstantied;
        }
    }

    @Destroy
    public void destroy() throws OrderException {
        synchronized (SessionScopedOrderedInitPojo.lock) {
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
