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

package org.apache.tuscany.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.osoa.sca.ComponentContext;

/**
 * SCARuntime is used to start a Tuscany SCA runtime.
 */
public abstract class SCARuntime {

    private static SCARuntime instance;

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
     * Returns a SCARuntime instance. If the system property
     * "org.apache.tuscany.api.SCARuntime" is set, its value is used as the name
     * of the implementation class. Otherwise, if the resource
     * "META-INF/services/org.apache.tuscany.api.SCARuntime" can be loaded from
     * the supplied classloader. Otherwise, it will use
     * "org.apache.tuscany.host.embedded.DefaultSCARuntime" as the default.
     * The named class is loaded from the supplied classloader and instantiated
     * using its default (no-arg) constructor.
     * 
     * @return
     */
    private static SCARuntime newInstance(final ClassLoader classLoader) {

        try {
            final String name = SCARuntime.class.getName();
            String className = AccessController.doPrivileged(new PrivilegedAction<String>() {
                public String run() {
                    return System.getProperty(name);
                }
            });

            if (className == null) {
                className = getServiceName(classLoader, name);
            }
            if (className == null) {
                className = "org.apache.tuscany.host.embedded.DefaultSCARuntime";
            }
            Class cls = Class.forName(className, true, classLoader);
            return (SCARuntime)cls.newInstance(); // NOPMD lresende
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Get an instance of SCA Runtime
     * 
     * @return The instance
     */
    public static synchronized SCARuntime getInstance() { // NOPMD
        if (instance != null) {
            return instance;
        }
        ClassLoader classLoader = SCARuntime.class.getClassLoader();
        instance = newInstance(classLoader);
        return instance;
    }

    /**
     * Start the Tuscany runtime using default SCDLs
     */
    public static void start() {
        try {
            getInstance().startup(null, null);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Start the SCA Runtime with the given SCDL
     * 
     * @param application The URL for the application SCDL
     */
    public static void start(URL application, String compositePath) {
        try {
            getInstance().startup(application, compositePath);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Start the SCA Runtime with the given composite file.
     * 
     * @param compositePath The path of the composite file.
     */
    public static void start(String compositePath) {
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            URL applicationURL = cl.getResource(compositePath);
            getInstance().startup(applicationURL, compositePath);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Get the ComponentContext by name
     * 
     * @param componentName
     * @return
     */
    public static ComponentContext getComponentContext(String componentName) {
        return getInstance().getContext(componentName);
    }

    /**
     * Get access to an extension point
     * 
     * @param extensionPointType The interface for the extension point
     * @return The instance of the extension point
     */
    protected abstract <T> T getExtensionPoint(Class<T> extensionPointType);

    /**
     * Stop the SCA Runtime
     */
    public static void stop() {
        try {
            getInstance().shutdown();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            instance = null;
        }
    }

    /**
     * Look up the ComponentContext by name
     * 
     * @param componentName
     * @return
     */
    protected abstract ComponentContext getContext(String componentName);

    /**
     * Start up the runtime
     * 
     * @param application The URL of the SCDL for the application composite
     * @param compositePath The path of the application composite relative to
     *            the application URL
     * @throws Exception
     */
    protected abstract void startup(URL application, String compositePath)
        throws Exception;

    /**
     * Shutdown the runtime
     * 
     * @throws Exception
     */
    protected abstract void shutdown() throws Exception;
}
