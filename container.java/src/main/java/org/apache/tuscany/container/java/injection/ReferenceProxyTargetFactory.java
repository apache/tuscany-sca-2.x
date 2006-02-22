package org.apache.tuscany.container.java.injection;

import org.apache.tuscany.core.injection.FactoryInitException;
import org.apache.tuscany.core.injection.ObjectCreationException;
import org.apache.tuscany.core.injection.ObjectFactory;
import org.apache.tuscany.core.invocation.spi.ProxyCreationException;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.model.assembly.ConfiguredReference;

/**
 * Returns a service component reference target for injection onto a component implementation instance. The target may
 * be a proxy or an actual component implementation instance.
 * 
 * @version $Rev$ $Date$
 */
public class ReferenceProxyTargetFactory<T> implements ObjectFactory<T> {

    //FIXME we don't need to cache this information here
    // the logical model reference
    private ConfiguredReference reference;

    //FIXME we don't need to cache this information here
    // the SCDL name of the target component/service for this reference
    private String targetName;

    // the proxy factory for the reference
    private ProxyFactory<T> factory;

    // ----------------------------------
    // Constructors
    // ----------------------------------

    public ReferenceProxyTargetFactory(ConfiguredReference reference) throws FactoryInitException {
        // FIXME how to handle a reference that is a list - may take different proxy factories for each entry
        assert (reference != null) : "Reference was null";
        this.reference = reference;
        // FIXME should not need the cast to ProxyFactory
        factory = (ProxyFactory) reference.getProxyFactory();
        if (factory == null) {
            throw new FactoryInitException("No proxy factory found");
        }
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    public T getInstance() throws ObjectCreationException {
        try {
            return factory.createProxy();
        } catch (ProxyCreationException e) {
            throw new ObjectCreationException(e);
        }
    }

}
