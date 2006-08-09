package org.apache.tuscany.spi.wire;

import java.lang.reflect.Method;

import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ReferenceTarget;
import org.apache.tuscany.spi.model.ServiceDefinition;

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

    InboundWire createWire(ServiceDefinition service);

    OutboundWire createWire(ReferenceTarget reference, ReferenceDefinition def);

    void createWires(Component component, ComponentDefinition<?> definition);

    void createWires(Reference<?> reference);

    void createWires(Service<?> service);

}
