/* 
 * Copyright(C) OASIS(R) 2005,2009. All Rights Reserved. 
 * OASIS trademark, IPR and other policies apply. 
 */
package org.oasisopen.sca.client;

import java.util.Properties;

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
    public static SCAClient newInstance() {
        return newInstance(null, null);
    }

    /**
     * Creates a new instance of the SCAClient that can be used to lookup SCA
     * Services.
     * 
     * @param properties Properties that may be used when creating a new
     *                instance of the SCAClient
     * @return A new SCAClient instance
     */
    public static SCAClient newInstance(Properties properties) {
        return newInstance(properties, null);
    }

    /**
     * Creates a new instance of the SCAClient that can be used to lookup SCA
     * Services.
     * 
     * @param classLoader ClassLoader that may be used when creating a new
     *                instance of the SCAClient
     * @return A new SCAClient instance
     */
    public static SCAClient newInstance(ClassLoader classLoader) {
        return newInstance(null, classLoader);
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
    public static SCAClient newInstance(Properties properties, ClassLoader classLoader) {
        final SCAClientFactory factory;
        if (defaultFactory == null) {
            factory = SCAClientFactoryFinder.find(properties, classLoader);
        } else {
            factory = defaultFactory;
        }
        return factory.createSCAClient();
    }

    /**
     * This method is invoked to create a new SCAClient instance.
     * 
     * @return A new SCAClient instance
     */
    protected abstract SCAClient createSCAClient();
}
