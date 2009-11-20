/* 
 * Copyright(C) OASIS(R) 2005,2009. All Rights Reserved. 
 * OASIS trademark, IPR and other policies apply. 
 */
package org.oasisopen.sca.client;

import java.net.URI;
import java.util.Properties;

import org.oasisopen.sca.NoSuchDomainException;
import org.oasisopen.sca.NoSuchServiceException;
import org.oasisopen.sca.client.impl.SCAClientFactoryFinder;

/**
 * The SCAClientFactory can be used by non-SCA managed code to lookup services
 * that exist in a SCADomain.
 * 
 * @see SCAClientFactoryFinder
 * @see SCAClient
 * @author OASIS Open
 */
public abstract class SCAClientFactory {
    
    private URI domainURI;

    private SCAClientFactory() {
    }

    /**
     * Constructor used by concrete subclasses
     * @param domainURI - The Domain URI of the Domain accessed via this SCAClientFactory
     */
    protected SCAClientFactory(URI domainURI) {
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
     * The default implementation of the SCAClientFactory. A Vendor may use
     * reflection to inject a default SCAClientFactory instance that will be
     * used in the newInstance() methods rather than using the
     * SCAClientFactoryFinder.
     */
    protected static SCAClientFactory defaultFactory;

    /**
     * Creates a new instance of the SCAClient that can be used to lookup SCA
     * Services.
     * 
     * @return A new SCAClient
     */
    public static SCAClientFactory newInstance(URI domainURI) throws NoSuchDomainException {
        return newInstance(null, null, domainURI);
    }

    /**
     * Creates a new instance of the SCAClient that can be used to lookup SCA
     * Services.
     * 
     * @param properties Properties that may be used when creating a new
     *                instance of the SCAClient
     * @return A new SCAClient instance
     */
    public static SCAClientFactory newInstance(Properties properties, URI domainURI) {
        return newInstance(properties, null, domainURI);
    }

    /**
     * Creates a new instance of the SCAClient that can be used to lookup SCA
     * Services.
     * 
     * @param classLoader ClassLoader that may be used when creating a new
     *                instance of the SCAClient
     * @return A new SCAClient instance
     */
    public static SCAClientFactory newInstance(ClassLoader classLoader, URI domainURI) {
        return newInstance(null, classLoader, domainURI);
    }

    /**
     * Creates a new instance of the SCAClient that can be used to lookup SCA
     * Services.
     * 
     * @param properties Properties that may be used when creating a new
     *                instance of the SCAClient
     * @param classLoader ClassLoader that may be used when creating a new
     *                instance of the SCAClient
     * @return A new SCAClient instance
     */
    public static SCAClientFactory newInstance(Properties properties, ClassLoader classLoader, URI domainURI) {
        final SCAClientFactory factory;
        if (defaultFactory == null) {
            factory = SCAClientFactoryFinder.find(properties, classLoader, domainURI);
        } else {
            factory = defaultFactory;
        }
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
     public abstract <T> T getService(Class<T> interfaze, String serviceURI) throws NoSuchServiceException, NoSuchDomainException;
}
