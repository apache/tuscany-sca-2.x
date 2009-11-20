/* 
 * Copyright(C) OASIS(R) 2005,2009. All Rights Reserved. 2299
 * OASIS trademark, IPR and other policies apply. 2300
 */
package org.oasisopen.sca.client;

import java.net.URI;
import org.oasisopen.sca.NoSuchDomainException;
import org.oasisopen.sca.NoSuchServiceException;

/**
 * Client side helper that can be used to lookup SCA Services within a SCA Domain.
 * 
 * @see SCAClientFactory
 */
public class SCAClient {

    /**
     * Returns a reference proxy that implements the business interface <T> of a
     * service in a domain
     * 
     * @param uri the URI of the target service. Takes the form domainURI/componentName/serviceName. 
     *           The domainURI can be left off and defaults to the implementation specific default domain
     *           The service can also take the extended form
     *                domainURI/componentName/serviceName (or /componentName/serviceName). 
     *            Can also take the extended form domainURI/componentName/serviceName/bindingName 
     *            (or /componentName/serviceName/bindingName) to use a specific binding of the target service
     * @param interfaze The business interface class of the service in the
     *                domain
     * @param <T> The business interface class of the service in the domain
     * @return a proxy to the target service, in the specified SCA Domain that
     *         implements the business interface <B>.
     * @throws NoSuchServiceException Service requested was not found
     * @throws NoSuchDomainException Domain requested was not found
     */
    public static <T> T getService(Class<T> interfaze, String uri) throws NoSuchServiceException, NoSuchDomainException {
        URI domainURI = null;
        String serviceURI;
        int i = uri.indexOf('/');
        if (i == -1) {
            domainURI = null;
            serviceURI = uri;
        } else {
            serviceURI = uri.substring(i+1);
            if (i > 0) {
                domainURI = URI.create(uri.substring(0, i));
            } else {
                domainURI = null;
            }
        }
        return SCAClientFactory.newInstance(domainURI).getService(interfaze, serviceURI);       
    }
}
