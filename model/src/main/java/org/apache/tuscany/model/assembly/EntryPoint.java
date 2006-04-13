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
package org.apache.tuscany.model.assembly;

import java.util.List;


/**
 * An entry point exposed by its containing {@link Composite}.
 * References from outside the composite can only be connected to its entry points.
 */
public interface EntryPoint extends Part {

    /**
     * Returns the bindings supported by this entry point.
     * A single entry point may be bound to multiple transports.
     *
     * @return a list of bindings supported by this entry point
     */
    List<Binding> getBindings();

    /**
     * Returns the configured service exposed by this entry point.
     *
     * @return the configured service exposed by this entry point
     */
    ConfiguredService getConfiguredService();

    /**
     * Sets the configured service exposed by this entry point.
     *
     * @param configuredService the configured service exposed by this entry point
     */
    void setConfiguredService(ConfiguredService configuredService);

    /**
     * Returns the configured reference that wires this entry point to the published service
     * inside the aggregate.
     *
     * @return the reference that wires this entry point to the published service
     */
    ConfiguredReference getConfiguredReference();

    /**
     * Sets the configured reference that wires this entry point to the published service
     * inside the aggregate.
     *
     * @param configuredReference the configured reference that wires this entry point to
     * the published service inside the aggregate
     */
    void setConfiguredReference(ConfiguredReference configuredReference);

}
