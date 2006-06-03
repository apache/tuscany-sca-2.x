package org.apache.tuscany.spi.wire;

/**
 * Creates proxies that implement Java interfaces and invocation handlers for fronting wires
 * @version $$Rev$$ $$Date$$
 */

public interface WireService {

    <T> T createProxy(RuntimeWire<T> wire) throws ProxyCreationException;

    WireInvocationHandler createHandler(RuntimeWire<?> wire);
}
