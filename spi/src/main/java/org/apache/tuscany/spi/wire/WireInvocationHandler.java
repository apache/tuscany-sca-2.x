package org.apache.tuscany.spi.wire;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @version $$Rev$$ $$Date$$
 */
public interface WireInvocationHandler extends InvocationHandler {

    void setConfiguration(Map<Method, ? extends InvocationChain> configuration);
}
