package org.apache.tuscany.core.injection;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.WireService;

/**
 * Uses a wire to return an object instance
 *
 * @version $Rev$ $Date$
 */
public class WireObjectFactory implements ObjectFactory {

    private OutboundWire<?> wire;
    private WireService wireService;

    public WireObjectFactory(OutboundWire<?> factory, WireService wireService) {
        this.wire = factory;
        this.wireService = wireService;
    }

    public Object getInstance() throws ObjectCreationException {
        return wireService.createProxy(wire);


    }

}
