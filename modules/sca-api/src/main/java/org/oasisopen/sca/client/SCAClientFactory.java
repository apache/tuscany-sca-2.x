/*
 * Copyright(C) OASIS(R) 2005,2010. All Rights Reserved.
 * OASIS trademark, IPR and other policies apply.
 */
package org.oasisopen.sca.client;

import java.net.URI;
import java.util.Properties;

import org.oasisopen.sca.NoSuchDomainException;
import org.oasisopen.sca.NoSuchServiceException;
import org.oasisopen.sca.client.SCAClientFactoryFinder;
import org.oasisopen.sca.client.impl.SCAClientFactoryFinderImpl;

/**
 * The SCAClientFactory can be used by non-SCA managed code to 
 * lookup services that exist in a SCA Domain.
 * 
 * @see SCAClientFactoryFinderImpl
 * 
 * @author OASIS Open
 */

public abstract class SCAClientFactory {

    /**
     * The SCAClientFactoryFinder. 
     * Provides a means by which a provider of an SCAClientFactory
     * implementation can inject a factory finder implementation into
     * the abstract SCAClientFactory class - once this is done, future
     * invocations of the SCAClientFactory use the injected factory
     * finder to locate and return an instance of a subclass of
     * SCAClientFactory.
     */
    protected static SCAClientFactoryFinder factoryFinder;
    /**
     * The Domain URI of the SCA Domain which is accessed by this 
     * SCAClientFactory
     */
    private URI domainURI;

    /**
     * Prevent concrete subclasses from using the no-arg constructor
     */
    private SCAClientFactory() {
    }

    /**
     * Constructor used by concrete subclasses
     * @param domainURI - The Domain URI of the Domain accessed via this
     * SCAClientFactory
     */
    protected SCAClientFactory(URI domainURI)
    	throws NoSuchDomainException {
        this.domainURI = domainURI;
    }

    /**
     * Gets the Domain URI of the Domain accessed via this SCAClientFactory
     * @return - the URI for the Domain
     */
    protected URI getDomainURI() {
        return domainURI;
    }
   
      
    /**
     * Creates a new instance of the SCAClientFactory that can be 
     * used to lookup SCA Services.
     * 
     * @param domainURI 	URI of the target domain for the SCAClientFactory
     * @return A new SCAClientFactory 
     */
    public static SCAClientFactory newInstance( URI domainURI ) 
    	throws NoSuchDomainException {
        return newInstance(null, null, domainURI);
    }
    
    /**
     * Creates a new instance of the SCAClientFactory that can be 
     * used to lookup SCA Services.
     *
     * @param properties   Properties that may be used when 
     * creating a new instance of the SCAClientFactory
     * @param domainURI 	URI of the target domain for the SCAClientFactory
     * @return A new SCAClientFactory instance
     */
    public static SCAClientFactory newInstance(Properties properties,
    									URI domainURI) 
    	throws NoSuchDomainException {
        return newInstance(properties, null, domainURI);
    }

    /**
     * Creates a new instance of the SCAClientFactory that can be 
     * used to lookup SCA Services.
     *
     * @param classLoader   ClassLoader that may be used when 
     * creating a new instance of the SCAClientFactory
     * @param domainURI 	URI of the target domain for the SCAClientFactory
     * @return A new SCAClientFactory instance
     */
    public static SCAClientFactory newInstance(ClassLoader classLoader, 
    									URI domainURI) 
    	throws NoSuchDomainException {
        return newInstance(null, classLoader, domainURI);
    }

    /**
     * Creates a new instance of the SCAClientFactory that can be 
     * used to lookup SCA Services.
     *
     * @param properties    Properties that may be used when 
     * creating a new instance of the SCAClientFactory
     * @param classLoader   ClassLoader that may be used when 
     * creating a new instance of the SCAClientFactory
     * @param domainURI 	URI of the target domain for the SCAClientFactory
     * @return A new SCAClientFactory instance
     */
    public static SCAClientFactory newInstance(Properties properties, 
    		                            ClassLoader classLoader,
    		                            URI domainURI) 
    	throws NoSuchDomainException {
        final SCAClientFactoryFinder finder =
            factoryFinder != null ? factoryFinder :
            	new SCAClientFactoryFinderImpl();
        final SCAClientFactory factory
            = finder.find(properties, classLoader, domainURI);
        return factory;
    }
  
    /**
     * Returns a reference proxy that implements the business interface <T>
     * of a service in the SCA Domain handled by this SCAClientFactory
     *
     * @param serviceURI the relative URI of the target service. Takes the
     * form componentName/serviceName.
     * Can also take the extended form componentName/serviceName/bindingName
     * to use a specific binding of the target service
     * 
     * @param interfaze The business interface class of the service in the 
     * domain
     * @param <T> The business interface class of the service in the domain
     *
     * @return a proxy to the target service, in the specified SCA Domain 
     * that implements the business interface <B>.
     * @throws NoSuchServiceException Service requested was not found
     * @throws NoSuchDomainException Domain requested was not found
     */
    public abstract <T> T getService(Class<T> interfaze, String serviceURI) 
        throws NoSuchServiceException, NoSuchDomainException;    
}
