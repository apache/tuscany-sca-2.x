/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.common.discovery.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.tuscany.common.io.util.FixedURLInputStream;

//FIXME Port to 1.5 collections

/**
 * A Registry for service  providers defined using the
 * <a href="http://java.sun.com/j2se/1.5.0/docs/guide/jar/jar.html#Service%20Provider">JAR service provider mechanism</a>.
 *
 */
public class ServiceProviderRegistry {

    private Map registry = Collections.synchronizedMap(new WeakHashMap());

    private final static ServiceProviderRegistry instance = new ServiceProviderRegistry();

    /**
     * Constructor.
     */
    public ServiceProviderRegistry() {
        super();
    }

    /**
     * @return Returns the instance.
     */
    public static ServiceProviderRegistry getInstance() {
        return instance;
    }

    /**
     * Get the available providers of a given type.
     *
     * @param clazz
     * @return
     */
    public List getServiceProviders(final Class clazz) {
        List providers = (List) registry.get(clazz);
        if (providers != null)
            return providers;

        providers = (List) AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                return loadServiceProviders(clazz);
            }
        });

        registry.put(clazz, providers);
        return providers;
    }

    /**
     * Get an provider of a given type.
     *
     * @param clazz
     * @return
     */
    public Object getServiceProvider(Class clazz) {
        List providers = getServiceProviders(clazz);
        if (providers.isEmpty())
            return null;
        else {
            return providers.get(0);
        }
    }

    /**
     * Registers an provider programatically
     *
     * @param clazz
     * @param provider
     */
    public void registerServiceProvider(Class clazz, Object provider) {
        getServiceProviders(clazz).add(provider);
    }

    /**
     * Load providers of the given type
     *
     * @param clazz
     * @return
     */
    private List loadServiceProviders(Class clazz) {
        List classNames = new ArrayList();

        // First look for a system property named <SPI className>
        String className = System.getProperty(clazz.getName());
        if (className != null)
            classNames.add(className);

        //	 Find all the class names mentioned in all the META-INF/services/<SPI className>
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try {
            Enumeration files = loader.getResources("META-INF/services/" + clazz.getName());
            while (files.hasMoreElements()) {
                URL url = (URL) files.nextElement();
                readClassNames(url, classNames);
            }
        } catch (IOException e) {
        }

        // Instantiate an provider for each of the named classes
        List providers = new ArrayList();
        Iterator i = classNames.iterator();
        while (i.hasNext()) {
            String name = (String) i.next();
            try {
                Class providerClass = Class.forName(name, true, loader);
                providers.add(providerClass.newInstance());
            } catch (Exception e) {
                // Ignore ClassNotFoundException
            }
        }
        return providers;
    }

    /**
     * Read class names from the given URL.
     * @param url
     * @param classNames
     * @throws IOException
     */
    private void readClassNames(URL url, List classNames) throws IOException {
        InputStream is = new FixedURLInputStream(url);
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String inputLine = null;
            while ((inputLine = in.readLine()) != null) {
                int i = inputLine.indexOf('#');
                if (i >= 0) {
                    inputLine = inputLine.substring(0, i);
                }
                inputLine = inputLine.trim();
                if (inputLine.length() > 0) {
                    if (!classNames.contains(inputLine)) {
                        classNames.add(inputLine);
                    }
                }
            }
        } finally {
            is.close();
		}
	}

}
