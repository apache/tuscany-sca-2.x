package org.apache.tuscany.core.builder;

import org.apache.tuscany.core.injection.ObjectCreationException;
import org.apache.tuscany.core.injection.ObjectFactory;
import org.apache.tuscany.core.invocation.spi.ProxyCreationException;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;

public class ProxyObjectFactory implements ObjectFactory {

    private ProxyFactory factory;

    public ProxyObjectFactory(ProxyFactory factory) {
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
