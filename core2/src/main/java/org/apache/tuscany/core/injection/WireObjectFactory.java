package org.apache.tuscany.core.injection;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.wire.OutboundWire;

/**
 * Uses a wire to return an object instance
 *
 * @version $Rev$ $Date$
 */
public class WireObjectFactory implements ObjectFactory {

    private OutboundWire factory;

    public WireObjectFactory(OutboundWire factory) {
        this.factory = factory;
    }

    public Object getInstance() throws ObjectCreationException {
        return factory.getTargetService();


    }

}
