package org.apache.tuscany.core.wire;

import org.apache.tuscany.spi.wire.OutboundWire;

/**
 * A specialized outbound wire that uses an autowire algorithm to return reference to the target.
 *
 * @version $$Rev$$ $$Date$$
 */
public interface OutboundAutowire<T> extends OutboundWire<T> {
}
