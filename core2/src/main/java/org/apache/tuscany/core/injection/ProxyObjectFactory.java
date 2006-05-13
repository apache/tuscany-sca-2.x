package org.apache.tuscany.core.injection;

import org.apache.tuscany.common.ObjectCreationException;
import org.apache.tuscany.common.ObjectFactory;
import org.apache.tuscany.spi.wire.ProxyCreationException;
import org.apache.tuscany.spi.wire.SourceWire;

/**
 * Uses a proxy factory to return an object instance
 *
 * @version $Rev$ $Date$
 */
public class ProxyObjectFactory implements ObjectFactory {

    private SourceWire factory;

    public ProxyObjectFactory(SourceWire factory) {
        this.factory = factory;
    }

    public Object getInstance() throws ObjectCreationException {
        try {
            return factory.createProxy();
        } catch (ProxyCreationException e) {
            throw new ObjectCreationException(e);
        }
    }

}
