/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
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
package org.apache.tuscany.spi.component;

import org.apache.tuscany.spi.Lifecycle;
import org.apache.tuscany.spi.event.EventPublisher;
import org.apache.tuscany.spi.model.Scope;

/**
 * Represents the base SCA artifact type in an assembly
 *
 * @version $Rev$ $Date$
 */
public interface SCAObject<T> extends EventPublisher, Lifecycle {

    /**
     * Returns the artifact name
     */
    String getName();

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
    T getServiceInstance() throws TargetException;

    /**
     * Called to signal that the parent composite has been activated and that the artifact should perform any required
     * initialization steps
     */
    void prepare();

}
