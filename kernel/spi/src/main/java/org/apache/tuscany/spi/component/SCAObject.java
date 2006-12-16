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
package org.apache.tuscany.spi.component;

import java.util.Map;

import org.apache.tuscany.spi.Lifecycle;
import org.apache.tuscany.spi.event.EventPublisher;
import org.apache.tuscany.spi.model.Scope;

/**
 * Represents the base SCA artifact type in an assembly
 *
 * @version $Rev$ $Date$
 */
public interface SCAObject extends EventPublisher, Lifecycle {

    /**
     * Returns the artifact name
     */
    String getName();

    /**
     * Returns the canonical artifact name
     */
    String getCanonicalName();

    /**
     * Returns the parent composite, or null if the artifact does not have one
     */
    CompositeComponent getParent();

    /**
     * Returns the artifact scope
     */
    Scope getScope();

    /**
     * Returns an instance associated with the default service
     *
     * @throws TargetException if an error occurs retrieving the instance
     */
    Object getServiceInstance() throws TargetException;

    /**
     * Called to signal that the composite should perform and required steps prior to registration with its parent such
     * as wiring of its children
     */
    void prepare() throws PrepareException;

    /**
     * The extensions map contains other runtime context such as type systems for various databindings
     *
     * @return A live map of extended context
     */
    Map<Object, Object> getExtensions();

    /**
     * Returns true if the artifact is a system artifact
     *
     * @return true if the artifact is a system artifact
     */
    boolean isSystem();

}
