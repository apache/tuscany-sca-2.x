package org.apache.tuscany.core.injection;

import org.apache.tuscany.common.ObjectCreationException;
import org.apache.tuscany.common.ObjectFactory;
import org.apache.tuscany.spi.wire.SourceWire;

/**
 * Uses a wire to return an object instance
 *
 * @version $Rev$ $Date$
 */
public class WireObjectFactory implements ObjectFactory {

    private SourceWire factory;

    public WireObjectFactory(SourceWire factory) {
        this.factory = factory;
    }

    public Object getInstance() throws ObjectCreationException {
        return factory.getTargetService();


    }

}
