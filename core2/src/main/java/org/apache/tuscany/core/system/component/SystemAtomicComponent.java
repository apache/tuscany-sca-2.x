package org.apache.tuscany.core.system.component;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.TargetException;

/**
 * Implementations are specialized atomic components used to provide system services by the runtime
 *
 * @version $$Rev$$ $$Date$$
 */
public interface SystemAtomicComponent<T> extends AtomicComponent<T> {

    /**
     * Returns the target instance associated with the component. A target instance is the actual object a request is
     * dispatched to sans wire chain.
     *
     * @throws org.apache.tuscany.spi.component.TargetException
     *
     */
    Object getTargetInstance() throws TargetException;

}
