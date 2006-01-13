package org.apache.tuscany.core.builder;

import org.apache.tuscany.core.context.Context;

/**
 * Implementations create instance contexts based on a compiled runtime
 * configuration
 *
 * @version $Rev$ $Date$
 */
public interface RuntimeConfiguration<T extends Context> {

    /**
     * Creates an instance context based on a set of runtime configuration
     * information
     *
     * @return a new instance context
     * @throws ContextCreationException if an error occurs creating the context
     */
    T createInstanceContext() throws ContextCreationException;
    
    int getScope();
    
    String getName();
}
