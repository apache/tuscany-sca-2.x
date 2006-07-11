package org.apache.tuscany.core.implementation;

/**
 * A system service which tracks {@link ImplementationProcessor}s
 *
 * @version $Rev$ $Date$
 */
public interface IntrospectionRegistry extends Introspector {

    /**
     * Registers the given processor and makes it available during assembly evaluation (i.e. build)
     */
    void registerProcessor(ImplementationProcessor processor);

    /**
     * Deregisters the given processor 
     */
    void unregisterProcessor(ImplementationProcessor processor);

}
