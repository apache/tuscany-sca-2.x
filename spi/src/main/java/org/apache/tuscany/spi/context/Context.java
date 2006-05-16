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
package org.apache.tuscany.spi.context;

import java.lang.reflect.Method;

import org.apache.tuscany.spi.Lifecycle;
import org.apache.tuscany.spi.event.EventPublisher;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * Provides an execution context for application artifacts derived from an assembly
 *
 * @version $Rev: 396520 $ $Date: 2006-04-24 12:38:07 +0100 (Mon, 24 Apr 2006) $
 */
public interface Context<T> extends EventPublisher, Lifecycle {

    /**
     * Returns the name of the context.
     *
     * @return the name of the context
     */
    String getName();

    /**
     * Sets the name of the context.
     *
     * @param name the name of the context
     */
    void setName(String name);

    /**
     * Returns the parent context, or null if the context does not have one
     */
    CompositeContext getParent();

    /**
     * Sets the parent context
     */
    void setParent(CompositeContext parent);

    /**
     * Returns an instance associated with the default service for the context
     *
     * @throws TargetException if an error occurs retrieving the instance
     */
    T getService() throws TargetException;

    /**
     * Callback to create a {@link org.apache.tuscany.spi.wire.TargetInvoker} which dispatches to a service
     * contained by the context
     * 
     * @param serviceName the name of the service
     * @param operation the operation to invoke
     */
    TargetInvoker createTargetInvoker(String serviceName, Method operation);

    /**
     * Called to signal to the configuration that its parent context has been activated and that it shoud
     * perform any required initialization steps
     */
    void prepare();

}
