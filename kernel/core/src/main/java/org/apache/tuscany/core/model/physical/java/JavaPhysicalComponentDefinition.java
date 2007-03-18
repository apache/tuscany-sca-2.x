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
package org.apache.tuscany.core.model.physical.java;

import java.net.URI;

import org.apache.tuscany.core.component.InstanceFactoryProvider;
import org.apache.tuscany.spi.model.physical.PhysicalComponentDefinition;

/**
 * Represents the physical component definition for a Java implementation.
 *
 * @version $Rev$ $Date$
 * @param <T> the implementation class (if known)
 * @param <GROUP> the component group id type
 */
public class JavaPhysicalComponentDefinition<T, GROUP> extends PhysicalComponentDefinition<GROUP> {

    private InstanceFactoryProvider<T> provider;

    // Class loader id
    private URI classLoaderId;

    /**
     * Gets the classloader id.
     *
     * @return Classloader id.
     */
    public URI getClassLoaderId() {
        return classLoaderId;
    }

    /**
     * Set the classloader id.
     *
     * @param classLoaderId Classloader id.
     */
    public void setClassLoaderId(URI classLoaderId) {
        this.classLoaderId = classLoaderId;
    }

    /**
     * Return the provider for the component's instance factory.
     *
     * @return the provider for the component's instance factory
     */
    public InstanceFactoryProvider<T> getProvider() {
        return provider;
    }

    /**
     * Sets the provider for the component's instance factory.
     *
     * @param provider the provider for the component's instance factory
     */
    public void setProvider(InstanceFactoryProvider<T> provider) {
        this.provider = provider;
    }
}
