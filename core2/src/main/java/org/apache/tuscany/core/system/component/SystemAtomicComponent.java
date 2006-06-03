package org.apache.tuscany.core.system.component;

import org.apache.tuscany.spi.context.AtomicComponent;
import org.apache.tuscany.spi.context.TargetException;

/**
 * Implementations manage system atomic components
 *
 * @version $$Rev$$ $$Date$$
 */
public interface SystemAtomicComponent<T> extends AtomicComponent<T> {

    /**
     * Returns the target instance associated with the component. A target instance is the actual object a
     * request is dispatched to sans wire chain.
     *
     * @throws org.apache.tuscany.spi.context.TargetException
     */
    Object getTargetInstance() throws TargetException;
    
}
