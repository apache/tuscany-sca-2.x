package org.apache.tuscany.core.system.wire;

import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.context.TargetException;

/**
 * @version $$Rev$$ $$Date$$
 */
public interface SystemInboundWire<T> extends InboundWire<T> {

    /**
     * Returns the target instance for this wire
     */
    T getTargetService() throws TargetException;

}
