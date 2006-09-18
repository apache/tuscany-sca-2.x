package org.apache.tuscany.container.spring.impl;

import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.InvocationRuntimeException;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * A temporary interceptor until the connector is updated
 *
 * @version $Rev$ $Date$
 */
public class SpringInterceptor implements Interceptor {

    public Message invoke(Message msg) throws InvocationRuntimeException {
        TargetInvoker invoker = msg.getTargetInvoker();
        if (invoker == null) {
            throw new InvocationRuntimeException("No target invoker specified on message");
        }
        return invoker.invoke(msg);
    }

    public void setNext(Interceptor next) {
        throw new IllegalStateException("This interceptor must be the last one in an target interceptor chain");
    }

    public Interceptor getNext() {
        return null;
    }

    public boolean isOptimizable() {
        return true;
    }

}
