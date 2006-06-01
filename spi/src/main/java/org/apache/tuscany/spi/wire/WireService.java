package org.apache.tuscany.spi.wire;

/**
 * @version $$Rev$$ $$Date$$
 */

public interface WireService {

    <T> T createProxy(RuntimeWire<T> wire) throws ProxyCreationException;

    WireInvocationHandler createHandler(RuntimeWire<?> wire);
}
