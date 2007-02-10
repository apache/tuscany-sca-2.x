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
package org.apache.tuscany.core.services.classloading;

import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import org.apache.tuscany.spi.services.classloading.ClassLoaderRegistry;
import org.apache.tuscany.spi.services.classloading.DuplicateClassLoaderException;

/**
 * Implementation of a registry for classloaders.
 * 
 * @version $Rev$ $Date$
 */
public class ClassLoaderRegistryImpl implements ClassLoaderRegistry {
    private final Map<URI, ClassLoader> registry = new ConcurrentHashMap<URI, ClassLoader>();

    public synchronized void register(URI id, ClassLoader classLoader) throws DuplicateClassLoaderException {
        if (registry.containsKey(id)) {
            throw new DuplicateClassLoaderException("Duplicate class loader", id.toString());
        }
        registry.put(id, classLoader);
    }

    public ClassLoader getClassLoader(URI id) {
        return registry.get(id);
    }

    public ClassLoader unregister(URI id) {
        return registry.remove(id);
    }
}
