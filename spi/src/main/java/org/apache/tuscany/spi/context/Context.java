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

import org.apache.tuscany.spi.Lifecycle;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.event.EventPublisher;

/**
 * An entity that provides an execution context for a runtime artifact or artifacts. A <code>Context</code>
 * may be a composite, managing child contexts or it may be an atomic, i.e. leaf, context.
 *
 * @version $Rev: 396520 $ $Date: 2006-04-24 12:38:07 +0100 (Mon, 24 Apr 2006) $
 */
public interface Context extends EventPublisher, Lifecycle {
    /**
     * Returns the name of the Context.
     *
     * @return the name of the Context
     */
    String getName();

    /**
     * Sets the name of the Context.
     *
     * @param name the name of the Context
     */
    void setName(String name);

    /**
     * Returns the instance associated with the requested name, which may be in a atomic or composite form.
     * Atomic (i.e. leaf) contexts will return an instance associated with the service name part of the
     * compound name, which may be null.
     * <p/>
     * Composite contexts will return an instance (likely a proxy) of a contained entry point context. In this
     * case, the port name on the qualified name will correspond to the composite context name and the part
     * name will be used to retrieve the contained entry point context. The latter may be null. If the
     * contained context is not an entry point context, an exception will be thrown.
     *
     * @param qName a qualified name of the requested instance
     * @return the implementation instance or a proxy to it
     * @throws TargetException if an error occurs retrieving the instance or the requested component is not an
     *                         entry point.
     * @see CompositeContext
     */
    Object getInstance(QualifiedName qName) throws TargetException;


    /**
     * Returns the parent context, or null if the context does not have one
     */
    CompositeContext getParent();

    /**
     * Sets the parent context
     */
    void setParent(CompositeContext parent);
}
