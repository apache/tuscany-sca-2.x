package org.apache.tuscany.core.implementation.system.component;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;

import org.apache.tuscany.core.implementation.system.wire.SystemOutboundWire;

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
