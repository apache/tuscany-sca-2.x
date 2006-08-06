package org.apache.tuscany.spi.wire;

import java.lang.reflect.Method;

/**
 * Creates proxies that implement Java interfaces and invocation handlers for fronting wires
 *
 * @version $$Rev$$ $$Date$$
 */

public interface WireService {

    <T> T createProxy(RuntimeWire<T> wire) throws ProxyCreationException;

    <T> T createCallbackProxy(Class<T> interfaze) throws ProxyCreationException;

    WireInvocationHandler createHandler(RuntimeWire<?> wire);

    WireInvocationHandler createCallbackHandler();

    OutboundWire createOutboundWire();

    InboundWire createInboundWire();

    OutboundInvocationChain createOutboundChain(Method operation);

    InboundInvocationChain createInboundChain(Method operation);


}
