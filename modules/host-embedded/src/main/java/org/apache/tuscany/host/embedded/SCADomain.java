/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

package org.apache.tuscany.host.embedded;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.apache.tuscany.host.embedded.impl.DefaultSCADomain;
import org.osoa.sca.CallableReference;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.ServiceRuntimeException;

/**
 * A handle to an SCA domain.
 *
 * @version $Rev$ $Date$
 */
public abstract class SCADomain {
    
    /**
     * Returns a new instance of a local SCA domain. The specified deployable composites will
     * be included in the SCA domain.
     * 
     * @param domainURI the URI of the SCA domain
     * @param contributionLocation the location of an SCA contribution 
     * @param composites the deployable composites to include in the SCA domain.
     * @return
     */
    public static SCADomain newInstance(String domainURI, String contributionLocation, String...composites) {
        return createNewInstance(domainURI, contributionLocation, composites);
    }
    
    /**
     * Returns an SCADomain representing a remote SCA domain.
     * 
     * @param domainURI the URI of the SCA domain
     * @return
     */
    public static SCADomain connect(String domainURI) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Returns a new instance of a local SCA domain. The specified deployable composite will
     * be included in the SCA domain.
     * 
     * @param composite the deployable composite to include in the SCA domain.
     * @return
     */
    public static SCADomain newInstance(String composite) {
        return createNewInstance("http://localhost", ".", composite);
    }
    
    /**
     * Close the SCA domain.
     */
    public abstract void close();
    
    /**
     * Returns the URI of the SCA Domain.
     *
     * @return the URI of the SCA Domain
     */
    public abstract String getURI();

    /**
     * Cast a type-safe reference to a CallableReference.
     * Converts a type-safe reference to an equivalent CallableReference; if the target refers to a service
     * then a ServiceReference will be returned, if the target refers to a callback then a CallableReference
     * will be returned.
     *
     * @param target a reference proxy provided by the SCA runtime
     * @param <B> the Java type of the business interface for the reference
     * @param <R> the type of reference to be returned
     * @return a CallableReference equivalent for the proxy
     * @throws IllegalArgumentException if the supplied instance is not a reference supplied by the SCA runtime
     */
    public abstract <B, R extends CallableReference<B>> R cast(B target) throws IllegalArgumentException;

    /**
     * Returns a proxy for a service provided by a component in the SCA domain.
     *
     * @param businessInterface the interface that will be used to invoke the service
     * @param serviceName the name of the service
     * @param <B> the Java type of the business interface for the service
     * @return an object that implements the business interface
     */
    public abstract <B> B getService(Class<B> businessInterface, String serviceName);

    /**
     * Returns a ServiceReference for a service provided by a component in the SCA domain.
     *
     * @param businessInterface the interface that will be used to invoke the service
     * @param serviceName the name of the service
     * @param <B> the Java type of the business interface for the service
     * @return a ServiceReference for the designated service
     */
    public abstract <B> ServiceReference<B> getServiceReference(Class<B> businessInterface, String referenceName);

    /**
     * Read the service name from a configuration file
     * 
     * @param classLoader
     * @param name The name of the service class
     * @return A class name which extends/implements the service class
     * @throws IOException
     */
    private static String getServiceName(ClassLoader classLoader, String name) throws IOException {
        InputStream is = classLoader.getResourceAsStream("META-INF/services/" + name);
        if (is == null) {
            return null;
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is));
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                } else if (!line.startsWith("#")) {
                    return line.trim();
                }
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return null;
    }

    /**
     * Returns an SCADomain instance. If the system property
     * "org.apache.tuscany.host.embedded.SCADomain" is set, its value is used as the name
     * of the implementation class. Otherwise, if the resource
     * "META-INF/services/org.apache.tuscany.host.embedded.SCADomain" can be loaded from
     * the supplied classloader. Otherwise, it will use
     * "org.apache.tuscany.host.embedded.impl.DefaultSCADomain" as the default. The
     * named class is loaded from the supplied classloader.
     * 
     * @param classLoader
     * @param domainURI
     * @param contributionLocation
     * @param composites
     * @return
     */
    static SCADomain createNewInstance(String domainURI, String contributionLocation, String...composites) {

        try {
            final ClassLoader runtimeClassLoader = SCADomain.class.getClassLoader();
            final ClassLoader applicationClassLoader = Thread.currentThread().getContextClassLoader();
            
            final String name = SCADomain.class.getName();
            String className = AccessController.doPrivileged(new PrivilegedAction<String>() {
                public String run() {
                    return System.getProperty(name);
                }
            });

            if (className == null) {
                className = getServiceName(runtimeClassLoader, name);
            }
            if (className == null) {
                return new DefaultSCADomain(runtimeClassLoader, applicationClassLoader,
                                            domainURI, contributionLocation, composites);
            }
            Class cls = Class.forName(className, true, runtimeClassLoader);
            Constructor<?> constructor = cls.getConstructor(String.class, String.class, String[].class);
            SCADomain domain = (SCADomain)constructor.newInstance(
                                                                  runtimeClassLoader, applicationClassLoader,
                                                                  domainURI, contributionLocation, composites);
            return domain;
            
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
    }

}
