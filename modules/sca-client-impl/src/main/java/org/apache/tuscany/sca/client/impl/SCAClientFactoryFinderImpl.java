/*
 * Copyright(C) OASIS(R) 2005,2009. All Rights Reserved.
 * OASIS trademark, IPR and other policies apply.
 */
package org.apache.tuscany.sca.client.impl;

import java.net.URI;
import java.util.Properties;

import org.oasisopen.sca.NoSuchDomainException;
import org.oasisopen.sca.ServiceRuntimeException;
import org.oasisopen.sca.client.SCAClientFactory;
import org.oasisopen.sca.client.SCAClientFactoryFinder;

/**
 * A Tuscany specific implementation of an SCAClientFactoryFinder which finds
 * hard codes the use of the Tuscany SCAClientFactory instead of doscovering it.
 * 
 * @see SCAClientFactoryFinder
 * @see SCAClientFactory
 */
public class SCAClientFactoryFinderImpl implements SCAClientFactoryFinder {

    /**
     * Public Constructor
     */
    public SCAClientFactoryFinderImpl() {
    }
    
    /**
     * Creates an instance of the SCAClientFactorySPI implementation. 
     * This discovers the SCAClientFactorySPI Implementation and instantiates
     * the provider's implementation.
     * 
     * @param properties    Properties that may be used when creating a new 
     * instance of the SCAClient
     * @param classLoader   ClassLoader that may be used when creating a new 
     * instance of the SCAClient
     * @return new instance of the SCAClientFactory
     * @throws ServiceRuntimeException Failed to create SCAClientFactory
     * Implementation.
     */
    public SCAClientFactory find(Properties properties,
                                 ClassLoader classLoader,
                                 URI domainURI ) throws NoSuchDomainException, ServiceRuntimeException {
        return new SCAClientFactoryImpl(domainURI);
    }
    
}
