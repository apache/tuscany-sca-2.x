/*
 * Copyright(C) OASIS(R) 2005,2010. All Rights Reserved.
 * OASIS trademark, IPR and other policies apply.
 */

package org.oasisopen.sca.client;

import java.net.URI;
import java.util.Properties;

import org.oasisopen.sca.NoSuchDomainException;

/* A Service Provider Interface representing a SCAClientFactory finder.  
 * SCA provides a default reference implementation of this interface.
 * SCA runtime vendors can create alternative implementations of this 
 * interface that use different class loading or lookup mechanisms.
 */
public interface SCAClientFactoryFinder {

	/**
	 * Method for finding the SCAClientFactory for a given Domain URI using
	 * a specified set of properties and a a specified ClassLoader
	 * @param properties - properties to use - may be null
	 * @param classLoader - ClassLoader to use - may be null
	 * @param domainURI - the Domain URI - must be a valid SCA Domain URI
	 * @return - the SCAClientFactory or null if the factory could not be
	 * @throws - NoSuchDomainException if the domainURI does not reference 
	 * a valid SCA Domain 
	 * found
	 */
    SCAClientFactory find(Properties properties,
                          ClassLoader classLoader,
                          URI domainURI ) 
    	throws NoSuchDomainException ;
}
