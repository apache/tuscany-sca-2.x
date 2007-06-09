package org.apache.tuscany.implementation.spring;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.factory.ObjectFactory;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.io.Resource;

/**
 * A Spring ParentApplicationContext for a given Spring Implementation
 * TODO - incomplete at present
 * @author MikeEdwards
 *
 */
class SCAParentApplicationContext implements ApplicationContext {
	
	// The Spring implementation for which this is the parent application context
	private SpringImplementation 	implementation;
	private RuntimeComponent 		component;
	private ProxyFactory 			proxyService;
	
    private static final String[] EMPTY_ARRAY = new String[0];
	
	public SCAParentApplicationContext( 	RuntimeComponent component, 
											SpringImplementation implementation,
											ProxyFactory proxyService ) {
		this.implementation = implementation;
		this.component		= component;
		this.proxyService 	= proxyService;
	} // end constructor

    public Object getBean(String name) throws BeansException {
        return getBean(name, null);
    }
    
    /**
     * Get a Bean for a reference..
     */
    public Object getBean(String name, Class requiredType) throws BeansException {
    	// TODO provide a real implementation of this
    	System.out.println("Spring parent context - getBean called for name: " + name );
    	// The expectation is that the requested Bean is a reference from the Spring context
    	Reference theReference = null;
    	for ( Reference reference : implementation.getReferences() ) {
    		if( reference.getName().equals(name) ) { 
    			theReference = reference;
    			break;
    		}
    	} // end for
    	if( theReference == null ) 
    		throw new NoSuchBeanDefinitionException("Unable to find Bean with name " + name );
    	// Extract the Java interface for the reference (it can't be any other interface type
    	// for a Spring application context)
    	if( requiredType == null ) {
    		JavaInterface javaInterface = 
    			(JavaInterface) theReference.getInterfaceContract().getInterface();
    		requiredType = javaInterface.getJavaClass();
    	}
    	// Create and return eturn the proxy for the reference
    	return getService( requiredType, theReference.getName() );
    } // end method getBean( String, Class )
    
   
    private <B> B getService(Class<B> businessInterface, String referenceName) {
        List<ComponentReference> refs = component.getReferences();
        for (ComponentReference ref : refs) {
            if (ref.getName().equals(referenceName)) {
                RuntimeComponentReference attachPoint = (RuntimeComponentReference)ref;
                RuntimeWire wire = attachPoint.getRuntimeWires().get(0);
                return proxyService.createProxy(businessInterface, wire);
            }
        }
        return null;
    }
    
    public boolean containsBean(String name) {
    	// TODO
    	System.out.println("Spring parent context - containsBean called for name: " + name );
        return false;
    }

    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
    	// TODO
        return false;
    }

    public boolean isTypeMatch(String name, Class targetType) throws NoSuchBeanDefinitionException {
        throw new UnsupportedOperationException();
    }

    public Class getType(String name) throws NoSuchBeanDefinitionException {
        return null;
    }

    public String[] getAliases(String name) throws NoSuchBeanDefinitionException {
        return EMPTY_ARRAY;
    }

    public ApplicationContext getParent() {
        return null;
    }

    public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
        return null;
    }

    public String getDisplayName() {
        return implementation.getURI();
    }

    public long getStartupDate() {
        return 0;
    }

    public boolean containsBeanDefinition(String beanName) {
        return false;
    }

    public int getBeanDefinitionCount() {
        return 0;
    }

    public String[] getBeanDefinitionNames() {
        return new String[0];
    }

    public String[] getBeanNamesForType(Class type) {
        return new String[0];
    }

    public String[] getBeanNamesForType(Class type, boolean includePrototypes, boolean includeFactoryBeans) {
        return new String[0];
    }

    public Map getBeansOfType(Class type) throws BeansException {
        return null;
    }

    public Map getBeansOfType(Class type, boolean includePrototypes, boolean includeFactoryBeans)
        throws BeansException {
        return null;
    }
    
    public boolean isPrototype( String theString ) {
    	return false;
    }

    public BeanFactory getParentBeanFactory() {
        return null;
    }

    public boolean containsLocalBean(String name) {
        return false;
    }

    public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        return null;
    }

    public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
        return null;
    }

    public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
        return null;
    }

    public void publishEvent(ApplicationEvent event) {

    }

    public Resource[] getResources(String locationPattern) throws IOException {
        return new Resource[0];
    }

    public Resource getResource(String location) {
        return null;
    }

    public ClassLoader getClassLoader() {
        // REVIEW: this is almost certainly flawed, but it's not clear how the SCA runtime's
        // resource loading mechanism is exposed right now.
        return this.getClass().getClassLoader();
    }
}
