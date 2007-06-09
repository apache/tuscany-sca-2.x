package org.apache.tuscany.implementation.spring;

import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.provider.ImplementationProviderFactory;
import org.apache.tuscany.sca.runtime.RuntimeComponent;

/**
 * ImplementationProviderFactory for Spring implementation type
 * @author MikeEdwards
 *
 */
public class SpringImplementationProviderFactory implements ImplementationProviderFactory<SpringImplementation> {

	private ProxyFactory proxyService;
	
	/**
	 * Simple constructor
	 *
	 */
    public SpringImplementationProviderFactory( ProxyFactory proxyService ) {
        super();
        this.proxyService = proxyService;
    } 

    /**
     * Returns a SpringImplementationProvider for a given component and Spring implementation
     * @param component the component for which implementation instances are required
     * @param implementation the Spring implementation with details of the component
     * implementation
     * @return the SpringImplementationProvider for the specified component
     */
    public ImplementationProvider createImplementationProvider(RuntimeComponent component,
                                                               SpringImplementation implementation) {
        return new SpringImplementationProvider( component, implementation, proxyService );
    }

    /**
     * Returns the class of the Spring implementation
     */
    public Class<SpringImplementation> getModelType() {
        return SpringImplementation.class;
    }
} // end class SpringImplementationProviderFactory
