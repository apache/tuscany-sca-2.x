package org.apache.tuscany.implementation.spring;

import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.implementation.java.context.JavaPropertyValueObjectFactory;
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
	private JavaPropertyValueObjectFactory propertyFactory;
	
	/**
	 * Simple constructor
	 *
	 */
    public SpringImplementationProviderFactory( ProxyFactory proxyService,
    		JavaPropertyValueObjectFactory propertyValueObjectFactory) {
        super();
        this.proxyService 		= proxyService;
        this.propertyFactory 	= propertyValueObjectFactory;
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
        return new SpringImplementationProvider( component, implementation, 
        										 proxyService, propertyFactory );
    }

    /**
     * Returns the class of the Spring implementation
     */
    public Class<SpringImplementation> getModelType() {
        return SpringImplementation.class;
    }
} // end class SpringImplementationProviderFactory
