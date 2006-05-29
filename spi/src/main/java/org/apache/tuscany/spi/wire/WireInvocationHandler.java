package org.apache.tuscany.spi.wire;

import java.lang.reflect.Method;

/**
 * Implementations are responsible for dispatching an operation down an invocation chain
 *
 * @version $$Rev$$ $$Date$$
 */
public interface WireInvocationHandler {

    public Object invoke(Method method, Object[] args) throws Throwable;

}
