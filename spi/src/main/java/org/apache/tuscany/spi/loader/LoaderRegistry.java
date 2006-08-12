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
package org.apache.tuscany.spi.loader;

import javax.xml.namespace.QName;

import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.ModelObject;

/**
 * Registry for XML loaders that can parse a StAX input stream and return model objects.
 * <p/>
 * Loaders will typically be contributed to the system by any extension that needs to handle extension specific
 * information contained in some XML configuration file. The loader can be contributed as a system component with an
 * autowire reference to this builderRegistry which is used during initialization to actually register. </p> This
 * builderRegistry can also be used to parse an input stream, dispatching to the appropriate loader for each element
 * accepted. Loaders can call back to the builderRegistry to load sub-elements that they are not able to handle
 * directly.
 *
 * @version $Rev$ $Date$
 */
public interface LoaderRegistry extends Loader {
    /**
     * Register a loader. This operation will typically be called by a loader during its initialization.
     *
     * @param element the element that should be delegated to the contibuted loader
     * @param loader  a loader that is being contributed to the system
     */
    <T extends ModelObject> void registerLoader(QName element, StAXElementLoader<T> loader);

    /**
     * Unregister a loader. This will typically be called by a loader as it is being destroyed.
     *
     * @param element the element that was being delegated to the contibuted loader
     * @param loader  a loader that should no longer be used
     */
    <T extends ModelObject> void unregisterLoader(QName element, StAXElementLoader<T> loader);

    /**
     * Regsiter a component type loader.
     *
     * @param key    a type of implementation this loader can load component types for
     * @param loader the loader that is being contributed to the system
     */
    <I extends Implementation<?>> void registerLoader(Class<I> key, ComponentTypeLoader<I> loader);

    /**
     * Unregister a component type loader form the system.
     *
     * @param key a type of implementation whose loader should be unregistered
     */
    <I extends Implementation<?>> void unregisterLoader(Class<I> key);
}
