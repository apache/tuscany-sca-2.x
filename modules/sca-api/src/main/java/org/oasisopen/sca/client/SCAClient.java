/* 
 * Copyright(C) OASIS(R) 2005,2009. All Rights Reserved. 2299
 * OASIS trademark, IPR and other policies apply. 2300
 */
package org.oasisopen.sca.client;

import java.net.URI;
import org.oasisopen.sca.NoSuchDomainException;
import org.oasisopen.sca.NoSuchServiceException;

/**
 * Client side interface that can be used to lookup SCA Services within a SCA
 * Domain.
 * <p>
 * The SCAClientFactory is used to obtain an implementation instance of the
 * SCAClient.
 * 
 * @see SCAClientFactory
 * @author OASIS Open
 */
public interface SCAClient {

    /**
     * Returns a reference proxy that implements the business interface <T> of a
     * service in a domain
     * 
     * @param serviceURI the relative URI of the target service. Takes the form
     *                componentName/serviceName. Can also take the extended form
     *                componentName/serviceName/bindingName to use a specific
     *                binding of the target service
     * @param domainURI the URI of an SCA Domain.
     * @param interfaze The business interface class of the service in the
     *                domain
     * @param <T> The business interface class of the service in the domain
     * @return a proxy to the target service, in the specified SCA Domain that
     *         implements the business interface <B>.
     * @throws NoSuchServiceException Service requested was not found
     * @throws NoSuchDomainException Domain requested was not found
     */
    <T> T getService(Class<T> interfaze, String serviceURI, URI domainURI) throws NoSuchServiceException, NoSuchDomainException;
}
