package org.apache.tuscany.core.system.context;

import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.TargetException;

/**
 * @version $$Rev$$ $$Date$$
 */
public interface SystemAtomicContext<T> extends AtomicContext<T> {

    /**
     * Returns the target instance associated with the context. A target instance is the actual object a
     * request is dispatched to sans wire chain.
     *
     * @throws org.apache.tuscany.spi.context.TargetException
     */
    Object getTargetInstance() throws TargetException;
    
}
