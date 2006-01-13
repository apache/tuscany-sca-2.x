package org.apache.tuscany.container.java.injection;

import java.util.List;

import org.apache.tuscany.container.java.assembly.JavaImplementation;
import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.core.injection.FactoryInitException;
import org.apache.tuscany.core.injection.ObjectCreationException;
import org.apache.tuscany.core.injection.ObjectFactory;
import org.apache.tuscany.core.invocation.spi.ProxyCreationException;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.ExternalService;

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
        // FIXME Why do we need any of the code below? START
        targetName = reference.getReference().getName();
        Class targetInterfaceType = null;

        // either an ExternalService or ServiceValue
        List<ConfiguredService> targetServiceEndpoints = reference.getConfiguredServices();
        ConfiguredService targetServiceEndpoint = !targetServiceEndpoints.isEmpty() ? targetServiceEndpoints.get(0) : null;
        if (targetServiceEndpoint.getPart() instanceof ExternalService) {
            targetName = ((ExternalService) targetServiceEndpoint.getPart()).getName();
        } else if (targetServiceEndpoint.getPart() instanceof Component) {
            // @FIXME this will only work for Java types
            try {
                Component targetComponent = (Component) targetServiceEndpoint.getPart();
                targetInterfaceType = JavaIntrospectionHelper.loadClass(((JavaImplementation) targetComponent
                        .getComponentImplementation()).getClass_());
                targetName = targetComponent.getName();
            } catch (ClassNotFoundException e) {
                throw new FactoryInitException(e);
            }
        } else {
            if (targetServiceEndpoint == null) {
                throw new FactoryInitException("Target type was null");

            } else {
                if (targetServiceEndpoint.getPart() != null) {
                    FactoryInitException e = new FactoryInitException("Unknown reference target type");
                    e.setIdentifier(targetServiceEndpoint.getPart().getClass().getName());
                    throw e;
                } else {
                    throw new FactoryInitException("Reference target type was null");
                }
            }
        }
        // FIXME Why do we need any of the code above? END
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
