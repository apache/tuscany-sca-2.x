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
package org.apache.tuscany.core.implementation;

import java.net.URI;

import org.apache.tuscany.spi.extension.AbstractComponentExtension;
import org.apache.tuscany.spi.component.InstanceWrapper;
import org.apache.tuscany.core.component.InstanceFactory;

/**
 * Base class for implementations of physical components.
 *
 * @version $Rev$ $Date$
 * @param <T> the type of the physical instance
 */
public abstract class PhysicalComponent<T> extends AbstractComponentExtension {
    private final InstanceFactory<T> factory;

    /**
     * Constructor specifying the component id.
     *
     * @param id the component id
     * @param factory a factory for physical instances
     */
    protected PhysicalComponent(URI id, InstanceFactory<T> factory) {
        super(id);
        this.factory = factory;
    }

    public InstanceWrapper<T> createInstance() {
        return factory.newInstance();
    }
}
