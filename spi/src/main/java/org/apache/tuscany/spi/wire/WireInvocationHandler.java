package org.apache.tuscany.spi.wire;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Implementations are responsible for dispatching an operation down an invocation chain
 * @version $$Rev$$ $$Date$$
 */
public interface WireInvocationHandler extends InvocationHandler {

    void setChains(Map<Method, ? extends InvocationChain> chains);
}
