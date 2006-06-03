package org.apache.tuscany.core.system.component;

import org.apache.tuscany.core.system.wire.SystemOutboundWire;
import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;

/**
 * Uses a system wire to return an object instance
 *
 * @version $Rev$ $Date$
 */
public class SystemWireObjectFactory implements ObjectFactory {

    private SystemOutboundWire<?> wire;

    public SystemWireObjectFactory(SystemOutboundWire<?> factory) {
        this.wire = factory;
    }

    public Object getInstance() throws ObjectCreationException {
        return wire.getTargetService();
    }

}
