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
package org.apache.tuscany.spi.context;

import org.apache.tuscany.common.ObjectCreationException;
import org.apache.tuscany.model.Scope;

/**
 * A runtime entity that manages an atomic (i.e. leaf-type) artifact.
 *
 * @version $Rev$ $Date$
 */
public interface AtomicContext<T> extends ComponentContext<T> {

    /**
     * Returns the context implementation scope
     */
    Scope getScope();

    /**
     * Sets the scope context used by this atomic context for instance management
     */
    void setScopeContext(ScopeContext<AtomicContext> context);

    /**
     * Returns whether the context should be eagerly initialized
     */
    boolean isEagerInit();

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
     * Creates a new implementation instance, generally used as a callback by a {@link ScopeContext}
     *
     * @throws ObjectCreationException
     */
    InstanceWrapper createInstance() throws ObjectCreationException;


}
