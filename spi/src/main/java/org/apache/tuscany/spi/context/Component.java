package org.apache.tuscany.spi.context;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.InboundWire;

/**
 * The runtime instantiation of an SCA component
 *
 * @version $$Rev$$ $$Date$$
 */
public interface Component<T> extends SCAObject<T> {

    /**
     * Returns a service associated with the given name
     *
     * @throws TargetException if an error occurs retrieving the service instance
     */
    Object getServiceInstance(String name) throws TargetException;

    /**
     * Returns the service interfaces implemented by the component
     */
    List<Class<?>> getServiceInterfaces();

    /**
     * Adds a target-side wire. Target-side wire factories contain the invocation chains associated with the
     * destination service of a wire
     */
    void addInboundWire(InboundWire wire);

    /**
     * Returns the target-side wire associated with the given service name
     */
    InboundWire getInboundWire(String serviceName);

    /**
     * Adds a source-side wire for the given reference. Source-side wires contain the invocation chains for a
     * reference in the implementation associated with the instance wrapper created by this configuration.
     */
    void addOutboundWire(OutboundWire wire);

    /**
     * Adds a set of source-side multiplicity wires for the given reference. Source-side wires contain the
     * invocation chains for a reference in the implementation associated with the instance wrapper created by
     * this configuration.
     */
    void addOutboundWires(Class<?> multiplicityClass, List<OutboundWire> wires);

    /**
     * Returns a map of source-side wires for references. There may be 1..n wires per reference.
     */
    Map<String,List<OutboundWire>> getOutboundWires();

    /**
     * Callback to create a {@link org.apache.tuscany.spi.wire.TargetInvoker} which dispatches to a service
     * offered by the component
     *
     * @param serviceName the name of the service
     * @param operation   the operation to invoke
     */
    TargetInvoker createTargetInvoker(String serviceName, Method operation);

}
