package org.apache.tuscany.spi.context;

import java.util.List;
import java.util.Map;

import org.apache.tuscany.spi.wire.TargetWire;
import org.apache.tuscany.spi.wire.SourceWire;

/**
 * @version $$Rev$$ $$Date$$
 */
public interface ComponentContext<T> extends Context<T> {

    /**
     * Returns an instance associated with the given service name
     *
     * @throws TargetException if an error occurs retrieving the instance
     */
    Object getService(String name) throws TargetException;

    
    /**
     * Returns the service interfaces implemented by the component the context represents
     */
    List<Class<?>>getServiceInterfaces();

    /**
     * Adds a target-side wire factory for the given service name. Target-side wire factories contain the
     * invocation chains associated with the destination service of a wire and are responsible for generating
     * proxies
     */
    void addTargetWire(TargetWire wire);

    /**
     * Returns the target-side wire factory associated with the given service name
     */
    TargetWire getTargetWire(String serviceName);

    /**
     * Returns a collection of target-side wire factories keyed by service name
     */
    Map<String, TargetWire> getTargetWires();

    /**
     * Adds a source-side wire factory for the given reference. Source-side wire factories contain the
     * invocation chains for a reference in the implementation associated with the instance context created by
     * this configuration. Source-side wire factories also produce proxies that are injected on a reference in
     * a component implementation.
     */
    void addSourceWire(SourceWire wire);

    /**
     * Adds a set of source-side wire multiplicity factories for the given reference. Source-side wire
     * factories contain the invocation chains for a reference in the implementation associated with the
     * instance context created by this configuration. Source-side wire factories also produce proxies that
     * are injected on a reference in a component implementation.
     */
    void addSourceWires(Class<?> multiplicityClass, List<SourceWire> wires);

    /**
     * Returns a collection of source-side wire factories for references. There may 1..n wire factories per
     * reference.
     */
    List<SourceWire> getSourceWires();

    /**
     * Called to signal to the configuration that its parent context has been activated and that it shoud
     * perform any required initialization steps
     */
    void prepare();

}
