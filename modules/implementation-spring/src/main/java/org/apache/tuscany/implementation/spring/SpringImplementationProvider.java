package org.apache.tuscany.implementation.spring;

import java.net.URI;

import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.factory.ObjectFactory;
import org.apache.tuscany.sca.implementation.java.context.JavaPropertyValueObjectFactory;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.osoa.sca.ComponentContext;

import org.springframework.context.support.AbstractApplicationContext;

// TODO - create a working version of this class...
/**
 * A provider class for runtime Spring implementation instances
 */
public class SpringImplementationProvider implements ImplementationProvider {
    private SpringImplementation 	implementation;
    private RuntimeComponent 		component;
    private ProxyFactory 			proxyService;
    
    // A Spring application context object
    private AbstractApplicationContext springContext;
    
    /**
     * Constructor for the provider - takes a component definition and a Spring implementation
     * description
     * @param component - the component in the assembly
     * @param implementation - the implementation
     */
    public SpringImplementationProvider( RuntimeComponent component,
                                         SpringImplementation implementation,
                                         ProxyFactory proxyService,
                                         JavaPropertyValueObjectFactory propertyValueObjectFactory ) {
        super();
        this.implementation = implementation;
        this.component 		= component;
        this.proxyService	= proxyService;
        SCAParentApplicationContext scaParentContext = 
            	new SCAParentApplicationContext( component, implementation, 
            			                         proxyService, propertyValueObjectFactory );
        springContext = new SCAApplicationContext(scaParentContext, implementation.getResource() );
    } // end constructor

    public Invoker createInvoker(RuntimeComponentService service, Operation operation) {
        return new SpringInvoker( component, springContext, service, operation );
    }

    public Invoker createCallbackInvoker(Operation operation) {
        return new SpringInvoker( component, springContext, null, operation );
    }
    
    /**
     * Start this Spring implementation instance
     */
    public void start() {
    	// Do refresh here to ensure that Spring Beans are not touched before the SCA config process 
    	// is complete...
    	springContext.refresh();
        springContext.start();
        System.out.println("SpringImplementationProvider: Spring context started");
    } // end method start()

    /**
     * Stop this implementation instance
     */
    public void stop() {
        // TODO - complete 
    	springContext.stop();
    	//System.out.println("SpringImplementationProvider: Spring context stopped");
    } // end method stop

} // end class SpringImplementationProvider
