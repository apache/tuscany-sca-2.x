package org.apache.tuscany.core.system.wire;

import org.apache.tuscany.spi.wire.OutboundWire;

/**
 * Specified by a {@link org.apache.tuscany.core.system.model.SystemBinding}, a specialized outbound wire that returns a
 * direct reference to the target.
 *
 * @version $$Rev$$ $$Date$$
 */
public interface SystemOutboundWire<T> extends OutboundWire<T> {

}
