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
package org.apache.tuscany.spi.extension;

import javax.xml.namespace.QName;

import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;

import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.StAXElementLoader;
import org.apache.tuscany.spi.model.ModelObject;

/**
 * Support class for extending the Loader mechanism.
 *
 * @version $Rev$ $Date$
 */
@EagerInit
public abstract class LoaderExtension<T extends ModelObject> implements StAXElementLoader<T> {
    /**
     * The LoaderRegistry that this loader should register with; usually set by injection. This registry may also be
     * used to load sub-elements.
     */
    protected LoaderRegistry registry;

    /**
     * Constructor specifies the registry to register with.
     *
     * @param registry the LoaderRegistry this loader should register with
     */
    protected LoaderExtension(@Reference LoaderRegistry registry) {
        this.registry = registry;
    }

    /**
     * Initialize the loader. The base implementation registers this loader with the registry as a handler for the XML
     * type returned by {@link #getXMLType()}. Implementations may override this to register the loader as a handler for
     * multiple XML types.
     */
    @Init
    public void start() {
        registry.registerLoader(getXMLType(), this);
    }

    /**
     * Destroy the loader. The base implementation unregisters the loader from the regsitry based on the type returned
     * by {@link #getXMLType()}.
     */
    @Destroy
    public void stop() {
        registry.unregisterLoader(getXMLType(), this);
    }

    /**
     * Returns the QName of the element that this implementation handles.
     *
     * @return the QName of the element that this implementation handles
     */
    public abstract QName getXMLType();
}
