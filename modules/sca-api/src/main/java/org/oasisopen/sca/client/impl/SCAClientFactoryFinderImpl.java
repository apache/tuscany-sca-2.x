/*
 * Copyright(C) OASIS(R) 2005,2009. All Rights Reserved.
 * OASIS trademark, IPR and other policies apply.
 */
package org.oasisopen.sca.client.impl;

import org.oasisopen.sca.client.SCAClientFactoryFinder;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URL;
import java.util.Properties;

import org.oasisopen.sca.NoSuchDomainException;
import org.oasisopen.sca.ServiceRuntimeException;
import org.oasisopen.sca.client.SCAClientFactory;

/**
 * This is a default implementation of an SCAClientFactoryFinder which is 
 * used to find an implementation of the SCAClientFactory interface.
 * 
 * @see SCAClientFactoryFinder
 * @see SCAClientFactory
 * 
 * @author OASIS Open
 */
public class SCAClientFactoryFinderImpl implements SCAClientFactoryFinder {

    /**
     * The name of the System Property used to determine the SPI 
     * implementation to use for the SCAClientFactory.
     */
    private static final String SCA_CLIENT_FACTORY_PROVIDER_KEY = 
        SCAClientFactory.class.getName();

    /**
     * The name of the file loaded from the ClassPath to determine 
     * the SPI implementation to use for the SCAClientFactory.
     */
    private static final String SCA_CLIENT_FACTORY_PROVIDER_META_INF_SERVICE 
        = "META-INF/services/" + SCA_CLIENT_FACTORY_PROVIDER_KEY;

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
                                 URI domainURI ) 
        throws NoSuchDomainException, ServiceRuntimeException {
                if (classLoader == null) {
                        classLoader = getThreadContextClassLoader ();
                }
                final String factoryImplClassName =
                        discoverProviderFactoryImplClass(properties, classLoader);
                final Class<? extends SCAClientFactory> factoryImplClass
                        = loadProviderFactoryClass(factoryImplClassName, 
                                                           classLoader);
                final SCAClientFactory factory =
                        instantiateSCAClientFactoryClass(factoryImplClass,
                                                                   domainURI );
                return factory;
        }
    
    /**
     * Gets the Context ClassLoader for the current Thread.
     * 
     * @return The Context ClassLoader for the current Thread.
     */
    private static ClassLoader getThreadContextClassLoader () {
        final ClassLoader threadClassLoader = 
                Thread.currentThread().getContextClassLoader();
        return threadClassLoader;
    }

    /**
     * Attempts to discover the class name for the SCAClientFactorySPI
     * implementation from the specified Properties, the System Properties
     * or the specified ClassLoader.
     * 
     * @return The class name of the SCAClientFactorySPI implementation
     * @throw ServiceRuntimeException Failed to find implementation for 
     * SCAClientFactorySPI.
     */
    private static String 
        discoverProviderFactoryImplClass(Properties properties, 
                                             ClassLoader classLoader) 
        throws ServiceRuntimeException {
        String providerClassName = 
                checkPropertiesForSPIClassName(properties);
        if (providerClassName != null) {
            return providerClassName;
        }

        providerClassName = 
                checkPropertiesForSPIClassName(System.getProperties());
        if (providerClassName != null) {
            return providerClassName;
        }
        
        providerClassName = checkMETAINFServicesForSIPClassName(classLoader);
        if (providerClassName == null) {
            throw new ServiceRuntimeException(
                "Failed to find implementation for SCAClientFactory");
        }
        
        return providerClassName;
    }
    
    /**
     * Attempts to find the class name for the SCAClientFactorySPI 
     * implementation from the specified Properties.
     * 
     * @return The class name for the SCAClientFactorySPI implementation 
     * or <code>null</code> if not found.
     */
    private static String 
        checkPropertiesForSPIClassName(Properties properties) {
        if (properties == null) {
            return null;
        }
        
        final String providerClassName = 
                properties.getProperty(SCA_CLIENT_FACTORY_PROVIDER_KEY);
        if (providerClassName != null && providerClassName.length() > 0) {
            return providerClassName;
        }
        
        return null;
    }

    /**
     * Attempts to find the class name for the SCAClientFactorySPI 
     * implementation from the META-INF/services directory
     * 
     * @return The class name for the SCAClientFactorySPI implementation or
     * <code>null</code> if not found.
     */
    private static String checkMETAINFServicesForSIPClassName(ClassLoader cl) 
    {
        final URL url = 
                cl.getResource(SCA_CLIENT_FACTORY_PROVIDER_META_INF_SERVICE);
        if (url == null) {
            return null;
        }
        
        InputStream in = null; 
        try {
            in = url.openStream();
            BufferedReader reader = null;
            try {
                reader = 
                        new BufferedReader(new InputStreamReader(in, "UTF-8"));

                String line;
                while ((line = readNextLine(reader)) != null) {
                    if (!line.startsWith("#") && line.length() > 0) {
                        return line;
                    }
                }
                
                return null;
            } finally {
                closeStream(reader);
            }
        } catch (IOException ex) {
            throw new ServiceRuntimeException(
                        "Failed to discover SCAClientFactory provider", ex);
        } finally {
            closeStream(in);
        }
    }

    /**
     * Reads the next line from the reader and returns the trimmed version 
     * of that line
     * 
     * @param reader The reader from which to read the next line
     * @return The trimmed next line or <code>null</code> if the end of the 
     * stream has been reached
     * @throws IOException I/O error occurred while reading from Reader
     */
    private static String readNextLine(BufferedReader reader) 
        throws IOException {
        
        String line = reader.readLine();
        if (line != null) {
            line = line.trim();
        }
        return line;
    }
    
    /**
     * Loads the specified SCAClientFactory Implementation class.
     * 
     * @param factoryImplClassName The name of the SCAClientFactory 
     * Implementation class to load
     * @return The specified SCAClientFactory Implementation class
     * @throws ServiceRuntimeException Failed to load the SCAClientFactory 
     * Implementation class 
     */
    private static Class<? extends SCAClientFactory> 
        loadProviderFactoryClass(String factoryImplClassName, 
                                         ClassLoader classLoader) 
        throws ServiceRuntimeException {

        try {
            final Class<?> providerClass = 
                classLoader.loadClass(factoryImplClassName);
            final Class<? extends SCAClientFactory> providerFactoryClass =
                providerClass.asSubclass(SCAClientFactory.class);
            return providerFactoryClass;
        } catch (ClassNotFoundException ex) {
            throw new ServiceRuntimeException(
                "Failed to load SCAClientFactory implementation class "
                + factoryImplClassName, ex);
        } catch (ClassCastException ex) {
            throw new ServiceRuntimeException(
                        "Loaded SCAClientFactory implementation class "
                        + factoryImplClassName
                    + " is not a subclass of " 
                    + SCAClientFactory.class.getName() , ex);
        }
    }

    /**
     * Instantiate an instance of the specified SCAClientFactorySPI 
     * Implementation class.
     *  
     * @param factoryImplClass The SCAClientFactorySPI Implementation 
     * class to instantiate.
     * @return An instance of the SCAClientFactorySPI Implementation class
     * @throws ServiceRuntimeException Failed to instantiate the specified 
     * specified SCAClientFactorySPI Implementation class  
     */
    private static SCAClientFactory instantiateSCAClientFactoryClass(
                        Class<? extends SCAClientFactory> factoryImplClass,
                        URI domainURI)
        throws NoSuchDomainException, ServiceRuntimeException {
        
        try {
            Constructor<? extends SCAClientFactory> URIConstructor = 
                factoryImplClass.getConstructor(domainURI.getClass());
            SCAClientFactory provider = 
               URIConstructor.newInstance( domainURI );
            return provider;
        } catch (Throwable ex) {
            throw new ServiceRuntimeException(
               "Failed to instantiate SCAClientFactory implementation class " 
               + factoryImplClass, ex);
        }
    }
    
    /**
     * Utility method for closing Closeable Object.
     * 
     * @param closeable The Object to close.
     */
    private static void closeStream(Closeable closeable) {
        if (closeable != null) {
            try{
                closeable.close();
            } catch (IOException ex) {
                throw new ServiceRuntimeException("Failed to close stream", 
                                                          ex);
            }
        }
    }
}
