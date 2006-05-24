package org.apache.tuscany.spi.context;

import java.util.List;
import java.util.Map;
import java.lang.reflect.Method;

import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.spi.wire.TargetWire;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * Provides a runtime context for application artifacts configured as components
 *
 * @version $$Rev$$ $$Date$$
 */
public interface ComponentContext<T> extends Context<T> {

    /**
     * Returns a service associated with the given name
     *
     * @throws TargetException if an error occurs retrieving the service instance
     */
    Object getService(String name) throws TargetException;


    /**
     * Returns the service interfaces implemented by the context
     */
    List<Class<?>> getServiceInterfaces();


    /**
     * Adds a source-side wire for the given reference. Source-side wires contain the invocation chains for a
     * reference in the implementation associated with the instance wrapper created by this configuration.
     */
    void addSourceWire(SourceWire wire);

    /**
     * Adds a set of source-side multiplicity wires for the given reference. Source-side wires contain the
     * invocation chains for a reference in the implementation associated with the instance wrapper created by
     * this configuration.
     */
    void addSourceWires(Class<?> multiplicityClass, List<SourceWire> wires);

    /**
     * Returns a map of source-side wires for references. There may 1..n wires per reference.
     */
    Map<String,List<SourceWire>> getSourceWires();

    /**
     * Adds a target-side wire. Target-side wire factories contain the invocation chains associated with the
     * destination service of a wire
     */
    void addTargetWire(TargetWire wire);

    /**
     * Returns the target-side wire associated with the given service name
     */
    TargetWire getTargetWire(String serviceName);

    /**
     * Returns a collection of target-side wires keyed by service name
     */
    Map<String, TargetWire> getTargetWires();

    /**
     * Callback to create a {@link org.apache.tuscany.spi.wire.TargetInvoker} which dispatches to a service
     * contained by the context
     *
     * @param serviceName the name of the service
     * @param operation the operation to invoke
     */
    TargetInvoker createTargetInvoker(String serviceName, Method operation);


}
