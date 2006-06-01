package org.apache.tuscany.core.system.wire;

import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.context.TargetException;

/**
 * @version $$Rev$$ $$Date$$
 */
public interface SystemOutboundWire<T> extends OutboundWire<T> {

    /**
     * Returns the target instance for this wire
     */
    T getTargetService() throws TargetException;

}
