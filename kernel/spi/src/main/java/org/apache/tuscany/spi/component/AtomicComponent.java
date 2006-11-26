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

import org.apache.tuscany.spi.ObjectCreationException;

/**
 * The runtime instantiation of an SCA atomic, or leaf-type, component
 *
 * @version $Rev$ $Date$
 */
public interface AtomicComponent extends Component {

    /**
     * Returns whether component instances should be eagerly initialized
     */
    boolean isEagerInit();

    /**
     * Returns the initialization level for this component.
     *
     * @return the initialization level for this component
     * @see org.apache.tuscany.spi.model.ComponentDefinition#getInitLevel()
     */
    int getInitLevel();

    /**
     * Notifies the given instance of an initialization event
     *
     * @throws TargetException
     */
    void init(Object instance) throws TargetException;

    /**
     * Notifies the given instance of a destroy event
     *
     * @throws TargetException
     */
    void destroy(Object instance) throws TargetException;

    /**
     * Creates a new implementation instance, generally used as a callback by a {@link
     * org.apache.tuscany.spi.component.ScopeContainer}
     *
     * @throws ObjectCreationException
     */
    Object createInstance() throws ObjectCreationException;

    /**
     * Removes an implementation instance associated with the current invocation context
     */
    void removeInstance();

}
